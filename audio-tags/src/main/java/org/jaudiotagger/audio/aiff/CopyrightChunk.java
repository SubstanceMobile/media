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

public class CopyrightChunk extends TextChunk {

    private AiffAudioHeader aiffHeader;

    /**
     * Constructor.
     *
     * @param hdr  The header for this chunk
     * @param raf  The file from which the AIFF data are being read
     * @param aHdr The AiffAudioHeader into which information is stored
     */
    public CopyrightChunk(
            ChunkHeader hdr,
            RandomAccessFile raf,
            AiffAudioHeader aHdr) {
        super(hdr, raf);
        aiffHeader = aHdr;
    }

    @Override
    public boolean readChunk() throws IOException {
        if (!super.readChunk()) {
            return false;
        }
        aiffHeader.setCopyright(chunkText);
        return true;
    }

}
