package mobile.substance.media.music.playback

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import mobile.substance.media.music.playback.players.GaplessPlayback
import mobile.substance.media.music.playback.players.Playback
import mobile.substance.media.utils.MusicCoreUtil

fun MediaPlayer.prepareWithDataSource(context: Context, dataSource: Uri) {
    try {
        val url = dataSource.toString()
        Log.d("Checking url validity", url)
        if (!MusicCoreUtil.isHttpUrl(url)) setDataSource(GaplessPlayback.SERVICE!!.applicationContext, dataSource) else setDataSource(url)
    } catch (e: Exception) {
        Log.e(Playback.TAG, "Unable to play " + MusicCoreUtil.getFilePath(GaplessPlayback.SERVICE!!, dataSource), e)
    } finally {
        prepareAsync()
    }
}

fun MediaPlayer.destroy() {
    try {
        stop()
        reset()
        release()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun MediaSessionCompat.destroy() {
    isActive = false
    release()
}
