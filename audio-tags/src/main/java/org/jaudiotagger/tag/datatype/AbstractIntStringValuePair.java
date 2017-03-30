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
import java.util.Map;

/**
 * A two way mapping between an Integral Id and a String value
 */
public class AbstractIntStringValuePair extends AbstractValuePair<Integer, String> {
    protected Integer key = null;

    /**
     * Get Id for Value
     * @param value
     * @return
     */
    public Integer getIdForValue(String value) {
        return valueToId.get(value);
    }

    /**
     * Get value for Id
     * @param id
     * @return
     */
    public String getValueForId(int id) {
        return idToValue.get(id);
    }

    protected void createMaps() {
        //Create the reverse the map
        for (Map.Entry<Integer, String> entry : idToValue.entrySet()) {
            valueToId.put(entry.getValue(), entry.getKey());
        }

        //Value List sort alphabetically
        valueList.addAll(idToValue.values());
        Collections.sort(valueList);
    }
}
