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
 * Represents a String whose size is determined by finding of a null character at the end of the String with fixed text encoding.
 *
 * The String will be encoded using the default encoding regardless of what encoding may be specified in the framebody
 */
public class StringNullTerminated extends TextEncodedStringNullTerminated {
    /**
     * Creates a new ObjectStringNullTerminated datatype.
     *
     * @param identifier identifies the frame type
     * @param frameBody
     */
    public StringNullTerminated(String identifier, AbstractTagFrameBody frameBody) {
        super(identifier, frameBody);
    }

    public StringNullTerminated(StringNullTerminated object) {
        super(object);
    }

    public boolean equals(Object obj) {
        return obj instanceof StringNullTerminated && super.equals(obj);
    }

    protected String getTextEncodingCharSet() {
        return TextEncoding.CHARSET_ISO_8859_1;
    }
}
