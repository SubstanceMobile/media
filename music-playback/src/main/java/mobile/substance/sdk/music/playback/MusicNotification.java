package mobile.substance.sdk.music.playback;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;

import mobile.substance.sdk.music.core.objects.Song;
import mobile.substance.sdk.music.loading.Library;

/**
 * Created by Julian Ostarek on 22.12.2015.
 */
public class MusicNotification {

    public static Notification create(Context context, boolean pauseTruePlayFalse, MediaSessionCompat sessionCompat) {
        Song song = MusicQueue.INSTANCE.getCurrentSong();

        PendingIntent content = PendingIntent.getBroadcast(context, 0, new Intent(context, MusicService.class).setAction(MusicUtil.getAction(context, MusicUtil.NOTIFICATION)), 0);
        PendingIntent skipBackward = PendingIntent.getBroadcast(context, 0, new Intent(context, MusicService.class).setAction(MusicUtil.getAction(context, MusicUtil.SKIP_BACKWARD)), 0);
        PendingIntent skipForward = PendingIntent.getBroadcast(context, 0, new Intent(context, MusicService.class).setAction(MusicUtil.getAction(context, MusicUtil.SKIP_FORWARD)), 0);

        PendingIntent close = PendingIntent.getBroadcast(context, 0, new Intent(context, MusicService.class).setAction(MusicUtil.getAction(context, MusicUtil.STOP)), 0);
        String playPauseString = pauseTruePlayFalse ? "Pause Playback" : "Resume Playback";
        int playPauseResId = pauseTruePlayFalse ? R.drawable.ic_pause_white_24dp : R.drawable.ic_play_arrow_white_24dp;
        PendingIntent playPause = pauseTruePlayFalse ? PendingIntent.getBroadcast(context, 0, new Intent(context, MusicService.class).setAction(MusicUtil.getAction(context, MusicUtil.PAUSE)), 0) : PendingIntent.getService(context, 0, new Intent(context, MusicService.class).setAction(MusicUtil.getAction(context, MusicUtil.RESUME)), 0);

        NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();
        style.setShowActionsInCompactView(2);
        style.setMediaSession(sessionCompat.getSessionToken());

        return new NotificationCompat.Builder(context)
                .setStyle(style)
                .setContentTitle(song.getSongTitle())
                .setContentText(song.getSongArtistName())
                .setSubText(song.getSongAlbumName())
                .setSmallIcon(R.drawable.ic_media_play)
                .setLargeIcon(BitmapFactory.decodeFile(Library.findAlbumById(song.getSongAlbumID()).getAlbumArtworkPath()))
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(false)
                .setColor(Color.GRAY)
                .setContentIntent(content)
                .setDeleteIntent(close)
                .addAction(R.drawable.ic_skip_previous_white_24dp, "Skip Backward", skipBackward)
                .addAction(playPauseResId, playPauseString, playPause)
                .addAction(R.drawable.ic_skip_next_white_24dp, "Skip Forward", skipForward)
                .build();
    }
}