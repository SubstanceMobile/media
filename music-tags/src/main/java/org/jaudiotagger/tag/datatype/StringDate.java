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
import org.jaudiotagger.tag.id3.ID3Tags;

/**
 * Represents a timestamp field
 */
public class StringDate extends StringFixedLength {
    /**
     * Creates a new ObjectStringDate datatype.
     *
     * @param identifier
     * @param frameBody
     */
    public StringDate(String identifier, AbstractTagFrameBody frameBody) {
        super(identifier, frameBody, 8);
    }

    public StringDate(StringDate object) {
        super(object);
    }

    /**
     * @param value
     */
    public void setValue(Object value) {
        if (value != null) {
            this.value = ID3Tags.stripChar(value.toString(), '-');
        }
    }

    /**
     * @return
     */
    public Object getValue() {
        if (value != null) {
            return ID3Tags.stripChar(value.toString(), '-');
        } else {
            return null;
        }
    }

    public boolean equals(Object obj) {
        return obj instanceof StringDate && super.equals(obj);

    }
}
