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

import org.jaudiotagger.audio.asf.data.GUID;
import org.jaudiotagger.audio.asf.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A chunk modifier which works with information provided by
 * {@link WriteableChunk} objects.<br>
 *
 * @author Christian Laireiter
 */
public class WriteableChunkModifer implements ChunkModifier {

    /**
     * The chunk to write.
     */
    private final WriteableChunk writableChunk;

    /**
     * Creates an instance.<br>
     *
     * @param chunk chunk to write
     */
    public WriteableChunkModifer(final WriteableChunk chunk) {
        this.writableChunk = chunk;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isApplicable(final GUID guid) {
        return guid.equals(this.writableChunk.getGuid());
    }

    /**
     * {@inheritDoc}
     */
    public ModificationResult modify(final GUID guid, final InputStream chunk,
                                     OutputStream destination) throws IOException { // NOPMD by Christian Laireiter on 5/9/09 5:03 PM
        int chunkDiff = 0;
        long newSize = 0;
        long oldSize = 0;
        /*
         * Replace the outputstream with the counting one, only if assert's are
         * evaluated.
         */
        assert (destination = new CountingOutputstream(destination)) != null;
        if (!this.writableChunk.isEmpty()) {
            newSize = this.writableChunk.writeInto(destination);
            assert newSize == this.writableChunk.getCurrentAsfChunkSize();
            /*
             * If assert's are evaluated, we have replaced destination by a
             * CountingOutpustream and can now verify if
             * getCurrentAsfChunkSize() really works correctly.
             */
            assert ((CountingOutputstream) destination).getCount() == newSize;
            if (guid == null) {
                chunkDiff++;
            }

        }
        if (guid != null) {
            assert isApplicable(guid);
            if (this.writableChunk.isEmpty()) {
                chunkDiff--;
            }
            oldSize = Utils.readUINT64(chunk);
            chunk.skip(oldSize - 24);
        }
        return new ModificationResult(chunkDiff, (newSize - oldSize), guid);
    }

}
