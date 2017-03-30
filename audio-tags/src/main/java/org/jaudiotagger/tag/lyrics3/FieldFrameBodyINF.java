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

public class FieldFrameBodyINF extends AbstractLyrics3v2FieldFrameBody {
    /**
     * Creates a new FieldBodyINF datatype.
     */
    public FieldFrameBodyINF() {
        //        this.setObject("Additional Information", "");
    }

    public FieldFrameBodyINF(FieldFrameBodyINF body) {
        super(body);
    }

    /**
     * Creates a new FieldBodyINF datatype.
     *
     * @param additionalInformation
     */
    public FieldFrameBodyINF(String additionalInformation) {
        this.setObjectValue("Additional Information", additionalInformation);
    }

    /**
     * Creates a new FieldBodyINF datatype.
     * @param byteBuffer
     * @throws org.jaudiotagger.tag.InvalidTagException
     */
    public FieldFrameBodyINF(ByteBuffer byteBuffer) throws InvalidTagException {
        this.read(byteBuffer);

    }

    /**
     * @param additionalInformation
     */
    public void setAdditionalInformation(String additionalInformation) {
        setObjectValue("Additional Information", additionalInformation);
    }

    /**
     * @return
     */
    public String getAdditionalInformation() {
        return (String) getObjectValue("Additional Information");
    }

    /**
     * @return
     */
    public String getIdentifier() {
        return "INF";
    }

    /**
     *
     */
    protected void setupObjectList() {
        objectList.add(new StringSizeTerminated("Additional Information", this));
    }
}
