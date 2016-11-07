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

import org.jaudiotagger.audio.asf.data.MetadataDescriptor;
import org.jaudiotagger.tag.TagField;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * An <code>AbstractAsfTagImageField</code> is an abstract class for representing tag
 * fields containing image data.<br>
 *
 * @author Christian Laireiter
 */
abstract class AbstractAsfTagImageField extends AsfTagField {

    /**
     * Creates a image tag field.
     *
     * @param field the ASF field that should be represented.
     */
    public AbstractAsfTagImageField(final AsfFieldKey field) {
        super(field);
    }

    /**
     * Creates an instance.
     *
     * @param source The descriptor which should be represented as a
     *               {@link TagField}.
     */
    public AbstractAsfTagImageField(final MetadataDescriptor source) {
        super(source);
    }

    /**
     * Creates a tag field.
     *
     * @param fieldKey The field identifier to use.
     */
    public AbstractAsfTagImageField(final String fieldKey) {
        super(fieldKey);
    }

    /**
     * Returns the size of the {@linkplain #getRawImageData() image data}.<br>
     *
     * @return image data size in bytes.
     */
    public abstract int getImageDataSize();

    /**
     * Returns the raw data of the represented image.<br>
     *
     * @return raw image data
     */
    public abstract byte[] getRawImageData();

}
