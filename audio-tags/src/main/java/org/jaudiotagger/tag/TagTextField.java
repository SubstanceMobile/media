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
 * This interface extends the default field definition by methods for working
 * with human readable text.<br>
 * A TagTextField does not store binary data.
 *
 * @author Rapha�l Slinckx
 */
public interface TagTextField extends TagField {

    /**
     * Returns the content of the field.
     *
     * @return Content
     */
    public String getContent();

    /**
     * Returns the current used charset encoding.
     *
     * @return Charset encoding.
     */
    public String getEncoding();

    /**
     * Sets the content of the field.
     *
     * @param content fields content.
     */
    public void setContent(String content);

    /**
     * Sets the charset encoding used by the field.
     *
     * @param encoding charset.
     */
    public void setEncoding(String encoding);
}