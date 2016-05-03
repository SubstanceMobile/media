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
