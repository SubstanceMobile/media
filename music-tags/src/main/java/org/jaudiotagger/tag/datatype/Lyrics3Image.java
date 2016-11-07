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

import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.tag.InvalidDataTypeException;
import org.jaudiotagger.tag.id3.AbstractTagFrameBody;

public class Lyrics3Image extends AbstractDataType {
    /**
     *
     */
    private Lyrics3TimeStamp time = null;

    /**
     *
     */
    private String description = "";

    /**
     *
     */
    private String filename = "";

    /**
     * Creates a new ObjectLyrics3Image datatype.
     *
     * @param identifier
     * @param frameBody
     */
    public Lyrics3Image(String identifier, AbstractTagFrameBody frameBody) {
        super(identifier, frameBody);
    }

    public Lyrics3Image(Lyrics3Image copy) {
        super(copy);
        this.time = new Lyrics3TimeStamp(copy.time);
        this.description = copy.description;
        this.filename = copy.filename;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return
     */
    public String getFilename() {
        return this.filename;
    }

    /**
     * @return
     */
    public int getSize() {
        int size;

        size = filename.length() + 2 + description.length() + 2;

        if (time != null) {
            size += time.getSize();
        }

        return size;
    }

    /**
     * @param time
     */
    public void setTimeStamp(Lyrics3TimeStamp time) {
        this.time = time;
    }

    /**
     * @return
     */
    public Lyrics3TimeStamp getTimeStamp() {
        return this.time;
    }

    /**
     * @param obj
     * @return
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof Lyrics3Image)) {
            return false;
        }

        Lyrics3Image object = (Lyrics3Image) obj;

        if (!this.description.equals(object.description)) {
            return false;
        }

        if (!this.filename.equals(object.filename)) {
            return false;
        }

        if (this.time == null) {
            if (object.time != null) {
                return false;
            }
        } else {
            if (!this.time.equals(object.time)) {
                return false;
            }
        }

        return super.equals(obj);
    }

    /**
     * @param imageString
     * @param offset
     * @throws NullPointerException
     * @throws IndexOutOfBoundsException
     */
    public void readString(String imageString, int offset) {
        if (imageString == null) {
            throw new NullPointerException("Image string is null");
        }

        if ((offset < 0) || (offset >= imageString.length())) {
            throw new IndexOutOfBoundsException("Offset to image string is out of bounds: offset = " + offset + ", string.length()" + imageString.length());
        }

        if (imageString != null) {
            String timestamp;
            int delim;

            delim = imageString.indexOf("||", offset);
            filename = imageString.substring(offset, delim);

            offset = delim + 2;
            delim = imageString.indexOf("||", offset);
            description = imageString.substring(offset, delim);

            offset = delim + 2;
            timestamp = imageString.substring(offset);

            if (timestamp.length() == 7) {
                time = new Lyrics3TimeStamp("Time Stamp");
                time.readString(timestamp);
            }
        }
    }

    /**
     * @return
     */
    public String toString() {
        String str;
        str = "filename = " + filename + ", description = " + description;

        if (time != null) {
            str += (", timestamp = " + time.toString());
        }

        return str + "\n";
    }

    /**
     * @return
     */
    public String writeString() {
        String str;

        if (filename == null) {
            str = "||";
        } else {
            str = filename + "||";
        }

        if (description == null) {
            str += "||";
        } else {
            str += (description + "||");
        }

        if (time != null) {
            str += time.writeString();
        }

        return str;
    }

    public void readByteArray(byte[] arr, int offset) throws InvalidDataTypeException {
        readString(arr.toString(), offset);
    }

    public byte[] writeByteArray() {
        return Utils.getDefaultBytes(writeString(), "ISO-8859-1");
    }

}
