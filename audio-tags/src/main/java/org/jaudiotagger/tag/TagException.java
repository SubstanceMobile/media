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
 * This is the exception that is always generated by any class in these
 * packages.
 *
 * @author Eric Farng
 * @version $Revision$
 */
public class TagException extends Exception {
    /**
     * Creates a new TagException datatype.
     */
    public TagException() {
    }

    /**
     * Creates a new TagException datatype.
     *
     * @param ex the cause.
     */
    public TagException(Throwable ex) {
        super(ex);
    }

    /**
     * Creates a new TagException datatype.
     *
     * @param msg the detail message.
     */
    public TagException(String msg) {
        super(msg);
    }

    /**
     * Creates a new TagException datatype.
     *
     * @param msg the detail message.
     * @param ex  the cause.
     */
    public TagException(String msg, Throwable ex) {
        super(msg, ex);
    }
}