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
package org.jaudiotagger.tag.lyrics3;

import org.jaudiotagger.tag.datatype.AbstractStringStringValuePair;

public class Lyrics3v2Fields extends AbstractStringStringValuePair {

    private static Lyrics3v2Fields lyrics3Fields;

    /**
     * CRLF int set
     */
    private static final byte[] crlfByte = {13, 10};
    /**
     * CRLF int set
     */
    public static final String CRLF = new String(crlfByte);

    public static Lyrics3v2Fields getInstanceOf() {
        if (lyrics3Fields == null) {
            lyrics3Fields = new Lyrics3v2Fields();
        }
        return lyrics3Fields;
    }

    public static final String FIELD_V2_INDICATIONS = "IND";
    public static final String FIELD_V2_LYRICS_MULTI_LINE_TEXT = "LYR";
    public static final String FIELD_V2_ADDITIONAL_MULTI_LINE_TEXT = "INF";
    public static final String FIELD_V2_AUTHOR = "AUT";
    public static final String FIELD_V2_ALBUM = "EAL";
    public static final String FIELD_V2_ARTIST = "EAR";
    public static final String FIELD_V2_TRACK = "ETT";
    public static final String FIELD_V2_IMAGE = "IMG";


    private Lyrics3v2Fields() {
        idToValue.put(FIELD_V2_INDICATIONS, "Indications field");
        idToValue.put(FIELD_V2_LYRICS_MULTI_LINE_TEXT, "Lyrics multi line text");
        idToValue.put(FIELD_V2_ADDITIONAL_MULTI_LINE_TEXT, "Additional information multi line text");
        idToValue.put(FIELD_V2_AUTHOR, "Lyrics/Music Author name");
        idToValue.put(FIELD_V2_ALBUM, "Extended Album name");
        idToValue.put(FIELD_V2_ARTIST, "Extended Artist name");
        idToValue.put(FIELD_V2_TRACK, "Extended Track Title");
        idToValue.put(FIELD_V2_IMAGE, "Link to an image files");
        createMaps();
    }

    /**
     * Returns true if the identifier is a valid Lyrics3v2 frame identifier
     *
     * @param identifier string to test
     * @return true if the identifier is a valid Lyrics3v2 frame identifier
     */
    public static boolean isLyrics3v2FieldIdentifier(String identifier) {
        return identifier.length() >= 3 && getInstanceOf().getIdToValueMap().containsKey(identifier.substring(0, 3));
    }
}
