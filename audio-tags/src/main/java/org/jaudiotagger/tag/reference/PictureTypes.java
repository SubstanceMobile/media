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
package org.jaudiotagger.tag.reference;

import org.jaudiotagger.tag.datatype.AbstractIntStringValuePair;

/**
 * Pictures types for Attached Pictures
 *
 * <P>Note this list is used by APIC and PIC frames within ID3v2. It is also used by Flac format Picture blocks
 * and WMA Picture fields.
 */
public class PictureTypes extends AbstractIntStringValuePair {
    private static PictureTypes pictureTypes;

    public static PictureTypes getInstanceOf() {
        if (pictureTypes == null) {
            pictureTypes = new PictureTypes();
        }
        return pictureTypes;
    }

    public static final int PICTURE_TYPE_FIELD_SIZE = 1;
    public static final String DEFAULT_VALUE = "Cover (front)";
    public static final Integer DEFAULT_ID = 3;

    private PictureTypes() {
        idToValue.put(0, "Other");
        idToValue.put(1, "32x32 pixels 'file icon' (PNG only)");
        idToValue.put(2, "Other file icon");
        idToValue.put(3, "Cover (front)");
        idToValue.put(4, "Cover (back)");
        idToValue.put(5, "Leaflet page");
        idToValue.put(6, "Media (e.g. label side of CD)");
        idToValue.put(7, "Lead artist/lead performer/soloist");
        idToValue.put(8, "Artist/performer");
        idToValue.put(9, "Conductor");
        idToValue.put(10, "Band/Orchestra");
        idToValue.put(11, "Composer");
        idToValue.put(12, "Lyricist/text writer");
        idToValue.put(13, "Recording Location");
        idToValue.put(14, "During recording");
        idToValue.put(15, "During performance");
        idToValue.put(16, "Movie/video screen capture");
        idToValue.put(17, "A bright coloured fish");
        idToValue.put(18, "Illustration");
        idToValue.put(19, "Band/artist logotype");
        idToValue.put(20, "Publisher/Studio logotype");

        createMaps();
    }

}
