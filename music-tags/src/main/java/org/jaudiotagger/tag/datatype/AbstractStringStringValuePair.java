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

import java.util.Collections;

public class AbstractStringStringValuePair extends AbstractValuePair<String, String> {
    protected String lkey = null;

    /**
     * Get Id for Value
     * @param value
     * @return
     */
    public String getIdForValue(String value) {
        return valueToId.get(value);
    }

    /**
     * Get value for Id
     * @param id
     * @return
     */
    public String getValueForId(String id) {
        return idToValue.get(id);
    }

    protected void createMaps() {
        iterator = idToValue.keySet().iterator();
        while (iterator.hasNext()) {
            lkey = iterator.next();
            value = idToValue.get(lkey);
            valueToId.put(value, lkey);
        }

        //Value List
        iterator = idToValue.keySet().iterator();
        while (iterator.hasNext()) {
            valueList.add(idToValue.get(iterator.next()));
        }
        //Sort alphabetically
        Collections.sort(valueList);
    }
}
