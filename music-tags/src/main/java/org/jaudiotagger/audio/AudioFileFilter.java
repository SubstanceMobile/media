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
package org.jaudiotagger.audio;

import org.jaudiotagger.audio.generic.Utils;

import java.io.FileFilter;
import java.io.File;

/**
 * <p>This is a simple FileFilter that will only allow the file supported by this library.
 * <p>It will also accept directories. An additional condition is that file must be readable (read permission) and
 * are not hidden (dot files, or hidden files)
 *
 * @author Raphael Slinckx
 * @version $Id$
 * @since v0.01
 */
public class AudioFileFilter implements FileFilter {
    /**
     * allows Directories
     */
    private final boolean allowDirectories;

    public AudioFileFilter(boolean allowDirectories) {
        this.allowDirectories = allowDirectories;
    }

    public AudioFileFilter() {
        this(true);
    }

    /**
     * <p>Check whether the given file meet the required conditions (supported by the library OR directory).
     * The File must also be readable and not hidden.
     *
     * @param f The file to test
     * @return a boolean indicating if the file is accepted or not
     */
    public boolean accept(File f) {
        if (f.isHidden() || !f.canRead()) {
            return false;
        }

        if (f.isDirectory()) {
            return allowDirectories;
        }

        String ext = Utils.getExtension(f);

        try {
            if (SupportedFileFormat.valueOf(ext.toUpperCase()) != null) {
                return true;
            }
        } catch (IllegalArgumentException iae) {
            //Not known enum value
            return false;
        }
        return false;
    }
}
