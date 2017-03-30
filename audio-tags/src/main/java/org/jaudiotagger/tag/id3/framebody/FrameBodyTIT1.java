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
 * Content group description Text information frame.
 * <p>The 'Content group description' frame is used if the sound belongs to a larger category of sounds/music.
 * For example, classical music is often sorted in different musical sections (e.g. "Piano Concerto", "Weather - Hurricane").
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
public class FrameBodyTIT1 extends AbstractFrameBodyTextInfo implements ID3v24FrameBody, ID3v23FrameBody {
    /**
     * Creates a new FrameBodyTIT1 datatype.
     */
    public FrameBodyTIT1() {
    }

    public FrameBodyTIT1(FrameBodyTIT1 body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTIT1 datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTIT1(byte textEncoding, String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTIT1 datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTIT1(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v24Frames.FRAME_ID_CONTENT_GROUP_DESC;
    }
}