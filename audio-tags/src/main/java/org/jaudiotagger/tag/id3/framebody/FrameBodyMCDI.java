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

/**
 * Music CD identifier frame.
 * <p>
 * <p>
 * This frame is intended for music that comes from a CD, so that the CD
 * can be identified in databases such as the CDDB. The frame
 * consists of a binary dump of the Table Of Contents, TOC, from the CD,
 * which is a header of 4 bytes and then 8 bytes/track on the CD plus 8
 * bytes for the 'lead out' making a maximum of 804 bytes. The offset to
 * the beginning of every track on the CD should be described with a
 * four bytes absolute CD-frame address per track, and not with absolute
 * time. This frame requires a present and valid "TRCK" frame, even if
 * the CD's only got one track. There may only be one "MCDI" frame in
 * each tag.
 * <p><table border=0 width="70%">
 * <tr><td colspan=2> &lt;Header for 'Music CD identifier', ID: "MCDI"&gt;</td></tr>
 * <tr><td>CD TOC</td><td>&lt;binary data&gt;</td></tr>
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
public class FrameBodyMCDI extends AbstractID3v2FrameBody implements ID3v24FrameBody, ID3v23FrameBody {
    /**
     * Creates a new FrameBodyMCDI datatype.
     */
    public FrameBodyMCDI() {
        this.setObjectValue(DataTypes.OBJ_DATA, new byte[0]);
    }

    public FrameBodyMCDI(FrameBodyMCDI body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyMCDI datatype.
     *
     * @param cdTOC
     */
    public FrameBodyMCDI(byte[] cdTOC) {
        this.setObjectValue(DataTypes.OBJ_DATA, cdTOC);
    }

    /**
     * Creates a new FrameBodyMCDI datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    public FrameBodyMCDI(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v24Frames.FRAME_ID_MUSIC_CD_ID;
    }

    /**
     *
     */
    protected void setupObjectList() {
        objectList.add(new ByteArraySizeTerminated(DataTypes.OBJ_DATA, this));
    }
}
