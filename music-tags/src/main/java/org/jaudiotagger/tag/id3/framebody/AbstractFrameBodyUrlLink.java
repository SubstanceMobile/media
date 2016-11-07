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
package org.jaudiotagger.tag.id3.framebody;

import org.jaudiotagger.logging.ErrorMessage;
import org.jaudiotagger.tag.InvalidTagException;
import org.jaudiotagger.tag.datatype.DataTypes;
import org.jaudiotagger.tag.datatype.StringSizeTerminated;
import org.jaudiotagger.tag.id3.valuepair.TextEncoding;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * Abstract super class of all URL Frames
 */
public abstract class AbstractFrameBodyUrlLink extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyUrlLink datatype.
     */
    protected AbstractFrameBodyUrlLink() {
        super();
    }

    /**
     * Copy Constructor
     * @param body
     */
    protected AbstractFrameBodyUrlLink(AbstractFrameBodyUrlLink body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyUrlLink datatype., set up with data.
     *
     * @param urlLink
     */
    public AbstractFrameBodyUrlLink(String urlLink) {
        setObjectValue(DataTypes.OBJ_URLLINK, urlLink);
    }

    /**
     * Creates a new FrameBodyUrlLink datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    protected AbstractFrameBodyUrlLink(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    public String getUserFriendlyValue() {
        return getUrlLink();
    }

    /**
     * Set URL Link
     *
     * @param urlLink
     */
    public void setUrlLink(String urlLink) {
        if (urlLink == null) {
            throw new IllegalArgumentException(ErrorMessage.GENERAL_INVALID_NULL_ARGUMENT.getMsg());
        }
        setObjectValue(DataTypes.OBJ_URLLINK, urlLink);
    }

    /**
     * Get URL Link
     *
     * @return the urllink
     */
    public String getUrlLink() {
        return (String) getObjectValue(DataTypes.OBJ_URLLINK);
    }

    /**
     * If the description cannot be encoded using the current encoding change the encoder
     */
    public void write(ByteArrayOutputStream tagBuffer) {
        CharsetEncoder encoder = Charset.forName(TextEncoding.CHARSET_ISO_8859_1).newEncoder();
        String origUrl = getUrlLink();
        if (!encoder.canEncode(origUrl)) {
            //ALL W Frames only support ISO-8859-1 for the url itself, if unable to encode let us assume
            //the link just needs url encoding
            setUrlLink(encodeURL(origUrl));

            //We still cant convert so just set log error and set to blank to allow save to continue
            if (!encoder.canEncode(getUrlLink())) {
                logger.warning(ErrorMessage.MP3_UNABLE_TO_ENCODE_URL.getMsg(origUrl));
                setUrlLink("");
            }
            //it was ok, just note the modification made
            else {
                logger.warning(ErrorMessage.MP3_URL_SAVED_ENCODED.getMsg(origUrl, getUrlLink()));
            }
        }
        super.write(tagBuffer);
    }

    /**
     *
     */
    protected void setupObjectList() {
        objectList.add(new StringSizeTerminated(DataTypes.OBJ_URLLINK, this));
    }

    /**
     * Encode url because may receive url already encoded or not, but we can only store as ISO8859-1
     *
     * @param url
     * @return
     */
    private String encodeURL(String url) {
        try {
            final String[] splitURL = url.split("(?<!/)/(?!/)", -1);
            final StringBuffer sb = new StringBuffer(splitURL[0]);
            for (int i = 1; i < splitURL.length; i++) {
                sb.append("/").append(URLEncoder.encode(splitURL[i], "utf-8"));
            }
            return sb.toString();
        } catch (UnsupportedEncodingException uee) {
            //Should never happen as utf-8 is always availablebut in case it does we just return the utl
            //unmodified
            logger.warning("Uable to url encode because utf-8 charset not available:" + uee.getMessage());
            return url;
        }
    }
}