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
package org.jaudiotagger.audio.wav.util;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.generic.GenericAudioHeader;

import java.io.IOException;
import java.io.RandomAccessFile;

public class WavInfoReader {
    public GenericAudioHeader read(RandomAccessFile raf) throws CannotReadException, IOException {
        // Reads wav header----------------------------------------
        GenericAudioHeader info = new GenericAudioHeader();

        if (raf.length() < 12) {
            throw new CannotReadException("This is not a WAV File (<12 bytes)");
        }
        byte[] b = new byte[12];
        raf.read(b);

        WavRIFFHeader wh = new WavRIFFHeader(b);
        if (wh.isValid()) {
            b = new byte[34];
            raf.read(b);

            WavFormatHeader wfh = new WavFormatHeader(b);
            if (wfh.isValid()) {
                // Populates
                // encodingInfo----------------------------------------------------
                info.setPreciseLength(((float) raf.length() - (float) 36) / wfh.getBytesPerSecond());
                info.setChannelNumber(wfh.getChannelNumber());
                info.setSamplingRate(wfh.getSamplingRate());
                info.setBitsPerSample(wfh.getBitsPerSample());
                info.setEncodingType("WAV-RIFF " + wfh.getBitsPerSample() + " bits");
                info.setExtraEncodingInfos("");
                info.setBitrate(wfh.getBytesPerSecond() * 8 / 1000);
                info.setVariableBitRate(false);
            } else {
                throw new CannotReadException("Wav Format Header not valid");
            }
        } else {
            throw new CannotReadException("Wav RIFF Header not valid");
        }

        return info;
    }
}
