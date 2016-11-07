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
import org.jaudiotagger.tag.id3.ID3Tags;


/**
 * Represents a number held as a fixed number of digits.
 *
 * The bitorder in ID3v2 is most significant bit first (MSB). The byteorder in multibyte numbers is most significant
 * byte first (e.g. $12345678 would be encoded $12 34 56 78), also known as big endian and network byte order.
 *
 * In ID3Specification would be denoted as $xx xx this denotes exactly two bytes required
 */
public class NumberFixedLength extends AbstractDataType {
    /**
     * Creates a new ObjectNumberFixedLength datatype.
     *
     * @param identifier
     * @param frameBody
     * @param size       the number of significant places that the number is held to
     * @throws IllegalArgumentException
     */
    public NumberFixedLength(String identifier, AbstractTagFrameBody frameBody, int size) {
        super(identifier, frameBody);
        if (size < 0) {
            throw new IllegalArgumentException("Length is less than zero: " + size);
        }
        this.size = size;

    }

    public NumberFixedLength(NumberFixedLength copy) {
        super(copy);
        this.size = copy.size;
    }


    /**
     * Set Size in Bytes of this Object
     *
     * @param size in bytes that this number will be held as
     */
    public void setSize(int size) {
        if (size > 0) {
            this.size = size;
        }
    }

    /**
     * Return size
     *
     * @return the size of this number
     */
    public int getSize() {
        return size;
    }

    public void setValue(Object value) {
        if (!(value instanceof Number)) {
            throw new IllegalArgumentException("Invalid value type for NumberFixedLength:" + value.getClass());
        }
        super.setValue(value);
    }


    /**
     * @param obj
     * @return true if obj equivalent to this
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof NumberFixedLength)) {
            return false;
        }
        NumberFixedLength object = (NumberFixedLength) obj;
        return this.size == object.size && super.equals(obj);
    }

    /**
     * Read the number from the byte array
     *
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
            throw new InvalidDataTypeException("Offset to byte array is out of bounds: offset = " + offset + ", array.length = " + arr.length);
        }

        if (offset + size > arr.length) {
            throw new InvalidDataTypeException("Offset plus size to byte array is out of bounds: offset = "
                    + offset + ", size = " + size + " + arr.length " + arr.length);
        }

        long lvalue = 0;
        for (int i = offset; i < (offset + size); i++) {
            lvalue <<= 8;
            lvalue += (arr[i] & 0xff);
        }
        value = lvalue;
        logger.config("Read NumberFixedlength:" + value);
    }


    /**
     * @return String representation of this datatype
     */
    public String toString() {
        if (value == null) {
            return "";
        } else {
            return value.toString();
        }
    }

    /**
     * Write data to byte array
     *
     * @return the datatype converted to a byte array
     */
    public byte[] writeByteArray() {
        byte[] arr;
        arr = new byte[size];
        if (value != null) {
            //Convert value to long
            long temp = ID3Tags.getWholeNumber(value);

            for (int i = size - 1; i >= 0; i--) {
                arr[i] = (byte) (temp & 0xFF);
                temp >>= 8;
            }
        }
        return arr;
    }
}
