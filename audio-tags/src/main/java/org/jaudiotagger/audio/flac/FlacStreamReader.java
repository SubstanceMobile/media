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

package org.jaudiotagger.audio.flac;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.logging.ErrorMessage;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v22Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

/**
 * Flac Stream
 * <p>
 * Reader files and identifies if this is in fact a flac stream
 */
public class FlacStreamReader {
    public static Logger logger = Logger.getLogger("org.jaudiotagger.audio.flac");

    public static final int FLAC_STREAM_IDENTIFIER_LENGTH = 4;
    public static final String FLAC_STREAM_IDENTIFIER = "fLaC";

    private RandomAccessFile raf;
    private int startOfFlacInFile;

    /**
     * Create instance for holding stream info
     *
     * @param raf
     */
    public FlacStreamReader(RandomAccessFile raf) {
        this.raf = raf;

    }

    /**
     * Reads the stream block to ensure it is a flac file
     *
     * @throws IOException
     * @throws CannotReadException
     */
    public void findStream() throws IOException, CannotReadException {
        //Begins tag parsing
        if (raf.length() == 0) {
            //Empty File
            throw new CannotReadException("Error: File empty");
        }
        raf.seek(0);

        //FLAC Stream at start
        if (isFlacHeader()) {
            startOfFlacInFile = 0;
            return;
        }

        //Ok maybe there is an ID3v24tag first
        if (isId3v2Tag()) {
            startOfFlacInFile = (int) (raf.getFilePointer() - FLAC_STREAM_IDENTIFIER_LENGTH);
            return;
        }
        throw new CannotReadException(ErrorMessage.FLAC_NO_FLAC_HEADER_FOUND.getMsg());
    }

    private boolean isId3v2Tag() throws IOException {
        raf.seek(0);
        if (AbstractID3v2Tag.isId3Tag(raf)) {
            logger.warning(ErrorMessage.FLAC_CONTAINS_ID3TAG.getMsg(raf.getFilePointer()));
            //FLAC Stream immediately after end of id3 tag
            if (isFlacHeader()) {
                return true;
            }
        }
        return false;
    }

    private boolean isFlacHeader() throws IOException {
        //FLAC Stream at start
        byte[] b = new byte[FLAC_STREAM_IDENTIFIER_LENGTH];
        raf.read(b);
        String flac = new String(b);
        return flac.equals(FLAC_STREAM_IDENTIFIER);
    }

    /**
     * Usually flac header is at start of file, but unofficially and ID3 tag is allowed at the start of the file.
     *
     * @return the start of the Flac within file
     */
    public int getStartOfFlacInFile() {
        return startOfFlacInFile;
    }
}
