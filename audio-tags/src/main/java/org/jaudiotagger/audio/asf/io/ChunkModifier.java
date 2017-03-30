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

package org.jaudiotagger.audio.asf.io;

import org.jaudiotagger.audio.asf.data.GUID;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Reads an ASF chunk and writes a modified copy.<br>
 *
 * @author Christian Laireiter
 */
public interface ChunkModifier {

    /**
     * Determines, whether the modifier handles chunks identified by given
     * <code>guid</code>.
     *
     * @param guid GUID to test.
     * @return <code>true</code>, if this modifier can be used to modify the
     * chunk.
     */
    boolean isApplicable(GUID guid);

    /**
     * Writes a modified copy of the chunk into the <code>destination.</code>.<br>
     *
     * @param guid        GUID of the chunk to modify.
     * @param source      a stream providing the chunk, starting at the chunks length
     *                    field.
     * @param destination destination for the modified chunk.
     * @return the differences between source and destination.
     * @throws IOException on I/O errors.
     */
    ModificationResult modify(GUID guid, InputStream source,
                              OutputStream destination) throws IOException;

}
