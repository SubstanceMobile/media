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

package mobile.substance.sdk.music.playback.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.app.NotificationCompat
import android.util.Log
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.music.playback.MusicPlaybackOptions
import mobile.substance.sdk.music.playback.PlaybackRemote
import mobile.substance.sdk.music.playback.R

/**
 * This class will be used to create your own media notification. It is required for you to override two of the methods, createNotification and populate. The rest already have code
 * in them, but can be overridden.
 *
 * If you are using Java, please use MusicNotificationWrapper
 */
interface MediaNotification {

    /**
     * This function is called whenever a notification needs to be created by the service. Do NOT populate this notification with any data such as album art and song metadata.
     * That will be handled by the populate function.
     * @param playIntent The intent you should pass to your created notification in order to resume music.
     * @param pauseIntent The intent you should pass to your created notification in order to pause music.
     * @param nextIntent The intent you should pass to your created notification in order to skip to the next song.
     * @param prevIntent The intent you should pass to your created notification in order to skip to the previous song.
     * @param clickedIntent The intent you should pass to your created notification for when it is clicked. Then override onNotificationClicked to handle the event.
     * @param removedIntent The intent you should pass to your created notification for when it is dismissed.
     * @return The created notification builder that will eventually be passed on to methods like loadArt and populate.
     */
    fun createNotification(context: Context, session: MediaSessionCompat?,
                           playIntent: PendingIntent, pauseIntent: PendingIntent, nextIntent: PendingIntent, prevIntent: PendingIntent,
                           clickedIntent: PendingIntent, removedIntent: PendingIntent): NotificationCompat.Builder

    /**
     * Populate the notification with the current song's details. Override this for custom notifications
     */
    fun populate(song: Song, notificationBuilder: NotificationCompat.Builder, session: MediaSessionCompat?, playIntent: PendingIntent, pauseIntent: PendingIntent) {
        notificationBuilder.setContentTitle(song.songTitle).setContentText(song.songArtistName).setSubText(song.songAlbumName)
    }

    /**
     * Override this if you use a custom notification layout in order to set a loaded album art.
     */
    fun loadArt(albumArt: Bitmap, notificationBuilder: NotificationCompat.Builder) {
        notificationBuilder.setLargeIcon(albumArt)
    }

    /**
     * Override this if you want to call something when (or after) the notification is built.
     */
    fun buildNotification(builder: NotificationCompat.Builder): Notification = builder.build()

    ///////////////////////////////////////////////////////////////////////////
    // Callbacks
    ///////////////////////////////////////////////////////////////////////////

    fun onNotificationClicked() {
        Log.d("MediaNotification", "Please override onNotificationClicked in order to handle the event.")
    }

    fun onNotificationDismissed() = PlaybackRemote.stop()
}

/**
 * This class wraps around the MediaNotification allowing Java users to use the default implementations of everything.
 */
abstract class MediaNotificationWrapper : MediaNotification

/**
 * This class is the default implementation of the notification API for the SDK
 */
class DefaultMediaNotification : MediaNotification {

    override fun createNotification(context: Context, session: MediaSessionCompat?,
                                    playIntent: PendingIntent, pauseIntent: PendingIntent, nextIntent: PendingIntent, prevIntent: PendingIntent,
                                    clickedIntent: PendingIntent, removedIntent: PendingIntent): NotificationCompat.Builder {
        val isPlaying = session?.controller?.playbackState?.state?.equals(PlaybackStateCompat.STATE_PLAYING) ?: false
        return NotificationCompat.Builder(context).setSmallIcon(MusicPlaybackOptions.statusbarIconResId)
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(false)
                .setContentIntent(clickedIntent)
                .setDeleteIntent(removedIntent)
                .addAction(R.drawable.ic_skip_previous_white_24dp, "Skip Backward", prevIntent)
                .addAction(if (isPlaying) R.drawable.ic_pause_white_24dp else R.drawable.ic_play_arrow_white_24dp, if (isPlaying) "Pause Playback" else "Resume Playback", if (isPlaying) pauseIntent else playIntent)
                .addAction(R.drawable.ic_skip_next_white_24dp, "Skip Forward", nextIntent)
                .setStyle(NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(removedIntent)
                        .setMediaSession(session?.sessionToken))
                as NotificationCompat.Builder
    }

    override fun populate(song: Song, notificationBuilder: NotificationCompat.Builder, session: MediaSessionCompat?, playIntent: PendingIntent, pauseIntent: PendingIntent) {
        super.populate(song, notificationBuilder, session, playIntent, pauseIntent)
        val playPause = notificationBuilder.mActions[1]
        val isPlaying = session?.controller?.playbackState?.state?.equals(PlaybackStateCompat.STATE_PLAYING) ?: false
        playPause.icon = if (isPlaying) R.drawable.ic_pause_white_24dp else R.drawable.ic_play_arrow_white_24dp
        playPause.title = if (isPlaying) "Pause Playback" else "Resume Playback"
        playPause.actionIntent = if (isPlaying) pauseIntent else playIntent
    }
}