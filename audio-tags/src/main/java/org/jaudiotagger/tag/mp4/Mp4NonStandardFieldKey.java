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

package org.jaudiotagger.tag.mp4;

import org.jaudiotagger.tag.reference.Tagger;

/**
 * This a list of mp4boxes identifiers that break various rules, but should be documented nonetheless, they will
 * be created by applications other than iTunes, as we regard iTunes as the defacto standard for Mp4 files (but
 * certainly not any other format such as mp3 !).
 */
public enum Mp4NonStandardFieldKey {
    AAPR("AApr", "MM3 Album Art Attributes", Tagger.MEDIA_MONKEY),
    ALFN("Alfn", "MM3 Album Art Unknown", Tagger.MEDIA_MONKEY),
    AMIM("AMIM", "MM3 Album Art MimeType", Tagger.MEDIA_MONKEY),
    ADCP("Adcp", "MM3 Album Art Description", Tagger.MEDIA_MONKEY),
    APTY("Apty", "MM3 Album Art ID3 Picture Type", Tagger.MEDIA_MONKEY);

    private String fieldName;
    private String description;
    private Tagger tagger;

    Mp4NonStandardFieldKey(String fieldName, String description, Tagger tagger) {
        this.fieldName = fieldName;
        this.description = description;
        this.tagger = tagger;

    }

    /**
     * This is the value of the fieldname that is actually used to write mp4
     *
     * @return
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @return description, human redable description of the atom
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return tagger that defined (and probably craeted) instance of field
     */
    public Tagger geTagger() {
        return tagger;
    }
}
