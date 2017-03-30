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
 * Publishers official webpage URL link frames.
 * <p>The 'Publishers official webpage' frame is a URL pointing at the official wepage for the publisher.
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
public class FrameBodyWPUB extends AbstractFrameBodyUrlLink implements ID3v24FrameBody, ID3v23FrameBody {
    /**
     * Creates a new FrameBodyWPUB datatype.
     */
    public FrameBodyWPUB() {
    }

    /**
     * Creates a new FrameBodyWPUB datatype.
     *
     * @param urlLink
     */
    public FrameBodyWPUB(String urlLink) {
        super(urlLink);
    }

    public FrameBodyWPUB(FrameBodyWPUB body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyWPUB datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws java.io.IOException
     * @throws InvalidTagException
     */
    public FrameBodyWPUB(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v24Frames.FRAME_ID_URL_PUBLISHERS;
    }
}