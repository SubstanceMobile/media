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
