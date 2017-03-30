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
package org.jaudiotagger.tag.id3.framebody;

import org.jaudiotagger.tag.InvalidTagException;
import org.jaudiotagger.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Composer Text information frame.
 * <p>The 'Composer(s)' frame is intended for the name of the composer(s).
 * They are separated with the "/" character in ID3v23.
 * Null separated as is the norm in ID3v24
 * <p>
 * <p>
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 *
 * @author : Paul Taylor
 * @author : Eric Farng
 * @version $Id$
 */
public class FrameBodyTCOM extends AbstractFrameBodyTextInfo implements ID3v24FrameBody, ID3v23FrameBody {
    /**
     * Creates a new FrameBodyTCOM datatype.
     */
    public FrameBodyTCOM() {
    }

    public FrameBodyTCOM(FrameBodyTCOM body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTCOM datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTCOM(byte textEncoding, String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTCOM datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    public FrameBodyTCOM(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }


    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v24Frames.FRAME_ID_COMPOSER;
    }
}
