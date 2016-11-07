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
import java.util.List;

/**
 * This class represents the "Stream Bitrate Properties" chunk of an ASF media
 * file. <br>
 * It is optional, but contains useful information about the streams bitrate.<br>
 *
 * @author Christian Laireiter
 */
public class StreamBitratePropertiesChunk extends Chunk {

    /**
     * For each call of {@link #addBitrateRecord(int, long)} an {@link Long}
     * object is appended, which represents the average bitrate.
     */
    private final List<Long> bitRates;

    /**
     * For each call of {@link #addBitrateRecord(int, long)} an {@link Integer}
     * object is appended, which represents the stream-number.
     */
    private final List<Integer> streamNumbers;

    /**
     * Creates an instance.
     *
     * @param chunkLen Length of current chunk.
     */
    public StreamBitratePropertiesChunk(final BigInteger chunkLen) {
        super(GUID.GUID_STREAM_BITRATE_PROPERTIES, chunkLen);
        this.bitRates = new ArrayList<Long>();
        this.streamNumbers = new ArrayList<Integer>();
    }

    /**
     * Adds the public values of a stream-record.
     *
     * @param streamNum      The number of the referred stream.
     * @param averageBitrate Its average bitrate.
     */
    public void addBitrateRecord(final int streamNum, final long averageBitrate) {
        this.streamNumbers.add(streamNum);
        this.bitRates.add(averageBitrate);
    }

    /**
     * Returns the average bitrate of the given stream.<br>
     *
     * @param streamNumber Number of the stream whose bitrate to determine.
     * @return The average bitrate of the numbered stream. <code>-1</code> if no
     * information was given.
     */
    public long getAvgBitrate(final int streamNumber) {
        final Integer seach = streamNumber;
        final int index = this.streamNumbers.indexOf(seach);
        long result;
        if (index == -1) {
            result = -1;
        } else {
            result = this.bitRates.get(index);
        }
        return result;
    }

    /**
     * (overridden)
     *
     * @see org.jaudiotagger.audio.asf.data.Chunk#prettyPrint(String)
     */
    @Override
    public String prettyPrint(final String prefix) {
        final StringBuilder result = new StringBuilder(super.prettyPrint(prefix));
        for (int i = 0; i < this.bitRates.size(); i++) {
            result.append(prefix).append("  |-> Stream no. \"").append(
                    this.streamNumbers.get(i)).append(
                    "\" has an average bitrate of \"").append(
                    this.bitRates.get(i)).append('"').append(
                    Utils.LINE_SEPARATOR);
        }
        return result.toString();
    }

}
