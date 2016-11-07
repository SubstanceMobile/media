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

package org.jaudiotagger.tag.asf;

import org.jaudiotagger.audio.asf.data.ContentBranding;

import org.jaudiotagger.audio.asf.data.ContainerType;
import org.jaudiotagger.audio.asf.data.MetadataDescriptor;
import org.jaudiotagger.tag.asf.AbstractAsfTagImageField;
import org.jaudiotagger.tag.asf.AsfFieldKey;

/**
 * This field represents the image content of the banner image which is stored
 * in the {@linkplain ContentBranding content branding} chunk of ASF files.<br>
 *
 * @author Christian Laireiter
 */
public class AsfTagBannerField extends AbstractAsfTagImageField {

    /**
     * Creates an instance with no image data.<br>
     */
    public AsfTagBannerField() {
        super(AsfFieldKey.BANNER_IMAGE);
    }

    /**
     * Creates an instance with given descriptor as image content.<br>
     *
     * @param descriptor image content.
     */
    public AsfTagBannerField(final MetadataDescriptor descriptor) {
        super(descriptor);
        assert descriptor.getName().equals(
                AsfFieldKey.BANNER_IMAGE.getFieldName());
    }

    /**
     * Creates an instance with specified data as image content.
     *
     * @param imageData image content.
     */
    public AsfTagBannerField(final byte[] imageData) {
        super(new MetadataDescriptor(ContainerType.CONTENT_BRANDING,
                AsfFieldKey.BANNER_IMAGE.getFieldName(),
                MetadataDescriptor.TYPE_BINARY));
        this.toWrap.setBinaryValue(imageData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getImageDataSize() {
        return this.toWrap.getRawDataSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getRawImageData() {
        return getRawContent();
    }

}
