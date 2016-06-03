package mobile.substance.sdk.music.playback.cast;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.images.WebImage;

import java.io.IOException;

import mobile.substance.sdk.music.playback.MusicQueue;
import mobile.substance.sdk.music.playback.MusicUtil;
import mobile.substance.sdk.music.playback.PlaybackRemote;

/**
 * Created by Julian Os on 27.03.2016.
 */
public class CastPlaybackHandler implements RemoteMediaPlayer.OnStatusUpdatedListener, RemoteMediaPlayer.OnMetadataUpdatedListener {
    public static final String TAG = CastPlaybackHandler.class.getSimpleName();
    private RemoteMediaPlayer remotePlayer;
    private Context context;
    private GoogleApiClient apiClient;
    private MediaMetadata currentMetadata;
    private MediaInfo currentInfo;
    private MediaStatus currentStatus;
    private FileServer songServer;
    private FileServer artworkServer;

    public CastPlaybackHandler(Context context, GoogleApiClient apiClient) {
        remotePlayer = new RemoteMediaPlayer();
        remotePlayer.setOnMetadataUpdatedListener(this);
        remotePlayer.setOnStatusUpdatedListener(this);
        this.context = context;
        this.apiClient = apiClient;
        songServer = new FileServer(MusicUtil.FILE_PORT, 1);
        artworkServer = new FileServer(MusicUtil.ARTWORK_PORT, 2);
        try {
            songServer.start();
            artworkServer.start();
        } catch (IOException e) {
            Log.d(TAG, "Exception while starting FileServers");
        }
    }

    public MediaMetadata getCurrentMetadata() {
        return currentMetadata;
    }

    public MediaInfo getCurrentMediaInfo() {
        return currentInfo;
    }

    public void load(final long startPosition) {
        try {
            Cast.CastApi.setMessageReceivedCallbacks(apiClient, remotePlayer.getNamespace(), remotePlayer);
        } catch (IOException e) {
            Log.d(TAG, "Exception while creating the media channel!");
        }

        remotePlayer.requestStatus(apiClient)
                .setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
                    @Override
                    public void onResult(@NonNull RemoteMediaPlayer.MediaChannelResult mStatus) {
                        if (!mStatus.getStatus().isSuccess()) {
                            Toast.makeText(context, "Failed to load the song! STATE 1", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                remotePlayer.load(apiClient, createMediaInfo(), true, startPosition)
                                        .setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
                                            @Override
                                            public void onResult(@NonNull RemoteMediaPlayer.MediaChannelResult mStatus) {
                                                if (!mStatus.getStatus().isSuccess()) {
                                                    Toast.makeText(context, "Failed to load the song! STATE 2", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } catch (IllegalStateException e) {
                                Log.e(TAG, "Problem occurred with song during loading", e);
                            } catch (Exception e) {
                                Log.e(TAG, "Problem opening song during loading", e);
                            }
                        }
                    }
                });
    }

    public void pause() {
        remotePlayer.pause(apiClient)
                .setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
                    @Override
                    public void onResult(@NonNull RemoteMediaPlayer.MediaChannelResult mStatus) {
                        if (!mStatus.getStatus().isSuccess()) {
                            Toast.makeText(context, "Failed to pause! " + mStatus.getStatus().getStatusCode(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void resume() {
        remotePlayer.play(apiClient)
                .setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
                    @Override
                    public void onResult(@NonNull RemoteMediaPlayer.MediaChannelResult mStatus) {
                        if (!mStatus.getStatus().isSuccess()) {
                            Toast.makeText(context, "Failed to resume! " + mStatus.getStatus().getStatusCode(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public boolean isPlaying() {
        return isConnected() && remotePlayer != null && remotePlayer.getMediaStatus() != null && remotePlayer.getMediaStatus().getPlayerState() == MediaStatus.PLAYER_STATE_PLAYING;
    }

    public boolean isConnected() {
        return apiClient != null && apiClient.isConnected();
    }

    public void seek(int newPosition, boolean resume) {
        remotePlayer.seek(apiClient, newPosition)
                .setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
                    @Override
                    public void onResult(@NonNull RemoteMediaPlayer.MediaChannelResult mStatus) {
                        if (!mStatus.getStatus().isSuccess()) {
                            Toast.makeText(context, "Failed to seek! " + mStatus.getStatus().getStatusCode(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void stop() {
        remotePlayer.stop(apiClient)
                .setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
                    @Override
                    public void onResult(@NonNull RemoteMediaPlayer.MediaChannelResult mStatus) {
                        if (!mStatus.getStatus().isSuccess()) {
                            Toast.makeText(context, "Failed to stop! " + mStatus.getStatus().getStatusCode(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public int getPlaybackPosition() {
        return (int) remotePlayer.getApproximateStreamPosition();
    }

    public int getCurrentDuration() {
        return (int) remotePlayer.getMediaInfo().getStreamDuration();
    }

    public MediaStatus getMediaStatus() {
        return currentStatus;
    }

    public int getPlayerState() {
        return currentStatus.getPlayerState();
    }

    @Override
    public void onStatusUpdated() {
        currentStatus = remotePlayer.getMediaStatus();
        if (currentStatus != null && currentStatus.getPlayerState() == MediaStatus.PLAYER_STATE_IDLE && currentStatus.getIdleReason() == MediaStatus.IDLE_REASON_FINISHED)
            PlaybackRemote.INSTANCE.skipForward();
    }

    @Override
    public void onMetadataUpdated() {
        currentInfo = remotePlayer.getMediaInfo();
        if (currentInfo != null)
            currentMetadata = currentInfo.getMetadata();
    }

    private MediaInfo createMediaInfo() {
        String finalUrl = null;
        songServer.serve(null);
        finalUrl = "http://" + MusicUtil.getIP(context) + MusicUtil.FILE_PORT;
        Toast.makeText(context, finalUrl, Toast.LENGTH_LONG).show();
        MediaInfo mInfo = new MediaInfo.Builder(finalUrl)
                .setContentType("audio/*")
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(createCastMetadata())
                .build();

        return mInfo;
    }

    private MediaMetadata createCastMetadata() {
        MediaMetadataCompat base = buildMetadata();
        MediaMetadata data = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK);
        data.putString(MediaMetadata.KEY_TITLE, base.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        data.putString(MediaMetadata.KEY_ARTIST, base.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        data.putString(MediaMetadata.KEY_ALBUM_TITLE, base.getString(MediaMetadataCompat.METADATA_KEY_ALBUM));
        artworkServer.serve(null);
        data.addImage(new WebImage(Uri.parse("http://" + MusicUtil.getIP(context) + MusicUtil.ARTWORK_PORT)));
        return data;
    }

    private MediaMetadataCompat buildMetadata() {
        return MusicQueue.INSTANCE.getCurrentSong().getMetadata();
    }
}
