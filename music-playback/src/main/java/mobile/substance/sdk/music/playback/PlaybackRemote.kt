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

package mobile.substance.sdk.music.playback

import android.app.Notification
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v7.app.NotificationCompat
import android.util.Log
import android.view.MenuItem
import mobile.substance.sdk.music.core.MusicCoreOptions
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.music.playback.notification.DefaultMediaNotification
import mobile.substance.sdk.music.playback.notification.MediaNotification
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by Julian Os on 07.05.2016.
 */
object PlaybackRemote : ServiceConnection {
    private var context: Context? = null
    private var service: MusicService? = null
    private var isBound = false

    ///////////////////////////////////////////////////////////////////////////
    // Manages Connections
    ///////////////////////////////////////////////////////////////////////////

    private val SERVICE_BOUND_LISTENERS: MutableList<ServiceLoadListener> = ArrayList();
    private interface ServiceLoadListener {
        fun respond(service: MusicService?);
    }

    private fun getService(listener: ServiceLoadListener) {
        val running = MusicPlaybackUtil.isServiceRunning(context)
        if (running && isBound) {
            listener.respond(service)
            return
        }
        if (!running) context?.startService(Intent(context, MusicService::class.java))
        if (!isBound) context?.bindService(Intent(context, MusicService::class.java), this, Context.BIND_IMPORTANT)
        SERVICE_BOUND_LISTENERS.add(listener)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        service = (service as MusicService.ServiceBinder).service
        service!!.registerCallback(REMOTE_CALLBACK)
        isBound = true
        for (listener in SERVICE_BOUND_LISTENERS) listener.respond(service)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        service = null
        isBound = false
    }

    ///////////////////////////////////////////////////////////////////////////
    // Main
    ///////////////////////////////////////////////////////////////////////////

    private var REMOTE_CALLBACK: RemoteCallback? = null

    fun setup(context: Context, callback: RemoteCallback?) {
        this.context = context
        this.REMOTE_CALLBACK = callback
    }

    fun cleanup() {
        if (isBound) context!!.unbindService(this)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Callback
    ///////////////////////////////////////////////////////////////////////////

    fun registerCallback(callback: RemoteCallback) {
        service!!.registerCallback(callback)
    }

    fun unregisterCallback(callback: RemoteCallback) {
        service!!.unregisterCallback(callback)
    }

    interface RemoteCallback {

        fun onReceivedIntent(intent: Intent) {
            when {
                intent.action.endsWith(".RESUME") -> resume()
                intent.action.endsWith(".PAUSE") -> pause()
                intent.action.endsWith(".skip.FORWARD") -> playNext()
                intent.action.endsWith(".skip.BACKWARD") -> playPrevious()
                intent.action.endsWith(".NOTIFICATION") -> context!!.startActivity(context!!.packageManager.getLaunchIntentForPackage(context!!.applicationContext.packageName))
                intent.action.endsWith(".SEEK") -> seekTo(intent.getIntExtra("progress", 0))
            }
        }

        fun onProgressChanged(progress: Int)

        fun onDurationChanged(duration: Int)

        fun onSongChanged(song: Song)

        fun onStateChanged(state: PlaybackState, isRepeating: Boolean)

        fun onQueueChanged(queue: List<Song>)

    }

    ///////////////////////////////////////////////////////////////////////////
    // Controls
    ///////////////////////////////////////////////////////////////////////////

    internal fun play() {
        getService(object : ServiceLoadListener {
            override fun respond(service: MusicService?) {
                service!!.play()
            }
        })
    }

    fun resume() {
        getService(object : ServiceLoadListener {
            override fun respond(service: MusicService?) {
                service!!.resume()
            }
        })
    }

    fun pause() {
        getService(object : ServiceLoadListener {
            override fun respond(service: MusicService?) {
                service!!.pause()
            }
        })
    }

    fun play(songs: MutableList<Song>, position: Int) {
        var play = true
        if (getCurrentSong() != null)
            play = songs.first().id != getCurrentSong()?.id
        Log.d(PlaybackRemote::class.java.simpleName, "play(${songs.size}, $position)")
        MusicQueue.set(songs, position)
        if (play) play()
    }

    fun play(song: Song) {
        val songs: MutableList<Song> = ArrayList()
        songs.add(song)
        play(songs, 0)
    }

    fun playNext() {
        MusicQueue.moveForward(1)
        play()
    }

    fun playPrevious() {
        MusicQueue.moveBackward(1)
        play()
    }

    fun seekTo(progress: Int) {
        getService(object : ServiceLoadListener {
            override fun respond(service: MusicService?) {
                service!!.seekTo(progress)
            }
        })
    }

    fun shuffle() {
        //TODO
    }

    ///////////////////////////////////////////////////////////////////////////
    // Queue
    ///////////////////////////////////////////////////////////////////////////

    fun getCurrentSong(): Song? = MusicQueue.getCurrentSong()

    fun getQueue(): List<Song>? = MusicQueue.getMutableQueue(false)

    fun getQueue(startAtPosition: Boolean): List<Song>? = MusicQueue.getMutableQueue(startAtPosition)

    fun setQueue(queue: MutableList<Song>, position: Int) = MusicQueue.set(queue, position)

    ///////////////////////////////////////////////////////////////////////////
    // Notification
    ///////////////////////////////////////////////////////////////////////////

    private var notificationCreator: MediaNotification = DefaultMediaNotification()
    private var notificationBuilder: NotificationCompat.Builder? = null;

    fun setNotification(notification: MediaNotification) {
        this.notificationCreator = notification
    }

    fun makeNotificaion(): Notification {

    }

    interface NotificationUpdateInterface {
        fun updateNotification(notification: Notification)
    }

    fun makeNotification(updateInterface: NotificationUpdateInterface): Notification {
        var firstArt: Bitmap? = getCurrentSong()?.metadataCompat?.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART);
        if (firstArt == null) firstArt = (MusicCoreOptions.getDefaultArt() as BitmapDrawable).bitmap
        else updateInterface.updateNotification(PlaybackRemote.makeNotification(firstArt))
        //Fetch the album, then fetch the art, and then finally pass this all to create the notification. Meanwhile, this will use
        //the default art as a placeholder until the new art is fetched.
        //TODO: Use DataLinkers to get the album art (to create the notification)
    }

    internal fun makeNotification(albumArt: Bitmap): Notification {
        if (notificationBuilder != null) notificationBuilder = notificationCreator.createNotification(context!!, getMediaSession(),
                MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.PLAY),
                MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.PAUSE),
                MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.SKIP_FORWARD),
                MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.SKIP_BACKWARD),
                MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.NOTIFICATION),
                MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.STOP));
        notificationCreator.populate(getCurrentSong()!!, notificationBuilder!!)
        notificationCreator.loadArt(albumArt, notificationBuilder!!)
        return notificationCreator.buildNotif(notificationBuilder!!)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Other
    ///////////////////////////////////////////////////////////////////////////

    fun initGoogleCast(item: MenuItem) {
        service!!.initGoogleCast(item, MusicPlaybackOptions.castApplicationId)
    }

    fun getMediaSession() = service?.getMediaSession()

    fun isReady() = isBound && REMOTE_CALLBACK != null

    fun isPlaying() = service?.playback.isPlaying()

}