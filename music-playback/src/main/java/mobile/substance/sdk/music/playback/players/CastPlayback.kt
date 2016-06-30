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
import android.util.Log
import com.google.android.gms.cast.framework.*
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import mobile.substance.sdk.music.core.CoreUtil
import mobile.substance.sdk.music.playback.HeadsetPlugReceiver
import mobile.substance.sdk.music.playback.LocalPlayback
import mobile.substance.sdk.music.playback.MusicPlaybackUtil
import mobile.substance.sdk.music.playback.cast.LocalServer

object CastPlayback : Playback(), SessionManagerListener<Session> {

    private var castSession: CastSession? = null
    private var sessionManager: SessionManager? = null
    private var remoteMediaClient: RemoteMediaClient? = null

    private val fileServer = LocalServer(MusicPlaybackUtil.SERVER_TYPE_AUDIO)
    private val artworkServer = LocalServer(MusicPlaybackUtil.SERVER_TYPE_ARTWORK)

    override fun init() {
        sessionManager = CastContext.getSharedInstance(SERVICE!!).sessionManager
        castSession = sessionManager?.currentCastSession
        sessionManager?.addSessionManagerListener(this)
    }

    override fun doPlay(uri: Uri, listenersAlreadyNotified: Boolean) {
        //Clear out the player if a song is being played right now.
        if (isPlaying()) {
            remoteMediaClient?.stop()
        }

        //Register the broadcast receiver
        HeadsetPlugReceiver register SERVICE!!

        //Notify the listeners if it hasn't already happened externally
        if (!listenersAlreadyNotified) {
            //TODO Work with listeners
        }

        //Start the service and do some work!
        try {
            val url = MusicPlaybackUtil.getUrlFromUri(uri)
            if (url.equals(""))

            else {
                LocalPlayback.triggerStartBuffer()
            }
        } catch (e: Exception) {
            Log.e(LocalPlayback.TAG, "Unable to play " + CoreUtil.getFilePath(LocalPlayback.SERVICE, uri), e)
        }
    }

    override fun doResume() {

    }

    override fun doPause() {

    }

    override fun doStop() {
        sessionManager?.removeSessionManagerListener(this)
    }

    override fun doSeek(time: Long) {

    }

    override fun repeat(repeating: Boolean) {

    }

    override fun isPlaying(): Boolean {
        return false
    }

    override fun isRepeating(): Boolean {
        return false
    }

    override fun getCurrentPosInSong(): Int {
        return 0
    }

    override fun onSessionResumeFailed(p0: Session?, p1: Int) {

    }

    override fun onSessionStartFailed(p0: Session?, p1: Int) {

    }

    override fun onSessionEnding(p0: Session?) {

    }

    override fun onSessionEnded(p0: Session?, p1: Int) {
        SERVICE?.replacePlaybackEngine(LocalPlayback, shouldHotSwap(), true)
    }

    override fun onSessionStarted(p0: Session?, p1: String?) {

    }

    override fun onSessionResumed(p0: Session?, p1: Boolean) {

    }

    override fun onSessionStarting(p0: Session?) {

    }

    override fun onSessionSuspended(p0: Session?, p1: Int) {

    }

    override fun onSessionResuming(p0: Session?, p1: String?) {

    }

    private fun isSongLoaded(): Boolean = false

    private fun shouldHotSwap(): Boolean = isPlaying() && isSongLoaded()


}