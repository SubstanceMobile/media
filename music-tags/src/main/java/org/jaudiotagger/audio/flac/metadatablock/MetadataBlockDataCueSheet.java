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
package org.jaudiotagger.audio.flac.metadatablock;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Cuesheet Block
 * <p>
 * <p>This block is for storing various information that can be used in a cue sheet. It supports track and index points,
 * compatible with Red Book CD digital audio discs, as well as other CD-DA metadata such as media catalog number and
 * track ISRCs. The CUESHEET block is especially useful for backing up CD-DA discs, but it can be used as a general
 * purpose cueing mechanism for playback
 */
public class MetadataBlockDataCueSheet implements MetadataBlockData {
    private byte[] data;

    public MetadataBlockDataCueSheet(MetadataBlockHeader header, RandomAccessFile raf) throws IOException {
        data = new byte[header.getDataLength()];
        raf.readFully(data);
    }

    public byte[] getBytes() {
        return data;
    }

    public int getLength() {
        return data.length;
    }
}
