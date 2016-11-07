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
import org.jaudiotagger.audio.asf.data.ContentBranding;
import org.jaudiotagger.audio.asf.data.GUID;
import org.jaudiotagger.audio.asf.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * This reader is used to read the content branding object of ASF streams.<br>
 *
 * @author Christian Laireiter
 * @see org.jaudiotagger.audio.asf.data.ContainerType#CONTENT_BRANDING
 * @see ContentBranding
 */
public class ContentBrandingReader implements ChunkReader {
    /**
     * The GUID this reader {@linkplain #getApplyingIds() applies to}
     */
    private final static GUID[] APPLYING = {GUID.GUID_CONTENT_BRANDING};

    /**
     * Should not be used for now.
     */
    protected ContentBrandingReader() {
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
                      final long streamPosition) throws IOException {
        assert GUID.GUID_CONTENT_BRANDING.equals(guid);
        final BigInteger chunkSize = Utils.readBig64(stream);
        final long imageType = Utils.readUINT32(stream);
        assert imageType >= 0 && imageType <= 3 : imageType;
        final long imageDataSize = Utils.readUINT32(stream);
        assert imageType > 0 || imageDataSize == 0 : imageDataSize;
        assert imageDataSize < Integer.MAX_VALUE;
        final byte[] imageData = Utils.readBinary(stream, imageDataSize);
        final long copyRightUrlLen = Utils.readUINT32(stream);
        final String copyRight = new String(Utils.readBinary(stream,
                copyRightUrlLen));
        final long imageUrlLen = Utils.readUINT32(stream);
        final String imageUrl = new String(Utils
                .readBinary(stream, imageUrlLen));
        final ContentBranding result = new ContentBranding(streamPosition,
                chunkSize);
        result.setImage(imageType, imageData);
        result.setCopyRightURL(copyRight);
        result.setBannerImageURL(imageUrl);
        return result;
    }

}
