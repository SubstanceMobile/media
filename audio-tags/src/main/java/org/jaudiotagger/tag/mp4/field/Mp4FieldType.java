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

package org.jaudiotagger.tag.mp4.field;

import org.jaudiotagger.tag.mp4.Mp4FieldKey;

import java.util.EnumSet;
import java.util.HashMap;

/**
 * Describes the possible types of data held within a Databox
 */
public enum Mp4FieldType {
    IMPLICIT(0x0),  //used for specialized formats such as TrackNo or DiscNo
    TEXT(0x1),      //UTF-8
    TEXT_UTF16BE(0x02),
    TEXT_JAPANESE(0x03),
    HTML(0x06),
    XML(0x07),
    GUID(0x08),
    ISRC(0x09),
    MI3P(0x0a),
    COVERART_GIF(0x0c),
    COVERART_JPEG(0x0d),
    COVERART_PNG(0x0e),
    URL(0x0f),
    DURATION(0x10),
    DATETIME(0x11),
    GENRES(0x12),
    INTEGER(0x15), //Formally known as byte
    RIAAPA(0x18),
    UPC(0x19),
    COVERART_BMP(0x1B),;


    private int fileClassId;

    Mp4FieldType(int fileClassId) {
        this.fileClassId = fileClassId;
    }

    public int getFileClassId() {
        return fileClassId;
    }

    private final static HashMap<Integer, Mp4FieldType> fileClassIdFiedTypeMap;

    static {
        fileClassIdFiedTypeMap = new HashMap<Integer, Mp4FieldType>(Mp4FieldType.values().length);
        for (Mp4FieldType curr : Mp4FieldType.values()) {
            fileClassIdFiedTypeMap.put(curr.fileClassId, curr);
        }
    }

    /**
     * @param fieldClassId
     * @return the Mp4FieldType that this fieldClassId maps to
     */
    public static Mp4FieldType getFieldType(int fieldClassId) {
        return fileClassIdFiedTypeMap.get(fieldClassId);
    }

    private static EnumSet<Mp4FieldType> coverArtTypes;

    static {
        coverArtTypes = EnumSet.of(COVERART_GIF, COVERART_JPEG, COVERART_PNG, COVERART_BMP);
    }

    /**
     * @param mp4FieldType
     * @return true if this type is for identifying a image format to be used in cover art
     */
    public static boolean isCoverArtType(Mp4FieldType mp4FieldType) {
        return coverArtTypes.contains(mp4FieldType);
    }
}
