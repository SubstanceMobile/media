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

/**
 * This output stream wraps around another {@link OutputStream} and delegates
 * the write calls.<br>
 * Additionally all written bytes are counted and available by
 * {@link #getCount()}.
 *
 * @author Christian Laireiter
 */
public class CountingOutputstream extends OutputStream {

    /**
     * Stores the amount of bytes written.
     */
    private long count = 0;

    /**
     * The stream to forward the write calls.
     */
    private final OutputStream wrapped;

    /**
     * Creates an instance which will delegate the write calls to the given
     * output stream.
     *
     * @param outputStream stream to wrap.
     */
    public CountingOutputstream(final OutputStream outputStream) {
        super();
        assert outputStream != null;
        this.wrapped = outputStream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        this.wrapped.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() throws IOException {
        this.wrapped.flush();
    }

    /**
     * @return the count
     */
    public long getCount() {
        return this.count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final byte[] bytes) throws IOException {
        this.wrapped.write(bytes);
        this.count += bytes.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final byte[] bytes, final int off, final int len)
            throws IOException {
        this.wrapped.write(bytes, off, len);
        this.count += len;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final int toWrite) throws IOException {
        this.wrapped.write(toWrite);
        this.count++;
    }

}
