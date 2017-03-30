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

import java.io.IOException;
import java.io.RandomAccessFile;

public class ChunkHeader {

    private long _size;              // This does not include the 8 bytes of header
    private String _chunkID;         // 4-character ID of the chunk

    /**
     * Constructor.
     */
    public ChunkHeader() {
    }


    /**
     * Reads the header of a chunk.  If _chunkID is non-null,
     * it's assumed to have already been read.
     */
    public boolean readHeader(RandomAccessFile raf) throws IOException {
        StringBuffer id = new StringBuffer(4);
        for (int i = 0; i < 4; i++) {
            int ch = raf.read();
            if (ch < 32) {
                String hx = Integer.toHexString(ch);
                if (hx.length() < 2) {
                    hx = "0" + hx;
                }
                return false;
            }
            id.append((char) ch);
        }
        _chunkID = id.toString();
        _size = AiffUtil.readUINT32(raf);
        return true;
    }


    /**
     * Sets the chunk type, which is a 4-character code, directly.
     */
    public void setID(String id) {
        _chunkID = id;
    }

    /**
     * Returns the chunk type, which is a 4-character code
     */
    public String getID() {
        return _chunkID;
    }

    /**
     * Returns the chunk size (excluding the first 8 bytes)
     */
    public long getSize() {
        return _size;
    }
}
