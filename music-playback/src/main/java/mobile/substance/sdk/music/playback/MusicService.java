package mobile.substance.sdk.music.playback;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mobile.substance.sdk.music.core.CoreUtil;
import mobile.substance.sdk.music.core.objects.Song;
import mobile.substance.sdk.music.loading.Library;
import mobile.substance.sdk.music.playback.cast.CastCallbacks;
import mobile.substance.sdk.music.playback.cast.CastPlaybackHandler;
import mobile.substance.sdk.music.playback.cast.ConnectionCallbacks;
import mobile.substance.sdk.music.playback.cast.ConnectionResultListener;
import mobile.substance.sdk.music.playback.cast.MediaRouterCallback;

public class MusicService extends MediaBrowserServiceCompat implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        AudioManager.OnAudioFocusChangeListener,
        CastCallbacks {
    public static final int UNIQUE_ID = new Random().nextInt(1000000);

    // Log Tag
    public static final String TAG = MusicService.class.getSimpleName();

    // IBinder
    private final IBinder binder = new ServiceBinder();

    NotificationManager notificationManager;
    AudioManager audioManager;

    // BroadCast Receivers
    HeadsetPlugReceiver plugReceiver = null;
    private Thread progressThread = null;
    // Local MediaPLayer
    private MediaPlayer localPlayer;
    // Media Session
    private MediaSessionCompat sessionCompat;
    // Playback State booleans
    private boolean wasPlayingBeforeAction = false;
    private boolean isRepeating = false;
    // PlaybackRemote Callbacks
    private volatile List<PlaybackRemote.RemoteCallback> CALLBACKS = new ArrayList<>();
    // Gooogle Cast
    private GoogleApiClient apiClient;
    private CastPlaybackHandler castPlaybackHandler;
    // Playback Progress
    private Runnable progressUpdate = new Runnable() {
        @Override
        public void run() {
            for (PlaybackRemote.RemoteCallback CALLBACK : CALLBACKS) {
                CALLBACK.onProgressChanged(getCurrentPosition());
            }
            sessionCompat.setPlaybackState(getPlaybackState());
        }
    };
    private volatile MediaRouter mediaRouter;
    private volatile MediaRouteSelector routeSelector;
    private ConnectionCallbacks connectionCallbacks;
    private MediaRouterCallback routerCallback;
    private boolean isCastInitialized = false;
    private String applicationId;

    // Required empty constructor
    public MusicService() {

    }

    public void registerCallback(PlaybackRemote.RemoteCallback callback) {
        CALLBACKS.add(callback);
    }

    public void unregisterCallback(PlaybackRemote.RemoteCallback callback) {
        if (CALLBACKS.contains(callback)) CALLBACKS.remove(callback);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Playback data
    ///////////////////////////////////////////////////////////////////////////

    private MediaMetadataCompat getMetadata() {
        Song song = MusicQueue.INSTANCE.getCurrentSong();
        song.embedArtwork(BitmapFactory.decodeFile(Library.findAlbumById(song.getSongAlbumID()).getAlbumArtworkPath()));
        return song.getMetadataCompat();
    }

    private PlaybackStateCompat getPlaybackState() {
        long centerAction = isPlaying() ? PlaybackStateCompat.ACTION_PAUSE : PlaybackStateCompat.ACTION_PLAY;
        try {
            return new PlaybackStateCompat.Builder()
                    .setActions(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS | centerAction | PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
                    .setState(isLocalPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED, getCurrentPosition(), 1.0f).build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isPlaying() {
        return isLocalPlaying() || isCastPlaying();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Playback methods
    ///////////////////////////////////////////////////////////////////////////

    private void updatePlaybackState() {
        int state = 0;
        if (isPlaying()) {
            state = PlaybackState.STATE_PLAYING;
        } else if (MusicQueue.INSTANCE.getCurrentSong() != null) {
            state = PlaybackState.STATE_PAUSED;
        } else state = PlaybackState.STATE_IDLE;
        for (PlaybackRemote.RemoteCallback CALLBACK : CALLBACKS) {
            CALLBACK.onStateChanged(state, isRepeating);
        }
    }

    public void requestUpdate() {
        updatePlaybackState();
        updateSong();
    }

    private void updateSong() {
        for (PlaybackRemote.RemoteCallback CALLBACK : CALLBACKS) {
            CALLBACK.onSongChanged(MusicQueue.INSTANCE.getCurrentSong());
        }
    }

    public void togglePlayPause() {
        if (isLocalPlaying() || isCastPlaying()) {
            pause();
        } else resume();
    }

    public void setIsRepeating(boolean isRepeating) {
        this.isRepeating = isRepeating;
        updatePlaybackState();
    }

    public boolean isLocalPlaying() {
        try {
            return localPlayer.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Initialization
    ///////////////////////////////////////////////////////////////////////////

    private void init() {
        plugReceiver = new HeadsetPlugReceiver();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        mediaRouter = MediaRouter.getInstance(getApplicationContext());
        routeSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(applicationId))
                .build();
    }

    private void initMediaSession() {
        sessionCompat = new MediaSessionCompat(this, MusicService.class.getSimpleName());
        sessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        sessionCompat.setCallback(new SessionCallback());
        setSessionToken(sessionCompat.getSessionToken());
    }

    private void initMediaPlayer() {
        localPlayer = new MediaPlayer();
        localPlayer.setOnPreparedListener(this);
        localPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        localPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        localPlayer.setOnErrorListener(this);
        localPlayer.setOnCompletionListener(this);
    }

    public void initGoogleCast(MenuItem routeItem, String applicationId) {
        MediaRouteActionProvider mRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(routeItem);
        mRouteActionProvider.setRouteSelector(routeSelector);
        routerCallback = new MediaRouterCallback(this);
        mediaRouter.addCallback(routeSelector, routerCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    private void launchCast(CastDevice mDevice) {
        localPlayer.pause();
        Cast.Listener mListener = new Cast.Listener();
        Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                .builder(mDevice, mListener);

        connectionCallbacks = new ConnectionCallbacks(routerCallback, new ConnectionResultListener() {
            @Override
            public void onApplicationConnected() {
                castPlaybackHandler.load(getCurrentPosition());
                Log.d(TAG, String.valueOf(localPlayer == null));
                localPlayer = null;
            }
        });

        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Cast.API, apiOptionsBuilder.build())
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MusicService.this, "Connection failed", Toast.LENGTH_LONG).show();
                    }
                })
                .build();
        connectionCallbacks.setApiClient(apiClient);
        castPlaybackHandler = new CastPlaybackHandler(this, apiClient);
        apiClient.connect();
    }

    public boolean isCastPlaying() {
        return isCastConnected() && castPlaybackHandler.isPlaying();
    }

    public int getCurrentPosition() {
        try {
            return isCastPlaying() ? castPlaybackHandler.getPlaybackPosition() : localPlayer.getCurrentPosition();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getCurrentDuration() {
        try {
            return isCastPlaying() ? castPlaybackHandler.getCurrentDuration() : localPlayer.getDuration();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void play() {
        if (isLocalPlaying()) {
            shutdownPlayer();
        }

        Song newSong = MusicQueue.INSTANCE.getCurrentSong();

        initMediaPlayer();

        try {
            localPlayer.setDataSource(getApplicationContext(), newSong.getUri());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to play this file! Path: " + CoreUtil.getFilePath(this, newSong.getUri()), Toast.LENGTH_LONG).show();
        } finally {
            try {
                Log.d(MusicService.class.getSimpleName(), "localPlayer.prepareAsync()");
                localPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void pause() {
        try {
            if (localPlayer.isPlaying()) {
                localPlayer.pause();
                shutdownProgressThread();
                startForeground(UNIQUE_ID, MusicNotification.create(this, false, sessionCompat));
                stopForeground(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void seekTo(int position) {
        wasPlayingBeforeAction = true;

        localPlayer.pause();
        if (position >= localPlayer.getDuration()) {
            localPlayer.seekTo(position);
        } else skipForward();

        if (wasPlayingBeforeAction)
            localPlayer.start();
    }

    public void skipForward() {
        if (!isRepeating) {
            PlaybackRemote.INSTANCE.skipForward();
        } else play();
    }

    public void resume() {
        startProgressThread();
        localPlayer.start();
        startForeground(UNIQUE_ID, MusicNotification.create(this, true, sessionCompat));
    }

    private void skipBackward() {
        if (getCurrentDuration() > 5000 && getCurrentPosition() > 5000) {
            play();
            return;
        }
        isRepeating = false;
        PlaybackRemote.INSTANCE.skipBackward();
    }

    private void startLocalPlayback() {
        startProgressThread();
        localPlayer.start();
        startForeground(UNIQUE_ID, MusicNotification.create(this, true, sessionCompat));
        requestUpdate();
    }

    private void shutdownPlayer() {
        shutdownProgressThread();
        if (localPlayer != null) {
            localPlayer.stop();
            localPlayer.release();
            localPlayer = null;
        }
    }

    private void startProgressThread() {
        initProgressThread();
        progressThread.start();
    }

    private void shutdownProgressThread() {
        if (progressThread != null) {
            progressThread.interrupt();
            progressThread = null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // MediaPlayer Callbacks
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(MusicService.class.getSimpleName(), "onPrepared()");
        int requestResult = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        switch (requestResult) {
            case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
                Log.d(MusicService.class.getSimpleName(), "onPrepared(), GRANTED");
                startLocalPlayback();
                break;
            case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                Toast.makeText(MusicService.this, "Another app blocks the audio channel!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        audioManager.abandonAudioFocus(this);
        if (!isRepeating) {
            PlaybackRemote.INSTANCE.skipForward();
        } else play();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        try {
            progressThread.interrupt();
            progressThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // AudioManager functions
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (isLocalPlaying()) {
                    wasPlayingBeforeAction = isLocalPlaying();
                    pause();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (isLocalPlaying()) {
                    pause();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                wasPlayingBeforeAction = isLocalPlaying();
                adjustVolume(true);
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                if (wasPlayingBeforeAction) {
                    resume();
                } else adjustVolume(false);
                break;
        }
    }

    private void adjustVolume(boolean duck) {
        try {
            float volume = duck ? 0.5f : 1.0f;
            localPlayer.setVolume(volume, volume);
        } catch (Exception ignored) {
        }
    }

    private void initProgressThread() {
        progressThread = new Thread() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        interrupt();
                    }
                    try {
                        progressUpdate.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                        interrupt();
                    }
                }
            }
        };
    }

    ///////////////////////////////////////////////////////////////////////////
    // MediaSession
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onCastDeviceSelected(CastDevice mDevice) {
        launchCast(mDevice);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Google Cast functions
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onCastDeviceUnselected() {
        progressThread.interrupt();
        progressThread = null;
    }

    private boolean isCastConnected() {
        return castPlaybackHandler != null && castPlaybackHandler.isConnected();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MusicService", "onCreate()");
        init();
        initMediaSession();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(plugReceiver);
        destroySession();
    }

    private void destroySession() {
        sessionCompat.setActive(false);
        sessionCompat.release();
        sessionCompat = null;
    }

    public void kill() {
        destroySession();
        stopForeground(true);
        stopSelf();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MusicService", "onStartCommand()");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("MusicService", "onBind()");
        return binder;
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) { // We don't use this
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) { // We don't use this

    }

    private class SessionCallback extends MediaSessionCompat.Callback {

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            return super.onMediaButtonEvent(mediaButtonEvent);
        }

        @Override
        public void onStop() {
            // kill()
        }

        @Override
        public void onSeekTo(long pos) {
            seekTo(Math.round(pos));
        }

        @Override
        public void onPause() {
            pause();
        }

        @Override
        public void onPlay() {
            resume();
        }

        @Override
        public void onSkipToNext() {
            skipForward();
        }

        @Override
        public void onSkipToPrevious() {
            skipBackward();
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
        }

        @Override
        public void onPlayFromSearch(String query, Bundle extras) {
            super.onPlayFromSearch(query, extras);
        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            super.onPlayFromUri(uri, extras);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Binder
    ///////////////////////////////////////////////////////////////////////////

    public class ServiceBinder extends android.os.Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}