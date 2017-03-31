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
package org.jaudiotagger.tag.datatype;


public class DataTypes {
    /**
     * Represents a text encoding, now only IDv2Frames not Lyrics3 tags use
     * text encoding objects but both use Object Strings and these check
     * for a text encoding. The method below returns a default if one not set.
     */
    public static final String OBJ_TEXT_ENCODING = "TextEncoding";
    //Reference to datatype holding the main textual data
    public static final String OBJ_TEXT = "Text";
    //Reference to datatype holding non textual textual data
    public static final String OBJ_DATA = "Data";
    //Reference to datatype holding a description of the textual data
    public static final String OBJ_DESCRIPTION = "Description";
    //Reference to datatype holding reference to owner of frame.
    public static final String OBJ_OWNER = "Owner";
    //Reference to datatype holding a number
    public static final String OBJ_NUMBER = "Number";
    //Reference to timestamps
    public static final String OBJ_DATETIME = "DateTime";
    /**
     *
     */
    public static final String OBJ_GENRE = "Genre";
    /**
     *
     */
    public static final String OBJ_ID3V2_FRAME_DESCRIPTION = "ID3v2FrameDescription";

    //ETCO Frame
    public static final String OBJ_TYPE_OF_EVENT = "TypeOfEvent";
    public static final String OBJ_TIMED_EVENT = "TimedEvent";
    public static final String OBJ_TIMED_EVENT_LIST = "TimedEventList";
    //SYTC Frame
    public static final String OBJ_SYNCHRONISED_TEMPO_DATA = "SynchronisedTempoData";
    public static final String OBJ_SYNCHRONISED_TEMPO = "SynchronisedTempo";
    public static final String OBJ_SYNCHRONISED_TEMPO_LIST = "SynchronisedTempoList";
    /**
     *
     */
    public static final String OBJ_TIME_STAMP_FORMAT = "TimeStampFormat";
    /**
     *
     */
    public static final String OBJ_TYPE_OF_CHANNEL = "TypeOfChannel";
    /**
     *
     */
    public static final String OBJ_RECIEVED_AS = "RecievedAs";

    //APIC Frame
    public static final String OBJ_PICTURE_TYPE = "PictureType";
    public static final String OBJ_PICTURE_DATA = "PictureData";
    public static final String OBJ_MIME_TYPE = "MIMEType";
    public static final String OBJ_IMAGE_FORMAT = "ImageType";

    //AENC Frame
    public static final String OBJ_PREVIEW_START = "PreviewStart";
    public static final String OBJ_PREVIEW_LENGTH = "PreviewLength";
    public static final String OBJ_ENCRYPTION_INFO = "EncryptionInfo";

    //COMR Frame
    public static final String OBJ_PRICE_STRING = "PriceString";
    public static final String OBJ_VALID_UNTIL = "ValidUntil";
    public static final String OBJ_CONTACT_URL = "ContactURL";
    public static final String OBJ_SELLER_NAME = "SellerName";
    public static final String OBJ_SELLER_LOGO = "SellerLogo";

    //CRM Frame
    public static final String OBJ_ENCRYPTED_DATABLOCK = "EncryptedDataBlock";

    //ENCR Frame
    public static final String OBJ_METHOD_SYMBOL = "MethodSymbol";

    //EQU2 Frame
    public static final String OBJ_FREQUENCY = "Frequency";
    public static final String OBJ_VOLUME_ADJUSTMENT = "Volume Adjustment";
    public static final String OBJ_INTERPOLATION_METHOD = "InterpolationMethod";

    public static final String OBJ_FILENAME = "Filename";

    //GRID Frame
    public static final String OBJ_GROUP_SYMBOL = "GroupSymbol";
    public static final String OBJ_GROUP_DATA = "GroupData";

    //LINK Frame
    public static final String OBJ_URL = "URL";
    public static final String OBJ_ID = "ID";

    //OWNE Frame
    public static final String OBJ_PRICE_PAID = "PricePaid";
    public static final String OBJ_PURCHASE_DATE = "PurchaseDate";

    //POPM Frame
    public static final String OBJ_EMAIL = "Email";
    public static final String OBJ_RATING = "Rating";
    public static final String OBJ_COUNTER = "Counter";

    //POSS Frame
    public static final String OBJ_POSITION = "Position";

    //RBUF Frame
    public static final String OBJ_BUFFER_SIZE = "BufferSize";
    public static final String OBJ_EMBED_FLAG = "EmbedFlag";
    public static final String OBJ_OFFSET = "Offset";

    //RVRB Frame
    public static final String OBJ_REVERB_LEFT = "ReverbLeft";
    public static final String OBJ_REVERB_RIGHT = "ReverbRight";
    public static final String OBJ_REVERB_BOUNCE_LEFT = "ReverbBounceLeft";
    public static final String OBJ_REVERB_BOUNCE_RIGHT = "ReverbBounceRight";
    public static final String OBJ_REVERB_FEEDBACK_LEFT_TO_LEFT = "ReverbFeedbackLeftToLeft";
    public static final String OBJ_REVERB_FEEDBACK_LEFT_TO_RIGHT = "ReverbFeedbackLeftToRight";
    public static final String OBJ_REVERB_FEEDBACK_RIGHT_TO_RIGHT = "ReverbFeedbackRightToRight";
    public static final String OBJ_REVERB_FEEDBACK_RIGHT_TO_LEFT = "ReverbFeedbackRightToLeft";
    public static final String OBJ_PREMIX_LEFT_TO_RIGHT = "PremixLeftToRight";
    public static final String OBJ_PREMIX_RIGHT_TO_LEFT = "PremixRightToLeft";

    //SIGN Frame
    public static final String OBJ_SIGNATURE = "Signature";

    //SYLT Frame
    public static final String OBJ_CONTENT_TYPE = "contentType";

    //ULST Frame
    public static final String OBJ_LANGUAGE = "Language";
    public static final String OBJ_LYRICS = "Lyrics";
    public static final String OBJ_URLLINK = "URLLink";

    //CHAP Frame
    public static final String OBJ_ELEMENT_ID = "ElementID";
    public static final String OBJ_START_TIME = "StartTime";
    public static final String OBJ_END_TIME = "EndTime";
    public static final String OBJ_START_OFFSET = "StartOffset";
    public static final String OBJ_END_OFFSET = "EndOffset";

    //CTOC Frame
}