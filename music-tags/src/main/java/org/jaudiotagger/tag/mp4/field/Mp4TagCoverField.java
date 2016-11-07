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
package org.jaudiotagger.tag.mp4.field;

import org.jaudiotagger.audio.mp4.atom.Mp4BoxHeader;
import org.jaudiotagger.tag.mp4.Mp4FieldKey;
import org.jaudiotagger.tag.mp4.atom.Mp4DataBox;
import org.jaudiotagger.tag.mp4.atom.Mp4NameBox;
import org.jaudiotagger.tag.reference.PictureTypes;
import org.jaudiotagger.tag.id3.valuepair.ImageFormats;
import org.jaudiotagger.logging.ErrorMessage;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Represents Cover Art
 * <p>
 * <p>Note:Within this library we have a seperate TagCoverField for every image stored, however this does not map
 * very directly to how they are physically stored within a file, because all are stored under a single covr atom, so
 * a more complex conversion has to be done then for other fields when writing multiple images back to file.
 */
public class Mp4TagCoverField extends Mp4TagBinaryField {

    //Type
    private Mp4FieldType imageType;

    //Contains the size of each atom including header, required because may only have data atom or
    //may have data and name atom
    private int dataAndHeaderSize;

    /**
     * Empty CoverArt Field
     */
    public Mp4TagCoverField() {
        super(Mp4FieldKey.ARTWORK.getFieldName());
    }

    /**
     * @return data and header size
     */
    public int getDataAndHeaderSize() {
        return dataAndHeaderSize;
    }

    /**
     * Construct CoverField by reading data from audio file
     *
     * @param raw
     * @param imageType
     * @throws UnsupportedEncodingException
     */
    public Mp4TagCoverField(ByteBuffer raw, Mp4FieldType imageType) throws UnsupportedEncodingException {
        super(Mp4FieldKey.ARTWORK.getFieldName(), raw);
        this.imageType = imageType;
        if (!Mp4FieldType.isCoverArtType(imageType)) {
            logger.warning(ErrorMessage.MP4_IMAGE_FORMAT_IS_NOT_TO_EXPECTED_TYPE.getMsg(imageType));
        }
    }

    /**
     * Construct new cover art with binarydata provided
     * <p>
     * <p>
     * Identifies the imageType by looking at the data
     *
     * @param data
     * @throws UnsupportedEncodingException
     */
    public Mp4TagCoverField(byte[] data) {
        super(Mp4FieldKey.ARTWORK.getFieldName(), data);

        //Read signature
        if (ImageFormats.binaryDataIsPngFormat(data)) {
            imageType = Mp4FieldType.COVERART_PNG;
        } else if (ImageFormats.binaryDataIsJpgFormat(data)) {
            imageType = Mp4FieldType.COVERART_JPEG;
        } else if (ImageFormats.binaryDataIsGifFormat(data)) {
            imageType = Mp4FieldType.COVERART_GIF;
        } else if (ImageFormats.binaryDataIsBmpFormat(data)) {
            imageType = Mp4FieldType.COVERART_BMP;
        } else {
            logger.warning(ErrorMessage.GENERAL_UNIDENITIFED_IMAGE_FORMAT.getMsg());
            imageType = Mp4FieldType.COVERART_PNG;
        }
    }


    /**
     * Return field type, for artwork this also identifies the imagetype
     *
     * @return field type
     */
    public Mp4FieldType getFieldType() {
        return imageType;
    }

    public boolean isBinary() {
        return true;
    }


    public String toString() {
        return imageType + ":" + dataBytes.length + "bytes";
    }

    protected void build(ByteBuffer raw) {
        Mp4BoxHeader header = new Mp4BoxHeader(raw);
        dataSize = header.getDataLength();
        dataAndHeaderSize = header.getLength();

        //Skip the version and length fields
        raw.position(raw.position() + Mp4DataBox.PRE_DATA_LENGTH);

        //Read the raw data into byte array
        this.dataBytes = new byte[dataSize - Mp4DataBox.PRE_DATA_LENGTH];
        raw.get(dataBytes, 0, dataBytes.length);

        //Is there room for another atom (remember actually passed all the data so unless Covr is last atom
        //there will be room even though more likely to be for the text top level atom)
        int positionAfterDataAtom = raw.position();
        if (raw.position() + Mp4BoxHeader.HEADER_LENGTH <= raw.limit()) {
            //Is there a following name field (not the norm)
            Mp4BoxHeader nameHeader = new Mp4BoxHeader(raw);
            if (nameHeader.getId().equals(Mp4NameBox.IDENTIFIER)) {
                dataSize += nameHeader.getDataLength();
                dataAndHeaderSize += nameHeader.getLength();
            } else {
                raw.position(positionAfterDataAtom);
            }
        }

        //After returning buffers position will be after the end of this atom
    }

    /**
     * @param imageType
     * @return the corresponding mimetype
     */
    public static String getMimeTypeForImageType(Mp4FieldType imageType) {
        if (imageType == Mp4FieldType.COVERART_PNG) {
            return ImageFormats.MIME_TYPE_PNG;
        } else if (imageType == Mp4FieldType.COVERART_JPEG) {
            return ImageFormats.MIME_TYPE_JPEG;
        } else if (imageType == Mp4FieldType.COVERART_GIF) {
            return ImageFormats.MIME_TYPE_GIF;
        } else if (imageType == Mp4FieldType.COVERART_BMP) {
            return ImageFormats.MIME_TYPE_BMP;
        } else {
            return null;
        }
    }
}
