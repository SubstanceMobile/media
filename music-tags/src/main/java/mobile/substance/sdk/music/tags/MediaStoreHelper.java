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

package mobile.substance.sdk.music.tags;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

public class MediaStoreHelper {

    public static void updateMedia(final String[] paths, Context context, final MediaStoreCallbacks callbacks) {
        String[] mimeTypes = new String[paths.length];
        for (int i = 0; i < paths.length; i++) {
            mimeTypes[i] = "audio/" + paths[i].substring(paths[i].lastIndexOf(".") + 1, paths[i].length());
        }

        final int[] count = {0};
        MediaScannerConnection.scanFile(context, paths, mimeTypes, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                count[0]++;
                callbacks.onScanFinished(path, uri);

                if (count[0] == paths.length)
                    callbacks.onAllFinished();
            }
        });
    }
}
