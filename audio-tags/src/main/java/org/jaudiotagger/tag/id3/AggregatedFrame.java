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

package org.jaudiotagger.tag.id3;

import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.TagTextField;
import org.jaudiotagger.tag.id3.valuepair.TextEncoding;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Required when a single generic field maps to multiple ID3 Frames
 */
public class AggregatedFrame implements TagTextField {
    //TODO rather than just maintaining insertion order we want to define a preset order
    protected Set<AbstractID3v2Frame> frames = new LinkedHashSet<AbstractID3v2Frame>();

    public void addFrame(AbstractID3v2Frame frame) {
        frames.add(frame);
    }

    public Set<AbstractID3v2Frame> getFrames() {
        return frames;
    }

    /**
     * Returns the content of the underlying frames in order.
     *
     * @return Content
     */
    public String getContent() {
        StringBuilder sb = new StringBuilder();
        for (AbstractID3v2Frame next : frames) {
            sb.append(next.getContent());
        }
        return sb.toString();
    }

    /**
     * Returns the current used charset encoding.
     *
     * @return Charset encoding.
     */
    public String getEncoding() {
        return TextEncoding.getInstanceOf().getValueForId(frames.iterator().next().getBody().getTextEncoding());
    }

    /**
     * Sets the content of the field.
     *
     * @param content fields content.
     */
    public void setContent(String content) {

    }

    /**
     * Sets the charset encoding used by the field.
     *
     * @param encoding charset.
     */
    public void setEncoding(String encoding) {

    }

    //TODO:needs implementing but not sure if this method is required at all
    public void copyContent(TagField field) {

    }

    public String getId() {
        StringBuilder sb = new StringBuilder();
        for (AbstractID3v2Frame next : frames) {
            sb.append(next.getId());
        }
        return sb.toString();
    }


    public boolean isCommon() {
        return true;
    }

    public boolean isBinary() {
        return false;
    }

    public void isBinary(boolean b) {
        ;
    }

    public boolean isEmpty() {
        return false;
    }

    public byte[] getRawContent() throws UnsupportedEncodingException {
        throw new UnsupportedEncodingException();
    }
}
