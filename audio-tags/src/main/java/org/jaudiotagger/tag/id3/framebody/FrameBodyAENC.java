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
 * Audio encryption Frame.
 * <p>
 * <p>
 * This frame indicates if the actual audio stream is encrypted, and by
 * whom. Since standardisation of such encrypion scheme is beyond this
 * document, all "AENC" frames begin with a terminated string with a
 * URL containing an email address, or a link to a location where an
 * email address can be found, that belongs to the organisation
 * responsible for this specific encrypted audio file. Questions
 * regarding the encrypted audio should be sent to the email address
 * specified. If a $00 is found directly after the 'Frame size' and the
 * audiofile indeed is encrypted, the whole file may be considered
 * useless.
 * <p>
 * After the 'Owner identifier', a pointer to an unencrypted part of the
 * audio can be specified. The 'Preview start' and 'Preview length' is
 * described in frames. If no part is unencrypted, these fields should
 * be left zeroed. After the 'preview length' field follows optionally a
 * datablock required for decryption of the audio. There may be more
 * than one "AENC" frames in a tag, but only one with the same 'Owner
 * identifier'.
 * <p><table border=0 width="70%">
 * <tr><td colspan=2>&lt;Header for 'Audio encryption', ID: "AENC"&gt;</td></tr>
 * <tr><td>Owner identifier  </td><td>&lt;text string&gt; $00    </td></tr>
 * <tr><td>Preview start     </td><td>$xx xx                     </td></tr>
 * <tr><td>Preview length    </td><td>$xx xx                     </td></tr>
 * <tr><td>Encryption info   </td><td>&lt;binary data&gt;        </td></tr>
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
public class FrameBodyAENC extends AbstractID3v2FrameBody implements ID3v24FrameBody, ID3v23FrameBody {

    /**
     * Creates a new FrameBodyAENC datatype.
     */
    public FrameBodyAENC() {
        this.setObjectValue(DataTypes.OBJ_OWNER, "");
        this.setObjectValue(DataTypes.OBJ_PREVIEW_START, (short) 0);
        this.setObjectValue(DataTypes.OBJ_PREVIEW_LENGTH, (short) 0);
        this.setObjectValue(DataTypes.OBJ_ENCRYPTION_INFO, new byte[0]);
    }

    public FrameBodyAENC(FrameBodyAENC body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyAENC datatype.
     *
     * @param owner
     * @param previewStart
     * @param previewLength
     * @param data
     */
    public FrameBodyAENC(String owner, short previewStart, short previewLength, byte[] data) {
        this.setObjectValue(DataTypes.OBJ_OWNER, owner);
        this.setObjectValue(DataTypes.OBJ_PREVIEW_START, previewStart);
        this.setObjectValue(DataTypes.OBJ_PREVIEW_LENGTH, previewLength);
        this.setObjectValue(DataTypes.OBJ_ENCRYPTION_INFO, data);
    }

    /**
     * Creates a new FrameBodyAENC datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    public FrameBodyAENC(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v24Frames.FRAME_ID_AUDIO_ENCRYPTION;
    }

    /**
     * @return owner
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
        objectList.add(new NumberFixedLength(DataTypes.OBJ_PREVIEW_START, this, 2));
        objectList.add(new NumberFixedLength(DataTypes.OBJ_PREVIEW_LENGTH, this, 2));
        objectList.add(new ByteArraySizeTerminated(DataTypes.OBJ_ENCRYPTION_INFO, this));
    }
}
