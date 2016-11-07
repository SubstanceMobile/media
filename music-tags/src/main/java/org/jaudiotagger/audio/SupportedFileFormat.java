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

package org.jaudiotagger.audio;

/**
 * Files formats currently supported by Library.
 * Each enum value is associated with a file suffix (extension).
 */
public enum SupportedFileFormat {
    OGG("ogg"),
    MP3("mp3"),
    FLAC("flac"),
    MP4("mp4"),
    M4A("m4a"),
    M4P("m4p"),
    WMA("wma"),
    WAV("wav"),
    RA("ra"),
    RM("rm"),
    M4B("m4b"),
    AIF("aif");

    private String filesuffix;

    /**
     * Constructor for internal use by this enum.
     */
    SupportedFileFormat(String filesuffix) {
        this.filesuffix = filesuffix;
    }

    /**
     * Returns the file suffix (lower case without initial .) associated with the format.
     */
    public String getFilesuffix() {
        return filesuffix;
    }
}
