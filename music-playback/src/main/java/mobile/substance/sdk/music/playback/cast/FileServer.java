package mobile.substance.sdk.music.playback.cast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import fi.iki.elonen.NanoHTTPD;
import mobile.substance.sdk.music.loading.Library;
import mobile.substance.sdk.music.playback.MusicQueue;

/**
 * Created by Julian Os on 13.02.2016.
 */
public class FileServer extends NanoHTTPD {
    public static final String TAG = FileServer.class.getSimpleName();
    public static final String CONTENT_RANGE_BYTES = "bytes";
    int type;

    public FileServer(int port, int type) {
        super(port);
        this.type = type;
    }

    @Override
    public Response serve(IHTTPSession mSession) {
        try {
            switch (type) {
                case 1:
                    return new Response(Response.Status.OK, "audio/*", new FileInputStream(new File(MusicQueue.INSTANCE.getCurrentSong().getFilePath())));
                case 2:
                    return new Response(Response.Status.OK, "image/*", new FileInputStream(new File(Library.findAlbumById(MusicQueue.INSTANCE.getCurrentSong().getSongAlbumID()).getAlbumArtworkPath())));
                default:
                    return null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
