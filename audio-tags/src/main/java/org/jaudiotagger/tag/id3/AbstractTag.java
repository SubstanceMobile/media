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
package org.jaudiotagger.tag.id3;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * A tag is term given to a container that holds audio metadata
 */
public abstract class AbstractTag extends AbstractTagItem {
    protected static final String TYPE_TAG = "tag";


    public AbstractTag() {
    }

    public AbstractTag(AbstractTag copyObject) {
        super(copyObject);
    }

    /**
     * Looks for this tag in the buffer
     *
     * @param byteBuffer
     * @return returns true if found, false otherwise.
     */
    abstract public boolean seek(ByteBuffer byteBuffer);

    /**
     * Writes the tag to the file
     *
     * @param file
     * @throws IOException
     */
    public abstract void write(RandomAccessFile file) throws IOException;


    /**
     * Removes the specific tag from the file
     *
     * @param file MP3 file to append to.
     * @throws IOException on any I/O error
     */
    abstract public void delete(RandomAccessFile file) throws IOException;


    /**
     * Determines whether another datatype is equal to this tag. It just compares
     * if they are the same class, then calls <code>super.equals(obj)</code>.
     *
     * @param obj The object to compare
     * @return if they are equal
     */
    public boolean equals(Object obj) {
        return (obj instanceof AbstractTag) && super.equals(obj);

    }

    /**
     * @return
     */
    abstract public Iterator iterator();
}



