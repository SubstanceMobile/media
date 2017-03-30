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

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * Wraps a {@link RandomAccessFile} into an {@link OutputStream}.<br>
 *
 * @author Christian Laireiter
 */
public final class RandomAccessFileOutputStream extends OutputStream {

    /**
     * the file to write to.
     */
    private final RandomAccessFile targetFile;

    /**
     * Creates an instance.<br>
     *
     * @param target file to write to.
     */
    public RandomAccessFileOutputStream(final RandomAccessFile target) {
        super();
        this.targetFile = target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final byte[] bytes, final int off, final int len)
            throws IOException {
        this.targetFile.write(bytes, off, len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final int toWrite) throws IOException {
        this.targetFile.write(toWrite);
    }

}
