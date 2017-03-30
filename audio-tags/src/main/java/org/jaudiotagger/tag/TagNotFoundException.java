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
 * Thrown if the tag o isn't found. This is different from
 * the <code>InvalidTagException</code>. Each tag  has an
 * ID string or some way saying that it simply exists. If this string is
 * missing, <code>TagNotFoundException</code> is thrown. If the ID string
 * exists, then any other error while reading throws an
 * <code>InvalidTagException</code>.
 *
 * @author Eric Farng
 * @version $Revision$
 */
public class TagNotFoundException extends TagException {
    /**
     * Creates a new TagNotFoundException datatype.
     */
    public TagNotFoundException() {
    }

    /**
     * Creates a new TagNotFoundException datatype.
     *
     * @param ex the cause.
     */
    public TagNotFoundException(Throwable ex) {
        super(ex);
    }

    /**
     * Creates a new TagNotFoundException datatype.
     *
     * @param msg the detail message.
     */
    public TagNotFoundException(String msg) {
        super(msg);
    }

    /**
     * Creates a new TagNotFoundException datatype.
     *
     * @param msg the detail message.
     * @param ex  the cause.
     */
    public TagNotFoundException(String msg, Throwable ex) {
        super(msg, ex);
    }
}