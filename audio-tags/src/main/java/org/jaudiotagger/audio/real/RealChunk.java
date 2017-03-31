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

package org.jaudiotagger.audio.real;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.generic.Utils;

public class RealChunk {

    protected static final String RMF = ".RMF";
    protected static final String PROP = "PROP";
    protected static final String MDPR = "MDPR";
    protected static final String CONT = "CONT";
    protected static final String DATA = "DATA";
    protected static final String INDX = "INDX";

    private final String id;
    private final int size;
    private final byte[] bytes;

    public static RealChunk readChunk(RandomAccessFile raf)
            throws CannotReadException, IOException {
        final String id = Utils.readString(raf, 4);
        final int size = Utils.readUint32AsInt(raf);
        if (size < 8) {
            throw new CannotReadException(
                    "Corrupt file: RealAudio chunk length at position "
                            + (raf.getFilePointer() - 4)
                            + " cannot be less than 8");
        }
        if (size > (raf.length() - raf.getFilePointer() + 8)) {
            throw new CannotReadException(
                    "Corrupt file: RealAudio chunk length of " + size
                            + " at position " + (raf.getFilePointer() - 4)
                            + " extends beyond the end of the file");
        }
        final byte[] bytes = new byte[size - 8];
        raf.readFully(bytes);
        return new RealChunk(id, size, bytes);
    }

    public RealChunk(String id, int size, byte[] bytes) {
        super();
        this.id = id;
        this.size = size;
        this.bytes = bytes;
    }

    public DataInputStream getDataInputStream() {
        return new DataInputStream(new ByteArrayInputStream(getBytes()));
    }

    public boolean isCONT() {
        return CONT.equals(id);
    }

    public boolean isPROP() {
        return PROP.equals(id);
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return id + "\t" + size;
    }
}