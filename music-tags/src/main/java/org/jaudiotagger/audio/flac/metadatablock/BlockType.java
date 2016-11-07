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

package org.jaudiotagger.audio.flac.metadatablock;

/**
 * The different types of metadata block
 * <p>
 * 7 - 126 are reserved for future use
 * 127 is invalid
 * User: Paul Taylor
 * Date: 21-Nov-2007
 */
public enum BlockType {
    STREAMINFO(0),
    PADDING(1),
    APPLICATION(2),
    SEEKTABLE(3),
    VORBIS_COMMENT(4),
    CUESHEET(5),
    PICTURE(6);

    private int id;

    BlockType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
