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

package org.jaudiotagger.tag.aiff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jaudiotagger.audio.generic.AbstractTag;
import org.jaudiotagger.audio.generic.GenericTag;
import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.TagTextField;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.images.Artwork;

/**
 * AiffTag wraps ID3Tag for most of its metadata
 */
public class AiffTag /* extends GenericTag */ implements Tag {

    private AbstractID3v2Tag id3Tag;

    /**
     * No-argument constructor
     */
    public AiffTag() {
    }

    public AiffTag(AbstractID3v2Tag t) {
        id3Tag = t;
    }

    /**
     * Returns the ID3 tag
     */
    public AbstractID3v2Tag getID3Tag() {
        return id3Tag;
    }

    /**
     * Sets the ID3 tag
     */
    public void setID3Tag(AbstractID3v2Tag t) {
        id3Tag = t;
    }

    public void addField(TagField field) throws FieldDataInvalidException {
        id3Tag.addField(field);
    }

    public List<TagField> getFields(String id) {
        return id3Tag.getFields(id);
    }

    /**
     * Maps the generic key to the specific key and return the list of values for this field as strings
     *
     * @param genericKey
     * @return
     * @throws KeyNotFoundException
     */
    public List<String> getAll(FieldKey genericKey) throws KeyNotFoundException {
        return id3Tag.getAll(genericKey);
    }

    public boolean hasCommonFields() {
        return id3Tag.hasCommonFields();
    }

    /**
     * Determines whether the tag has no fields specified.<br>
     * <p>
     * <p>If there are no images we return empty if either there is no VorbisTag or if there is a
     * VorbisTag but it is empty
     *
     * @return <code>true</code> if tag contains no field.
     */
    public boolean isEmpty() {
        return (id3Tag == null || id3Tag.isEmpty());
    }

    public void setField(FieldKey genericKey, String value) throws KeyNotFoundException, FieldDataInvalidException {
        TagField tagfield = createField(genericKey, value);
        setField(tagfield);
    }

    public void addField(FieldKey genericKey, String value) throws KeyNotFoundException, FieldDataInvalidException {
        TagField tagfield = createField(genericKey, value);
        addField(tagfield);
    }

    /**
     * @param field
     * @throws FieldDataInvalidException
     */
    public void setField(TagField field) throws FieldDataInvalidException {
        id3Tag.setField(field);
    }


    public TagField createField(FieldKey genericKey, String value) throws KeyNotFoundException, FieldDataInvalidException {
        return id3Tag.createField(genericKey, value);
    }


    public String getFirst(String id) {
        return id3Tag.getFirst(id);
    }

    public String getValue(FieldKey id, int index) throws KeyNotFoundException {
        return id3Tag.getValue(id, index);
    }

    public String getFirst(FieldKey id) throws KeyNotFoundException {
        return getValue(id, 0);
    }

    public TagField getFirstField(String id) {
        return id3Tag.getFirstField(id);
    }

    public TagField getFirstField(FieldKey genericKey) throws KeyNotFoundException {
        if (genericKey == null) {
            throw new KeyNotFoundException();
        } else {
            return id3Tag.getFirstField(genericKey);
        }
    }

    /**
     * Delete any instance of tag fields with this key
     *
     * @param fieldKey
     */
    public void deleteField(FieldKey fieldKey) throws KeyNotFoundException {
        id3Tag.deleteField(fieldKey);
    }

    public void deleteField(String id) throws KeyNotFoundException {
        id3Tag.deleteField(id);
    }

    public Iterator<TagField> getFields() {
        return id3Tag.getFields();
    }

    public int getFieldCount() {
        return id3Tag.getFieldCount();
    }

    public int getFieldCountIncludingSubValues() {
        return getFieldCount();
    }

    public boolean setEncoding(String enc) throws FieldDataInvalidException {
        return id3Tag.setEncoding(enc);
    }

    /**
     * Create artwork field. Not currently supported.
     */
    public TagField createField(Artwork artwork) throws FieldDataInvalidException {
        throw new FieldDataInvalidException("Not supported");
    }

    public void setField(Artwork artwork) throws FieldDataInvalidException {
        throw new FieldDataInvalidException("Not supported");
    }

    public void addField(Artwork artwork) throws FieldDataInvalidException {
        throw new FieldDataInvalidException("Not supported");
    }


    public List<Artwork> getArtworkList() {
        return new ArrayList<Artwork>();
    }

    public List<TagField> getFields(FieldKey id) throws KeyNotFoundException {
        return id3Tag.getFields(id);
    }

    public Artwork getFirstArtwork() {
        return null;
    }

    /**
     * Delete all instance of artwork Field
     *
     * @throws KeyNotFoundException
     */
    public void deleteArtworkField() throws KeyNotFoundException {
    }

    /**
     * @param genericKey
     * @return
     */
    public boolean hasField(FieldKey genericKey) {
        return id3Tag.hasField(genericKey);
    }


    public boolean hasField(String id) {
        return id3Tag.hasField(id);
    }

    public TagField createCompilationField(boolean value) throws KeyNotFoundException, FieldDataInvalidException {
        return createField(FieldKey.IS_COMPILATION, String.valueOf(value));
    }
}
