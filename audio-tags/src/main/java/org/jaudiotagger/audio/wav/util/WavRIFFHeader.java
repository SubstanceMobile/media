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

public class WavRIFFHeader {

    private boolean isValid = false;

    public WavRIFFHeader(byte[] b) {
        //System.err.println(b.length);
        String RIFF = new String(b, 0, 4);
        //System.err.println(RIFF);
        String WAVE = new String(b, 8, 4);
        //System.err.println(WAVE);
        if (RIFF.equals("RIFF") && WAVE.equals("WAVE")) {
            isValid = true;
        }

    }

    public boolean isValid() {
        return isValid;
    }

    public String toString() {
        String out = "RIFF-WAVE Header:\n";
        out += "Is valid?: " + isValid;
        return out;
    }
}