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

import java.nio.ByteBuffer;

/**
 * File type Text information frame.
 * <p>
 * <p>The 'File type' frame indicates which type of audio this tag defines.
 * The following type and refinements are defined:
 * <p><table border=0 width="70%">
 * <tr><td>MPG</td><td rowspan=8>&nbsp;</td><td width="100%">MPEG Audio</td></tr>
 * <tr><td align=right>/1   </td><td>MPEG 1/2 layer I           </td></tr>
 * <tr><td align=right>/2   </td><td>MPEG 1/2 layer II          </td></tr>
 * <tr><td align=right>/3   </td><td>MPEG 1/2 layer III         </td></tr>
 * <tr><td align=right>/2.5 </td><td>MPEG 2.5                   </td></tr>
 * <tr><td align=right>/AAC </td><td>Advanced audio compression </td></tr>
 * <tr><td>VQF</td><td>Transform-domain Weighted Interleave Vector Quantization</td></tr>
 * <tr><td>PCM              </td><td>Pulse Code Modulated audio </td></tr>
 * </table><p>
 * but other types may be used, not for these types though. This is used
 * in a similar way to the predefined types in the "TMED" frame, but
 * without parentheses. If this frame is not present audio type is
 * assumed to be "MPG".
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
public class FrameBodyTFLT extends AbstractFrameBodyTextInfo implements ID3v24FrameBody, ID3v23FrameBody {
    /**
     * Creates a new FrameBodyTFLT datatype.
     */
    public FrameBodyTFLT() {
    }

    public FrameBodyTFLT(FrameBodyTFLT body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTFLT datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTFLT(byte textEncoding, String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTFLT datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTFLT(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v24Frames.FRAME_ID_FILE_TYPE;
    }
}
