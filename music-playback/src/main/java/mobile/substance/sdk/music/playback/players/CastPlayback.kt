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

import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.MediaStatus
import com.google.android.gms.cast.framework.*
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.android.gms.common.images.WebImage
import mobile.substance.sdk.music.core.dataLinkers.MusicData
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.music.core.utils.MusicCoreUtil
import mobile.substance.sdk.music.playback.MusicPlaybackUtil
import mobile.substance.sdk.music.playback.cast.LocalServer
import mobile.substance.sdk.music.playback.service.HeadsetPlugReceiver

object CastPlayback : Playback(), SessionManagerListener<Session>, RemoteMediaClient.Listener {

    private var repeating = false

    override fun onStatusUpdated() {
        when (remoteMediaClient?.mediaStatus?.playerState) {
            MediaStatus.PLAYER_STATE_IDLE -> {
                playbackState = PlaybackStateCompat.STATE_NONE
                when (remoteMediaClient!!.mediaStatus.idleReason) {
                    MediaStatus.IDLE_REASON_ERROR -> {

                    }
                    MediaStatus.IDLE_REASON_FINISHED -> next()
                }
            }
            MediaStatus.PLAYER_STATE_BUFFERING -> {
                playbackState = PlaybackStateCompat.STATE_BUFFERING
            }
            MediaStatus.PLAYER_STATE_PAUSED -> {
                playbackState = PlaybackStateCompat.STATE_PAUSED
            }
            MediaStatus.PLAYER_STATE_PLAYING -> {
                playbackState = PlaybackStateCompat.STATE_PLAYING
            }
            MediaStatus.PLAYER_STATE_UNKNOWN -> {
                playbackState = PlaybackStateCompat.STATE_NONE
            }
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

    private var fileServer: LocalServer? = null
    private var artworkServer: LocalServer? = null

    override fun init() {
        fileServer = LocalServer(MusicPlaybackUtil.SERVER_TYPE_AUDIO)
        artworkServer = LocalServer(MusicPlaybackUtil.SERVER_TYPE_ARTWORK)

        sessionManager = CastContext.getSharedInstance(SERVICE!!).sessionManager
        castSession = sessionManager?.currentCastSession
        sessionManager?.addSessionManagerListener(this)
    }

    override fun createPlayer() {
        remoteMediaClient = castSession?.remoteMediaClient
    }

    override fun configPlayer() {
        remoteMediaClient?.addListener(this)
    }

    override fun doPlay(uri: Uri, listenersAlreadyNotified: Boolean, mediaId: Long?) {
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

        if (fileServer?.isAlive!!)
            fileServer?.stop()
        if (artworkServer?.isAlive!!)
            artworkServer?.stop()

        //Register the broadcast receiver
        HeadsetPlugReceiver register SERVICE!!

        //Notify the listeners if it hasn't already happened externally
        if (!listenersAlreadyNotified) {
            //TODO Work with listeners
        }

        //Start the service and do some work!
        try {
            val url = MusicCoreUtil.getUrlFromUri(uri)

            val song: Song? = if (mediaId != null) MusicData.findSongById(mediaId) else null

            if (url == null) {

                fileServer?.setUri(SERVICE!!, uri)

                if (mediaId != null)
                    try {
                        artworkServer?.setUri(SERVICE!!, MusicData.findAlbumById(song?.songAlbumId!!)?.albumArtworkUri!!)
                    } catch (ignored: KotlinNullPointerException) {}
                val ipAddress = MusicPlaybackUtil.getIpAddressString(SERVICE!!)
                val fileUrl = "http://$ipAddress:${MusicPlaybackUtil.SERVER_PORT_AUDIO}"
                val artworkUrl = "http://$ipAddress:${MusicPlaybackUtil.SERVER_PORT_ARTWORK}"

                Log.d(TAG, "Serving local files for Cast playback... Here are the urls: $fileUrl $artworkUrl")

                fileServer?.start()
                artworkServer?.start()

                val mediaInfo = MediaInfo.Builder(fileUrl)
                        .setContentType("audio/*")
                        .setMetadata(buildMetadata(artworkUrl, song?.metadata!!)) // TODO
                        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                        .build()
                doLoad(mediaInfo, true)
            } else {
                val mediaInfo = MediaInfo.Builder(url)
                    .setContentType("audio/*")
                    .setMetadata(buildMetadata(song?.explicitArtworkPath ?: "", song?.metadata!!)) // TODO
                    .setStreamType(MediaInfo.STREAM_TYPE_LIVE)
                    .build()
                doLoad(mediaInfo, true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unable to play " + MusicCoreUtil.getFilePath(SERVICE!!, uri), e)
        }
    }

    private fun doLoad(info: MediaInfo, autoplay: Boolean) {
        remoteMediaClient?.load(info, autoplay)?.setResultCallback {
            if (!(it.status?.isSuccess ?: false)) {
                Toast.makeText(SERVICE!!, "Unable to start playback", Toast.LENGTH_SHORT).show()
                Log.d(TAG, it.status?.statusMessage.toString())
            } else nowPlaying()
        }
    }

    private fun buildMetadata(imageUrl: String, base: MediaMetadataCompat): MediaMetadata {
        val metadata = MediaMetadata()
        metadata.addImage(WebImage(Uri.parse(imageUrl)))
        metadata.putString(MediaMetadata.KEY_TITLE, base.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
        metadata.putString(MediaMetadata.KEY_ALBUM_TITLE, base.getString(MediaMetadataCompat.METADATA_KEY_ALBUM))
        metadata.putString(MediaMetadata.KEY_ARTIST, base.getString(MediaMetadataCompat.METADATA_KEY_ARTIST))
        return metadata
    }

    override fun doResume() {
        remoteMediaClient?.play()?.setResultCallback {
            if (!it.status.isSuccess) {
                Toast.makeText(SERVICE!!, "Unable to resume", Toast.LENGTH_SHORT).show()
                Log.d(TAG, it.status.statusMessage)
            } else nowPlaying()
        }
    }

    override fun doPause() {
        remoteMediaClient?.pause()?.setResultCallback {
            if (!it.status.isSuccess) {
                Toast.makeText(SERVICE!!, "Unable to resume", Toast.LENGTH_SHORT).show()
                Log.d(TAG, it.status.statusMessage)
            } else nowPaused()
        }
    }

    override fun doStop() {
        fileServer?.stop()
        artworkServer?.stop()
        sessionManager?.removeSessionManagerListener(this)
    }

    override fun doSeek(time: Long) {
        remoteMediaClient?.seek(time)?.setResultCallback {
            if (!it.status.isSuccess) {
                Toast.makeText(SERVICE!!, "Unable to resume", Toast.LENGTH_SHORT).show()
                Log.d(TAG, it.status.statusMessage.toString())
            }
        }
    }

    override fun setRepeating(repeating: Boolean) {
        this.repeating = repeating
    }

    override fun isPlaying(): Boolean {
        return remoteMediaClient?.isPlaying ?: false
    }

    override fun isRepeating(): Boolean {
        return repeating
    }

    override fun getCurrentPosInSong(): Int {
        return remoteMediaClient?.approximateStreamPosition?.toInt() ?: 0
    }

    override fun onSessionResumeFailed(p0: Session?, p1: Int) {}

    override fun onSessionStartFailed(p0: Session?, p1: Int) {}

    override fun onSessionEnding(p0: Session?) {}

    override fun onSessionEnded(p0: Session?, p1: Int) {
        SERVICE?.replacePlaybackEngine(LocalPlayback, shouldHotSwap(), true)
    }

    override fun onSessionStarted(p0: Session?, p1: String?) {}

    override fun onSessionResumed(p0: Session?, p1: Boolean) {}

    override fun onSessionStarting(p0: Session?) {}

    override fun onSessionSuspended(p0: Session?, p1: Int) {}

    override fun onSessionResuming(p0: Session?, p1: String?) {}

    private fun isSongLoaded(): Boolean = false

    private fun shouldHotSwap(): Boolean = isPlaying() && isSongLoaded()

    override val playerCreatedOnClassCreation: Boolean = false

}