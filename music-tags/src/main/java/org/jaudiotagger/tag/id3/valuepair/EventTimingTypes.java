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

public class EventTimingTypes extends AbstractIntStringValuePair {
    private static EventTimingTypes eventTimingTypes;

    public static EventTimingTypes getInstanceOf() {
        if (eventTimingTypes == null) {
            eventTimingTypes = new EventTimingTypes();
        }
        return eventTimingTypes;
    }

    private EventTimingTypes() {
        idToValue.put(0x00, "Padding (has no meaning)");
        idToValue.put(0x01, "End of initial silence");
        idToValue.put(0x02, "Intro start");
        idToValue.put(0x03, "Main part start");
        idToValue.put(0x04, "Outro start");
        idToValue.put(0x05, "Outro end");
        idToValue.put(0x06, "Verse start");
        idToValue.put(0x07, "Refrain start");
        idToValue.put(0x08, "Interlude start");
        idToValue.put(0x09, "Theme start");
        idToValue.put(0x0A, "Variation start");
        idToValue.put(0x0B, "Key change");
        idToValue.put(0x0C, "Time change");
        idToValue.put(0x0D, "Momentary unwanted noise (Snap, Crackle & Pop)");
        idToValue.put(0x0E, "Sustained noise");
        idToValue.put(0x0F, "Sustained noise end");
        idToValue.put(0x10, "Intro end");
        idToValue.put(0x11, "Main part end");
        idToValue.put(0x12, "Verse end");
        idToValue.put(0x13, "Refrain end");
        idToValue.put(0x14, "Theme end");
        idToValue.put(0x15, "Profanity");
        idToValue.put(0x16, "Profanity end");

        // 0x17-0xDF  reserved for future use

        idToValue.put(0xE0, "Not predefined synch 0");
        idToValue.put(0xE1, "Not predefined synch 1");
        idToValue.put(0xE2, "Not predefined synch 2");
        idToValue.put(0xE3, "Not predefined synch 3");
        idToValue.put(0xE4, "Not predefined synch 4");
        idToValue.put(0xE5, "Not predefined synch 5");
        idToValue.put(0xE6, "Not predefined synch 6");
        idToValue.put(0xE7, "Not predefined synch 7");
        idToValue.put(0xE8, "Not predefined synch 8");
        idToValue.put(0xE9, "Not predefined synch 9");
        idToValue.put(0xEA, "Not predefined synch A");
        idToValue.put(0xEB, "Not predefined synch B");
        idToValue.put(0xEC, "Not predefined synch C");
        idToValue.put(0xED, "Not predefined synch D");
        idToValue.put(0xEE, "Not predefined synch E");
        idToValue.put(0xEF, "Not predefined synch F");

        // 0xF0-0xFC  reserved for future use

        idToValue.put(0xFD, "Audio end (start of silence)");
        idToValue.put(0xFE, "Audio file ends");

        createMaps();
    }
}