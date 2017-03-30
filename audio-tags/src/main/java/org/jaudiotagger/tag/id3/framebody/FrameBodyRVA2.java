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
import org.jaudiotagger.tag.datatype.ByteArraySizeTerminated;
import org.jaudiotagger.tag.datatype.DataTypes;
import org.jaudiotagger.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

public class FrameBodyRVA2 extends AbstractID3v2FrameBody implements ID3v24FrameBody {

    /**
     * Creates a new FrameBodyRVA2 datatype.
     */
    public FrameBodyRVA2() {
    }

    public FrameBodyRVA2(FrameBodyRVA2 body) {
        super(body);
    }

    /**
     * Convert from V3 to V4 Frame
     * @param body
     */
    public FrameBodyRVA2(FrameBodyRVAD body) {
        setObjectValue(DataTypes.OBJ_DATA, body.getObjectValue(DataTypes.OBJ_DATA));
    }


    /**
     * Creates a new FrameBodyRVAD datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    public FrameBodyRVA2(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v24Frames.FRAME_ID_RELATIVE_VOLUME_ADJUSTMENT2;
    }

    /**
     * Setup the Object List. A byte Array which will be read upto frame size
     * bytes.
     */
    protected void setupObjectList() {
        objectList.add(new ByteArraySizeTerminated(DataTypes.OBJ_DATA, this));
    }


}
