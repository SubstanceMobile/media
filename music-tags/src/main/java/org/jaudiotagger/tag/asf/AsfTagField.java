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
import org.jaudiotagger.tag.asf.AsfFieldKey;
import org.jaudiotagger.tag.asf.AsfTag;

/**
 * This class encapsulates a
 * {@link org.jaudiotagger.audio.asf.data.MetadataDescriptor}and provides access
 * to it. <br>
 * The metadata descriptor used for construction is copied.
 *
 * @author Christian Laireiter (liree)
 */
public class AsfTagField implements TagField, Cloneable {

    /**
     * This descriptor is wrapped.
     */
    protected MetadataDescriptor toWrap;

    /**
     * Creates a tag field.
     *
     * @param field the ASF field that should be represented.
     */
    public AsfTagField(final AsfFieldKey field) {
        assert field != null;
        this.toWrap = new MetadataDescriptor(field.getHighestContainer(), field
                .getFieldName(), MetadataDescriptor.TYPE_STRING);
    }

    /**
     * Creates an instance.
     *
     * @param source The descriptor which should be represented as a
     *               {@link TagField}.
     */
    public AsfTagField(final MetadataDescriptor source) {
        assert source != null;
        // XXX Copy ? maybe not really.
        this.toWrap = source.createCopy();
    }

    /**
     * Creates a tag field.
     *
     * @param fieldKey The field identifier to use.
     */
    public AsfTagField(final String fieldKey) {
        assert fieldKey != null;
        this.toWrap = new MetadataDescriptor(AsfFieldKey.getAsfFieldKey(
                fieldKey).getHighestContainer(), fieldKey,
                MetadataDescriptor.TYPE_STRING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * {@inheritDoc}
     */
    public void copyContent(final TagField field) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Returns the wrapped metadata descriptor (which actually stores the
     * values).
     *
     * @return the wrapped metadata descriptor
     */
    public MetadataDescriptor getDescriptor() {
        return this.toWrap;
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return this.toWrap.getName();
    }

    /**
     * {@inheritDoc}
     */
    public byte[] getRawContent() {
        return this.toWrap.getRawData();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBinary() {
        return this.toWrap.getType() == MetadataDescriptor.TYPE_BINARY;
    }

    /**
     * {@inheritDoc}
     */
    public void isBinary(final boolean value) {
        if (!value && isBinary()) {
            throw new UnsupportedOperationException("No conversion supported.");
        }
        this.toWrap.setBinaryValue(this.toWrap.getRawData());
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCommon() {
        // HashSet is safe against null comparison
        return AsfTag.COMMON_FIELDS.contains(AsfFieldKey
                .getAsfFieldKey(getId()));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return this.toWrap.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.toWrap.getString();
    }

}