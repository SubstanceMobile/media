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

import org.jaudiotagger.audio.exceptions.CannotReadException;

import java.nio.ByteBuffer;

/**
 * StsdBox ( sample (frame encoding) description box)
 * <p>
 * <p>4 bytes version/flags = byte hex version + 24-bit hex flags
 * (current = 0)
 * 4 bytes number of descriptions = long unsigned total
 * (default = 1)
 * Then if audio contains mp4a,alac or drms box
 */
public class Mp4StsdBox extends AbstractMp4Box {
    public static final int VERSION_FLAG_POS = 0;
    public static final int OTHER_FLAG_POS = 1;
    public static final int NO_OF_DESCRIPTIONS_POS = 4;

    public static final int VERSION_FLAG_LENGTH = 1;
    public static final int OTHER_FLAG_LENGTH = 3;
    public static final int NO_OF_DESCRIPTIONS_POS_LENGTH = 4;

    /**
     * @param header     header info
     * @param dataBuffer data of box (doesnt include header data)
     */
    public Mp4StsdBox(Mp4BoxHeader header, ByteBuffer dataBuffer) {
        this.header = header;
        this.dataBuffer = dataBuffer;
    }

    public void processData() throws CannotReadException {
        //Skip the data
        dataBuffer.position(dataBuffer.position() + VERSION_FLAG_LENGTH + OTHER_FLAG_LENGTH + NO_OF_DESCRIPTIONS_POS_LENGTH);
    }
}
