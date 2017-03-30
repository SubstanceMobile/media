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
 * <p>The 'Tagging time' frame contains a timestamp describing then the
 *  audio was tagged. Timestamp format is described in the ID3v2
 *  structure document
 */
public class FrameBodyTDTG extends AbstractFrameBodyTextInfo implements ID3v24FrameBody {

    /**
     * Creates a new FrameBodyTDTG datatype.
     */
    public FrameBodyTDTG() {
    }

    public FrameBodyTDTG(FrameBodyTDTG body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTDTG datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTDTG(byte textEncoding, String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTDTG datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws java.io.IOException
     * @throws InvalidTagException
     */
    public FrameBodyTDTG(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * @return the frame identifier
     */
    public String getIdentifier() {
        return ID3v24Frames.FRAME_ID_TAGGING_TIME;
    }


}
