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

package org.jaudiotagger.tag.id3;

import org.jaudiotagger.logging.ErrorMessage;
import org.jaudiotagger.tag.InvalidFrameException;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * compresses frame data
 * <p>
 * Is currently required for V23Frames and V24Frames
 */
//TODO also need to support compress framedata
public class ID3Compression {
    //Logger
    public static Logger logger = Logger.getLogger("org.jaudiotagger.tag.id3");

    /**
     * Decompress realFrameSize bytes to decompressedFrameSize bytes and return as ByteBuffer
     *
     * @param byteBuffer
     * @param decompressedFrameSize
     * @param realFrameSize
     * @return
     * @throws org.jaudiotagger.tag.InvalidFrameException
     */
    protected static ByteBuffer uncompress(String identifier, String filename, ByteBuffer byteBuffer, int decompressedFrameSize, int realFrameSize) throws InvalidFrameException {
        logger.config(filename + ":About to decompress " + realFrameSize + " bytes, expect result to be:" + decompressedFrameSize + " bytes");
        // Decompress the bytes into this buffer, size initialized from header field
        byte[] result = new byte[decompressedFrameSize];
        byte[] input = new byte[realFrameSize];

        //Store position ( just after frame header and any extra bits)
        //Read frame data into array, and then put buffer back to where it was
        int position = byteBuffer.position();
        byteBuffer.get(input, 0, realFrameSize);
        byteBuffer.position(position);

        Inflater decompresser = new Inflater();
        decompresser.setInput(input);
        try {
            int inflatedTo = decompresser.inflate(result);
            logger.config(filename + ":Decompressed to " + inflatedTo + " bytes");
        } catch (DataFormatException dfe) {
            logger.log(Level.CONFIG, "Unable to decompress this frame:" + identifier, dfe);

            //Update position of main buffer, so no attempt is made to reread these bytes
            byteBuffer.position(byteBuffer.position() + realFrameSize);
            throw new InvalidFrameException(ErrorMessage.ID3_UNABLE_TO_DECOMPRESS_FRAME.getMsg(identifier, filename, dfe.getMessage()));
        }
        decompresser.end();
        return ByteBuffer.wrap(result);
    }

}