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
 * Vorbis Packet Type
 * <p>
 * In an Vorbis Stream there should be one instance of the three headers, and many audio packets
 */
public enum VorbisPacketType {
    AUDIO(0),
    IDENTIFICATION_HEADER(1),
    COMMENT_HEADER(3),
    SETUP_HEADER(5);

    int type;

    VorbisPacketType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
