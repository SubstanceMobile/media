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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Structure to tell the differences occurred by altering a chunk.
 *
 * @author Christian Laireiter
 */
final class ModificationResult {

    /**
     * Stores the difference of bytes.<br>
     */
    private final long byteDifference;

    /**
     * Stores the difference of the amount of chunks.<br>
     * &quot;-1&quot; if the chunk disappeared upon modification.<br>
     * &quot;0&quot; if the chunk was just modified.<br>
     * &quot;1&quot; if a chunk has been created.<br>
     */
    private final int chunkDifference;

    /**
     * Stores all GUIDs, which have been read.<br>
     */
    private final Set<GUID> occuredGUIDs = new HashSet<GUID>();

    /**
     * Creates an instance.<br>
     *
     * @param chunkCountDiff amount of chunks appeared, disappeared
     * @param bytesDiffer    amount of bytes added or removed.
     * @param occurred       all GUIDs which have been occurred, during processing
     */
    public ModificationResult(final int chunkCountDiff, final long bytesDiffer,
                              final GUID... occurred) {
        assert occurred != null && occurred.length > 0;
        this.chunkDifference = chunkCountDiff;
        this.byteDifference = bytesDiffer;
        this.occuredGUIDs.addAll(Arrays.asList(occurred));
    }

    /**
     * Creates an instance.<br>
     *
     * @param chunkCountDiff amount of chunks appeared, disappeared
     * @param bytesDiffer    amount of bytes added or removed.
     * @param occurred       all GUIDs which have been occurred, during processing
     */
    public ModificationResult(final int chunkCountDiff, final long bytesDiffer,
                              final Set<GUID> occurred) {
        this.chunkDifference = chunkCountDiff;
        this.byteDifference = bytesDiffer;
        this.occuredGUIDs.addAll(occurred);
    }

    /**
     * Returns the difference of bytes.
     *
     * @return the byte difference
     */
    public long getByteDifference() {
        return this.byteDifference;
    }

    /**
     * Returns the difference of the amount of chunks.
     *
     * @return the chunk count difference
     */
    public int getChunkCountDifference() {
        return this.chunkDifference;
    }

    /**
     * Returns all GUIDs which have been occurred during processing.
     *
     * @return see description.s
     */
    public Set<GUID> getOccuredGUIDs() {
        return new HashSet<GUID>(this.occuredGUIDs);
    }

}
