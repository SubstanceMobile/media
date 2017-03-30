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
import org.jaudiotagger.audio.asf.data.EncryptionChunk;
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
class EncryptionChunkReader implements ChunkReader {

    /**
     * The GUID this reader {@linkplain #getApplyingIds() applies to}
     */
    private final static GUID[] APPLYING = {GUID.GUID_CONTENT_ENCRYPTION};

    /**
     * Should not be used for now.
     */
    protected EncryptionChunkReader() {
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
        EncryptionChunk result;
        final BigInteger chunkLen = Utils.readBig64(stream);
        result = new EncryptionChunk(chunkLen);

        // Can't be interpreted
        /*
         * Object ID GUID 128 Object Size QWORD 64 Secret Data Length DWORD 32
         * Secret Data INTEGER varies Protection Type Length DWORD 32 Protection
         * Type char varies Key ID Length DWORD 32 Key ID char varies License
         * URL Length DWORD 32 License URL char varies * Read the
         */
        byte[] secretData;
        byte[] protectionType;
        byte[] keyID;
        byte[] licenseURL;

        // Secret Data length
        int fieldLength;
        fieldLength = (int) Utils.readUINT32(stream);
        // Secret Data
        secretData = new byte[fieldLength + 1];
        stream.read(secretData, 0, fieldLength);
        secretData[fieldLength] = 0;

        // Protection type Length
        fieldLength = 0;
        fieldLength = (int) Utils.readUINT32(stream);
        // Protection Data Length
        protectionType = new byte[fieldLength + 1];
        stream.read(protectionType, 0, fieldLength);
        protectionType[fieldLength] = 0;

        // Key ID length
        fieldLength = 0;
        fieldLength = (int) Utils.readUINT32(stream);
        // Key ID
        keyID = new byte[fieldLength + 1];
        stream.read(keyID, 0, fieldLength);
        keyID[fieldLength] = 0;

        // License URL length
        fieldLength = 0;
        fieldLength = (int) Utils.readUINT32(stream);
        // License URL
        licenseURL = new byte[fieldLength + 1];
        stream.read(licenseURL, 0, fieldLength);
        licenseURL[fieldLength] = 0;

        result.setSecretData(new String(secretData));
        result.setProtectionType(new String(protectionType));
        result.setKeyID(new String(keyID));
        result.setLicenseURL(new String(licenseURL));

        result.setPosition(chunkStart);

        return result;
    }

}