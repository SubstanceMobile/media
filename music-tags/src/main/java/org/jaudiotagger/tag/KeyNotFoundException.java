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

package org.jaudiotagger.tag;

/**
 * Thrown if the key cannot be found
 * <p>
 * <p>Should not happen with well written code, hence RuntimeException.
 */
public class KeyNotFoundException extends RuntimeException {
    /**
     * Creates a new KeyNotFoundException datatype.
     */
    public KeyNotFoundException() {
    }

    /**
     * Creates a new KeyNotFoundException datatype.
     *
     * @param ex the cause.
     */
    public KeyNotFoundException(Throwable ex) {
        super(ex);
    }

    /**
     * Creates a new KeyNotFoundException datatype.
     *
     * @param msg the detail message.
     */
    public KeyNotFoundException(String msg) {
        super(msg);
    }

    /**
     * Creates a new KeyNotFoundException datatype.
     *
     * @param msg the detail message.
     * @param ex  the cause.
     */
    public KeyNotFoundException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
