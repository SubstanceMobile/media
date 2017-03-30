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
import org.jaudiotagger.audio.asf.data.FileHeader;
import org.jaudiotagger.audio.asf.data.GUID;
import org.jaudiotagger.audio.asf.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * Reads and interprets the data of the file header. <br>
 *
 * @author Christian Laireiter
 */
public class FileHeaderReader implements ChunkReader {

    /**
     * The GUID this reader {@linkplain #getApplyingIds() applies to}
     */
    private final static GUID[] APPLYING = {GUID.GUID_FILE};

    /**
     * Should not be used for now.
     */
    protected FileHeaderReader() {
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
        // Skip client GUID.
        stream.skip(16);
        final BigInteger fileSize = Utils.readBig64(stream);
        // fileTime in 100 ns since midnight of 1st january 1601 GMT
        final BigInteger fileTime = Utils.readBig64(stream);

        final BigInteger packageCount = Utils.readBig64(stream);

        final BigInteger timeEndPos = Utils.readBig64(stream);
        final BigInteger duration = Utils.readBig64(stream);
        final BigInteger timeStartPos = Utils.readBig64(stream);

        final long flags = Utils.readUINT32(stream);

        final long minPkgSize = Utils.readUINT32(stream);
        final long maxPkgSize = Utils.readUINT32(stream);
        final long uncompressedFrameSize = Utils.readUINT32(stream);

        final FileHeader result = new FileHeader(chunkLen, fileSize, fileTime,
                packageCount, duration, timeStartPos, timeEndPos, flags,
                minPkgSize, maxPkgSize, uncompressedFrameSize);
        result.setPosition(chunkStart);
        return result;
    }

}