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

package org.jaudiotagger.tag.id3.valuepair;

/**
 * ID3V2 Genre list
 * <p>
 * <p>These are additional genres added in the V2 Specification, they have a string key (RX,CV) rather than a
 * numeric key
 */
public enum ID3V2ExtendedGenreTypes {
    RX("Remix"),
    CR("Cover");

    private String description;

    ID3V2ExtendedGenreTypes(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
