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


public class FieldFrameBodyEAL extends AbstractLyrics3v2FieldFrameBody {
    /**
     * Creates a new FieldBodyEAL datatype.
     */
    public FieldFrameBodyEAL() {
        //        this.setObject("Album", "");
    }

    public FieldFrameBodyEAL(FieldFrameBodyEAL body) {
        super(body);
    }

    /**
     * Creates a new FieldBodyEAL datatype.
     *
     * @param album
     */
    public FieldFrameBodyEAL(String album) {
        this.setObjectValue("Album", album);
    }

    /**
     * Creates a new FieldBodyEAL datatype.
     *
     * @param byteBuffer
     * @throws InvalidTagException
     */
    public FieldFrameBodyEAL(ByteBuffer byteBuffer) throws InvalidTagException {
        read(byteBuffer);

    }

    /**
     * @param album
     */
    public void setAlbum(String album) {
        setObjectValue("Album", album);
    }

    /**
     * @return
     */
    public String getAlbum() {
        return (String) getObjectValue("Album");
    }

    /**
     * @return
     */
    public String getIdentifier() {
        return "EAL";
    }

    /**
     *
     */
    protected void setupObjectList() {
        objectList.add(new StringSizeTerminated("Album", this));
    }
}
