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

import java.util.Iterator;
import java.util.Map;

/**
 * Represents an interface allowing maping from key to value and value to key
 */
public interface HashMapInterface<K, V> {
    /**
     * @return a mapping between the key within the frame and the value
     */
    public Map<K, V> getKeyToValue();

    /**
     * @return a mapping between the value to the key within the frame
     */
    public Map<V, K> getValueToKey();

    /**
     * @return an interator of the values within the map
     */
    public Iterator<V> iterator();
}
