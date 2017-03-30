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

package org.jaudiotagger.audio.mp4;

/**
 * Encoder Type actually identifies the format of the audio within the mp4. This is because
 * mp4 container can be used to hold different types of files.
 */
public enum EncoderType {
    AAC("AAC"),
    DRM_AAC("DRM AAC"),
    APPLE_LOSSLESS("Apple Lossless"),;

    private String description;

    EncoderType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
