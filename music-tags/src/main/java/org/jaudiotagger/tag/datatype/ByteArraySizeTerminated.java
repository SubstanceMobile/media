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
 * Represents a stream of bytes, continuing until the end of the buffer. Usually used for binary data or where
 * we havent yet mapped the data to a better fitting type.
 */
public class ByteArraySizeTerminated extends AbstractDataType {
    public ByteArraySizeTerminated(String identifier, AbstractTagFrameBody frameBody) {
        super(identifier, frameBody);
    }

    public ByteArraySizeTerminated(ByteArraySizeTerminated object) {
        super(object);
    }

    /**
     * Return the size in byte of this datatype
     *
     * @return the size in bytes
     */
    public int getSize() {
        int len = 0;

        if (value != null) {
            len = ((byte[]) value).length;
        }

        return len;
    }

    public boolean equals(Object obj) {
        return obj instanceof ByteArraySizeTerminated && super.equals(obj);

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

        if (offset < 0) {
            throw new IndexOutOfBoundsException("Offset to byte array is out of bounds: offset = " + offset + ", array.length = " + arr.length);
        }

        //Empty Byte Array
        if (offset >= arr.length) {
            value = null;
            return;
        }

        int len = arr.length - offset;
        value = new byte[len];
        System.arraycopy(arr, offset, value, 0, len);
    }

    /**
     * Because this is usually binary data and could be very long we just return
     * the number of bytes held
     *
     * @return the number of bytes
     */
    public String toString() {
        return getSize() + " bytes";
    }

    /**
     * Write contents to a byte array
     *
     * @return a byte array that that contians the data that should be perisisted to file
     */
    public byte[] writeByteArray() {
        logger.config("Writing byte array" + this.getIdentifier());
        return (byte[]) value;
    }
}
