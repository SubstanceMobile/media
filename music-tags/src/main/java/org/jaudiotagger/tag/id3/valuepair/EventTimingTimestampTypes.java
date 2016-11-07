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

public class EventTimingTimestampTypes extends AbstractIntStringValuePair {

    private static EventTimingTimestampTypes eventTimingTimestampTypes;

    public static EventTimingTimestampTypes getInstanceOf() {
        if (eventTimingTimestampTypes == null) {
            eventTimingTimestampTypes = new EventTimingTimestampTypes();
        }
        return eventTimingTimestampTypes;
    }

    public static final int TIMESTAMP_KEY_FIELD_SIZE = 1;

    private EventTimingTimestampTypes() {
        idToValue.put(1, "Absolute time using MPEG [MPEG] frames as unit");
        idToValue.put(2, "Absolute time using milliseconds as unit");

        createMaps();
    }
}
