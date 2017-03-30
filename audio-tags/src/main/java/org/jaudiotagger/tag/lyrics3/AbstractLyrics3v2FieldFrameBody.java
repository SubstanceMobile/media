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
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jaudiotagger.tag.datatype.AbstractDataType;
import org.jaudiotagger.tag.id3.AbstractTagFrameBody;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Iterator;

public abstract class AbstractLyrics3v2FieldFrameBody extends AbstractTagFrameBody {
    public AbstractLyrics3v2FieldFrameBody() {
    }

    public AbstractLyrics3v2FieldFrameBody(AbstractLyrics3v2FieldFrameBody copyObject) {
        super(copyObject);
    }

    /**
     * This is called by superclass when attempt to read data from file.
     *
     * @param file
     * @return
     * @throws InvalidTagException
     * @throws IOException
     */
    protected int readHeader(RandomAccessFile file) throws InvalidTagException, IOException {
        int size;
        byte[] buffer = new byte[5];

        // read the 5 character size
        file.read(buffer, 0, 5);
        size = Integer.parseInt(new String(buffer, 0, 5));

        if ((size == 0) && (!TagOptionSingleton.getInstance().isLyrics3KeepEmptyFieldIfRead())) {
            throw new InvalidTagException("Lyircs3v2 Field has size of zero.");
        }

        return size;
    }

    /**
     * This is called by superclass when attempt to write data from file.
     *
     * @param file
     * @param size
     * @throws IOException
     */
    protected void writeHeader(RandomAccessFile file, int size) throws IOException {
        String str;
        int offset = 0;
        byte[] buffer = new byte[5];

        /**
         * @todo change this to use pad String
         */
        str = Integer.toString(getSize());

        for (int i = 0; i < (5 - str.length()); i++) {
            buffer[i] = (byte) '0';
        }

        offset += (5 - str.length());

        for (int i = 0; i < str.length(); i++) {
            buffer[i + offset] = (byte) str.charAt(i);
        }

        file.write(buffer);
    }

    /**
     * This reads a frame body from its file into the appropriate FrameBody class
     * Read the data from the given file into this datatype. The file needs to
     * have its file pointer in the correct location. The size as indicated in the
     * header is passed to the frame constructor when reading from file.
     *
     * @param byteBuffer file to read
     * @throws IOException         on any I/O error
     * @throws InvalidTagException if there is any error in the data format.
     */
    public void read(ByteBuffer byteBuffer) throws InvalidTagException {
        int size = getSize();
        //Allocate a buffer to the size of the Frame Body and read from file
        byte[] buffer = new byte[size];
        byteBuffer.get(buffer);
        //Offset into buffer, incremented by length of previous MP3Object
        int offset = 0;

        //Go through the ObjectList of the Frame reading the data into the
        //correct datatype.
        AbstractDataType object;
        Iterator<AbstractDataType> iterator = objectList.listIterator();
        while (iterator.hasNext()) {
            //The read has extended further than the defined frame size
            if (offset > (size - 1)) {
                throw new InvalidTagException("Invalid size for Frame Body");
            }

            //Get next Object and load it with data from the Buffer
            object = iterator.next();
            object.readByteArray(buffer, offset);
            //Increment Offset to start of next datatype.
            offset += object.getSize();
        }
    }

    /**
     * Write the contents of this datatype to the file at the position it is
     * currently at.
     *
     * @param file destination file
     * @throws IOException on any I/O error
     */
    public void write(RandomAccessFile file) throws IOException {
        //Write the various fields to file in order
        byte[] buffer;
        AbstractDataType object;
        Iterator<AbstractDataType> iterator = objectList.listIterator();
        while (iterator.hasNext()) {
            object = iterator.next();
            buffer = object.writeByteArray();
            file.write(buffer);
        }
    }

}
