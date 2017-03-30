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
 * Part of a set Text information frame.
 * <p>
 * <p>The 'Part of a set' frame is a numeric string that describes which part of a set the audio came from.
 * This frame is used if the source described in the "TALB" frame is divided into several mediums, e.g. a double CD.
 * The value may be extended with a "/" character and a numeric string containing the total number of parts in the set.
 * e.g. "1/2".
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
public class FrameBodyTPOS extends AbstractID3v2FrameBody implements ID3v23FrameBody, ID3v24FrameBody {
    /**
     * Creates a new FrameBodyTRCK datatype.
     */
    public FrameBodyTPOS() {
        setObjectValue(DataTypes.OBJ_TEXT_ENCODING, TextEncoding.ISO_8859_1);
        setObjectValue(DataTypes.OBJ_TEXT, new PartOfSet.PartOfSetValue());
    }

    public FrameBodyTPOS(FrameBodyTPOS body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTRCK datatype, the value is parsed literally
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTPOS(byte textEncoding, String text) {
        setObjectValue(DataTypes.OBJ_TEXT_ENCODING, textEncoding);
        setObjectValue(DataTypes.OBJ_TEXT, new PartOfSet.PartOfSetValue(text));
    }

    public FrameBodyTPOS(byte textEncoding, Integer discNo, Integer discTotal) {
        super();
        setObjectValue(DataTypes.OBJ_TEXT_ENCODING, textEncoding);
        setObjectValue(DataTypes.OBJ_TEXT, new PartOfSet.PartOfSetValue(discNo, discTotal));
    }


    /**
     * Creates a new FrameBodyTRCK datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws java.io.IOException
     * @throws InvalidTagException
     */
    public FrameBodyTPOS(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }


    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v24Frames.FRAME_ID_SET;
    }

    public String getUserFriendlyValue() {
        return String.valueOf(getDiscNo());
    }

    public String getText() {
        return getObjectValue(DataTypes.OBJ_TEXT).toString();
    }

    public Integer getDiscNo() {
        return ((PartOfSet.PartOfSetValue) getObjectValue(DataTypes.OBJ_TEXT)).getCount();
    }

    public String getDiscNoAsText() {
        return ((PartOfSet.PartOfSetValue) getObjectValue(DataTypes.OBJ_TEXT)).getCountAsText();
    }

    public void setDiscNo(Integer discNo) {
        ((PartOfSet.PartOfSetValue) getObjectValue(DataTypes.OBJ_TEXT)).setCount(discNo);
    }

    public void setDiscNo(String discNo) {
        ((PartOfSet.PartOfSetValue) getObjectValue(DataTypes.OBJ_TEXT)).setCount(discNo);
    }


    public Integer getDiscTotal() {
        return ((PartOfSet.PartOfSetValue) getObjectValue(DataTypes.OBJ_TEXT)).getTotal();
    }

    public String getDiscTotalAsText() {
        return ((PartOfSet.PartOfSetValue) getObjectValue(DataTypes.OBJ_TEXT)).getTotalAsText();
    }

    public void setDiscTotal(Integer discTotal) {
        ((PartOfSet.PartOfSetValue) getObjectValue(DataTypes.OBJ_TEXT)).setTotal(discTotal);
    }

    public void setDiscTotal(String discTotal) {
        ((PartOfSet.PartOfSetValue) getObjectValue(DataTypes.OBJ_TEXT)).setTotal(discTotal);
    }

    public void setText(String text) {
        setObjectValue(DataTypes.OBJ_TEXT, new PartOfSet.PartOfSetValue(text));
    }

    protected void setupObjectList() {
        objectList.add(new NumberHashMap(DataTypes.OBJ_TEXT_ENCODING, this, TextEncoding.TEXT_ENCODING_FIELD_SIZE));
        objectList.add(new PartOfSet(DataTypes.OBJ_TEXT, this));
    }
}
