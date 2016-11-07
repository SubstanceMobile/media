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
package org.jaudiotagger.tag.mp4;

import org.jaudiotagger.audio.generic.AbstractTagCreator;
import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.audio.mp4.Mp4AtomIdentifier;
import org.jaudiotagger.audio.mp4.atom.Mp4BoxHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.mp4.field.Mp4TagCoverField;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * Create raw content of mp4 tag data, concerns itself with atoms upto the ilst atom
 * <p>
 * <p>This level is was selected because the ilst atom can be recreated without reference to existing mp4 fields
 * but fields above this level are dependent upon other information that is not held in the tag.
 * <p>
 * <pre>
 * |--- ftyp
 * |--- moov
 * |......|
 * |......|----- mvdh
 * |......|----- trak
 * |......|----- udta
 * |..............|
 * |..............|-- meta
 * |....................|
 * |....................|-- hdlr
 * |....................|-- ilst
 * |....................|.. ..|
 * |....................|.....|---- @nam (Optional for each metadatafield)
 * |....................|.....|.......|-- data
 * |....................|.....|....... ecetera
 * |....................|.....|---- ---- (Optional for reverse dns field)
 * |....................|.............|-- mean
 * |....................|.............|-- name
 * |....................|.............|-- data
 * |....................|................ ecetere
 * |....................|-- free
 * |--- free
 * |--- mdat
 * </pre>
 */
public class Mp4TagCreator extends AbstractTagCreator {
    /**
     * Convert tagdata to rawdata ready for writing to file
     *
     * @param tag
     * @param padding TODO padding parameter currently ignored
     * @return
     * @throws UnsupportedEncodingException
     */
    public ByteBuffer convert(Tag tag, int padding) throws UnsupportedEncodingException {
        try {
            //Add metadata raw content
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Iterator<TagField> it = tag.getFields();
            boolean processedArtwork = false;
            while (it.hasNext()) {
                TagField frame = it.next();
                //To ensure order is maintained dont process artwork until iterator hits it.
                if (frame instanceof Mp4TagCoverField) {
                    if (processedArtwork) {
                        //ignore
                    } else {
                        processedArtwork = true;

                        //Because each artwork image is held within the tag as a separate field, but when
                        //they are written they are all held under a single covr box we need to do some checks
                        //and special processing here if we have any artwork image (this code only necessary
                        //if we have more than 1 but do it anyway even if only have 1 image)
                        ByteArrayOutputStream covrDataBaos = new ByteArrayOutputStream();

                        try {
                            for (TagField artwork : tag.getFields(FieldKey.COVER_ART)) {
                                covrDataBaos.write(((Mp4TagField) artwork).getRawContentDataOnly());
                            }
                        } catch (KeyNotFoundException knfe) {
                            //This cannot happen
                            throw new RuntimeException("Unable to find COVERART Key");
                        }

                        //Now create the parent Data
                        byte[] data = covrDataBaos.toByteArray();
                        baos.write(Utils.getSizeBEInt32(Mp4BoxHeader.HEADER_LENGTH + data.length));
                        baos.write(Utils.getDefaultBytes(Mp4FieldKey.ARTWORK.getFieldName(), "ISO-8859-1"));
                        baos.write(data);
                    }
                } else {
                    baos.write(frame.getRawContent());
                }
            }

            //Wrap into ilst box
            ByteArrayOutputStream ilst = new ByteArrayOutputStream();
            ilst.write(Utils.getSizeBEInt32(Mp4BoxHeader.HEADER_LENGTH + baos.size()));
            ilst.write(Utils.getDefaultBytes(Mp4AtomIdentifier.ILST.getFieldName(), "ISO-8859-1"));
            ilst.write(baos.toByteArray());

            //Put into ByteBuffer
            ByteBuffer buf = ByteBuffer.wrap(ilst.toByteArray());
            buf.rewind();
            return buf;
        } catch (IOException ioe) {
            //Should never happen as not writing to file at this point
            throw new RuntimeException(ioe);
        }
    }
}
