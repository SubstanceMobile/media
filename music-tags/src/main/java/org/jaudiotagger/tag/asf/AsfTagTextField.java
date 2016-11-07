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

import org.jaudiotagger.audio.asf.data.AsfHeader;
import org.jaudiotagger.audio.asf.data.MetadataDescriptor;
import org.jaudiotagger.audio.asf.util.Utils;
import org.jaudiotagger.tag.TagTextField;
import org.jaudiotagger.tag.asf.AsfFieldKey;
import org.jaudiotagger.tag.asf.AsfTagField;

/**
 * Represents a tag text field for ASF fields.<br>
 *
 * @author Christian Laireiter
 */
public class AsfTagTextField extends AsfTagField implements TagTextField {

    /**
     * Creates a tag text field and assigns the string value.
     *
     * @param field ASF field to represent.
     * @param value the value to assign.
     */
    public AsfTagTextField(final AsfFieldKey field, final String value) {
        super(field);
        toWrap.setString(value);
    }

    /**
     * Creates an instance.
     *
     * @param source The metadata descriptor, whose content is published.<br>
     *               Must not be of type {@link MetadataDescriptor#TYPE_BINARY}.
     */
    public AsfTagTextField(final MetadataDescriptor source) {
        super(source);
        if (source.getType() == MetadataDescriptor.TYPE_BINARY) {
            throw new IllegalArgumentException(
                    "Cannot interpret binary as string.");
        }
    }

    /**
     * Creates a tag text field and assigns the string value.
     *
     * @param fieldKey The fields identifier.
     * @param value    the value to assign.
     */
    public AsfTagTextField(final String fieldKey, final String value) {
        super(fieldKey);
        toWrap.setString(value);
    }

    /**
     * {@inheritDoc}
     */
    public String getContent() {
        return getDescriptor().getString();
    }

    /**
     * {@inheritDoc}
     */
    public String getEncoding() {
        return AsfHeader.ASF_CHARSET.name();
    }

    /**
     * @return true if blank or only contains whitespace
     */
    @Override
    public boolean isEmpty() {
        return Utils.isBlank(getContent());
    }

    /**
     * {@inheritDoc}
     */
    public void setContent(final String content) {
        getDescriptor().setString(content);
    }

    /**
     * {@inheritDoc}
     */
    public void setEncoding(final String encoding) {
        if (!AsfHeader.ASF_CHARSET.name().equals(encoding)) {
            throw new IllegalArgumentException(
                    "Only UTF-16LE is possible with ASF.");
        }
    }
}
