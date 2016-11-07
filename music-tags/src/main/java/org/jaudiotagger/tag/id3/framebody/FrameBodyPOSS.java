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
import org.jaudiotagger.tag.datatype.NumberVariableLength;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.jaudiotagger.tag.id3.valuepair.EventTimingTimestampTypes;

import java.nio.ByteBuffer;

/**
 * Position synchronisation frame.
 * <p>
 * <p>
 * This frame delivers information to the listener of how far into the
 * audio stream he picked up; in effect, it states the time offset of
 * the first frame in the stream. The frame layout is:
 * <p>
 * Head for 'Position synchronisation', ID:
 * Time stamp format $xx
 * Position          $xx (xx ...)
 * <p>
 * Where time stamp format is:
 * <p>
 * $01 Absolute time, 32 bit sized, using MPEG frames as unit
 * $02 Absolute time, 32 bit sized, using milliseconds as unit
 * <p>
 * and position is where in the audio the listener starts to receive,
 * i.e. the beginning of the next frame. If this frame is used in the
 * beginning of a file the value is always 0. There may only be one
 * "POSS" frame in each tag.
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
public class FrameBodyPOSS extends AbstractID3v2FrameBody implements ID3v24FrameBody, ID3v23FrameBody {
    /**
     * Creates a new FrameBodyPOSS datatype.
     */
    public FrameBodyPOSS() {
        //        this.setObject(ObjectNumberHashMap.OBJ_TIME_STAMP_FORMAT, new Byte((byte) 0));
        //        this.setObject("Position", new Long(0));
    }

    public FrameBodyPOSS(FrameBodyPOSS body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyPOSS datatype.
     *
     * @param timeStampFormat
     * @param position
     */
    public FrameBodyPOSS(byte timeStampFormat, long position) {
        this.setObjectValue(DataTypes.OBJ_TIME_STAMP_FORMAT, timeStampFormat);
        this.setObjectValue(DataTypes.OBJ_POSITION, position);
    }

    /**
     * Creates a new FrameBodyPOSS datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    public FrameBodyPOSS(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v24Frames.FRAME_ID_POSITION_SYNC;
    }


    /**
     *
     */
    protected void setupObjectList() {
        objectList.add(new NumberHashMap(DataTypes.OBJ_TIME_STAMP_FORMAT, this, EventTimingTimestampTypes.TIMESTAMP_KEY_FIELD_SIZE));
        objectList.add(new NumberVariableLength(DataTypes.OBJ_POSITION, this, 1));
    }
}
