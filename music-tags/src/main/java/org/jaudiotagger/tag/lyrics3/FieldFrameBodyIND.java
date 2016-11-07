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

import org.jaudiotagger.tag.InvalidTagException;
import org.jaudiotagger.tag.datatype.BooleanString;

import java.nio.ByteBuffer;


public class FieldFrameBodyIND extends AbstractLyrics3v2FieldFrameBody {
    /**
     * Creates a new FieldBodyIND datatype.
     */
    public FieldFrameBodyIND() {
        //        this.setObject("Lyrics Present", new Boolean(false));
        //        this.setObject("Timestamp Present", new Boolean(false));
    }

    public FieldFrameBodyIND(FieldFrameBodyIND body) {
        super(body);
    }

    /**
     * Creates a new FieldBodyIND datatype.
     *
     * @param lyricsPresent
     * @param timeStampPresent
     */
    public FieldFrameBodyIND(boolean lyricsPresent, boolean timeStampPresent) {
        this.setObjectValue("Lyrics Present", lyricsPresent);
        this.setObjectValue("Timestamp Present", timeStampPresent);
    }

    /**
     * Creates a new FieldBodyIND datatype.
     *
     * @param byteBuffer
     * @throws InvalidTagException
     */
    public FieldFrameBodyIND(ByteBuffer byteBuffer) throws InvalidTagException {
        this.read(byteBuffer);
    }

    /**
     * @param author
     */
    public void setAuthor(String author) {
        setObjectValue("Author", author);
    }

    /**
     * @return
     */
    public String getAuthor() {
        return (String) getObjectValue("Author");
    }

    /**
     * @return
     */
    public String getIdentifier() {
        return "IND";
    }

    /**
     *
     */
    protected void setupObjectList() {
        objectList.add(new BooleanString("Lyrics Present", this));
        objectList.add(new BooleanString("Timestamp Present", this));
    }
}
