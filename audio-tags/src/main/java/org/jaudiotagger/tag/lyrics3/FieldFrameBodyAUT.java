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

public class FieldFrameBodyAUT extends AbstractLyrics3v2FieldFrameBody {
    /**
     * Creates a new FieldBodyAUT datatype.
     */
    public FieldFrameBodyAUT() {
        //        this.setObject("Author", "");
    }

    public FieldFrameBodyAUT(FieldFrameBodyAUT body) {
        super(body);
    }

    /**
     * Creates a new FieldBodyAUT datatype.
     *
     * @param author
     */
    public FieldFrameBodyAUT(String author) {
        this.setObjectValue("Author", author);
    }

    /**
     * Creates a new FieldBodyAUT datatype.
     *
     * @param byteBuffer
     * @throws InvalidTagException
     */
    public FieldFrameBodyAUT(ByteBuffer byteBuffer) throws InvalidTagException {
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
        return "AUT";
    }

    /**
     *
     */
    protected void setupObjectList() {
        objectList.add(new StringSizeTerminated("Author", this));
    }
}
