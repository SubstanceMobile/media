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
package org.jaudiotagger.tag.lyrics3;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Lyrics3v1Iterator implements Iterator<String> {
    /**
     *
     */
    private Lyrics3v1 tag = null;

    /**
     *
     */
    private int lastIndex = 0;

    /**
     *
     */
    private int removeIndex = 0;

    /**
     * Creates a new Lyrics3v1Iterator datatype.
     *
     * @param lyrics3v1Tag
     */
    public Lyrics3v1Iterator(Lyrics3v1 lyrics3v1Tag) {
        tag = lyrics3v1Tag;
    }

    /**
     * @return
     */
    public boolean hasNext() {
        return !((tag.getLyric().indexOf('\n', lastIndex) < 0) && (lastIndex > tag.getLyric().length()));
    }

    /**
     * @return
     * @throws NoSuchElementException
     */
    public String next() {
        int nextIndex = tag.getLyric().indexOf('\n', lastIndex);

        removeIndex = lastIndex;

        String line;

        if (lastIndex >= 0) {
            if (nextIndex >= 0) {
                line = tag.getLyric().substring(lastIndex, nextIndex);
            } else {
                line = tag.getLyric().substring(lastIndex);
            }

            lastIndex = nextIndex;
        } else {
            throw new NoSuchElementException("Iteration has no more elements.");
        }

        return line;
    }

    /**
     *
     */
    public void remove() {
        String lyric = tag.getLyric().substring(0, removeIndex) + tag.getLyric().substring(lastIndex);
        tag.setLyric(lyric);
    }
}