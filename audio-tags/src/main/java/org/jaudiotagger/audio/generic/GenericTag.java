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
package org.jaudiotagger.audio.generic;

import org.jaudiotagger.logging.ErrorMessage;
import org.jaudiotagger.tag.*;
import org.jaudiotagger.tag.images.Artwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * This is a complete example implementation of
 * {@link AbstractTag} and it currenlty used to provide basic support to audio formats with only read tagging
 * ability such as Real or Wav files <br>
 *
 * @author Raphaël Slinckx
 */
public abstract class GenericTag extends AbstractTag {
    private static EnumSet<FieldKey> supportedKeys;

    static {
        supportedKeys = EnumSet.of(FieldKey.ALBUM, FieldKey.ARTIST, FieldKey.TITLE, FieldKey.TRACK, FieldKey.GENRE, FieldKey.COMMENT, FieldKey.YEAR);
    }

    /**
     * Implementations of {@link TagTextField} for use with
     * &quot;ISO-8859-1&quot; strings.
     *
     * @author Raphaël Slinckx
     */
    private class GenericTagTextField implements TagTextField {

        /**
         * Stores the string.
         */
        private String content;

        /**
         * Stores the identifier.
         */
        private final String id;

        /**
         * Creates an instance.
         *
         * @param fieldId        The identifier.
         * @param initialContent The string.
         */
        public GenericTagTextField(String fieldId, String initialContent) {
            this.id = fieldId;
            this.content = initialContent;
        }

        /**
         * (overridden)
         *
         * @see org.jaudiotagger.tag.TagField#copyContent(org.jaudiotagger.tag.TagField)
         */
        public void copyContent(TagField field) {
            if (field instanceof TagTextField) {
                this.content = ((TagTextField) field).getContent();
            }
        }

        /**
         * (overridden)
         *
         * @see org.jaudiotagger.tag.TagTextField#getContent()
         */
        public String getContent() {
            return this.content;
        }

        /**
         * (overridden)
         *
         * @see org.jaudiotagger.tag.TagTextField#getEncoding()
         */
        public String getEncoding() {
            return "ISO-8859-1";
        }

        /**
         * (overridden)
         *
         * @see org.jaudiotagger.tag.TagField#getId()
         */
        public String getId() {
            return id;
        }

        /**
         * (overridden)
         *
         * @see org.jaudiotagger.tag.TagField#getRawContent()
         */
        public byte[] getRawContent() {
            return this.content == null ? new byte[]{} : Utils.getDefaultBytes(this.content, getEncoding());
        }

        /**
         * (overridden)
         *
         * @see org.jaudiotagger.tag.TagField#isBinary()
         */
        public boolean isBinary() {
            return false;
        }

        /**
         * (overridden)
         *
         * @see org.jaudiotagger.tag.TagField#isBinary(boolean)
         */
        public void isBinary(boolean b) {
            /* not supported */
        }

        /**
         * (overridden)
         *
         * @see org.jaudiotagger.tag.TagField#isCommon()
         */
        public boolean isCommon() {
            return true;
        }

        /**
         * (overridden)
         *
         * @see org.jaudiotagger.tag.TagField#isEmpty()
         */
        public boolean isEmpty() {
            return this.content.equals("");
        }

        /**
         * (overridden)
         *
         * @see org.jaudiotagger.tag.TagTextField#setContent(java.lang.String)
         */
        public void setContent(String s) {
            this.content = s;
        }

        /**
         * (overridden)
         *
         * @see org.jaudiotagger.tag.TagTextField#setEncoding(java.lang.String)
         */
        public void setEncoding(String s) {
            /* Not allowed */
        }

        /**
         * (overridden)
         *
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return getContent();
        }
    }

    /**
     * (overridden)
     *
     * @see org.jaudiotagger.audio.generic.AbstractTag#isAllowedEncoding(java.lang.String)
     */
    protected boolean isAllowedEncoding(String enc) {
        return true;
    }

    public TagField createField(FieldKey genericKey, String value) throws KeyNotFoundException, FieldDataInvalidException {
        if (supportedKeys.contains(genericKey)) {
            return new GenericTagTextField(genericKey.name(), value);
        } else {
            throw new UnsupportedOperationException(ErrorMessage.GENERIC_NOT_SUPPORTED.getMsg());
        }
    }

    /**
     * @param genericKey
     * @return
     * @throws KeyNotFoundException
     */
    public String getFirst(FieldKey genericKey) throws KeyNotFoundException {
        return getValue(genericKey, 0);
    }

    public String getValue(FieldKey genericKey, int index) throws KeyNotFoundException {
        if (supportedKeys.contains(genericKey)) {
            return getItem(genericKey.name(), index);
        } else {
            throw new UnsupportedOperationException(ErrorMessage.GENERIC_NOT_SUPPORTED.getMsg());
        }
    }

    /**
     * @param genericKey The field id.
     * @return
     * @throws KeyNotFoundException
     */
    public List<TagField> getFields(FieldKey genericKey) throws KeyNotFoundException {
        List<TagField> list = fields.get(genericKey.name());
        if (list == null) {
            return new ArrayList<TagField>();
        }
        return list;
    }

    public List<String> getAll(FieldKey genericKey) throws KeyNotFoundException {
        return super.getAll(genericKey.name());
    }

    /**
     * @param genericKey
     * @throws KeyNotFoundException
     */
    public void deleteField(FieldKey genericKey) throws KeyNotFoundException {
        if (supportedKeys.contains(genericKey)) {
            deleteField(genericKey.name());
        } else {
            throw new UnsupportedOperationException(ErrorMessage.GENERIC_NOT_SUPPORTED.getMsg());
        }
    }

    /**
     * @param genericKey
     * @return
     * @throws KeyNotFoundException
     */
    public TagField getFirstField(FieldKey genericKey) throws KeyNotFoundException {
        if (supportedKeys.contains(genericKey)) {
            return getFirstField(genericKey.name());
        } else {
            throw new UnsupportedOperationException(ErrorMessage.GENERIC_NOT_SUPPORTED.getMsg());
        }
    }

    public List<Artwork> getArtworkList() {
        return Collections.emptyList();
    }

    public TagField createField(Artwork artwork) throws FieldDataInvalidException {
        throw new UnsupportedOperationException(ErrorMessage.GENERIC_NOT_SUPPORTED.getMsg());
    }
}
