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
import org.jaudiotagger.tag.datatype.StringSizeTerminated;

import java.nio.ByteBuffer;


public class FieldFrameBodyEAR extends AbstractLyrics3v2FieldFrameBody {
    /**
     * Creates a new FieldBodyEAR datatype.
     */
    public FieldFrameBodyEAR() {
        //        this.setObject("Artist", "");
    }

    public FieldFrameBodyEAR(FieldFrameBodyEAR body) {
        super(body);
    }

    /**
     * Creates a new FieldBodyEAR datatype.
     *
     * @param artist
     */
    public FieldFrameBodyEAR(String artist) {
        this.setObjectValue("Artist", artist);
    }

    /**
     * Creates a new FieldBodyEAR datatype.
     *
     * @param byteBuffer
     * @throws InvalidTagException
     */
    public FieldFrameBodyEAR(ByteBuffer byteBuffer) throws InvalidTagException {

        this.read(byteBuffer);

    }

    /**
     * @param artist
     */
    public void setArtist(String artist) {
        setObjectValue("Artist", artist);
    }

    /**
     * @return
     */
    public String getArtist() {
        return (String) getObjectValue("Artist");
    }

    /**
     * @return
     */
    public String getIdentifier() {
        return "EAR";
    }

    /**
     *
     */
    protected void setupObjectList() {
        objectList.add(new StringSizeTerminated("Artist", this));
    }
}
