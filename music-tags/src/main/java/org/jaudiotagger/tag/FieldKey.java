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

package org.jaudiotagger.tag;

/**
 * This is an enumeration of fields implemented by all major formats
 * <p>
 * <p>
 * <p>
 * This enumeration is used by subclasses to map from the common key to their implementation key, the keys
 * are grouped within EnumSets within Tag class.
 */
public enum FieldKey {
    ACOUSTID_FINGERPRINT,
    ACOUSTID_ID,
    ALBUM,
    ALBUM_ARTIST,
    ALBUM_ARTIST_SORT,
    ALBUM_SORT,
    AMAZON_ID,
    ARRANGER,
    ARTIST,
    ARTIST_SORT,
    ARTISTS,
    BARCODE,
    BPM,
    CATALOG_NO,
    COMMENT,
    COMPOSER,
    COMPOSER_SORT,
    CONDUCTOR,
    COUNTRY,
    COVER_ART,
    CUSTOM1,
    CUSTOM2,
    CUSTOM3,
    CUSTOM4,
    CUSTOM5,
    DISC_NO,
    DISC_SUBTITLE,
    DISC_TOTAL,
    DJMIXER,
    ENCODER,
    ENGINEER,
    FBPM,
    GENRE,
    GROUPING,
    ISRC,
    IS_COMPILATION,
    KEY,
    LANGUAGE,
    LYRICIST,
    LYRICS,
    MEDIA,
    MIXER,
    MOOD,
    MUSICBRAINZ_ARTISTID,
    MUSICBRAINZ_DISC_ID,
    MUSICBRAINZ_ORIGINAL_RELEASE_ID,
    MUSICBRAINZ_RELEASEARTISTID,
    MUSICBRAINZ_RELEASEID,
    MUSICBRAINZ_RELEASE_COUNTRY,
    MUSICBRAINZ_RELEASE_GROUP_ID,
    MUSICBRAINZ_RELEASE_STATUS,
    MUSICBRAINZ_RELEASE_TRACK_ID,
    MUSICBRAINZ_RELEASE_TYPE,
    MUSICBRAINZ_TRACK_ID,
    MUSICBRAINZ_WORK_ID,
    MUSICIP_ID,
    OCCASION,
    ORIGINAL_ALBUM,
    ORIGINAL_ARTIST,
    ORIGINAL_LYRICIST,
    ORIGINAL_YEAR,
    QUALITY,
    PRODUCER,
    RATING,
    RECORD_LABEL,
    REMIXER,
    SCRIPT,
    SUBTITLE,
    TAGS,
    TEMPO,
    TITLE,
    TITLE_SORT,
    TRACK,
    TRACK_TOTAL,
    URL_DISCOGS_ARTIST_SITE,
    URL_DISCOGS_RELEASE_SITE,
    URL_LYRICS_SITE,
    URL_OFFICIAL_ARTIST_SITE,
    URL_OFFICIAL_RELEASE_SITE,
    URL_WIKIPEDIA_ARTIST_SITE,
    URL_WIKIPEDIA_RELEASE_SITE,
    YEAR,;
}
