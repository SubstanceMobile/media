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
 * Equalisation (2)
 *
 * This is another subjective, alignment frame. It allows the user to
 * predefine an equalisation curve within the audio file. There may be
 * more than one "EQU2" frame in each tag, but only one with the same
 * identification string.
 *
 * <Header of 'Equalisation (2)', ID: "EQU2">
 * Interpolation method  $xx
 * Identification        <text string> $00
 *
 * The 'interpolation method' describes which method is preferred when
 * an interpolation between the adjustment point that follows. The
 * following methods are currently defined:
 *
 * $00  Band
 * No interpolation is made. A jump from one adjustment level to
 * another occurs in the middle between two adjustment points.
 * $01  Linear
 * Interpolation between adjustment points is linear.
 *
 * The 'identification' string is used to identify the situation and/or
 * device where this adjustment should apply. The following is then
 * repeated for every adjustment point
 *
 * Frequency          $xx xx
 * Volume adjustment  $xx xx
 *
 * The frequency is stored in units of 1/2 Hz, giving it a range from 0
 * to 32767 Hz.
 *
 * The volume adjustment is encoded as a fixed point decibel value, 16
 * bit signed integer representing (adjustment*512), giving +/- 64 dB
 * with a precision of 0.001953125 dB. E.g. +2 dB is stored as $04 00
 * and -2 dB is $FC 00.
 *
 * Adjustment points should be ordered by frequency and one frequency
 * should only be described once in the frame.
 */
public class FrameBodyEQU2 extends AbstractID3v2FrameBody implements ID3v24FrameBody {
    /**
     * Creates a new FrameBodyEQU2 datatype.
     */
    public FrameBodyEQU2() {

    }

    public FrameBodyEQU2(FrameBodyEQU2 body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyEQU2 datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    public FrameBodyEQU2(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v24Frames.FRAME_ID_EQUALISATION2;
    }

    /**
     *
     */
    protected void setupObjectList() {
        objectList.add(new ByteArraySizeTerminated(DataTypes.OBJ_DATA, this));
    }
}
