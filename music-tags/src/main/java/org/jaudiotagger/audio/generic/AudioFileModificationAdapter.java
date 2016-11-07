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
package org.jaudiotagger.audio.generic;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.ModifyVetoException;

import java.io.File;

/**
 * @author Christian Laireiter
 */
public class AudioFileModificationAdapter implements AudioFileModificationListener {

    /**
     * (overridden)
     *
     * @see org.jaudiotagger.audio.generic.AudioFileModificationListener#fileModified(org.jaudiotagger.audio.AudioFile,
     * File)
     */
    public void fileModified(AudioFile original, File temporary) throws ModifyVetoException {
        // Nothing to do
    }

    /**
     * (overridden)
     *
     * @see org.jaudiotagger.audio.generic.AudioFileModificationListener#fileOperationFinished(File)
     */
    public void fileOperationFinished(File result) {
        // Nothing to do
    }

    /**
     * (overridden)
     *
     * @see org.jaudiotagger.audio.generic.AudioFileModificationListener#fileWillBeModified(org.jaudiotagger.audio.AudioFile,
     * boolean)
     */
    public void fileWillBeModified(AudioFile file, boolean delete) throws ModifyVetoException {
        // Nothing to do
    }

    /**
     * (overridden)
     *
     * @see org.jaudiotagger.audio.generic.AudioFileModificationListener#vetoThrown(org.jaudiotagger.audio.generic.AudioFileModificationListener,
     * org.jaudiotagger.audio.AudioFile,
     * org.jaudiotagger.audio.exceptions.ModifyVetoException)
     */
    public void vetoThrown(AudioFileModificationListener cause, AudioFile original, ModifyVetoException veto) {
        // Nothing to do
    }

}
