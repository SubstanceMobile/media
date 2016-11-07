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

import java.util.*;

/**
 * A two way mapping between an id and a value
 */
public abstract class AbstractValuePair<I, V> {
    protected final Map<I, V> idToValue = new LinkedHashMap<I, V>();
    protected final Map<V, I> valueToId = new LinkedHashMap<V, I>();
    protected final List<V> valueList = new ArrayList<V>();

    protected Iterator<I> iterator = idToValue.keySet().iterator();

    protected String value;

    /**
     * Get list in alphabetical order
     * @return
     */
    public List<V> getAlphabeticalValueList() {
        return valueList;
    }

    public Map<I, V> getIdToValueMap() {
        return idToValue;
    }

    public Map<V, I> getValueToIdMap() {
        return valueToId;
    }

    /**
     * @return the number of elements in the mapping
     */
    public int getSize() {
        return valueList.size();
    }
}
