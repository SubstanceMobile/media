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
package org.jaudiotagger.audio.exceptions;

/**
 * This exception is thrown if the writing process of an audio file failed.
 *
 * @author Rapha�l Slinckx
 */
public class CannotWriteException extends Exception {
    /**
     * (overridden)
     *
     * @see Exception#Exception()
     */
    public CannotWriteException() {
        super();
    }

    /**
     * (overridden)
     *
     * @param message
     * @see Exception#Exception(java.lang.String)
     */
    public CannotWriteException(String message) {
        super(message);
    }

    /**
     * (overridden)
     *
     * @param message
     * @param cause
     * @see Exception#Exception(java.lang.String, java.lang.Throwable)
     */
    public CannotWriteException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * (overridden)
     *
     * @param cause
     * @see Exception#Exception(java.lang.Throwable)
     */
    public CannotWriteException(Throwable cause) {
        super(cause);

    }

}
