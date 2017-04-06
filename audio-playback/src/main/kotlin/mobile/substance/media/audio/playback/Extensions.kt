package mobile.substance.media.audio.playback

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import mobile.substance.media.utils.CoreUtil.toFilePath
import mobile.substance.media.audio.playback.players.Playback
import mobile.substance.media.utils.AudioCoreUtil

fun MediaPlayer.prepareWithDataSource(context: Context, dataSource: Uri) {
    try {
        val url = dataSource.toString()
        Log.d("Checking url validity", url)
        if (!AudioCoreUtil.isHttpUrl(url)) setDataSource(context, dataSource) else setDataSource(url)
    } catch (e: Exception) {
        Log.e(Playback.TAG, "Unable to play " + dataSource.toFilePath(context), e)
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
