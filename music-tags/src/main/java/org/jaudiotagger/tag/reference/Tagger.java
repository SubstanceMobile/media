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

package org.jaudiotagger.tag.reference;

/**
 * An enumeration of popular tagger applications
 * <p>
 * <p>This is not meant to be a definitive list but is first attempt to document a list of taggers in order
 * for us to link nonstandard fields, and link nonstandard tagging to them
 */
public enum Tagger {
    ITUNES(0, "iTunes"),
    MEDIAPLAYER(1, "Windows Media Player"),
    WINAMP(2, "Winamp"),
    MP3TAG(3, "Mp3 Tag"),
    MEDIA_MONKEY(4, "Media Monkey"),
    TAG_AND_RENAME(5, "Tag and Rename"),
    PICARD(6, "Picard"),
    JAIKOZ(7, "Jaikoz"),
    TAGSCANNER(8, "Tagscanner"),
    XIPH(9, "Xiph"),   //standards body rather than tagger xiph.org
    FOOBAR2000(10, "Foobar2000"),
    BEATUNES(11, "Beatunes"),
    SONGBIRD(12, "Songbird"),
    JRIVER(13, "JRiver"),
    GODFATHER(14, "The Godfather");

    private int compatability;
    private String desc;

    Tagger(int compatability, String desc) {
        this.compatability = compatability;
        this.desc = desc;
    }

    public String toString() {
        return desc;
    }

}
