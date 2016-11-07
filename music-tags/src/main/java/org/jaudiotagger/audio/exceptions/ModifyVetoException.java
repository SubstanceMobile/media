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
 * This exception is thrown if a
 * {@link org.jaudiotagger.audio.generic.AudioFileModificationListener} wants to
 * prevent; from actually finishing its
 * operation.<br>
 * This exception can be used in all methods but
 * {@link org.jaudiotagger.audio.generic.AudioFileModificationListener#fileOperationFinished(java.io.File)}.
 *
 * @author Christian Laireiter
 */
public class ModifyVetoException extends Exception {

    /**
     * (overridden)
     */
    public ModifyVetoException() {
        super();
    }

    /**
     * (overridden)
     *
     * @param message
     * @see Exception#Exception(java.lang.String)
     */
    public ModifyVetoException(String message) {
        super(message);
    }

    /**
     * (overridden)
     *
     * @param message
     * @param cause
     * @see Exception#Exception(java.lang.String, java.lang.Throwable)
     */
    public ModifyVetoException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * (overridden)
     *
     * @param cause
     * @see Exception#Exception(java.lang.Throwable)
     */
    public ModifyVetoException(Throwable cause) {
        super(cause);
    }

}
