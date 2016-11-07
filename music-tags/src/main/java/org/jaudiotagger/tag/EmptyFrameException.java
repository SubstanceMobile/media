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
 * Thrown when find a Frame but it contains no data.
 *
 * @version $Revision$
 */
public class EmptyFrameException extends InvalidFrameException {
    /**
     * Creates a new EmptyFrameException datatype.
     */
    public EmptyFrameException() {
    }

    /**
     * Creates a new EmptyFrameException datatype.
     *
     * @param ex the cause.
     */
    public EmptyFrameException(Throwable ex) {
        super(ex);
    }

    /**
     * Creates a new EmptyFrameException datatype.
     *
     * @param msg the detail message.
     */
    public EmptyFrameException(String msg) {
        super(msg);
    }

    /**
     * Creates a new EmptyFrameException datatype.
     *
     * @param msg the detail message.
     * @param ex  the cause.
     */
    public EmptyFrameException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
