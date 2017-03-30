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


public class FieldFrameBodyETT extends AbstractLyrics3v2FieldFrameBody {
    /**
     * Creates a new FieldBodyETT datatype.
     */
    public FieldFrameBodyETT() {
        //        this.setObject("Title", "");
    }

    public FieldFrameBodyETT(FieldFrameBodyETT body) {
        super(body);
    }

    /**
     * Creates a new FieldBodyETT datatype.
     *
     * @param title
     */
    public FieldFrameBodyETT(String title) {
        this.setObjectValue("Title", title);
    }

    /**
     * Creates a new FieldBodyETT datatype.
     *
     * @param byteBuffer
     * @throws InvalidTagException
     */
    public FieldFrameBodyETT(ByteBuffer byteBuffer) throws InvalidTagException {
        this.read(byteBuffer);
    }

    /**
     * @return
     */
    public String getIdentifier() {
        return "ETT";
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        setObjectValue("Title", title);
    }

    /**
     * @return
     */
    public String getTitle() {
        return (String) getObjectValue("Title");
    }

    /**
     *
     */
    protected void setupObjectList() {
        objectList.add(new StringSizeTerminated("Title", this));
    }
}
