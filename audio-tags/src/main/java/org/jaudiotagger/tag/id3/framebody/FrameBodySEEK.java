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
import org.jaudiotagger.tag.datatype.NumberFixedLength;
import org.jaudiotagger.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;


public class FrameBodySEEK extends AbstractID3v2FrameBody implements ID3v24FrameBody {
    /**
     * Creates a new FrameBodySEEK datatype.
     */
    public FrameBodySEEK() {
        //        this.setObject("Minimum Offset to Next Tag", new Integer(0));
    }

    /**
     * Creates a new FrameBodySEEK datatype.
     *
     * @param minOffsetToNextTag
     */
    public FrameBodySEEK(int minOffsetToNextTag) {
        this.setObjectValue(DataTypes.OBJ_OFFSET, minOffsetToNextTag);
    }

    public FrameBodySEEK(FrameBodySEEK body) {
        super(body);
    }

    /**
     * Creates a new FrameBodySEEK datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    public FrameBodySEEK(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v24Frames.FRAME_ID_AUDIO_SEEK_POINT_INDEX;
    }

    /**
     *
     */
    protected void setupObjectList() {
        objectList.add(new NumberFixedLength(DataTypes.OBJ_OFFSET, this, 4));
    }
}
