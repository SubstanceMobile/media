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
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.jaudiotagger.tag.reference.Languages;

import java.nio.ByteBuffer;

/**
 * Language(s) Text information frame.
 * <p>The 'Language(s)' frame should contain the languages of the text or lyrics spoken or sung in the audio. The language is represented with three characters according to ISO-639-2. If more than one language is used in the text their language codes should follow according to their usage.
 * <p>
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 * <p>
 * TODO:Although rare TLAN can actually return multiple language codes, at the moment they are all returned as a single
 * string via getText(), any additional parsing has to be done externally.
 *
 * @author : Paul Taylor
 * @author : Eric Farng
 * @version $Id$
 */
public class FrameBodyTLAN extends AbstractFrameBodyTextInfo implements ID3v24FrameBody, ID3v23FrameBody {

    /**
     * Creates a new FrameBodyTLAN datatype.
     */
    public FrameBodyTLAN() {
        super();
    }

    public FrameBodyTLAN(FrameBodyTLAN body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTLAN datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTLAN(byte textEncoding, String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTLAN datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTLAN(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v24Frames.FRAME_ID_LANGUAGE;
    }

    /**
     * @return true if text value is valid language code
     */
    public boolean isValid() {
        return Languages.getInstanceOf().getValueForId(getFirstTextValue()) != null;
    }
}
