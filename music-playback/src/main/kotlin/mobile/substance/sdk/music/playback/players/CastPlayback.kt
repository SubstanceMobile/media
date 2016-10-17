/*
 * Copyright 2016 Substance Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mobile.substance.sdk.music.playback.players

import android.content.ContentUris
import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import com.google.android.gms.cast.CastStatusCodes
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.MediaStatus
import com.google.android.gms.cast.framework.*
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.common.images.WebImage
import mobile.substance.sdk.music.core.dataLinkers.MusicData
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.music.core.utils.MusicCoreUtil
import mobile.substance.sdk.music.playback.MusicPlaybackUtil
import mobile.substance.sdk.music.playback.cast.LocalServer
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.InetAddress
import java.net.ServerSocket

object CastPlayback : Playback(), SessionManagerListener<Session>, RemoteMediaClient.Listener, RemoteMediaClient.ProgressListener {

    override fun onProgressUpdated(p0: Long, p1: Long) {
        passThroughPlaybackProgress(p0)
    }

    override fun onStatusUpdated() {
        try {
            when (remoteMediaClient?.mediaStatus?.playerState) {
                MediaStatus.PLAYER_STATE_IDLE -> {
                    when (remoteMediaClient!!.mediaStatus.idleReason) {
                        MediaStatus.IDLE_REASON_ERROR -> notifyError()
                        MediaStatus.IDLE_REASON_FINISHED -> {
                            notifyIdle()
                            next()
                        }
                    }
                }
                MediaStatus.PLAYER_STATE_BUFFERING -> {
                    notifyBuffering()
                }
                MediaStatus.PLAYER_STATE_PAUSED -> {
                    notifyPaused()
                }
                MediaStatus.PLAYER_STATE_PLAYING -> {
                    notifyPlaying()
                }
                MediaStatus.PLAYER_STATE_UNKNOWN -> {
                    notifyIdle()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onQueueStatusUpdated() {

    }

    override fun onPreloadStatusUpdated() {

    }

    override fun onSendingRemoteMediaRequest() {

    }

    override fun onMetadataUpdated() {

    }

    private var castSession: CastSession? = null
    private var sessionManager: SessionManager? = null
    private var remoteMediaClient: RemoteMediaClient? = null
    private var overrideIsPlaying = false

    private var fileServer = LocalServer()
    // private var artworkServer = AsyncHttpServer()

    override fun init() {
        sessionManager = CastContext.getSharedInstance(SERVICE!!).sessionManager
        castSession = sessionManager?.currentCastSession
        sessionManager?.addSessionManagerListener(this)
    }

    override fun createPlayer() {
        remoteMediaClient = castSession?.remoteMediaClient
    }

    override fun configPlayer() {
        remoteMediaClient?.addListener(this)
        remoteMediaClient?.addProgressListener(this, 500)
    }

    private fun doPlay(fileUri: Uri, artworkUri: Uri?, metadata: MediaMetadataCompat?) {

        /*if (metadata == null) {
            metadata = retrieveMetadata(fileUri)
        }*/

        //Clear out the player if a song is being played right now.
        if (isPlaying()) {
            remoteMediaClient?.stop()
                    ?.setResultCallback {
                        if (!it.status.isSuccess) {
                            Toast.makeText(SERVICE!!, "Unable to stop", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, it.status.statusMessage.toString())
                        }
                    }
        }

        fileServer.stop()
        // artworkServer.stop()

        try {
            val url = MusicCoreUtil.getUrlFromUri(fileUri)

            if (url == null) {

                val filePath = MusicCoreUtil.getFilePath(SERVICE!!, fileUri)
                val fileType = filePath?.substring(filePath.lastIndexOf(".") + 1)

                fileServer.paths = Pair(filePath, MusicCoreUtil.getFilePath(SERVICE!!, artworkUri ?: Uri.EMPTY))
                fileServer.start()

                val baseUrl = "http://${MusicPlaybackUtil.getIpAddressString(SERVICE!!)}:${MusicPlaybackUtil.SERVER_PORT}/"
                val audioUrl = baseUrl + MusicPlaybackUtil.URL_PATH_PART_AUDIO + "/" + fileUri.toString().hashCode()
                val artworkUrl = baseUrl + MusicPlaybackUtil.URL_PATH_PART_ARTWORK + "/" + artworkUri.toString().hashCode()

                Log.d(TAG, "Serving files. URLS: audio: $audioUrl artwork: $artworkUrl")

                val mediaInfo = MediaInfo.Builder(audioUrl)
                        .setContentType("audio/$fileType")
                        .setMetadata(buildMetadata(artworkUrl, metadata ?: MediaMetadataCompat.Builder().build()))
                        .setStreamType(MediaInfo.STREAM_TYPE_NONE)
                        .build()
                doLoad(mediaInfo, true)
            } else {
                val mediaInfo = MediaInfo.Builder(url)
                        .setContentType("audio/*")
                        .setMetadata(buildMetadata(artworkUri.toString(), metadata ?: MediaMetadataCompat.Builder().build()))
                        .setStreamType(MediaInfo.STREAM_TYPE_LIVE)
                        .build()
                doLoad(mediaInfo, true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unable to play $fileUri", e)
        }
    }

    override fun doPlay(song: Song) {
        val artworkUri: String? = MusicData.findAlbumById(song.songAlbumId ?: 0)?.albumArtworkPath
        doPlay(song.uri, if (artworkUri == null) Uri.parse(song.explicitArtworkPath.orEmpty()) else Uri.parse("file://$artworkUri"), song.getMetadata())
    }

    override fun doPlay(fileUri: Uri, artworkUri: Uri?) = doPlay(fileUri, artworkUri, null)

    private fun doLoad(info: MediaInfo, autoplay: Boolean) {
        remoteMediaClient?.stop()
        remoteMediaClient?.load(info, autoplay)?.setResultCallback {
            if (!(it.status?.isSuccess ?: false)) {
                logStatus(it.status, "Playback failed")
            } else notifyPlaying()
        }
    }

    private fun buildMetadata(imageUrl: String, base: MediaMetadataCompat): MediaMetadata {
        val metadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK)
        metadata.addImage(WebImage(Uri.parse(imageUrl)))
        metadata.putString(MediaMetadata.KEY_TITLE, base.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
        metadata.putString(MediaMetadata.KEY_ARTIST, base.getString(MediaMetadataCompat.METADATA_KEY_ARTIST))
        metadata.putString(MediaMetadata.KEY_ALBUM_TITLE, base.getString(MediaMetadataCompat.METADATA_KEY_ALBUM))
        return metadata
    }

    override fun doResume() {
        remoteMediaClient?.play()?.setResultCallback {
            if (!it.status.isSuccess) logStatus(it.status, "Resume failed")
        }
    }

    override fun doPause() {
        remoteMediaClient?.pause()?.setResultCallback {
            if (!it.status.isSuccess) logStatus(it.status, "Pause failed")
        }
    }

    override fun doStop() {
        fileServer.stop()
        sessionManager?.removeSessionManagerListener(this)
        remoteMediaClient = null
    }

    override fun doSeek(time: Long) {
        remoteMediaClient?.seek(time)?.setResultCallback {
            if (!it.status.isSuccess) logStatus(it.status, "Seek failed")
        }
    }

    private fun logStatus(status: Status, toast: String) {
        Log.d(TAG, "Google Cast returned unsuccessful. Code: ${status.statusCode}; Message: ${status.statusMessage}")
        var toastMessage: String? = null
        when (status.statusCode) {
            CastStatusCodes.FAILED -> toastMessage = toast
            CastStatusCodes.INTERNAL_ERROR -> toastMessage = status.statusMessage ?: "Internal error at the cast receiver"
            CastStatusCodes.TIMEOUT -> toastMessage = "Cast connection timed out"
            CastStatusCodes.UNKNOWN_ERROR -> toastMessage = "Unknown error"
        }
        Toast.makeText(SERVICE!!, toastMessage ?: return, Toast.LENGTH_SHORT).show()
    }

    override fun isPlaying(): Boolean {
        return (remoteMediaClient?.isPlaying ?: false) || overrideIsPlaying
    }

    override fun getCurrentPosInSong(): Int {
        return remoteMediaClient?.approximateStreamPosition?.toInt() ?: 0
    }

    override fun onSessionResumeFailed(p0: Session?, p1: Int) {}

    override fun onSessionStartFailed(p0: Session?, p1: Int) {}

    override fun onSessionEnding(p0: Session?) {}

    override fun onSessionEnded(p0: Session?, p1: Int) {
        SERVICE?.replacePlaybackEngine(LocalPlayback, true, true)
    }

    override fun onSessionStarted(p0: Session?, p1: String?) {}

    override fun onSessionResumed(p0: Session?, p1: Boolean) {}

    override fun onSessionStarting(p0: Session?) {}

    override fun onSessionSuspended(p0: Session?, p1: Int) {}

    override fun onSessionResuming(p0: Session?, p1: String?) {}

    override fun isPlayerNecessary(): Boolean = remoteMediaClient == null

    override val playerCreatedOnClassCreation: Boolean = false

}