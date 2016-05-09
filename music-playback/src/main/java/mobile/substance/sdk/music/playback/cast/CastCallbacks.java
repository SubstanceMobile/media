package mobile.substance.sdk.music.playback.cast;

import com.google.android.gms.cast.CastDevice;

/**
 * Created by Julian Os on 27.03.2016.
 */
public interface CastCallbacks {

    void onCastDeviceSelected(CastDevice mDevice);

    void onCastDeviceUnselected();

}
