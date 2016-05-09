package mobile.substance.sdk.music.playback

import android.util.Log
import mobile.substance.sdk.music.core.objects.Song
import java.util.*

/**
 * Created by Julian Os on 07.05.2016.
 */
object MusicQueue {
    private var POSITION: Int = 0
    @Volatile private var QUEUE: MutableList<Song>? = ArrayList<Song>()

    fun getQueue(startAtPosition: Boolean): List<Song> {
        if (QUEUE != null) {
            if (startAtPosition) {
                return QUEUE!!.subList(POSITION + 1, QUEUE!!.lastIndex)
            } else return QUEUE!!
        } else return emptyList()
    }

    internal fun getMutableQueue(startAtPosition: Boolean): MutableList<Song>? {
        if (QUEUE != null) {
            if (startAtPosition) {
                return QUEUE!!.subList(POSITION + 1, QUEUE!!.lastIndex)
            } else return QUEUE!!
        } else return null
    }

    fun getCurrentSong(): Song? {
        Log.d(MusicQueue::class.java.simpleName, "getCurrentSong(), Index is $POSITION")
        if (QUEUE != null && QUEUE!!.size > 0) return QUEUE!!.get(POSITION)
        return null
    }

    internal fun moveForward(by: Int) {
        Log.d(MusicQueue::class.java.simpleName, "moveForward($by)")
        POSITION += by
        if (POSITION >= QUEUE!!.size) POSITION = 0
    }

    internal fun moveBackward(by: Int) {
        POSITION -= by
        if (POSITION < 0) POSITION = QUEUE!!.lastIndex
    }

    internal fun set(songs: MutableList<Song>, position: Int) {
        Log.d(MusicQueue::class.java.simpleName, "set(${songs.size}), $position")
        QUEUE = songs
        POSITION = position
    }

}