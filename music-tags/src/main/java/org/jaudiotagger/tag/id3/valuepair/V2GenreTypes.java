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

import org.jaudiotagger.tag.reference.GenreTypes;

import java.util.Collections;
import java.util.List;

/**
 * ID3V2 Genre list
 * <p>
 * <p>Merging of Id3v2 genres and the extended ID3v2 genres
 */
public class V2GenreTypes {
    private static V2GenreTypes v2GenresTypes;

    private V2GenreTypes() {

    }

    public static V2GenreTypes getInstanceOf() {
        if (v2GenresTypes == null) {
            v2GenresTypes = new V2GenreTypes();
        }
        return v2GenresTypes;
    }

    /**
     * @return list of all valid v2 genres in alphabetical order
     */
    public List<String> getAlphabeticalValueList() {
        List<String> genres = GenreTypes.getInstanceOf().getAlphabeticalValueList();
        genres.add(ID3V2ExtendedGenreTypes.CR.getDescription());
        genres.add(ID3V2ExtendedGenreTypes.RX.getDescription());

        //Sort
        Collections.sort(genres);
        return genres;
    }
}
