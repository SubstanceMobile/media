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
 * Title Sort name, this is what MusicBrainz uses in ID3v23 because TSOT not supported.
 * <p>
 * However iTunes uses TSOT even in ID3v23, so we have two possible options
 */
public class FrameBodyXSOT extends AbstractFrameBodyTextInfo implements ID3v23FrameBody {
    /**
     * Creates a new FrameBodyTSOT datatype.
     */
    public FrameBodyXSOT() {
    }

    public FrameBodyXSOT(FrameBodyXSOT body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTSOT datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyXSOT(byte textEncoding, String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTSOT datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyXSOT(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v23Frames.FRAME_ID_V3_TITLE_SORT_ORDER_MUSICBRAINZ;
    }
}
