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
import org.jaudiotagger.tag.datatype.NumberFixedLength;
import org.jaudiotagger.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;


public class FrameBodySIGN extends AbstractID3v2FrameBody implements ID3v24FrameBody {
    /**
     * Creates a new FrameBodySIGN datatype.
     */
    public FrameBodySIGN() {
        //        this.setObject("Group Symbol", new Byte((byte) 0));
        //        this.setObject("Signature", new byte[0]);
    }

    public FrameBodySIGN(FrameBodySIGN body) {
        super(body);
    }

    /**
     * Creates a new FrameBodySIGN datatype.
     *
     * @param groupSymbol
     * @param signature
     */
    public FrameBodySIGN(byte groupSymbol, byte[] signature) {
        this.setObjectValue(DataTypes.OBJ_GROUP_SYMBOL, groupSymbol);
        this.setObjectValue(DataTypes.OBJ_SIGNATURE, signature);
    }

    /**
     * Creates a new FrameBodySIGN datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    public FrameBodySIGN(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * @param groupSymbol
     */
    public void setGroupSymbol(byte groupSymbol) {
        setObjectValue(DataTypes.OBJ_GROUP_SYMBOL, groupSymbol);
    }

    /**
     * @return
     */
    public byte getGroupSymbol() {
        if (getObjectValue(DataTypes.OBJ_GROUP_SYMBOL) != null) {
            return (Byte) getObjectValue(DataTypes.OBJ_GROUP_SYMBOL);
        } else {
            return (byte) 0;
        }
    }


    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v24Frames.FRAME_ID_SIGNATURE;
    }

    /**
     * @param signature
     */
    public void setSignature(byte[] signature) {
        setObjectValue(DataTypes.OBJ_SIGNATURE, signature);
    }

    /**
     * @return
     */
    public byte[] getSignature() {
        return (byte[]) getObjectValue(DataTypes.OBJ_SIGNATURE);
    }

    /**
     *
     */
    protected void setupObjectList() {
        objectList.add(new NumberFixedLength(DataTypes.OBJ_GROUP_SYMBOL, this, 1));
        objectList.add(new ByteArraySizeTerminated(DataTypes.OBJ_SIGNATURE, this));
    }
}
