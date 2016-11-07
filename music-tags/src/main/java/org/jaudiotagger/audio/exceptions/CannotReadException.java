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
 * This exception is thrown if an audio file cannot be read.<br>
 * Causes may be invalid data or IO errors.
 *
 * @author Raphaël Slinckx
 */
public class CannotReadException extends Exception {
    /**
     * Creates an instance.
     */
    public CannotReadException() {
        super();
    }

    public CannotReadException(Throwable ex) {
        super(ex);
    }

    /**
     * Creates an instance.
     *
     * @param message The message.
     */
    public CannotReadException(String message) {
        super(message);
    }

    /**
     * Creates an instance.
     *
     * @param message The error message.
     * @param cause   The throwable causing this exception.
     */
    public CannotReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
