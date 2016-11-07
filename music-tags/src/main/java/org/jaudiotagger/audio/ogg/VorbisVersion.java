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

package org.jaudiotagger.audio.ogg;

/**
 * Vorbis Version
 * <p>
 * Ordinal is used to map from internal representation
 */
public enum VorbisVersion {
    VERSION_ONE("Ogg Vorbis v1");

    //The display name for this version
    private String displayName;


    VorbisVersion(String displayName) {
        this.displayName = displayName;
    }

    public String toString() {
        return displayName;
    }
}
