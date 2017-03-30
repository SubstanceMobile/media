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
import org.jaudiotagger.tag.id3.ID3v23Frames;

import java.nio.ByteBuffer;

/**
 * Size Text information frame.
 * <p>The 'Size' frame contains the size of the audiofile in bytes, excluding the ID3v2 tag, represented as a numeric string.
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
public class FrameBodyTSIZ extends AbstractFrameBodyTextInfo implements ID3v23FrameBody {
    /**
     * Creates a new FrameBodyTSIZ datatype.
     */
    public FrameBodyTSIZ() {
    }

    public FrameBodyTSIZ(FrameBodyTSIZ body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTSIZ datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTSIZ(byte textEncoding, String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTSIZ datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTSIZ(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v23Frames.FRAME_ID_V3_TSIZ;
    }
}