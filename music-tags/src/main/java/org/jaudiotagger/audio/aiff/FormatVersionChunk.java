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
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.aiff.AiffTag;
import org.jaudiotagger.tag.aiff.AiffTagFieldKey;

public class FormatVersionChunk extends Chunk {

    private AiffAudioHeader aiffHeader;

    /**
     * Constructor.
     *
     * @param hdr  The header for this chunk
     * @param raf  The file from which the AIFF data are being read
     * @param aHdr The AiffTag into which information is stored
     */
    public FormatVersionChunk(
            ChunkHeader hdr,
            RandomAccessFile raf,
            AiffAudioHeader aHdr) {
        super(raf, hdr);
        aiffHeader = aHdr;
    }

    /**
     * Reads a chunk and extracts information.
     *
     * @return <code>false</code> if the chunk is structurally
     * invalid, otherwise <code>true</code>
     */
    public boolean readChunk() throws IOException {
        long rawTimestamp = Utils.readUint32(raf);
        // The timestamp is in seconds since January 1, 1904.
        // We must convert to Java time.
        Date timestamp = AiffUtil.timestampToDate(rawTimestamp);
        aiffHeader.setTimestamp(timestamp);
        return true;
    }

}
