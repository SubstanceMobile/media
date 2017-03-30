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

package org.jaudiotagger.audio;

/**
 * Representation of AudioHeader
 * <p>
 * <p>Contains info about the Audio Header
 */
public interface AudioHeader {
    /**
     * @return the audio file type
     */
    public abstract String getEncodingType();

    /**
     * @return the BitRate of the Audio
     */
    public String getBitRate();

    /**
     * @return birate as a number
     */
    public long getBitRateAsNumber();


    /**
     * @return the Sampling rate
     */
    public String getSampleRate();

    /**
     * @return
     */
    public int getSampleRateAsNumber();

    /**
     * @return the format
     */
    public String getFormat();

    /**
     * @return the Channel Mode such as Stereo or Mono
     */
    public String getChannels();

    /**
     * @return if the bitRate is variable
     */
    public boolean isVariableBitRate();

    /**
     * @return track length
     */
    public int getTrackLength();

    /**
     * @return the number of bits per sample
     */
    public int getBitsPerSample();

    /**
     * @return
     */
    public boolean isLossless();
}
