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
package org.jaudiotagger.tag.id3;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class ID3v1Iterator implements Iterator {
    /**
     *
     */
    private static final int TITLE = 1;

    /**
     *
     */
    private static final int ARTIST = 2;

    /**
     *
     */
    private static final int ALBUM = 3;

    /**
     *
     */
    private static final int COMMENT = 4;

    /**
     *
     */
    private static final int YEAR = 5;

    /**
     *
     */
    private static final int GENRE = 6;

    /**
     *
     */
    private static final int TRACK = 7;

    /**
     *
     */
    private ID3v1Tag id3v1tag;

    /**
     *
     */
    private int lastIndex = 0;

    /**
     * Creates a new ID3v1Iterator datatype.
     *
     * @param id3v1tag
     */
    public ID3v1Iterator(ID3v1Tag id3v1tag) {
        this.id3v1tag = id3v1tag;
    }

    /**
     * @return
     */
    public boolean hasNext() {
        return hasNext(lastIndex);
    }

    /**
     * @return
     */
    public Object next() {
        return next(lastIndex);
    }

    /**
     *
     */
    public void remove() {
        switch (lastIndex) {
            case TITLE:
                id3v1tag.title = "";

            case ARTIST:
                id3v1tag.artist = "";

            case ALBUM:
                id3v1tag.album = "";

            case COMMENT:
                id3v1tag.comment = "";

            case YEAR:
                id3v1tag.year = "";

            case GENRE:
                id3v1tag.genre = (byte) -1;

            case TRACK:

                if (id3v1tag instanceof ID3v11Tag) {
                    ((ID3v11Tag) id3v1tag).track = (byte) -1;
                }
        }
    }

    /**
     * @param index
     * @return
     */
    private boolean hasNext(int index) {
        switch (index) {
            case TITLE:
                return (id3v1tag.title.length() > 0) || hasNext(index + 1);

            case ARTIST:
                return (id3v1tag.artist.length() > 0) || hasNext(index + 1);

            case ALBUM:
                return (id3v1tag.album.length() > 0) || hasNext(index + 1);

            case COMMENT:
                return (id3v1tag.comment.length() > 0) || hasNext(index + 1);

            case YEAR:
                return (id3v1tag.year.length() > 0) || hasNext(index + 1);

            case GENRE:
                return (id3v1tag.genre >= (byte) 0) || hasNext(index + 1);

            case TRACK:

                if (id3v1tag instanceof ID3v11Tag) {
                    return (((ID3v11Tag) id3v1tag).track >= (byte) 0) || hasNext(index + 1);
                }

            default:
                return false;
        }
    }

    /**
     * @param index
     * @return
     * @throws NoSuchElementException
     */
    private Object next(int index) {
        switch (lastIndex) {
            case 0:
                return (id3v1tag.title.length() > 0) ? id3v1tag.title : next(index + 1);

            case TITLE:
                return (id3v1tag.artist.length() > 0) ? id3v1tag.artist : next(index + 1);

            case ARTIST:
                return (id3v1tag.album.length() > 0) ? id3v1tag.album : next(index + 1);

            case ALBUM:
                return (id3v1tag.comment.length() > 0) ? id3v1tag.comment : next(index + 1);

            case COMMENT:
                return (id3v1tag.year.length() > 0) ? id3v1tag.year : next(index + 1);

            case YEAR:
                return (id3v1tag.genre >= (byte) 0) ? id3v1tag.genre : next(index + 1);

            case GENRE:
                return (id3v1tag instanceof ID3v11Tag && (((ID3v11Tag) id3v1tag).track >= (byte) 0)) ? ((ID3v11Tag) id3v1tag).track : null;

            default:
                throw new NoSuchElementException("Iteration has no more elements.");
        }
    }
}