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

/**
 * Represents a bit flag within a byte
 */
public class BooleanByte extends AbstractDataType {
    /**
     *
     */
    int bitPosition = -1;

    /**
     * Creates a new ObjectBooleanByte datatype.
     *
     * @param identifier
     * @param frameBody
     * @param bitPosition
     * @throws IndexOutOfBoundsException
     */
    public BooleanByte(String identifier, AbstractTagFrameBody frameBody, int bitPosition) {
        super(identifier, frameBody);
        if ((bitPosition < 0) || (bitPosition > 7)) {
            throw new IndexOutOfBoundsException("Bit position needs to be from 0 - 7 : " + bitPosition);
        }

        this.bitPosition = bitPosition;
    }

    public BooleanByte(BooleanByte copy) {
        super(copy);
        this.bitPosition = copy.bitPosition;
    }

    /**
     * @return
     */
    public int getBitPosition() {
        return bitPosition;
    }

    /**
     * @return
     */
    public int getSize() {
        return 1;
    }

    /**
     * @param obj
     * @return
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof BooleanByte)) {
            return false;
        }

        BooleanByte object = (BooleanByte) obj;

        return this.bitPosition == object.bitPosition && super.equals(obj);

    }

    /**
     * @param arr
     * @param offset
     * @throws NullPointerException
     * @throws IndexOutOfBoundsException
     */
    public void readByteArray(byte[] arr, int offset) throws InvalidDataTypeException {
        if (arr == null) {
            throw new NullPointerException("Byte array is null");
        }

        if ((offset < 0) || (offset >= arr.length)) {
            throw new IndexOutOfBoundsException("Offset to byte array is out of bounds: offset = " + offset + ", array.length = " + arr.length);
        }

        byte newValue = arr[offset];

        newValue >>= bitPosition;
        newValue &= 0x1;
        this.value = newValue == 1;
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
        byte[] retValue;

        retValue = new byte[1];

        if (value != null) {
            retValue[0] = (byte) ((Boolean) value ? 1 : 0);
            retValue[0] <<= bitPosition;
        }

        return retValue;
    }
}
