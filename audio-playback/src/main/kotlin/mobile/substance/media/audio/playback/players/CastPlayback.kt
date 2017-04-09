/*
 * Copyright 2017 Substance Mobile
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

package mobile.substance.media.audio.playback.players

import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
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
import mobile.substance.media.core.audio.AudioHolder
import mobile.substance.media.audio.playback.PlaybackRemote
import mobile.substance.media.audio.playback.cast.HttpServer
import mobile.substance.media.core.audio.Song
import mobile.substance.media.options.AudioCoreOptions
import mobile.substance.media.options.AudioPlaybackOptions
import mobile.substance.media.utils.AudioCoreUtil
import mobile.substance.media.utils.AudioPlaybackUtil
import mobile.substance.media.utils.CoreUtil.toFilePath

object CastPlayback : Playback(), SessionManagerListener<Session>, RemoteMediaClient.Listener, RemoteMediaClient.ProgressListener {

    override fun onProgressUpdated(p0: Long, p1: Long) {
        dispatchPlaybackProgress(p0)
    }

    override fun onAdBreakStatusUpdated() {

    }

    override fun onStatusUpdated() {
        try {
            // We need to block status updates from the cast receiver which might be playing a song while we had been force stopped
            if (PlaybackRemote.isActive()) {
                when (remoteMediaClient?.mediaStatus?.playerState) {
                    MediaStatus.PLAYER_STATE_IDLE -> {
                        notifyIdle()
                        when (remoteMediaClient!!.mediaStatus.idleReason) {
                            MediaStatus.IDLE_REASON_ERROR -> notifyError()
                            MediaStatus.IDLE_REASON_FINISHED -> next()
                        }
                    }
                    MediaStatus.PLAYER_STATE_BUFFERING -> notifyBuffering()
                    MediaStatus.PLAYER_STATE_PAUSED -> notifyPaused()
                    MediaStatus.PLAYER_STATE_PLAYING -> notifyPlaying()
                    MediaStatus.PLAYER_STATE_UNKNOWN -> notifyIdle()
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
        println("onSendingRemoteMediaRequest()")
    }

    override fun onMetadataUpdated() {

    }

    private var castSession: CastSession? = null
    private var sessionManager: SessionManager? = null
    private var remoteMediaClient: RemoteMediaClient? = null

    private var overrideIsPlaying = false
    private var overridePosition: Int? = null

    private var fileServer = HttpServer(AudioPlaybackUtil.SERVER_PORT)
    // private var artworkServer = AsyncHttpServer()

    override fun init() {
        overrideIsPlaying = false
        overridePosition = null
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
        remoteMediaClient?.stop()?.setResultCallback {
            try {
                if (!AudioCoreUtil.isHttpUrl(fileUri.toString())) {

                    val filePath = fileUri.toFilePath(SERVICE!!)
                    val fileType = filePath?.substring(filePath.lastIndexOf(".") + 1)

                    fileServer.start()

                    val baseUrl = "http://${AudioPlaybackUtil.getIpAddressString(SERVICE!!)}:${AudioPlaybackUtil.SERVER_PORT}"
                    val audioUrl = baseUrl + filePath
                    val artworkUrl = baseUrl + artworkUri?.path

                    Log.d(TAG, "Serving files. URLS: audio: $audioUrl artwork: $artworkUrl")

                    val mediaInfo = MediaInfo.Builder(audioUrl)
                            .setContentType("audio/$fileType")
                            .setMetadata(buildMetadata(if (artworkUri != null) artworkUrl else AudioCoreOptions.defaultArtUri, metadata ?: MediaMetadataCompat.Builder().build()))
                            .setStreamType(MediaInfo.STREAM_TYPE_NONE)
                            .build()
                    doLoad(mediaInfo, true)
                } else {
                    var artworkUrl: String? = null

                    if (artworkUri != null && !AudioCoreUtil.isHttpUrl(artworkUri.toString())) {
                        fileServer.start()
                        artworkUrl = "http://${AudioPlaybackUtil.getIpAddressString(SERVICE!!)}:${AudioPlaybackUtil.SERVER_PORT}${artworkUri.path}"
                        Log.d(TAG, "Serving file. URL: artwork: $artworkUrl")
                    }

                    val mediaInfo = MediaInfo.Builder(fileUri.toString())
                            .setContentType("audio/*")
                            .setMetadata(buildMetadata(artworkUrl ?: artworkUri?.toString() ?: AudioCoreOptions.defaultArtUri, metadata ?: MediaMetadataCompat.Builder().build()))
                            .setStreamType(MediaInfo.STREAM_TYPE_LIVE)
                            .build()
                    doLoad(mediaInfo, true)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unable to play $fileUri", e)
            }
        }
    }

    override fun doPlay(song: Song) {
        doPlay(song.uri, song.uri, song.getMetadata())
    }

    override fun doPlay(fileUri: Uri, artworkUri: Uri?) = doPlay(fileUri, artworkUri, null)

    private fun doLoad(info: MediaInfo, autoPlay: Boolean) {
        notifyBuffering()
        remoteMediaClient?.load(info, autoPlay)?.setResultCallback {
            if (!it.status.isSuccess) logStatus(it.status, "Playback failed")
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
        castSession = null
        sessionManager?.removeSessionManagerListener(this)
        remoteMediaClient?.removeProgressListener(this)
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
            CastStatusCodes.FAILED -> {
                toastMessage = toast
                notifyError()
            }
            CastStatusCodes.INTERNAL_ERROR -> {
                toastMessage = status.statusMessage ?: "Internal error at the cast receiver"
                notifyError()
            }
            CastStatusCodes.TIMEOUT -> toastMessage = "Cast connection timed out"
            CastStatusCodes.UNKNOWN_ERROR -> {
                toastMessage = "Unknown error"
                notifyError()
            }
            CastStatusCodes.INVALID_REQUEST -> {
                toastMessage = "Error: Invalid request"
                notifyError()
            }
            CastStatusCodes.NETWORK_ERROR -> {
                toastMessage = "Network error"
                notifyError()
            }
        }
        Toast.makeText(SERVICE!!, toastMessage ?: return, Toast.LENGTH_SHORT).show()
    }

    override fun isPlaying(): Boolean {
        return (remoteMediaClient?.isPlaying ?: false) || overrideIsPlaying
    }

    override fun getCurrentPosition(): Int {
        return if (overrideIsPlaying) overridePosition ?: 0 else remoteMediaClient?.approximateStreamPosition?.toInt() ?: 0
    }

    override fun onSessionResumeFailed(p0: Session?, p1: Int) {}

    override fun onSessionStartFailed(p0: Session?, p1: Int) {}

    override fun onSessionEnding(p0: Session?) {
        overrideIsPlaying = true
        overridePosition = remoteMediaClient?.approximateStreamPosition?.toInt()
        SERVICE?.replacePlaybackEngine(if (AudioPlaybackOptions.isGaplessPlaybackEnabled) GaplessPlayback else LocalPlayback, true, true)
    }

    override fun onSessionEnded(p0: Session?, p1: Int) {
    }

    override fun onSessionStarted(p0: Session?, p1: String?) {}

    override fun onSessionResumed(p0: Session?, p1: Boolean) {}

    override fun onSessionStarting(p0: Session?) {}

    override fun onSessionSuspended(p0: Session?, p1: Int) {}

    override fun onSessionResuming(p0: Session?, p1: String?) {}

}