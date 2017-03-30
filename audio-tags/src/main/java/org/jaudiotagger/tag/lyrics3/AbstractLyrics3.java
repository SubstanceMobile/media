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
package org.jaudiotagger.tag.lyrics3;

import org.jaudiotagger.tag.id3.AbstractTag;
import org.jaudiotagger.tag.id3.ID3v1Tag;

import java.io.IOException;
import java.io.RandomAccessFile;


public abstract class AbstractLyrics3 extends AbstractTag {
    public AbstractLyrics3() {
    }

    public AbstractLyrics3(AbstractLyrics3 copyObject) {
        super(copyObject);
    }

    /**
     * @param file
     * @throws IOException
     */
    public void delete(RandomAccessFile file) throws IOException {
        long filePointer;
        ID3v1Tag id3v1tag = new ID3v1Tag();


    }
}
