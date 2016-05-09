package mobile.substance.sdk.music.playback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Julian Os on 30.01.2016.
 */
public class HeadsetPlugReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PlaybackRemote.INSTANCE.pause();
    }
}