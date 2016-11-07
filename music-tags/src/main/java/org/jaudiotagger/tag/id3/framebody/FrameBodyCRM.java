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
import org.jaudiotagger.tag.datatype.StringNullTerminated;
import org.jaudiotagger.tag.id3.ID3v22Frames;

import java.nio.ByteBuffer;

public class FrameBodyCRM extends AbstractID3v2FrameBody implements ID3v22FrameBody {
    /**
     * Creates a new FrameBodyCRM datatype.
     */
    public FrameBodyCRM() {
        //        this.setObject(ObjectTypes.OBJ_OWNER, "");
        //        this.setObject(ObjectTypes.OBJ_DESCRIPTION, "");
        //        this.setObject("Encrypted datablock", new byte[0]);
    }

    public FrameBodyCRM(FrameBodyCRM body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyCRM datatype.
     *
     * @param owner
     * @param description
     * @param data
     */
    public FrameBodyCRM(String owner, String description, byte[] data) {
        this.setObjectValue(DataTypes.OBJ_OWNER, owner);
        this.setObjectValue(DataTypes.OBJ_DESCRIPTION, description);
        this.setObjectValue(DataTypes.OBJ_ENCRYPTED_DATABLOCK, data);
    }

    /**
     * Creates a new FrameBodyCRM datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    public FrameBodyCRM(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v22Frames.FRAME_ID_V2_ENCRYPTED_FRAME;
    }

    /**
     * @return
     */
    public String getOwner() {
        return (String) getObjectValue(DataTypes.OBJ_OWNER);
    }

    /**
     * @param description
     */
    public void getOwner(String description) {
        setObjectValue(DataTypes.OBJ_OWNER, description);
    }

    /**
     *
     */
    protected void setupObjectList() {
        objectList.add(new StringNullTerminated(DataTypes.OBJ_OWNER, this));
        objectList.add(new StringNullTerminated(DataTypes.OBJ_DESCRIPTION, this));
        objectList.add(new ByteArraySizeTerminated(DataTypes.OBJ_ENCRYPTED_DATABLOCK, this));
    }
}
