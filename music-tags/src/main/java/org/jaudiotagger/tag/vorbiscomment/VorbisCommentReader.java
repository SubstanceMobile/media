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

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.audio.ogg.util.VorbisHeader;
import org.jaudiotagger.logging.ErrorMessage;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Create the VorbisCommentTag by reading from the raw packet data
 * <p>
 * <p>This is in the same format whether encoded with Ogg or Flac
 * except the framing bit is only present when used within Ogg Vorbis
 * <p>
 * <pre>
 * From the http://xiph.org/vorbis/doc/Vorbis_I_spec.html#vorbis-spec-comment
 * Read decodes the packet data using the following algorithm:
 *  [vendor_length] = read an unsigned integer of 32 bits
 *  [vendor_string] = read a UTF-8 vector as [vendor_length] octets
 *  [user_comment_list_length] = read an unsigned integer of 32 bits
 *  iterate [user_comment_list_length] times {
 *      5) [length] = read an unsigned integer of 32 bits
 *      6) this iteration's user comment = read a UTF-8 vector as [length] octets
 *    }
 *  [framing_bit] = read a single bit as boolean
 *  if ( [framing_bit] unset or end-of-packet ) then ERROR
 *  done.
 * </pre>
 */
public class VorbisCommentReader {
    // Logger Object
    public static Logger logger = Logger.getLogger("org.jaudiotagger.tag.vorbiscomment.VorbisCommentReader");

    public static final int FIELD_VENDOR_LENGTH_POS = 0;
    public static final int FIELD_VENDOR_STRING_POS = 4;

    public static final int FIELD_VENDOR_LENGTH_LENGTH = 4;
    public static final int FIELD_USER_COMMENT_LIST_LENGTH = 4;
    public static final int FIELD_COMMENT_LENGTH_LENGTH = 4;

    /**
     * max comment length that jaudiotagger can handle, this isnt the maximum column length allowed but we dont
     * dont allow comments larger than this because of problem with allocating memory  (10MB shoudl be fine for all apps)
     */
    private static final int JAUDIOTAGGER_MAX_COMMENT_LENGTH = 10000000;

    public VorbisCommentReader() {

    }

    /**
     * @param rawdata
     * @param isFramingBit
     * @return logical representation of VorbisCommentTag
     * @throws IOException
     * @throws CannotReadException
     */
    public VorbisCommentTag read(byte[] rawdata, boolean isFramingBit) throws IOException, CannotReadException {

        VorbisCommentTag tag = new VorbisCommentTag();

        byte[] b = new byte[FIELD_VENDOR_LENGTH_LENGTH];
        System.arraycopy(rawdata, FIELD_VENDOR_LENGTH_POS, b, FIELD_VENDOR_LENGTH_POS, FIELD_VENDOR_LENGTH_LENGTH);
        int pos = FIELD_VENDOR_LENGTH_LENGTH;
        int vendorStringLength = Utils.getIntLE(b);

        b = new byte[vendorStringLength];
        System.arraycopy(rawdata, pos, b, 0, vendorStringLength);
        pos += vendorStringLength;
        tag.setVendor(new String(b, VorbisHeader.CHARSET_UTF_8));
        logger.config("Vendor is:" + tag.getVendor());

        b = new byte[FIELD_USER_COMMENT_LIST_LENGTH];
        System.arraycopy(rawdata, pos, b, 0, FIELD_USER_COMMENT_LIST_LENGTH);
        pos += FIELD_USER_COMMENT_LIST_LENGTH;

        int userComments = Utils.getIntLE(b);
        logger.config("Number of user comments:" + userComments);

        for (int i = 0; i < userComments; i++) {
            b = new byte[FIELD_COMMENT_LENGTH_LENGTH];
            System.arraycopy(rawdata, pos, b, 0, FIELD_COMMENT_LENGTH_LENGTH);
            pos += FIELD_COMMENT_LENGTH_LENGTH;

            int commentLength = Utils.getIntLE(b);
            logger.config("Next Comment Length:" + commentLength);

            if (commentLength > JAUDIOTAGGER_MAX_COMMENT_LENGTH) {
                logger.warning(ErrorMessage.VORBIS_COMMENT_LENGTH_TOO_LARGE.getMsg(commentLength));
                break;
            } else if (commentLength > rawdata.length) {
                logger.warning(ErrorMessage.VORBIS_COMMENT_LENGTH_LARGE_THAN_HEADER.getMsg(commentLength, rawdata.length));
                break;
            } else {
                b = new byte[commentLength];
                System.arraycopy(rawdata, pos, b, 0, commentLength);
                pos += commentLength;

                VorbisCommentTagField fieldComment = new VorbisCommentTagField(b);
                logger.config("Adding:" + fieldComment.getId());
                tag.addField(fieldComment);
            }
        }

        //Check framing bit, only exists when vorbisComment used within OggVorbis       
        if (isFramingBit) {
            if ((rawdata[pos] & 0x01) != 1) {
                throw new CannotReadException(ErrorMessage.OGG_VORBIS_NO_FRAMING_BIT.getMsg((rawdata[pos] & 0x01)));
            }
        }
        return tag;
    }
}

