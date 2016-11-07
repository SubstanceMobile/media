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

package org.jaudiotagger.tag.mp4.atom;

/**
 * List of valid values for the Rating (rtng) atom
 * <p>
 * These are held as a byte field
 * <p>
 * TODO:Is this only used in video
 */
public enum Mp4RatingValue {
    CLEAN("Clean", 2),
    EXPLICIT("Explicit", 4);

    private String description;
    private int id;


    /**
     * @param description of value
     * @param id          used internally
     */
    Mp4RatingValue(String description, int id) {
        this.description = description;
        this.id = id;
    }

    /**
     * Return id used in the file
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * This is the value of the fieldname that is actually used to write mp4
     *
     * @return
     */
    public String getDescription() {
        return description;
    }


}
