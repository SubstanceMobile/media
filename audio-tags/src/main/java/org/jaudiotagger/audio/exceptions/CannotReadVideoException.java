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
 * This exception should be thrown idf it appears the file is a video file, jaudiotagger only supports audio
 * files.
 */
public class CannotReadVideoException extends CannotReadException {
    /**
     * Creates an instance.
     */
    public CannotReadVideoException() {
        super();
    }

    public CannotReadVideoException(Throwable ex) {
        super(ex);
    }

    /**
     * Creates an instance.
     *
     * @param message The message.
     */
    public CannotReadVideoException(String message) {
        super(message);
    }

    /**
     * Creates an instance.
     *
     * @param message The error message.
     * @param cause   The throwable causing this exception.
     */
    public CannotReadVideoException(String message, Throwable cause) {
        super(message, cause);
    }
}
