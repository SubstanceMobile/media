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
import org.jaudiotagger.audio.asf.data.GUID;
import org.jaudiotagger.audio.asf.data.StreamBitratePropertiesChunk;
import org.jaudiotagger.audio.asf.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * This class reads the chunk containing the stream bitrate properties.<br>
 *
 * @author Christian Laireiter
 */
public class StreamBitratePropertiesReader implements ChunkReader {

    /**
     * The GUID this reader {@linkplain #getApplyingIds() applies to}
     */
    private final static GUID[] APPLYING = {GUID.GUID_STREAM_BITRATE_PROPERTIES};

    /**
     * Should not be used for now.
     */
    protected StreamBitratePropertiesReader() {
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
        final StreamBitratePropertiesChunk result = new StreamBitratePropertiesChunk(
                chunkLen);

        /*
         * Read the amount of bitrate records
         */
        final long recordCount = Utils.readUINT16(stream);
        for (int i = 0; i < recordCount; i++) {
            final int flags = Utils.readUINT16(stream);
            final long avgBitrate = Utils.readUINT32(stream);
            result.addBitrateRecord(flags & 0x00FF, avgBitrate);
        }

        result.setPosition(chunkStart);

        return result;
    }

}