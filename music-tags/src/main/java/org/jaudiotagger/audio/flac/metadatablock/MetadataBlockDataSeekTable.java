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
 * SeekTable Block
 * <p>
 * <p>This is an optional block for storing seek points. It is possible to seek to any given sample in a FLAC stream
 * without a seek table, but the delay can be unpredictable since the bitrate may vary widely within a stream.
 * By adding seek points to a stream, this delay can be significantly reduced. Each seek point takes 18 bytes, so 1%
 * resolution within a stream adds less than 2k. There can be only one SEEKTABLE in a stream, but the table can have
 * any number of seek points. There is also a special 'placeholder' seekpoint which will be ignored by decoders but
 * which can be used to reserve space for future seek point insertion.
 */
public class MetadataBlockDataSeekTable implements MetadataBlockData {
    private byte[] data;

    public MetadataBlockDataSeekTable(MetadataBlockHeader header, RandomAccessFile raf) throws IOException {
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
