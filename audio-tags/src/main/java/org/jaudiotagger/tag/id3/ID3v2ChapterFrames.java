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
package org.jaudiotagger.tag.id3;

import java.util.TreeSet;

/**
 * Defines ID3 Chapter frames and collections that categorise frames.
 * <p>
 * <p>For more details, please refer to the ID3 Chapter Frame specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2-chapters-1.0.txt">ID3 v2 Chapter Frame Spec</a>
 * </ul>
 *
 * @author Marc Gimpel, Horizon Wimba S.A.
 * @version $Id$
 */
public class ID3v2ChapterFrames extends ID3Frames {
    public static final String FRAME_ID_CHAPTER = "CHAP";
    public static final String FRAME_ID_TABLE_OF_CONTENT = "CTOC";

    private static ID3v2ChapterFrames id3v2ChapterFrames;

    public static ID3v2ChapterFrames getInstanceOf() {
        if (id3v2ChapterFrames == null) {
            id3v2ChapterFrames = new ID3v2ChapterFrames();
        }
        return id3v2ChapterFrames;
    }

    private ID3v2ChapterFrames() {
        idToValue.put(FRAME_ID_CHAPTER, "Chapter");
        idToValue.put(FRAME_ID_TABLE_OF_CONTENT, "Table of content");
        createMaps();
        multipleFrames = new TreeSet<String>();
        discardIfFileAlteredFrames = new TreeSet<String>();
    }
}
