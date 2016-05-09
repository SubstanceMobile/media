package mobile.substance.sdk.music.playback.cast;

import android.support.v7.media.MediaRouter;

import com.google.android.gms.cast.CastDevice;

/**
 * Created by Julian Os on 27.03.2016.
 */
public class MediaRouterCallback extends MediaRouter.Callback {
    private CastDevice mSelectedDevice;
    private String mRouteID;
    private CastCallbacks mCallbacks;
    private RouteListener mRouteListener;

    public MediaRouterCallback(CastCallbacks mCallbacks) {
        this.mCallbacks = mCallbacks;
    }

    public CastDevice getSelectedDevice() {
        return mSelectedDevice;
    }

    public void nullDevice() {
        mSelectedDevice = null;
    }

    public String getRouteID() {
        return mRouteID;
    }

    public void setRouteListener(RouteListener mRouteListener) {
        this.mRouteListener = mRouteListener;
    }

    @Override
    public void onRouteSelected(MediaRouter mRouter, MediaRouter.RouteInfo mRoute) {
        mSelectedDevice = CastDevice.getFromBundle(mRoute.getExtras());
        mRouteID = mRoute.getId();
        mCallbacks.onCastDeviceSelected(mSelectedDevice);
    }

    @Override
    public void onRouteUnselected(MediaRouter mRouter, MediaRouter.RouteInfo mRoute) {
        if (mRouteListener != null)
            mRouteListener.onRouteUnselected();
        mSelectedDevice = null;
    }
}
