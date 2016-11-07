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

import org.jaudiotagger.audio.generic.Utils;

public class ApplicationChunk extends Chunk {

    //  private AiffTag aiffTag;
    private AiffAudioHeader aiffHeader;

    /**
     * Constructor.
     *
     * @param hdr The header for this chunk
     * @param raf The file from which the AIFF data are being read
     */
    public ApplicationChunk(
            ChunkHeader hdr,
            RandomAccessFile raf,
            AiffAudioHeader aHdr) {
        super(raf, hdr);
        aiffHeader = aHdr;
    }

    /**
     * Reads a chunk and puts an Application property into
     * the RepInfo object.
     *
     * @return <code>false</code> if the chunk is structurally
     * invalid, otherwise <code>true</code>
     */
    public boolean readChunk() throws IOException {
        String applicationSignature = Utils.readString(raf, 4);
        String applicationName = null;
        byte[] data = new byte[(int) (bytesLeft - 4)];
        raf.readFully(data);
        // If the application signature is 'pdos' or 'stoc',
        // then the beginning of the data area is a Pascal
        // string naming the application.  Otherwise, we
        // ignore the data.  ('pdos' is for Apple II
        // applications, 'stoc' for the entire non-Apple world.)
        if ("stoc".equals(applicationSignature) ||
                "pdos".equals(applicationSignature)) {
            applicationName = AiffUtil.bytesToPascalString(data);
        }
        aiffHeader.addApplicationIdentifier
                (applicationSignature + ": " + applicationName);

        return true;
    }
}
