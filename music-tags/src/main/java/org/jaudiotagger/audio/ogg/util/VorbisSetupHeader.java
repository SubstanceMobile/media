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

package org.jaudiotagger.audio.ogg.util;

import org.jaudiotagger.audio.generic.Utils;

import java.util.logging.Logger;

/**
 * Vorbis Setup header
 * <p>
 * We dont need to decode a vorbis setup header for metatagging, but we should be able to identify
 * it.
 *
 * @author Paul Taylor
 * @version 12th August 2007
 */
public class VorbisSetupHeader implements VorbisHeader {
    // Logger Object
    public static Logger logger = Logger.getLogger("org.jaudiotagger.audio.ogg.atom");

    private boolean isValid = false;

    public VorbisSetupHeader(byte[] vorbisData) {
        decodeHeader(vorbisData);
    }

    public boolean isValid() {
        return isValid;
    }

    public void decodeHeader(byte[] b) {
        int packetType = b[FIELD_PACKET_TYPE_POS];
        logger.fine("packetType" + packetType);
        String vorbis = Utils.getString(b, FIELD_CAPTURE_PATTERN_POS, FIELD_CAPTURE_PATTERN_LENGTH, "ISO-8859-1");
        if (packetType == VorbisPacketType.SETUP_HEADER.getType() && vorbis.equals(CAPTURE_PATTERN)) {
            isValid = true;
        }
    }

}
