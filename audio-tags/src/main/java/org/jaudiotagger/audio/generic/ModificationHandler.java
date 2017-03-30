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
import java.util.Enumeration;
import java.util.Vector;
import java.util.Iterator;

/**
 * This class multicasts the events to multiple listener instances.<br>
 * Additionally the Vetos are handled. (other listeners are notified).
 *
 * @author Christian Laireiter
 */
public class ModificationHandler implements AudioFileModificationListener {

    /**
     * The listeners to wich events are broadcasted are stored here.
     */
    private Vector<AudioFileModificationListener> listeners = new Vector<AudioFileModificationListener>();

    /**
     * This method adds an {@link AudioFileModificationListener}
     *
     * @param l Listener to add.
     */
    public void addAudioFileModificationListener(AudioFileModificationListener l) {
        if (!this.listeners.contains(l)) {
            this.listeners.add(l);
        }
    }

    /**
     * (overridden)
     *
     * @see org.jaudiotagger.audio.generic.AudioFileModificationListener#fileModified(org.jaudiotagger.audio.AudioFile,
     * File)
     */
    public void fileModified(AudioFile original, File temporary) throws ModifyVetoException {
        for (AudioFileModificationListener listener : this.listeners) {
            AudioFileModificationListener current = listener;
            try {
                current.fileModified(original, temporary);
            } catch (ModifyVetoException e) {
                vetoThrown(current, original, e);
                throw e;
            }
        }
    }

    /**
     * (overridden)
     *
     * @see org.jaudiotagger.audio.generic.AudioFileModificationListener#fileOperationFinished(File)
     */
    public void fileOperationFinished(File result) {
        for (AudioFileModificationListener listener : this.listeners) {
            AudioFileModificationListener current = listener;
            current.fileOperationFinished(result);
        }
    }

    /**
     * (overridden)
     *
     * @see org.jaudiotagger.audio.generic.AudioFileModificationListener#fileWillBeModified(org.jaudiotagger.audio.AudioFile,
     * boolean)
     */
    public void fileWillBeModified(AudioFile file, boolean delete) throws ModifyVetoException {
        for (AudioFileModificationListener listener : this.listeners) {
            AudioFileModificationListener current = listener;
            try {
                current.fileWillBeModified(file, delete);
            } catch (ModifyVetoException e) {
                vetoThrown(current, file, e);
                throw e;
            }
        }
    }

    /**
     * This method removes an {@link AudioFileModificationListener}
     *
     * @param l Listener to remove.
     */
    public void removeAudioFileModificationListener(AudioFileModificationListener l) {
        if (this.listeners.contains(l)) {
            this.listeners.remove(l);
        }
    }

    /**
     * (overridden)
     *
     * @see org.jaudiotagger.audio.generic.AudioFileModificationListener#vetoThrown(org.jaudiotagger.audio.generic.AudioFileModificationListener,
     * org.jaudiotagger.audio.AudioFile,
     * org.jaudiotagger.audio.exceptions.ModifyVetoException)
     */
    public void vetoThrown(AudioFileModificationListener cause, AudioFile original, ModifyVetoException veto) {
        for (AudioFileModificationListener listener : this.listeners) {
            AudioFileModificationListener current = listener;
            current.vetoThrown(cause, original, veto);
        }
    }
}
