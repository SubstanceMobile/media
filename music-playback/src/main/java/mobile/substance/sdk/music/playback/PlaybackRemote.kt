package mobile.substance.sdk.music.playback

import android.app.Notification
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.MenuItem
import mobile.substance.sdk.music.core.MusicOptions
import mobile.substance.sdk.music.core.objects.Song
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by Julian Os on 07.05.2016.
 */
object PlaybackRemote : ServiceConnection {

    override fun onServiceDisconnected(name: ComponentName?) {
        SERVICE = null
        isBound!!.set(false)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        SERVICE = (service as MusicService.ServiceBinder).service
        SERVICE!!.registerCallback(REMOTE_CALLBACK)
        isBound!!.set(true)
    }

    private var CONTEXT: Context? = null
    private var REMOTE_CALLBACK: RemoteCallback? = null
    private var NOTIFICATION_COLOR_CALLBACK: NotificationCallback? = null
    private var SERVICE: MusicService? = null
    private var isBound: AtomicBoolean? = AtomicBoolean(false)

    fun registerActivity(context: Context, callback: RemoteCallback?) {
        this.CONTEXT = context
        this.REMOTE_CALLBACK = callback
        if (isBound!!.get()) {
            isBound!!.set(false)
            SERVICE = null
        }
        if (!MusicUtil.isServiceRunning(context)) context.startService(Intent(context, MusicService::class.java))
        context.bindService(Intent(context, MusicService::class.java), this, Context.BIND_ADJUST_WITH_ACTIVITY)
    }

    fun unregisterActivity() {
        if (isBound!!.get()) CONTEXT!!.unbindService(this)
    }

    fun resume() {
        SERVICE!!.resume()
    }

    fun pause() {
        SERVICE!!.pause()
    }

    fun registerCallback(callback: RemoteCallback) {
        SERVICE!!.registerCallback(callback)
    }

    fun unregisterCallback(callback: RemoteCallback) {
        SERVICE!!.unregisterCallback(callback)
    }

    fun getNotificationCallable(song: Song): Callable<Notification> {
        return NOTIFICATION_COLOR_CALLBACK!!.getNotification(song)
    }

    fun setNotificationCallback(callback: NotificationCallback) {
        NOTIFICATION_COLOR_CALLBACK = callback
    }

    internal fun play() {
        SERVICE!!.play()
    }

    fun play(songs: MutableList<Song>, position: Int) {
        var play = true
        if (MusicQueue.getCurrentSong() != null)
            play = songs.first().id != MusicQueue.getCurrentSong()?.id
        Log.d(PlaybackRemote::class.java.simpleName, "play(${songs.size}, $position)")
        MusicQueue.set(songs, position)
        if (play)
            play()
    }

    fun play(song: Song) {
        var songs: MutableList<Song> = ArrayList<Song>()
        songs.add(song)
        play(songs, 0)
    }

    fun skipForward() {
        MusicQueue.moveForward(1)
        play()
    }

    fun skipBackward() {
        MusicQueue.moveBackward(1)
        play()
    }

    fun getMediaSession(): MediaSessionCompat {
        return SERVICE!!.session
    }

    fun seekTo(progress: Int) {
        SERVICE!!.seekTo(progress)
    }

    fun shuffle() {
        MusicQueue.set(MusicQueue.getMutableQueue(true)!!, 0)
    }

    fun requestUpdate() {
        SERVICE!!.requestUpdate()
    }

    fun isReady(): Boolean {
        return isBound!!.get() && REMOTE_CALLBACK != null
    }

    fun isPlaying(): Boolean {
        return SERVICE!!.isPlaying
    }

    fun initGoogleCast(item: MenuItem) {
        SERVICE!!.initGoogleCast(item, MusicOptions.castApplicationId)
    }

    fun getPendingIntent(action: String): PendingIntent {
        return PendingIntent.getService(CONTEXT!!, MusicService.UNIQUE_ID, Intent(CONTEXT!!, MusicService::class.java).setAction(action), PendingIntent.FLAG_CANCEL_CURRENT)
    }

    interface RemoteCallback {

        fun onReceivedIntent(intent: Intent) {
            when {
                intent.action.endsWith(".RESUME") -> resume()
                intent.action.endsWith(".PAUSE") -> pause()
                intent.action.endsWith(".skip.FORWARD") -> skipForward()
                intent.action.endsWith(".skip.BACKWARD") -> skipBackward()
                intent.action.endsWith(".NOTIFICATION") -> CONTEXT!!.startActivity(CONTEXT!!.packageManager.getLaunchIntentForPackage(CONTEXT!!.applicationContext.packageName))
                intent.action.endsWith(".SEEK") -> seekTo(intent.getIntExtra("progress", 0))
            }
        }

        fun onProgressChanged(progress: Int)

        fun onDurationChanged(duration: Int)

        fun onSongChanged(song: Song)

        fun onStateChanged(state: PlaybackState, isRepeating: Boolean)

        fun onQueueChanged(queue: List<Song>)

    }

    interface NotificationCallback {

        fun getNotification(song: Song): Callable<Notification> = Callable<Notification> {
            MusicNotification.create(CONTEXT!!, isPlaying(), getMediaSession())
        }

    }

}
