package mobile.substance.sdk.music.tags;

import android.net.Uri;

public interface MediaStoreCallbacks {

    void onScanFinished(String path, Uri uri);

    void onAllFinished();

}
