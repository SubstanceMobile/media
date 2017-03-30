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
package org.jaudiotagger.audio.asf.util;

import org.jaudiotagger.audio.asf.data.Chunk;

import java.io.Serializable;
import java.util.Comparator;

/**
 * This class is needed for ordering all types of
 * {@link org.jaudiotagger.audio.asf.data.Chunk}s ascending by their Position. <br>
 *
 * @author Christian Laireiter
 */
public final class ChunkPositionComparator implements Comparator<Chunk>,
        Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6337108235272376289L;

    /**
     * {@inheritDoc}
     */
    public int compare(final Chunk first, final Chunk second) {
        return Long.valueOf(first.getPosition())
                .compareTo(second.getPosition());
    }
}