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
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataPicture;
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockHeader;
import org.jaudiotagger.tag.InvalidFrameException;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentReader;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Read Flac Tag
 */
public class FlacTagReader {
    // Logger Object
    public static Logger logger = Logger.getLogger("org.jaudiotagger.audio.flac");

    private VorbisCommentReader vorbisCommentReader = new VorbisCommentReader();


    public FlacTag read(RandomAccessFile raf) throws CannotReadException, IOException {
        FlacStreamReader flacStream = new FlacStreamReader(raf);
        flacStream.findStream();

        //Hold the metadata
        VorbisCommentTag tag = null;
        List<MetadataBlockDataPicture> images = new ArrayList<MetadataBlockDataPicture>();

        //Seems like we have a valid stream
        boolean isLastBlock = false;
        while (!isLastBlock) {
            if (logger.isLoggable(Level.CONFIG)) {
                logger.config("Looking for MetaBlockHeader at:" + raf.getFilePointer());
            }

            //Read the header
            MetadataBlockHeader mbh = MetadataBlockHeader.readHeader(raf);
            if (mbh == null) {
                break;
            }

            if (logger.isLoggable(Level.CONFIG)) {
                logger.config("Reading MetadataBlockHeader:" + mbh.toString() + " ending at " + raf.getFilePointer());
            }

            //Is it one containing some sort of metadata, therefore interested in it?

            //JAUDIOTAGGER-466:CBlocktype can be null
            if (mbh.getBlockType() != null) {
                switch (mbh.getBlockType()) {
                    //We got a vorbiscomment comment block, parse it
                    case VORBIS_COMMENT:
                        byte[] commentHeaderRawPacket = new byte[mbh.getDataLength()];
                        raf.read(commentHeaderRawPacket);
                        tag = vorbisCommentReader.read(commentHeaderRawPacket, false);
                        break;

                    case PICTURE:
                        try {
                            MetadataBlockDataPicture mbdp = new MetadataBlockDataPicture(mbh, raf);
                            images.add(mbdp);
                        } catch (IOException ioe) {
                            logger.warning("Unable to read picture metablock, ignoring:" + ioe.getMessage());
                        } catch (InvalidFrameException ive) {
                            logger.warning("Unable to read picture metablock, ignoring" + ive.getMessage());
                        }

                        break;

                    //This is not a metadata block we are interested in so we skip to next block
                    default:
                        if (logger.isLoggable(Level.CONFIG)) {
                            logger.config("Ignoring MetadataBlock:" + mbh.getBlockType());
                        }
                        raf.seek(raf.getFilePointer() + mbh.getDataLength());
                        break;
                }
            }
            isLastBlock = mbh.isLastBlock();
            mbh = null;
        }

        //Note there may not be either a tag or any images, no problem this is valid however to make it easier we
        //just initialize Flac with an empty VorbisTag
        if (tag == null) {
            tag = VorbisCommentTag.createNewTag();
        }
        FlacTag flacTag = new FlacTag(tag, images);
        return flacTag;
    }
}

