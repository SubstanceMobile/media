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

package org.jaudiotagger.audio.mp4.atom;

import org.jaudiotagger.audio.exceptions.CannotReadException;

import java.nio.ByteBuffer;

/**
 * DrmsBox Replaces mp4a box on drm files
 * <p>
 * Need to skip over data in order to find esds atom
 * <p>
 * Specification not known, so just look for byte by byte 'esds' and then step back four bytes for size
 */
public class Mp4DrmsBox extends AbstractMp4Box {
    /**
     * @param header     header info
     * @param dataBuffer data of box (doesnt include header data)
     */
    public Mp4DrmsBox(Mp4BoxHeader header, ByteBuffer dataBuffer) {
        this.header = header;
        this.dataBuffer = dataBuffer;
    }

    /**
     * Process direct data
     *
     * @throws CannotReadException
     */
    public void processData() throws CannotReadException {
        while (dataBuffer.hasRemaining()) {
            byte next = dataBuffer.get();
            if (next != (byte) 'e') {
                continue;
            }

            //Have we found esds identifier, if so adjust buffer to start of esds atom
            ByteBuffer tempBuffer = dataBuffer.slice();
            if ((tempBuffer.get() == (byte) 's') & (tempBuffer.get() == (byte) 'd') & (tempBuffer.get() == (byte) 's')) {
                dataBuffer.position(dataBuffer.position() - 1 - Mp4BoxHeader.OFFSET_LENGTH);
                return;
            }
        }
    }
}
