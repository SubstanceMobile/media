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


/**
 * Represents a timestamp field
 */
public class StringDateTime extends StringSizeTerminated {
    /**
     * Creates a new ObjectStringDateTime datatype.
     *
     * @param identifier
     * @param frameBody
     */
    public StringDateTime(String identifier, AbstractTagFrameBody frameBody) {
        super(identifier, frameBody);
    }

    public StringDateTime(StringDateTime object) {
        super(object);
    }

    /**
     * @param value
     */
    public void setValue(Object value) {
        if (value != null) {
            this.value = value.toString().replace(' ', 'T');
        }
    }

    /**
     * @return
     */
    public Object getValue() {
        if (value != null) {
            return value.toString().replace(' ', 'T');
        } else {
            return null;
        }
    }

    public boolean equals(Object obj) {
        return obj instanceof StringDateTime && super.equals(obj);

    }
}
