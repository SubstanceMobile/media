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
 * This is the exception when try and access a read only file
 */
public class ReadOnlyFileException extends Exception {
    /**
     * Creates a new ReadOnlyException datatype.
     */
    public ReadOnlyFileException() {
    }

    public ReadOnlyFileException(Throwable ex) {
        super(ex);
    }

    /**
     * Creates a new ReadOnlyException datatype.
     *
     * @param msg the detail message.
     */
    public ReadOnlyFileException(String msg) {
        super(msg);
    }

    public ReadOnlyFileException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
