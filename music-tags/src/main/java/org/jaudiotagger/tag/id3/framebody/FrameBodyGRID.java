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
import org.jaudiotagger.tag.datatype.StringNullTerminated;
import org.jaudiotagger.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Group identification registration frame.
 * <p>
 * <p>
 * This frame enables grouping of otherwise unrelated frames. This can
 * be used when some frames are to be signed. To identify which frames
 * belongs to a set of frames a group identifier must be registered in
 * the tag with this frame. The 'Owner identifier' is a null-terminated
 * string with a URL containing an email address, or a link to a
 * location where an email address can be found, that belongs to the
 * organisation responsible for this grouping. Questions regarding the
 * grouping should be sent to the indicated email address. The 'Group
 * symbol' contains a value that associates the frame with this group
 * throughout the whole tag. Values below $80 are reserved. The 'Group
 * symbol' may optionally be followed by some group specific data, e.g.
 * a digital signature. There may be several "GRID" frames in a tag but
 * only one containing the same symbol and only one containing the same
 * owner identifier. The group symbol must be used somewhere in the tag.
 * See section 3.3.1, flag j for more information.
 * <p><table border=0 width="70%">
 * <tr><td colspan=2>&lt;Header for 'Group ID registration', ID: "GRID"&gt;</td></tr>
 * <tr><td>Owner identifier     </td><td>&lt;text string&gt; $00</td></tr>
 * <tr><td>Group symbol         </td><td width="80%">$xx        </td></tr>
 * <tr><td>Group dependent data </td><td>&lt;binary data&gt;    </td></tr>
 * </table>
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
public class FrameBodyGRID extends AbstractID3v2FrameBody implements ID3v24FrameBody, ID3v23FrameBody {
    /**
     * Creates a new FrameBodyGRID datatype.
     */
    public FrameBodyGRID() {
        //        this.setObject(ObjectTypes.OBJ_OWNER, "");
        //        this.setObject("Group Symbol", new Byte((byte) 0));
        //        this.setObject("Group Dependent Data", new byte[0]);
    }

    public FrameBodyGRID(FrameBodyGRID body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyGRID datatype.
     *
     * @param owner
     * @param groupSymbol
     * @param data
     */
    public FrameBodyGRID(String owner, byte groupSymbol, byte[] data) {
        this.setObjectValue(DataTypes.OBJ_OWNER, owner);
        this.setObjectValue(DataTypes.OBJ_GROUP_SYMBOL, groupSymbol);
        this.setObjectValue(DataTypes.OBJ_GROUP_DATA, data);
    }

    /**
     * Creates a new FrameBodyGRID datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    public FrameBodyGRID(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * @param textEncoding
     */
    public void setGroupSymbol(byte textEncoding) {
        setObjectValue(DataTypes.OBJ_GROUP_SYMBOL, textEncoding);
    }

    /**
     * @return
     */
    public byte getGroupSymbol() {
        if (getObjectValue(DataTypes.OBJ_GROUP_SYMBOL) != null) {
            return ((Long) getObjectValue(DataTypes.OBJ_GROUP_SYMBOL)).byteValue();
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
        return ID3v24Frames.FRAME_ID_GROUP_ID_REG;
    }


    /**
     * @param owner
     */
    public void setOwner(String owner) {
        setObjectValue(DataTypes.OBJ_OWNER, owner);
    }

    /**
     * @return
     */
    public String getOwner() {
        return (String) getObjectValue(DataTypes.OBJ_OWNER);
    }

    /**
     *
     */
    protected void setupObjectList() {
        objectList.add(new StringNullTerminated(DataTypes.OBJ_OWNER, this));
        objectList.add(new NumberFixedLength(DataTypes.OBJ_GROUP_SYMBOL, this, 1));
        objectList.add(new ByteArraySizeTerminated(DataTypes.OBJ_GROUP_DATA, this));
    }
}
