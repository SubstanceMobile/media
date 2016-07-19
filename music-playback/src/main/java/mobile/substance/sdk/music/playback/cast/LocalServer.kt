package mobile.substance.sdk.music.playback.cast

import android.content.Context
import android.net.Uri
import fi.iki.elonen.NanoHTTPD
import mobile.substance.sdk.music.core.utils.MusicCoreUtil
import mobile.substance.sdk.music.playback.MusicPlaybackUtil
import java.io.File
import java.io.FileInputStream

//TODO: Julian explain this
class LocalServer(private val type: Int) : NanoHTTPD(MusicPlaybackUtil.getServerPortForType(type)) {

    private var path: String? = null

    fun setUri(context: Context, uri: Uri) {
        path = MusicCoreUtil.getFilePath(context, uri)
    }

    override fun serve(session: IHTTPSession?): Response {
        val inputStream = FileInputStream(File(path))
        return newChunkedResponse(Response.Status.OK, if (type == MusicPlaybackUtil.SERVER_TYPE_AUDIO) "audio/*" else "image/*", inputStream)
    }

}