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

package org.jaudiotagger.audio.asf.data;

import java.math.BigInteger;

/**
 * This class represents the ASF extended header object (chunk).<br>
 * Like {@link AsfHeader} it contains multiple other ASF objects (chunks).<br>
 *
 * @author Christian Laireiter
 */
public final class AsfExtendedHeader extends ChunkContainer {

    /**
     * Creates an instance.<br>
     *
     * @param pos    Position within the stream.<br>
     * @param length the length of the extended header object.
     */
    public AsfExtendedHeader(final long pos, final BigInteger length) {
        super(GUID.GUID_HEADER_EXTENSION, pos, length);
    }

    /**
     * @return Returns the contentDescription.
     */
    public ContentDescription getContentDescription() {
        return (ContentDescription) getFirst(GUID.GUID_CONTENTDESCRIPTION,
                ContentDescription.class);
    }

    /**
     * @return Returns the tagHeader.
     */
    public MetadataContainer getExtendedContentDescription() {
        return (MetadataContainer) getFirst(
                GUID.GUID_EXTENDED_CONTENT_DESCRIPTION, MetadataContainer.class);
    }

    /**
     * Returns a language list object if present.
     *
     * @return a language list object.
     */
    public LanguageList getLanguageList() {
        return (LanguageList) getFirst(GUID.GUID_LANGUAGE_LIST,
                LanguageList.class);
    }

    /**
     * Returns a metadata library object if present.
     *
     * @return metadata library objet
     */
    public MetadataContainer getMetadataLibraryObject() {
        return (MetadataContainer) getFirst(GUID.GUID_METADATA_LIBRARY,
                MetadataContainer.class);
    }

    /**
     * Returns a metadata object if present.
     *
     * @return metadata object
     */
    public MetadataContainer getMetadataObject() {
        return (MetadataContainer) getFirst(GUID.GUID_METADATA,
                MetadataContainer.class);
    }

}
