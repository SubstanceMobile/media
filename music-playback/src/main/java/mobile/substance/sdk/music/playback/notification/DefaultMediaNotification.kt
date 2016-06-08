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

package mobile.substance.sdk.music.playback.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.support.v7.app.NotificationCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import mobile.substance.sdk.music.core.MusicCoreOptions
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.music.playback.MusicPlaybackOptions
import mobile.substance.sdk.music.playback.MusicPlaybackUtil
import mobile.substance.sdk.music.playback.PlaybackRemote
import mobile.substance.sdk.music.playback.R

class DefaultMediaNotification: MediaNotification {

    override fun createNotification(context: Context, session: MediaSessionCompat?,
                                    playIntent: PendingIntent, pauseIntent: PendingIntent, nextIntent: PendingIntent, prevIntent: PendingIntent,
                                    clickedIntent: PendingIntent, removedIntent: PendingIntent): NotificationCompat.Builder {
        val isPlaying = session.getController().playbackState.state.equals(PlaybackStateCompat.STATE_PLAYING);
        val playPauseString = if (isPlaying) "Pause Playback" else "Resume Playback"
        val playPauseResId = if (isPlaying) R.drawable.ic_pause_white_24dp else R.drawable.ic_play_arrow_white_24dp
        val notif = NotificationCompat.Builder(context);
        val style = NotificationCompat.MediaStyle().setShowActionsInCompactView(1)
                .setShowCancelButton(true).setCancelButtonIntent(removedIntent)
        if (session != null) style.setMediaSession(session.sessionToken)
        notif.setSmallIcon(MusicPlaybackOptions.statusbarIconResId)
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(false)
                .setContentIntent(PlaybackRemote.getPendingIntent(MusicPlaybackUtil.getAction(context, MusicPlaybackUtil.NOTIFICATION)))
                .setDeleteIntent(PlaybackRemote.getPendingIntent(MusicPlaybackUtil.getAction(context, MusicPlaybackUtil.STOP)))
                .addAction(R.drawable.ic_skip_previous_white_24dp, "Skip Backward", prevIntent)
                .addAction(playPauseResId, playPauseString, if (isPlaying) pauseIntent else playIntent)
                .addAction(R.drawable.ic_skip_next_white_24dp, "Skip Forward", nextIntent)
                .setStyle(style);
        return notif;
    }

    override fun populate(song: Song, notificationBuilder: NotificationCompat.Builder) {
        notificationBuilder.setContentTitle(song.songTitle)
                .setContentText(song.songArtistName)
                .setSubText(song.songAlbumName)
    }
}