package mobile.substance.sdk.music.playback;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
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

        int REQUEST_CODE = MusicService.UNIQUE_ID;

        final PendingIntent content = PendingIntent.getBroadcast(context, REQUEST_CODE, new Intent(context.getApplicationContext(), PlaybackRemote.class).setAction(MusicUtil.getAction(context, MusicUtil.NOTIFICATION)), PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent skipBackward = PendingIntent.getBroadcast(context, REQUEST_CODE, new Intent(context.getApplicationContext(), PlaybackRemote.class).setAction(MusicUtil.getAction(context, MusicUtil.SKIP_BACKWARD)), PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent skipForward = PendingIntent.getBroadcast(context, REQUEST_CODE, new Intent(context.getApplicationContext(), PlaybackRemote.class).setAction(MusicUtil.getAction(context, MusicUtil.SKIP_FORWARD)), PendingIntent.FLAG_CANCEL_CURRENT);

        PendingIntent close = PendingIntent.getBroadcast(context, REQUEST_CODE, new Intent(context, PlaybackRemote.class).setAction(MusicUtil.getAction(context, MusicUtil.STOP)), PendingIntent.FLAG_CANCEL_CURRENT);
        String playPauseString = pauseTruePlayFalse ? "Pause Playback" : "Resume Playback";
        int playPauseResId = pauseTruePlayFalse ? R.drawable.ic_pause_white_24dp : R.drawable.ic_play_arrow_white_24dp;
        PendingIntent playPause = pauseTruePlayFalse ? PendingIntent.getBroadcast(context, REQUEST_CODE, new Intent(context, PlaybackRemote.class).setAction(MusicUtil.getAction(context, MusicUtil.PAUSE)), PendingIntent.FLAG_CANCEL_CURRENT) : PendingIntent.getBroadcast(context, REQUEST_CODE, new Intent(context, PlaybackRemote.class).setAction(MusicUtil.getAction(context, MusicUtil.RESUME)), PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();
        style.setShowActionsInCompactView(1);
        style.setMediaSession(sessionCompat.getSessionToken());

        Bitmap artwork = BitmapFactory.decodeFile(Uri.parse(Library.findAlbumById(song.getSongAlbumID()).getAlbumArtworkPath()).getPath());
        if (artwork == null)
            artwork = BitmapFactory.decodeResource(context.getResources(), MusicOptions.getDefaultArt());

        return new NotificationCompat.Builder(context)
                .setContentTitle(song.getSongTitle())
                .setContentText(song.getSongArtistName())
                .setSubText(song.getSongAlbumName())
                .setSmallIcon(MusicOptions.getStatusbarIconResId())
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(false)
                .setColor(Color.GRAY)
                .setContentIntent(content)
                .setDeleteIntent(close)
                .addAction(R.drawable.ic_skip_previous_white_24dp, "Skip Backward", skipBackward)
                .addAction(playPauseResId, playPauseString, playPause)
                .addAction(R.drawable.ic_skip_next_white_24dp, "Skip Forward", skipForward)
                .setStyle(style)
                .setLargeIcon(artwork)
                .build();
    }
}