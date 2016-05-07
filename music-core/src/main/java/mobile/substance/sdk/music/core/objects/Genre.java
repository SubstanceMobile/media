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

package mobile.substance.sdk.music.core.objects;

import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;

public class Genre extends MediaObject {

    @Override
    protected Uri getBaseUri() {
        return MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Name
    ///////////////////////////////////////////////////////////////////////////

    public String getGenreName() {
        return data.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
    }

    public void setGenreName(String genreName) {
        putString(MediaMetadataCompat.METADATA_KEY_TITLE, genreName);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Builder
    ///////////////////////////////////////////////////////////////////////////

    public static class Builder {
        private Genre genre;

        public Builder() {
            this.genre = new Genre();
        }

        public Builder setGenreName(String genreName) {
            this.genre.setGenreName(genreName);
            return this;
        }

        public Builder setGenreId(long genreId) {
            this.genre.setID(genreId);
            return this;
        }

        public Genre build() {
            return genre;
        }
    }

}
