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
import org.jaudiotagger.tag.id3.ID3v23Frames;
import org.jaudiotagger.tag.id3.valuepair.TextEncoding;

import java.nio.ByteBuffer;

/**
 * Original release year Text information frame.
 * <p>The 'Original release year' frame is intended for the year when the original recording, if for example the music
 * in the file should be a cover of a previously released song, was released. The field is formatted as in the "TYER"
 * frame.
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
public class FrameBodyTORY extends AbstractFrameBodyTextInfo implements ID3v23FrameBody {
    private static final int NUMBER_OF_DIGITS_IN_YEAR = 4;

    /**
     * Creates a new FrameBodyTORY datatype.
     */
    public FrameBodyTORY() {
    }

    public FrameBodyTORY(FrameBodyTORY body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTORY datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTORY(byte textEncoding, String text) {
        super(textEncoding, text);
    }

    /**
     * When converting v4 TDOR to v3 TORY frame
     *
     * @param body
     */
    public FrameBodyTORY(FrameBodyTDOR body) {
        setObjectValue(DataTypes.OBJ_TEXT_ENCODING, TextEncoding.ISO_8859_1);
        String year = body.getText();
        if (body.getText().length() > NUMBER_OF_DIGITS_IN_YEAR) {
            year = body.getText().substring(0, NUMBER_OF_DIGITS_IN_YEAR);
        }
        setObjectValue(DataTypes.OBJ_TEXT, year);
    }

    /**
     * Creates a new FrameBodyTORY datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTORY(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v23Frames.FRAME_ID_V3_TORY;
    }
}