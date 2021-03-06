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
package org.jaudiotagger.audio.flac;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.generic.AudioFileReader;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.tag.Tag;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Read encoding and tag info for Flac file (open source lossless encoding)
 */
public class FlacFileReader extends AudioFileReader {

    private FlacInfoReader ir = new FlacInfoReader();
    private FlacTagReader tr = new FlacTagReader();

    protected GenericAudioHeader getEncodingInfo(RandomAccessFile raf) throws CannotReadException, IOException {
        return ir.read(raf);
    }

    protected Tag getTag(RandomAccessFile raf) throws CannotReadException, IOException {
        return tr.read(raf);
    }
}
