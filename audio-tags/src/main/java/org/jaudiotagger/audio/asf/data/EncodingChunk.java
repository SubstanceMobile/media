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
package org.jaudiotagger.audio.asf.data;

import org.jaudiotagger.audio.asf.util.Utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class was intended to store the data of a chunk which contained the
 * encoding parameters in textual form. <br>
 * Since the needed parameters were found in other chunks the implementation of
 * this class was paused. <br>
 * TODO complete analysis.
 *
 * @author Christian Laireiter
 */
public class EncodingChunk extends Chunk {

    /**
     * The read strings.
     */
    private final List<String> strings;

    /**
     * Creates an instance.
     *
     * @param chunkLen Length of current chunk.
     */
    public EncodingChunk(final BigInteger chunkLen) {
        super(GUID.GUID_ENCODING, chunkLen);
        this.strings = new ArrayList<String>();
    }

    /**
     * This method appends a String.
     *
     * @param toAdd String to add.
     */
    public void addString(final String toAdd) {
        this.strings.add(toAdd);
    }

    /**
     * This method returns a collection of all {@linkplain String Strings} which
     * were added due {@link #addString(String)}.
     *
     * @return Inserted Strings.
     */
    public Collection<String> getStrings() {
        return new ArrayList<String>(this.strings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String prettyPrint(final String prefix) {
        final StringBuilder result = new StringBuilder(super
                .prettyPrint(prefix));
        this.strings.iterator();
        for (final String string : this.strings) {
            result.append(prefix).append("  | : ").append(string).append(
                    Utils.LINE_SEPARATOR);
        }
        return result.toString();
    }
}