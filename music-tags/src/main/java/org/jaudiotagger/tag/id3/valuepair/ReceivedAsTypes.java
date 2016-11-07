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
package org.jaudiotagger.tag.id3.valuepair;

import org.jaudiotagger.tag.datatype.AbstractIntStringValuePair;

/**
 * Defines how song was purchased used by the COMR frame
 *
 */
public class ReceivedAsTypes extends AbstractIntStringValuePair {
    //The number of bytes used to hold the text encoding field size
    public static final int RECEIVED_AS_FIELD_SIZE = 1;

    private static ReceivedAsTypes receivedAsTypes;

    public static ReceivedAsTypes getInstanceOf() {
        if (receivedAsTypes == null) {
            receivedAsTypes = new ReceivedAsTypes();
        }
        return receivedAsTypes;
    }

    private ReceivedAsTypes() {
        idToValue.put(0x00, "Other");
        idToValue.put(0x01, "Standard CD album with other songs");
        idToValue.put(0x02, "Compressed audio on CD");
        idToValue.put(0x03, "File over the Internet");
        idToValue.put(0x04, "Stream over the Internet");
        idToValue.put(0x05, "As note sheets");
        idToValue.put(0x06, "As note sheets in a book with other sheets");
        idToValue.put(0x07, "Music on other media");
        idToValue.put(0x08, "Non-musical merchandise");
        createMaps();
    }
}
