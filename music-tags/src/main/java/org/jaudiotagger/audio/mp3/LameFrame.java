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
package org.jaudiotagger.audio.mp3;

import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.tag.id3.valuepair.TextEncoding;

import java.nio.ByteBuffer;

/**
 * The first frame can sometimes contain a LAME frame at the end of the Xing frame
 * <p>
 * <p>This useful to the library because it allows the encoder to be identified, full specification
 * can be found at http://gabriel.mp3-tech.org/mp3infotag.html
 * <p>
 * Summarized here:
 * 4 bytes:LAME
 * 5 bytes:LAME Encoder Version
 * 1 bytes:VNR Method
 * 1 bytes:Lowpass filter value
 * 8 bytes:Replay Gain
 * 1 byte:Encoding Flags
 * 1 byte:minimal byte rate
 * 3 bytes:extra samples
 * 1 byte:Stereo Mode
 * 1 byte:MP3 Gain
 * 2 bytes:Surround Dound
 * 4 bytes:MusicLength
 * 2 bytes:Music CRC
 * 2 bytes:CRC Tag
 */
public class LameFrame {
    public static final int LAME_HEADER_BUFFER_SIZE = 36;
    public static final int ENCODER_SIZE = 9;   //Includes LAME ID
    public static final int LAME_ID_SIZE = 4;
    public static final String LAME_ID = "LAME";
    private String encoder;

    /**
     * Initilise a Lame Mpeg Frame
     *
     * @param lameHeader
     */
    private LameFrame(ByteBuffer lameHeader) {
        encoder = Utils.getString(lameHeader, 0, ENCODER_SIZE, TextEncoding.CHARSET_ISO_8859_1);
    }

    /**
     * Parse frame
     *
     * @param bb
     * @return frame or null if not exists
     */
    public static LameFrame parseLameFrame(ByteBuffer bb) {
        ByteBuffer lameHeader = bb.slice();
        String id = Utils.getString(lameHeader, 0, LAME_ID_SIZE, TextEncoding.CHARSET_ISO_8859_1);
        lameHeader.rewind();
        if (id.equals(LAME_ID)) {
            LameFrame lameFrame = new LameFrame(lameHeader);
            return lameFrame;
        }
        return null;
    }

    /**
     * @return encoder
     */
    public String getEncoder() {
        return encoder;
    }
}

