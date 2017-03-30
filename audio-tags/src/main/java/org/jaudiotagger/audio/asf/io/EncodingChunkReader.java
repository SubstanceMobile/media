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
package org.jaudiotagger.audio.asf.io;

import org.jaudiotagger.audio.asf.data.Chunk;
import org.jaudiotagger.audio.asf.data.EncodingChunk;
import org.jaudiotagger.audio.asf.data.GUID;
import org.jaudiotagger.audio.asf.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * This class reads the chunk containing encoding data <br>
 * <b>Warning:<b><br>
 * Implementation is not completed. More analysis of this chunk is needed.
 *
 * @author Christian Laireiter
 */
class EncodingChunkReader implements ChunkReader {
    /**
     * The GUID this reader {@linkplain #getApplyingIds() applies to}
     */
    private final static GUID[] APPLYING = {GUID.GUID_ENCODING};

    /**
     * Should not be used for now.
     */
    protected EncodingChunkReader() {
        // NOTHING toDo
    }

    /**
     * {@inheritDoc}
     */
    public boolean canFail() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public GUID[] getApplyingIds() {
        return APPLYING.clone();
    }

    /**
     * {@inheritDoc}
     */
    public Chunk read(final GUID guid, final InputStream stream,
                      final long chunkStart) throws IOException {
        final BigInteger chunkLen = Utils.readBig64(stream);
        final EncodingChunk result = new EncodingChunk(chunkLen);
        int readBytes = 24;
        // Can't be interpreted
        /*
         * What do I think of this data, well it seems to be another GUID. Then
         * followed by a UINT16 indicating a length of data following (by half).
         * My test files just had the length of one and a two bytes zero.
         */
        stream.skip(20);
        readBytes += 20;

        /*
         * Read the number of strings which will follow
         */
        final int stringCount = Utils.readUINT16(stream);
        readBytes += 2;

        /*
         * Now reading the specified amount of strings.
         */
        for (int i = 0; i < stringCount; i++) {
            final String curr = Utils.readCharacterSizedString(stream);
            result.addString(curr);
            readBytes += 4 + 2 * curr.length();
        }
        stream.skip(chunkLen.longValue() - readBytes);
        result.setPosition(chunkStart);
        return result;
    }

}