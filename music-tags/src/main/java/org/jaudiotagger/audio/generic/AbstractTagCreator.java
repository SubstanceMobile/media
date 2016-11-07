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
package org.jaudiotagger.audio.generic;

import org.jaudiotagger.tag.Tag;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Abstract class for creating the raw content that represents the tag so it can be written
 * to file.
 */
public abstract class AbstractTagCreator {
    /**
     * Convert tagdata to rawdata ready for writing to file with no additional padding
     *
     * @param tag
     * @return
     * @throws UnsupportedEncodingException
     */
    public ByteBuffer convert(Tag tag) throws UnsupportedEncodingException {
        return convert(tag, 0);
    }

    /**
     * Convert tagdata to rawdata ready for writing to file
     *
     * @param tag
     * @param padding TODO is this padding or additional padding
     * @return
     * @throws UnsupportedEncodingException
     */
    public abstract ByteBuffer convert(Tag tag, int padding) throws UnsupportedEncodingException;
}
