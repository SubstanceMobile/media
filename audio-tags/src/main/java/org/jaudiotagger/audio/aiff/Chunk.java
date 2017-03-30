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

package org.jaudiotagger.audio.aiff;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * Abstract superclass for IFF/AIFF chunks.
 *
 * @author Gary McGath
 */
public abstract class Chunk {

    protected long bytesLeft;
    protected RandomAccessFile raf;

    /**
     * Constructor.
     *
     * @param hdr The header for this chunk
     */
    public Chunk(RandomAccessFile raf, ChunkHeader hdr) {
        this.raf = raf;
        bytesLeft = hdr.getSize();
    }


    /**
     * Reads a chunk and puts appropriate information into
     * the RepInfo object.
     *
     * @return <code>false</code> if the chunk is structurally
     * invalid, otherwise <code>true</code>
     */
    public abstract boolean readChunk() throws IOException;

    /**
     * Convert a byte buffer cleanly to an ASCII string.
     * This is used for fixed-allocation strings in Broadcast
     * WAVE chunks, and might have uses elsewhere.
     * If a string is shorter than its fixed allocation, we're
     * guaranteed only that there is a null terminating the string,
     * and noise could follow it.  So we can't use the byte buffer
     * constructor for a string.
     */
    protected String byteBufString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length);
        for (int i = 0; i < b.length; i++) {
            byte c = b[i];
            if (c == 0) {
                // Terminate when we see a null
                break;
            }
            sb.append((char) c);
        }
        return sb.toString();
    }
}
