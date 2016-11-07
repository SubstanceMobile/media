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

package org.jaudiotagger.audio.asf.data;

/**
 * This exception is used when a string was about to be interpreted as a GUID,
 * but did not match the format.<br>
 *
 * @author Christian Laireiter
 */
public class GUIDFormatException extends IllegalArgumentException {

    /**
     *
     */
    private static final long serialVersionUID = 6035645678612384953L;

    /**
     * Creates an instance.
     *
     * @param detail detail message.
     */
    public GUIDFormatException(final String detail) {
        super(detail);
    }
}
