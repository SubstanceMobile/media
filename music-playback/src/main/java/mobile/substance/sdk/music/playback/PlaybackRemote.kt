package mobile.substance.sdk.music.playback

import android.content.*
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.MenuItem
import mobile.substance.sdk.music.core.objects.Song
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by Julian Os on 07.05.2016.
 */
object PlaybackRemote : ServiceConnection, BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val packageName = context!!.applicationContext.packageName
        if (!intent!!.action.startsWith(packageName))
            return
        when {
            intent.action.endsWith(".RESUME") -> resume()
            intent.action.endsWith(".PAUSE") -> pause()
            intent.action.endsWith(".skip.FORWARD") -> skipForward()
            intent.action.endsWith(".skip.BACKWARD") -> skipBackward()
            intent.action.endsWith(".NOTIFICATION") -> context.startActivity(context.packageManager.getLaunchIntentForPackage(context.applicationContext.packageName))
            intent.action.endsWith(".SEEK") -> seekTo(intent.getIntExtra("progress", 0))
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        SERVICE = null
        isBound!!.set(false)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        SERVICE = (service as MusicService.ServiceBinder).service
        SERVICE!!.registerCallback(CALLBACK)
        isBound!!.set(true)
    }

    private var CONTEXT: Context? = null
    private var CALLBACK: RemoteCallback? = null
    private var SERVICE: MusicService? = null
    private var isBound: AtomicBoolean? = AtomicBoolean(false)
    private var BROADCAST_MANAGER: LocalBroadcastManager? = null

    fun registerActivity(context: Context, callback: RemoteCallback) {
        this.CONTEXT = context
        this.CALLBACK = callback
        if (isBound!!.get()) {
            isBound!!.set(false)
            SERVICE = null
        }
        if (!MusicUtil.isServiceRunning(context)) context.startService(Intent(context, MusicService::class.java))
        context.bindService(Intent(context, MusicService::class.java), this, Context.BIND_ADJUST_WITH_ACTIVITY)
        initReceiver()
    }

    private fun initReceiver() {
        BROADCAST_MANAGER = LocalBroadcastManager.getInstance(CONTEXT!!)
        BROADCAST_MANAGER!!.registerReceiver(this, IntentFilter(CONTEXT!!.applicationContext.packageName))
    }

    private fun deinitReceiver() {
        BROADCAST_MANAGER!!.unregisterReceiver(this)
    }

    fun unregisterActivity() {
        if (isBound!!.get()) CONTEXT!!.unbindService(this)
        try {
            BROADCAST_MANAGER!!.unregisterReceiver(this)
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    fun resume() {
        SERVICE!!.resume()
    }

    fun pause() {
        SERVICE!!.pause()
    }

    fun registerCallback(callback: RemoteCallback) {
        SERVICE!!.unregisterCallback(callback)
    }

    fun unregisterCallback(callback: RemoteCallback) {
        SERVICE!!.unregisterCallback(callback)
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

    fun seekTo(progress: Int) {
        SERVICE!!.seekTo(progress)
    }

    fun shuffle() {
        MusicQueue.set(MusicQueue.getMutableQueue(true)!!, 0)
    }

    fun initCast(item: MenuItem, applicationId: String) {
        SERVICE!!.initGoogleCast(item, applicationId)
    }

    fun requestUpdate() {
        SERVICE!!.requestUpdate()
    }

    fun isReady(): Boolean {
        return isBound!!.get() && CALLBACK != null
    }

    interface RemoteCallback {

        fun onProgressChanged(progress: Int)

        fun onDurationChanged(duration: Int)

        fun onSongChanged(song: Song)

        fun onStateChanged(state: Int, isRepeating: Boolean)

        fun onQueueChanged(queue: List<Song>)

    }

}
