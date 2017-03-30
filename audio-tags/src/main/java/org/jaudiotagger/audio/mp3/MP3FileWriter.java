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

package org.jaudiotagger.audio.mp3;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.generic.AudioFileWriter;
import org.jaudiotagger.tag.Tag;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Write Mp3 Info (retrofitted to entagged ,done differently to entagged which is why some methods throw RuntimeException)
 * because done elsewhere
 */
public class MP3FileWriter extends AudioFileWriter {
    public void deleteTag(AudioFile f) throws CannotWriteException {
        //Because audio file is an instanceof MP3File this directs it to save
        //taking into account if the tag has been sent to null in which case it will be deleted
        f.commit();
    }

    public void writeFile(AudioFile f) throws CannotWriteException {
        //Because audio file is an instanceof MP3File this directs it to save
        f.commit();
    }

    /**
     * Delete the Id3v1 and ID3v2 tags from file
     *
     * @param af
     * @throws CannotReadException
     * @throws CannotWriteException
     */
    @Override
    public synchronized void delete(AudioFile af) throws CannotReadException, CannotWriteException {
        ((MP3File) af).setID3v1Tag(null);
        ((MP3File) af).setID3v2Tag(null);
        af.commit();
    }

    protected void writeTag(Tag tag, RandomAccessFile raf, RandomAccessFile rafTemp) throws CannotWriteException, IOException {
        throw new RuntimeException("MP3FileReaderwriteTag should not be called");
    }

    protected void deleteTag(RandomAccessFile raf, RandomAccessFile tempRaf) throws CannotWriteException, IOException {
        throw new RuntimeException("MP3FileReader.getEncodingInfo should be called");
    }
}


