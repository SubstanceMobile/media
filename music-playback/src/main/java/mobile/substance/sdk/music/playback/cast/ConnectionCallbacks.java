package mobile.substance.sdk.music.playback.cast;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class ConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks, RouteListener {
    private GoogleApiClient mApiClient;
    private boolean mWaitingForReconnect = false;
    private boolean mApplicationStarted = false;
    private String mSessionID;
    private MediaRouterCallback mCallback;
    private ConnectionResultListener mListener;
    private boolean isConnected = false;

    public ConnectionCallbacks(MediaRouterCallback mCallback, ConnectionResultListener mListener) {
        this.mCallback = mCallback;
        this.mListener = mListener;
        mCallback.setRouteListener(this);
    }

    public void setApiClient(GoogleApiClient mApiClient) {
        this.mApiClient = mApiClient;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        isConnected = true;
        if (mWaitingForReconnect) {
            mWaitingForReconnect = false;
            Log.d(ConnectionCallbacks.class.getSimpleName(), "Waiting for Reconnect...");
        } else {
            try {
                Cast.CastApi.launchApplication(mApiClient, "FF7DFFB0")
                        .setResultCallback(
                                new ResultCallback<Cast.ApplicationConnectionResult>() {
                                    @Override
                                    public void onResult(Cast.ApplicationConnectionResult mResult) {
                                        Status mStatus = mResult.getStatus();
                                        if (mStatus.isSuccess()) {
                                            mApplicationStarted = true;
                                            ApplicationMetadata applicationMetadata =
                                                    mResult.getApplicationMetadata();
                                            mSessionID = mResult.getSessionId();
                                            if (mResult.getWasLaunched())
                                                mListener.onApplicationConnected();
                                        } else {
                                            mApplicationStarted = false;
                                            teardown();
                                        }
                                    }
                                });

            } catch (Exception e) {
                Log.e(ConnectionCallbacks.class.getSimpleName(), "Failed to launch application", e);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        isConnected = false;
        mWaitingForReconnect = true;
    }

    public boolean isConnected() {
        return isConnected;
    }

    private void teardown() {
        if (mApiClient != null) {
            if (mApplicationStarted) {
                if (mApiClient.isConnected() || mApiClient.isConnecting()) {
                    Cast.CastApi.stopApplication(mApiClient, mSessionID);
                    mApiClient.disconnect();
                }
                mApplicationStarted = false;
            }
            mApiClient = null;
        }
        mCallback.nullDevice();
        mWaitingForReconnect = false;
        mSessionID = null;
    }

    @Override
    public void onRouteUnselected() {
        teardown();
    }
}
