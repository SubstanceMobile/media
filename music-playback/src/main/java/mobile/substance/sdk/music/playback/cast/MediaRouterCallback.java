package mobile.substance.sdk.music.playback.cast;

import android.support.v7.media.MediaRouter;

import com.google.android.gms.cast.CastDevice;

/**
 * Created by Julian Os on 27.03.2016.
 */
public class MediaRouterCallback extends MediaRouter.Callback {
    private CastDevice selectedDevice;
    private String routeId;
    private CastCallbacks callbacks;
    private RouteListener routeListener;

    public MediaRouterCallback(CastCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    public CastDevice getSelectedDevice() {
        return selectedDevice;
    }

    public void nullDevice() {
        selectedDevice = null;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteListener(RouteListener mRouteListener) {
        this.routeListener = mRouteListener;
    }

    @Override
    public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo routeInfo) {
        selectedDevice = CastDevice.getFromBundle(routeInfo.getExtras());
        routeId = routeInfo.getId();
        callbacks.onCastDeviceSelected(selectedDevice);
    }

    @Override
    public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo route) {
        if (routeListener != null)
            routeListener.onRouteUnselected();
        selectedDevice = null;
    }
}
