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
