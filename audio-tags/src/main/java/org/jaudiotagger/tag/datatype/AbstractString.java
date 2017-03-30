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
package org.jaudiotagger.tag.datatype;

import org.jaudiotagger.tag.id3.AbstractTagFrameBody;
import org.jaudiotagger.tag.id3.valuepair.TextEncoding;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * A partial implementation for String based ID3 fields
 */
public abstract class AbstractString extends AbstractDataType {
    /**
     * Creates a new  datatype
     *
     * @param identifier
     * @param frameBody
     */
    protected AbstractString(String identifier, AbstractTagFrameBody frameBody) {
        super(identifier, frameBody);
    }

    /**
     * Creates a new  datatype, with value
     *
     * @param identifier
     * @param frameBody
     * @param value
     */
    public AbstractString(String identifier, AbstractTagFrameBody frameBody, String value) {
        super(identifier, frameBody, value);
    }

    /**
     * Copy constructor
     *
     * @param object
     */
    protected AbstractString(AbstractString object) {
        super(object);
    }

    /**
     * Return the size in bytes of this datatype as it was/is held in file this
     * will be effected by the encoding type.
     *
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the size in bytes of this data type.
     * This is set after writing the data to allow us to recalculate the size for
     * frame header.
     * @param size
     */
    protected void setSize(int size) {
        this.size = size;
    }

    /**
     * Return String representation of data type
     *
     * @return a string representation of the value
     */
    public String toString() {
        return (String) value;
    }

    /**
     * Check the value can be encoded with the specified encoding
     * @return
     */
    public boolean canBeEncoded() {
        //Try and write to buffer using the CharSet defined by the textEncoding field (note if using UTF16 we dont
        //need to worry about LE,BE at this point it makes no difference)
        byte textEncoding = this.getBody().getTextEncoding();
        String charSetName = TextEncoding.getInstanceOf().getValueForId(textEncoding);
        CharsetEncoder encoder = Charset.forName(charSetName).newEncoder();

        if (encoder.canEncode((String) value)) {
            return true;
        } else {
            logger.finest("Failed Trying to decode" + value + "with" + encoder.toString());
            return false;
        }
    }
}
