package mobile.substance.sdk.music.playback.cast;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class ConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks, RouteListener {
    private GoogleApiClient apiClient;
    private boolean isWaitingForReconnect = false;
    private boolean isApplicationStarted = false;
    private String sessionId;
    private String applicationId;
    private MediaRouterCallback callback;
    private ConnectionResultListener listener;
    private boolean isConnected = false;

    public ConnectionCallbacks(MediaRouterCallback mCallback, ConnectionResultListener listener) {
        this.callback = mCallback;
        this.listener = listener;
        mCallback.setRouteListener(this);
    }

    public void setApiClient(GoogleApiClient mApiClient) {
        this.apiClient = mApiClient;
    }

    public void setApplicationid(String applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        isConnected = true;
        if (isWaitingForReconnect) {
            isWaitingForReconnect = false;
            Log.d(ConnectionCallbacks.class.getSimpleName(), "Waiting for Reconnect...");
        } else {
            try {
                if (applicationId != null)
                    Cast.CastApi.launchApplication(apiClient, applicationId)
                        .setResultCallback(
                                new ResultCallback<Cast.ApplicationConnectionResult>() {
                                    @Override
                                    public void onResult(Cast.ApplicationConnectionResult result) {
                                        Status status = result.getStatus();
                                        if (status.isSuccess()) {
                                            isApplicationStarted = true;
                                            ApplicationMetadata applicationMetadata = result.getApplicationMetadata();
                                            sessionId = result.getSessionId();
                                            if (result.getWasLaunched())
                                                listener.onApplicationConnected();
                                        } else {
                                            isApplicationStarted = false;
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
        isWaitingForReconnect = true;
    }

    public boolean isConnected() {
        return isConnected;
    }

    private void teardown() {
        if (apiClient != null) {
            if (isApplicationStarted) {
                if (apiClient.isConnected() || apiClient.isConnecting()) {
                    Cast.CastApi.stopApplication(apiClient, sessionId);
                    apiClient.disconnect();
                }
                isApplicationStarted = false;
            }
            apiClient = null;
        }
        callback.nullDevice();
        isWaitingForReconnect = false;
        sessionId = null;
    }

    @Override
    public void onRouteUnselected() {
        teardown();
    }
}
