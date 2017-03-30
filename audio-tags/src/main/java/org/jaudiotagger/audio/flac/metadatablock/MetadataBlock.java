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

/**
 * Metadata Block
 * <p>
 * <p>A FLAC bitstream consists of the "fLaC" marker at the beginning of the stream,
 * followed by a mandatory metadata block (called the STREAMINFO block), any number of other metadata blocks,
 * then the audio frames.
 */
public class MetadataBlock {
    private MetadataBlockHeader mbh;
    private MetadataBlockData mbd;

    public MetadataBlock(MetadataBlockHeader mbh, MetadataBlockData mbd) {
        this.mbh = mbh;
        this.mbd = mbd;
    }

    public MetadataBlockHeader getHeader() {
        return mbh;
    }

    public MetadataBlockData getData() {
        return mbd;
    }

    public int getLength() {
        return MetadataBlockHeader.HEADER_LENGTH + mbh.getDataLength();
    }
}
