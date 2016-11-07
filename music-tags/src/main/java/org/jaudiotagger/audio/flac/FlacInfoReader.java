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
import org.jaudiotagger.audio.flac.metadatablock.BlockType;
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataStreamInfo;
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockHeader;
import org.jaudiotagger.audio.generic.GenericAudioHeader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Logger;

/**
 * Read info from Flac file
 */
public class FlacInfoReader {
    // Logger Object
    public static Logger logger = Logger.getLogger("org.jaudiotagger.audio.flac");

    private static final int NO_OF_BITS_IN_BYTE = 8;
    private static final int KILOBYTES_TO_BYTES_MULTIPLIER = 1000;

    public FlacAudioHeader read(RandomAccessFile raf) throws CannotReadException, IOException {
        FlacStreamReader flacStream = new FlacStreamReader(raf);
        flacStream.findStream();

        MetadataBlockDataStreamInfo mbdsi = null;
        boolean isLastBlock = false;

        //Search for StreamInfo Block, but even after we found it we still have to continue through all
        //the metadata blocks so that we can find the start of the audio frames which we need to calculate
        //the bitrate
        while (!isLastBlock) {
            MetadataBlockHeader mbh = MetadataBlockHeader.readHeader(raf);
            if (mbh.getBlockType() == BlockType.STREAMINFO) {
                mbdsi = new MetadataBlockDataStreamInfo(mbh, raf);
                if (!mbdsi.isValid()) {
                    throw new CannotReadException("FLAC StreamInfo not valid");
                }
                //TODO We have found streaminfo so do we need to continue checking, effects bitrate calc which is correct
                //break;
            } else {
                raf.seek(raf.getFilePointer() + mbh.getDataLength());
            }

            isLastBlock = mbh.isLastBlock();
            mbh = null; //Free memory
        }

        if (mbdsi == null) {
            throw new CannotReadException("Unable to find Flac StreamInfo");
        }

        FlacAudioHeader info = new FlacAudioHeader();
        info.setLength(mbdsi.getSongLength());
        info.setPreciseLength(mbdsi.getPreciseLength());
        info.setChannelNumber(mbdsi.getChannelNumber());
        info.setSamplingRate(mbdsi.getSamplingRate());
        info.setBitsPerSample(mbdsi.getBitsPerSample());
        info.setEncodingType(mbdsi.getEncodingType());
        info.setExtraEncodingInfos("");
        info.setBitrate(computeBitrate(mbdsi.getPreciseLength(), raf.length() - raf.getFilePointer()));
        info.setLossless(true);
        info.setMd5(mbdsi.getMD5Signature());
        return info;
    }

    private int computeBitrate(float length, long size) {
        return (int) ((size / KILOBYTES_TO_BYTES_MULTIPLIER) * NO_OF_BITS_IN_BYTE / length);
    }

    /**
     * Count the number of metadatablocks, useful for debugging
     *
     * @param f
     * @return
     * @throws CannotReadException
     * @throws IOException
     */
    public int countMetaBlocks(File f) throws CannotReadException, IOException {
        RandomAccessFile raf = new RandomAccessFile(f, "r");
        FlacStreamReader flacStream = new FlacStreamReader(raf);
        flacStream.findStream();


        boolean isLastBlock = false;

        int count = 0;
        while (!isLastBlock) {
            MetadataBlockHeader mbh = MetadataBlockHeader.readHeader(raf);
            logger.config("Found block:" + mbh.getBlockType());
            raf.seek(raf.getFilePointer() + mbh.getDataLength());
            isLastBlock = mbh.isLastBlock();
            mbh = null; //Free memory
            count++;
        }
        raf.close();
        return count;
    }
}
