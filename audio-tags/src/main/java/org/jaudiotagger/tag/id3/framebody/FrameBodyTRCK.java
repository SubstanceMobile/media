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
import org.jaudiotagger.tag.datatype.NumberHashMap;
import org.jaudiotagger.tag.datatype.PartOfSet;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.jaudiotagger.tag.id3.valuepair.TextEncoding;

import java.nio.ByteBuffer;

/**
 * Track number/position in set Text Information frame.
 * <p>
 * <p>The 'Track number/Position in set' frame is a numeric string containing the order number of the audio-file on its original recording.
 * <p>
 * This may be extended with a "/" character and a numeric string containing the total number of tracks/elements on the original recording.
 * e.g. "4/9".
 * <p>
 * Some applications like to prepend the track number with a zero to aid sorting, (i.e 02 comes before 10)
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
public class FrameBodyTRCK extends AbstractID3v2FrameBody implements ID3v23FrameBody, ID3v24FrameBody {

    /**
     * Creates a new FrameBodyTRCK datatype.
     */
    public FrameBodyTRCK() {
        setObjectValue(DataTypes.OBJ_TEXT_ENCODING, TextEncoding.ISO_8859_1);
        setObjectValue(DataTypes.OBJ_TEXT, new PartOfSet.PartOfSetValue());
    }

    public FrameBodyTRCK(FrameBodyTRCK body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTRCK datatype, the value is parsed literally
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTRCK(byte textEncoding, String text) {
        setObjectValue(DataTypes.OBJ_TEXT_ENCODING, textEncoding);
        setObjectValue(DataTypes.OBJ_TEXT, new PartOfSet.PartOfSetValue(text));
    }

    public FrameBodyTRCK(byte textEncoding, Integer trackNo, Integer trackTotal) {
        super();
        setObjectValue(DataTypes.OBJ_TEXT_ENCODING, textEncoding);
        setObjectValue(DataTypes.OBJ_TEXT, new PartOfSet.PartOfSetValue(trackNo, trackTotal));
    }

    public String getUserFriendlyValue() {
        return String.valueOf(getTrackNo());
    }

    /**
     * Creates a new FrameBodyTRCK datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws java.io.IOException
     * @throws InvalidTagException
     */
    public FrameBodyTRCK(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }


    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v24Frames.FRAME_ID_TRACK;
    }

    public Integer getTrackNo() {
        PartOfSet.PartOfSetValue value = (PartOfSet.PartOfSetValue) getObjectValue(DataTypes.OBJ_TEXT);
        return value.getCount();
    }

    public String getTrackNoAsText() {
        return ((PartOfSet.PartOfSetValue) getObjectValue(DataTypes.OBJ_TEXT)).getCountAsText();
    }

    public String getText() {
        return getObjectValue(DataTypes.OBJ_TEXT).toString();
    }

    public void setTrackNo(Integer trackNo) {
        ((PartOfSet.PartOfSetValue) getObjectValue(DataTypes.OBJ_TEXT)).setCount(trackNo);
    }

    public void setTrackNo(String trackNo) {
        ((PartOfSet.PartOfSetValue) getObjectValue(DataTypes.OBJ_TEXT)).setCount(trackNo);
    }

    public Integer getTrackTotal() {
        return ((PartOfSet.PartOfSetValue) getObjectValue(DataTypes.OBJ_TEXT)).getTotal();
    }

    public String getTrackTotalAsText() {
        return ((PartOfSet.PartOfSetValue) getObjectValue(DataTypes.OBJ_TEXT)).getTotalAsText();
    }


    public void setTrackTotal(Integer trackTotal) {
        ((PartOfSet.PartOfSetValue) getObjectValue(DataTypes.OBJ_TEXT)).setTotal(trackTotal);
    }

    public void setTrackTotal(String trackTotal) {
        ((PartOfSet.PartOfSetValue) getObjectValue(DataTypes.OBJ_TEXT)).setTotal(trackTotal);
    }

    public void setText(String text) {
        setObjectValue(DataTypes.OBJ_TEXT, new PartOfSet.PartOfSetValue(text));
    }

    protected void setupObjectList() {
        objectList.add(new NumberHashMap(DataTypes.OBJ_TEXT_ENCODING, this, TextEncoding.TEXT_ENCODING_FIELD_SIZE));
        objectList.add(new PartOfSet(DataTypes.OBJ_TEXT, this));
    }
}