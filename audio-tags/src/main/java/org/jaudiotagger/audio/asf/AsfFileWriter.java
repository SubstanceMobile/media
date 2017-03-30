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
package org.jaudiotagger.audio.asf;

import org.jaudiotagger.audio.asf.data.AsfHeader;
import org.jaudiotagger.audio.asf.data.ChunkContainer;
import org.jaudiotagger.audio.asf.data.MetadataContainer;
import org.jaudiotagger.audio.asf.io.*;
import org.jaudiotagger.tag.asf.AsfTag;
import org.jaudiotagger.audio.asf.util.TagConverter;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.generic.AudioFileWriter;
import org.jaudiotagger.tag.Tag;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * This class writes given tags to ASF files containing WMA content. <br>
 * <br>
 *
 * @author Christian Laireiter
 */
public class AsfFileWriter extends AudioFileWriter {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void deleteTag(final RandomAccessFile raf,
                             final RandomAccessFile tempRaf) throws CannotWriteException,
            IOException {
        writeTag(new AsfTag(true), raf, tempRaf);
    }

    private boolean[] searchExistence(final ChunkContainer container,
                                      final MetadataContainer[] metaContainers) {
        assert container != null;
        assert metaContainers != null;
        final boolean[] result = new boolean[metaContainers.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = container.hasChunkByGUID(metaContainers[i]
                    .getContainerType().getContainerGUID());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeTag(final Tag tag, final RandomAccessFile raf,
                            final RandomAccessFile rafTemp) throws CannotWriteException,
            IOException {
        /*
         * Since this implementation should not change the structure of the ASF
         * file (locations of content description chunks), we need to read the
         * content description chunk and the extended content description chunk
         * from the source file. In the second step we need to determine which
         * modifier (asf header or asf extended header) gets the appropriate
         * modifiers. The following policies are applied: if the source does not
         * contain any descriptor, the necessary descriptors are appended to the
         * header object.
         * 
         * if the source contains only one descriptor in the header extension
         * object, and the other type is needed as well, the other one will be
         * put into the header extension object.
         * 
         * for each descriptor type, if an object is found, an updater will be
         * configured.
         */
        final AsfHeader sourceHeader = AsfHeaderReader.readTagHeader(raf);
        raf.seek(0); // Reset for the streamer
        /*
         * Now createField modifiers for metadata descriptor and extended content
         * descriptor as implied by the given Tag.
         */
        // TODO not convinced that we need to copy fields here
        final AsfTag copy = new AsfTag(tag, true);
        final MetadataContainer[] distribution = TagConverter
                .distributeMetadata(copy);
        final boolean[] existHeader = searchExistence(sourceHeader,
                distribution);
        final boolean[] existExtHeader = searchExistence(sourceHeader
                .getExtendedHeader(), distribution);
        // Modifiers for the asf header object
        final List<ChunkModifier> headerModifier = new ArrayList<ChunkModifier>();
        // Modifiers for the asf header extension object
        final List<ChunkModifier> extHeaderModifier = new ArrayList<ChunkModifier>();
        for (int i = 0; i < distribution.length; i++) {
            final WriteableChunkModifer modifier = new WriteableChunkModifer(
                    distribution[i]);
            if (existHeader[i]) {
                // Will remove or modify chunks in ASF header
                headerModifier.add(modifier);
            } else if (existExtHeader[i]) {
                // Will remove or modify chunks in extended header
                extHeaderModifier.add(modifier);
            } else {
                // Objects (chunks) will be added here.
                if (i == 0 || i == 2 || i == 1) {
                    // Add content description and extended content description
                    // at header for maximum compatibility
                    headerModifier.add(modifier);
                } else {
                    // For now, the rest should be created at extended header
                    // since other positions aren't known.
                    extHeaderModifier.add(modifier);
                }
            }
        }
        // only addField an AsfExtHeaderModifier, if there is actually something to
        // change (performance)
        if (!extHeaderModifier.isEmpty()) {
            headerModifier.add(new AsfExtHeaderModifier(extHeaderModifier));
        }
        new AsfStreamer()
                .createModifiedCopy(new RandomAccessFileInputstream(raf),
                        new RandomAccessFileOutputStream(rafTemp),
                        headerModifier);
    }

}