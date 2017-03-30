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
import org.jaudiotagger.tag.datatype.StringFixedLength;
import org.jaudiotagger.tag.datatype.StringNullTerminated;
import org.jaudiotagger.tag.datatype.StringSizeTerminated;
import org.jaudiotagger.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Linked information frame.
 * <p>
 * <p>
 * To keep space waste as low as possible this frame may be used to link
 * information from another ID3v2 tag that might reside in another audio
 * file or alone in a binary file. It is recommended that this method is
 * only used when the files are stored on a CD-ROM or other
 * circumstances when the risk of file seperation is low. The frame
 * contains a frame identifier, which is the frame that should be linked
 * into this tag, a URL field, where a reference to the file where
 * the frame is given, and additional ID data, if needed. Data should be
 * retrieved from the first tag found in the file to which this link
 * points. There may be more than one "LINK" frame in a tag, but only
 * one with the same contents. A linked frame is to be considered as
 * part of the tag and has the same restrictions as if it was a physical
 * part of the tag (i.e. only one "RVRB" frame allowed, whether it's
 * linked or not).
 * <p><table border=0 width="70%">
 * <tr><td>&lt;Header for 'Linked information', ID: "LINK"&gt;   </td></tr>
 * <tr><td>Frame identifier      </td><td>$xx xx xx              </td></tr>
 * <tr><td>URL                   </td><td>&lt;text string&gt; $00</td></tr>
 * <tr><td>ID and additional data</td><td>&lt;text string(s)&gt; </td></tr>
 * </table>
 * <p>
 * Frames that may be linked and need no additional data are "IPLS",
 * "MCID", "ETCO", "MLLT", "SYTC", "RVAD", "EQUA", "RVRB", "RBUF", the
 * text information frames and the URL link frames.
 * <p>
 * The "TXXX", "APIC", "GEOB" and "AENC" frames may be linked with
 * the content descriptor as additional ID data.
 * <p>
 * The "COMM", "SYLT" and "USLT" frames may be linked with three bytes
 * of language descriptor directly followed by a content descriptor as
 * additional ID data.
 * <p>
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
public class FrameBodyLINK extends AbstractID3v2FrameBody implements ID3v24FrameBody, ID3v23FrameBody {
    /**
     * Creates a new FrameBodyLINK datatype.
     */
    public FrameBodyLINK() {
        //        this.setObject("Frame Identifier", "");
        //        this.setObject("URL", "");
        //        this.setObject("ID and Additional Data", "");
    }

    public FrameBodyLINK(FrameBodyLINK body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyLINK datatype.
     *
     * @param frameIdentifier
     * @param url
     * @param additionalData
     */
    public FrameBodyLINK(String frameIdentifier, String url, String additionalData) {
        this.setObjectValue(DataTypes.OBJ_DESCRIPTION, frameIdentifier);
        this.setObjectValue(DataTypes.OBJ_URL, url);
        this.setObjectValue(DataTypes.OBJ_ID, additionalData);
    }

    /**
     * Creates a new FrameBodyLINK datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    public FrameBodyLINK(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * @return
     */
    public String getAdditionalData() {
        return (String) getObjectValue(DataTypes.OBJ_ID);
    }

    /**
     * @param additionalData
     */
    public void getAdditionalData(String additionalData) {
        setObjectValue(DataTypes.OBJ_ID, additionalData);
    }

    /**
     * @return
     */
    public String getFrameIdentifier() {
        return (String) getObjectValue(DataTypes.OBJ_DESCRIPTION);
    }

    /**
     * @param frameIdentifier
     */
    public void getFrameIdentifier(String frameIdentifier) {
        setObjectValue(DataTypes.OBJ_DESCRIPTION, frameIdentifier);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v24Frames.FRAME_ID_LINKED_INFO;
    }


    /**
     *
     */
    protected void setupObjectList() {
        objectList.add(new StringFixedLength(DataTypes.OBJ_DESCRIPTION, this, 4));
        objectList.add(new StringNullTerminated(DataTypes.OBJ_URL, this));
        objectList.add(new StringSizeTerminated(DataTypes.OBJ_ID, this));
    }
}
