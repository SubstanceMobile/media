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

/**
 * Represents a String which is not delimited by null character with fixed text encoding.
 *
 * This type of String will usually only be used when it is the last field within a frame, when reading the remainder of the byte array will
 * be read, when writing the frame will accommodate the required size for the String. The String will be encoded
 * using the default encoding regardless of what encoding may be specified in the framebody
 */
public class StringSizeTerminated extends TextEncodedStringSizeTerminated {

    /**
     * Creates a new ObjectStringSizeTerminated datatype.
     *
     * @param identifier identifies the frame type
     * @param frameBody
     */
    public StringSizeTerminated(String identifier, AbstractTagFrameBody frameBody) {
        super(identifier, frameBody);
    }

    public StringSizeTerminated(StringSizeTerminated object) {
        super(object);
    }

    public boolean equals(Object obj) {
        return obj instanceof StringSizeTerminated && super.equals(obj);
    }

    protected String getTextEncodingCharSet() {
        return TextEncoding.CHARSET_ISO_8859_1;
    }
}
