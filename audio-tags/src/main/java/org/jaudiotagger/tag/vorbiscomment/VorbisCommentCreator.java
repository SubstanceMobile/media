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
package org.jaudiotagger.tag.vorbiscomment;

import org.jaudiotagger.audio.generic.AbstractTagCreator;
import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * Create the raw packet data for a Vorbis Comment Tag
 */
public class VorbisCommentCreator extends AbstractTagCreator {
    /**
     * Convert tagdata to rawdata ready for writing to file
     *
     * @param tag
     * @param padding
     * @return
     * @throws UnsupportedEncodingException
     */
    //TODO padding parameter currently ignored
    public ByteBuffer convert(Tag tag, int padding) throws UnsupportedEncodingException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //Vendor
            String vendorString = ((VorbisCommentTag) tag).getVendor();
            int vendorLength = Utils.getUTF8Bytes(vendorString).length;
            baos.write(Utils.getSizeLEInt32(vendorLength));
            baos.write(Utils.getUTF8Bytes(vendorString));

            //User Comment List
            int listLength = tag.getFieldCount() - 1; //Remove Vendor from count         
            baos.write(Utils.getSizeLEInt32(listLength));

            //Add metadata raw content
            Iterator<TagField> it = tag.getFields();
            while (it.hasNext()) {
                TagField frame = it.next();
                if (frame.getId().equals(VorbisCommentFieldKey.VENDOR.getFieldName())) {
                    //this is always stored above so ignore                    
                } else {
                    baos.write(frame.getRawContent());
                }
            }

            //Put into ByteBuffer
            ByteBuffer buf = ByteBuffer.wrap(baos.toByteArray());
            buf.rewind();
            return buf;
        } catch (IOException ioe) {
            //Should never happen as not writing to file at this point
            throw new RuntimeException(ioe);
        }
    }
}
