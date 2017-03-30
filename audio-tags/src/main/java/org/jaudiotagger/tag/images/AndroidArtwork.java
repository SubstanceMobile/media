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

package org.jaudiotagger.tag.images;

import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataPicture;
import org.jaudiotagger.tag.id3.valuepair.ImageFormats;
import org.jaudiotagger.tag.reference.PictureTypes;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Represents artwork in a format independent way
 */
public class AndroidArtwork implements Artwork {
    private byte[] binaryData;
    private String mimeType = "";
    private String description = "";
    private boolean isLinked = false;
    private String imageUrl = "";
    private int pictureType = -1;
    private int width;
    private int height;

    public AndroidArtwork() {

    }

    public byte[] getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDescription() {
        return description;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Should be called when you wish to prime the artwork for saving
     *
     * @return
     */
    public boolean setImageFromData() {
        throw new UnsupportedOperationException();
    }

    public Object getImage() throws IOException {
        throw new UnsupportedOperationException();
    }

    public boolean isLinked() {
        return isLinked;
    }

    public void setLinked(boolean linked) {
        isLinked = linked;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPictureType() {
        return pictureType;
    }

    public void setPictureType(int pictureType) {
        this.pictureType = pictureType;
    }

    /**
     * Create Artwork from File
     *
     * @param file
     * @throws java.io.IOException
     */
    public void setFromFile(File file) throws IOException {
        RandomAccessFile imageFile = new RandomAccessFile(file, "r");
        byte[] imagedata = new byte[(int) imageFile.length()];
        imageFile.read(imagedata);
        imageFile.close();

        setBinaryData(imagedata);
        setMimeType(ImageFormats.getMimeTypeForBinarySignature(imagedata));
        setDescription("");
        setPictureType(PictureTypes.DEFAULT_ID);
    }

    /**
     * Create Artwork from File
     *
     * @param file
     * @return
     * @throws java.io.IOException
     */
    public static AndroidArtwork createArtworkFromFile(File file) throws IOException {
        AndroidArtwork artwork = new AndroidArtwork();
        artwork.setFromFile(file);
        return artwork;
    }

    /**
     * @param url
     * @return
     * @throws IOException
     */
    public static AndroidArtwork createLinkedArtworkFromURL(String url) throws IOException {
        AndroidArtwork artwork = new AndroidArtwork();
        artwork.setLinkedFromURL(url);
        return artwork;
    }

    /**
     * Create Linked Artwork from URL
     *
     * @param url
     * @throws java.io.IOException
     */
    public void setLinkedFromURL(String url) throws IOException {
        setLinked(true);
        setImageUrl(url);
    }


    /**
     * Populate Artwork from MetadataBlockDataPicture as used by Flac and VorbisComment
     *
     * @param coverArt
     */
    public void setFromMetadataBlockDataPicture(MetadataBlockDataPicture coverArt) {
        setMimeType(coverArt.getMimeType());
        setDescription(coverArt.getDescription());
        setPictureType(coverArt.getPictureType());
        if (coverArt.isImageUrl()) {
            setLinked(coverArt.isImageUrl());
            setImageUrl(coverArt.getImageUrl());
        } else {
            setBinaryData(coverArt.getImageData());
        }
        setWidth(coverArt.getWidth());
        setHeight(coverArt.getHeight());
    }

    /**
     * Create artwork from Flac block
     *
     * @param coverArt
     * @return
     */
    public static AndroidArtwork createArtworkFromMetadataBlockDataPicture(MetadataBlockDataPicture coverArt) {
        AndroidArtwork artwork = new AndroidArtwork();
        artwork.setFromMetadataBlockDataPicture(coverArt);
        return artwork;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
