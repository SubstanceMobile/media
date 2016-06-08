package mobile.substance.sdk.music.core.libraryHooks

import mobile.substance.sdk.music.core.objects.Song

/**
 * This class is used only as a data source for the playback library
 */
object PlaybackLibHook {
    var songList: List<Song>? = null

    fun findSongById(id: Long): Song? {
        if (songList == null) return null
        if (songList!!.isEmpty()) return null
        for (song in songList!!) if (song.id == id) return song
        return null
    }



}
