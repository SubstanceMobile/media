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

package org.jaudiotagger.audio.ogg.util;

/**
 * Defines variables common to all vorbis headers
 */
public interface VorbisHeader {
    //Capture pattern at start of header
    public static final String CAPTURE_PATTERN = "vorbis";

    public static final byte[] CAPTURE_PATTERN_AS_BYTES = {'v', 'o', 'r', 'b', 'i', 's'};

    public static final int FIELD_PACKET_TYPE_POS = 0;
    public static final int FIELD_CAPTURE_PATTERN_POS = 1;

    public static final int FIELD_PACKET_TYPE_LENGTH = 1;
    public static final int FIELD_CAPTURE_PATTERN_LENGTH = 6;

    //Vorbis uses UTF-8 for all text
    public static final String CHARSET_UTF_8 = "UTF-8";

}
