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

import org.jaudiotagger.tag.InvalidDataTypeException;
import org.jaudiotagger.tag.id3.AbstractTagFrameBody;

public class BooleanString extends AbstractDataType {
    /**
     * Creates a new ObjectBooleanString datatype.
     *
     * @param identifier
     * @param frameBody
     */
    public BooleanString(String identifier, AbstractTagFrameBody frameBody) {
        super(identifier, frameBody);
    }

    public BooleanString(BooleanString object) {
        super(object);
    }

    /**
     * @return
     */
    public int getSize() {
        return 1;
    }

    public boolean equals(Object obj) {
        return obj instanceof BooleanString && super.equals(obj);

    }

    /**
     * @param offset
     * @throws NullPointerException
     * @throws IndexOutOfBoundsException
     */
    public void readByteArray(byte[] arr, int offset) throws InvalidDataTypeException {
        byte b = arr[offset];
        value = b != '0';
    }

    /**
     * @return
     */
    public String toString() {
        return "" + value;
    }

    /**
     * @return
     */
    public byte[] writeByteArray() {
        byte[] booleanValue = new byte[1];
        if (value == null) {
            booleanValue[0] = '0';
        } else {
            if ((Boolean) value) {
                booleanValue[0] = '0';
            } else {
                booleanValue[0] = '1';
            }
        }
        return booleanValue;
    }
}
