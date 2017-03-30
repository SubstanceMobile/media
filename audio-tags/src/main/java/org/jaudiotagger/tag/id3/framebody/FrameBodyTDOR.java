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
import org.jaudiotagger.tag.datatype.DataTypes;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.jaudiotagger.tag.id3.valuepair.TextEncoding;

import java.nio.ByteBuffer;


/**
 *  <p>The 'Original release time' frame contains a timestamp describing
 *  when the original recording of the audio was released. Timestamp
 *  format is described in the ID3v2 structure document.
 */
public class FrameBodyTDOR extends AbstractFrameBodyTextInfo implements ID3v24FrameBody {
    /**
     * Creates a new FrameBodyTDOR datatype.
     */
    public FrameBodyTDOR() {
    }

    public FrameBodyTDOR(FrameBodyTDOR body) {
        super(body);
    }

    /**
     * When converting v3 TDAT to v4 TDRC frame
     * @param body
     */
    public FrameBodyTDOR(FrameBodyTORY body) {
        setObjectValue(DataTypes.OBJ_TEXT_ENCODING, TextEncoding.ISO_8859_1);
        setObjectValue(DataTypes.OBJ_TEXT, body.getText());
    }

    /**
     * Creates a new FrameBodyTDOR datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTDOR(byte textEncoding, String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTDOR datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTDOR(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v24Frames.FRAME_ID_ORIGINAL_RELEASE_TIME;
    }

}
