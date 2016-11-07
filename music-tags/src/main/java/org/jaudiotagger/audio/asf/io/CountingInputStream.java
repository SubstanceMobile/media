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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This implementation of {@link FilterInputStream} counts each read byte.<br>
 * So at each time, with {@link #getReadCount()} one can determine how many
 * bytes have been read, by this classes read and skip methods (mark and reset
 * are also taken into account).<br>
 *
 * @author Christian Laireiter
 */
class CountingInputStream extends FilterInputStream {

    /**
     * If {@link #mark(int)} has been called, the current value of
     * {@link #readCount} is stored, in order to reset it upon {@link #reset()}.
     */
    private long markPos;

    /**
     * The amount of read or skipped bytes.
     */
    private long readCount;

    /**
     * Creates an instance, which delegates the commands to the given stream.
     *
     * @param stream stream to actually work with.
     */
    public CountingInputStream(final InputStream stream) {
        super(stream);
        this.markPos = 0;
        this.readCount = 0;
    }

    /**
     * Counts the given amount of bytes.
     *
     * @param amountRead number of bytes to increase.
     */
    private synchronized void bytesRead(final long amountRead) {
        if (amountRead >= 0) {
            this.readCount += amountRead;
        }
    }

    /**
     * @return the readCount
     */
    public synchronized long getReadCount() {
        return this.readCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void mark(final int readlimit) {
        super.mark(readlimit);
        this.markPos = this.readCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        final int result = super.read();
        bytesRead(1);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(final byte[] destination, final int off, final int len)
            throws IOException {
        final int result = super.read(destination, off, len);
        bytesRead(result);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void reset() throws IOException {
        super.reset();
        synchronized (this) {
            this.readCount = this.markPos;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long skip(final long amount) throws IOException {
        final long skipped = super.skip(amount);
        bytesRead(skipped);
        return skipped;
    }

}
