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
 * Padding Block
 * <p>
 * <p>This block allows for an arbitrary amount of padding. The contents of a PADDING block have no meaning.
 * This block is useful when it is known that metadata will be edited after encoding; the user can instruct the encoder
 * to reserve a PADDING block of sufficient size so that when metadata is added, it will simply overwrite the padding
 * (which is relatively quick) instead of having to insert it into the right place in the existing file
 * (which would normally require rewriting the entire file).
 */
public class MetadataBlockDataPadding implements MetadataBlockData {
    private int length;

    public MetadataBlockDataPadding(int length) {
        this.length = length;
    }

    public byte[] getBytes() {
        byte[] data = new byte[length];
        for (int i = 0; i < length; i++) {
            data[i] = 0;
        }
        return data;
    }

    public int getLength() {
        return length;
    }
}
