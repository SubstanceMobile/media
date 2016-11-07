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
import org.jaudiotagger.audio.asf.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * Default reader, Reads GUID and size out of an input stream and creates a
 * {@link org.jaudiotagger.audio.asf.data.Chunk}object, finally skips the
 * remaining chunk bytes.
 *
 * @author Christian Laireiter
 */
final class ChunkHeaderReader implements ChunkReader {

    /**
     * The GUID this reader {@linkplain #getApplyingIds() applies to}
     */
    private final static GUID[] APPLYING = {GUID.GUID_UNSPECIFIED};

    /**
     * Default instance.
     */
    private static final ChunkHeaderReader INSTANCE = new ChunkHeaderReader();

    /**
     * Returns an instance of the reader.
     *
     * @return instance.
     */
    public static ChunkHeaderReader getInstance() {
        return INSTANCE;
    }

    /**
     * Hidden Utility class constructor.
     */
    private ChunkHeaderReader() {
        // Hidden
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
        stream.skip(chunkLen.longValue() - 24);
        return new Chunk(guid, chunkStart, chunkLen);
    }

}