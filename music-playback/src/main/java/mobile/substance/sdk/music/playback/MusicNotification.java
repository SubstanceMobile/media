package mobile.substance.sdk.music.playback;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;

import mobile.substance.sdk.music.core.MusicOptions;
import mobile.substance.sdk.music.core.objects.Song;
import mobile.substance.sdk.music.loading.Library;

/**
 * Created by Julian Ostarek on 22.12.2015.
 */
public class MusicNotification {

    public static Notification create(final Context context, boolean pauseTruePlayFalse, MediaSessionCompat sessionCompat) {
        Song song = MusicQueue.INSTANCE.getCurrentSong();

        String playPauseString = pauseTruePlayFalse ? "Pause Playback" : "Resume Playback";
        int playPauseResId = pauseTruePlayFalse ? R.drawable.ic_pause_white_24dp : R.drawable.ic_play_arrow_white_24dp;

        NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();
        style.setShowActionsInCompactView(1);
        style.setMediaSession(sessionCompat.getSessionToken());

        Bitmap artwork = BitmapFactory.decodeFile(Library.findAlbumById(song.getSongAlbumID()).getAlbumArtworkPath());
        if (artwork == null)
            artwork = BitmapFactory.decodeResource(context.getResources(), MusicOptions.getDefaultArt());

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(song.getSongTitle())
                .setContentText(song.getSongArtistName())
                .setSubText(song.getSongAlbumName())
                .setSmallIcon(MusicOptions.getStatusbarIconResId())
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(false)
                .setContentIntent(PlaybackRemote.INSTANCE.getPendingIntent(MusicUtil.getAction(context, MusicUtil.NOTIFICATION)))
                .setDeleteIntent(PlaybackRemote.INSTANCE.getPendingIntent(MusicUtil.getAction(context, MusicUtil.STOP)))
                .addAction(R.drawable.ic_skip_previous_white_24dp, "Skip Backward", PlaybackRemote.INSTANCE.getPendingIntent(MusicUtil.getAction(context, MusicUtil.SKIP_BACKWARD)))
                .addAction(playPauseResId, playPauseString, PlaybackRemote.INSTANCE.getPendingIntent(MusicUtil.getAction(context, pauseTruePlayFalse ? MusicUtil.PAUSE : MusicUtil.RESUME)))
                .addAction(R.drawable.ic_skip_next_white_24dp, "Skip Forward", PlaybackRemote.INSTANCE.getPendingIntent(MusicUtil.getAction(context, MusicUtil.SKIP_FORWARD)))
                .setStyle(style)
                .setLargeIcon(artwork);

        return builder.build();
    }

    public static Notification get(Song song) {
        try {
            return PlaybackRemote.INSTANCE.getNotificationCallable(song).call();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}