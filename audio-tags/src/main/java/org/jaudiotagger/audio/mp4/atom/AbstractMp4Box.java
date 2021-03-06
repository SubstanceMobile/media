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

package org.jaudiotagger.audio.mp4.atom;

import java.nio.ByteBuffer;

/**
 * Abstract mp4 box, contain a header and then rawdata (which may include child boxes)
 */
public class AbstractMp4Box {
    protected Mp4BoxHeader header;
    protected ByteBuffer dataBuffer;

    /**
     * @return the box header
     */
    public Mp4BoxHeader getHeader() {
        return header;
    }

    /**
     * @return rawdata of this box
     */
    public ByteBuffer getData() {
        return dataBuffer;
    }


}
