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

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.support.v4.media.session.MediaSessionCompat
import android.support.v7.app.NotificationCompat
import android.util.Log
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.music.playback.PlaybackRemote

/**
 * This class will be used to create your own media notification. It is required for you to override two of the methods, createNotification and populate. The rest already have code
 * in them, but can be overridden.
 *
 * If you are using Java, please use MusicNotificationWrapper
 */
interface MediaNotification {

    /**
     * This method is called whenever a notification needs to be created by the service. Do NOT populate this notification with any data such as album art and song metadata.
     * That will be handled by the populate function.
     * @param context The application context.
     * @param session The media session. Can be used to get token and state.
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
    fun populate(song: Song, notificationBuilder: NotificationCompat.Builder)
    fun loadArt(albumArt: Bitmap, notificationBuilder: NotificationCompat.Builder) {
        notificationBuilder.setLargeIcon(albumArt);
    }
    fun buildNotif(builder: NotificationCompat.Builder) = builder.build()

    ///////////////////////////////////////////////////////////////////////////
    // Callbacks
    ///////////////////////////////////////////////////////////////////////////

    fun onPlay() = PlaybackRemote.resume()
    fun onPause() = PlaybackRemote.pause()
    fun onSkipForward() = PlaybackRemote.playNext()
    fun onSkipBackward() = PlaybackRemote.playPrevious()
    fun onNotificationClicked() {
        Log.d("MediaNotification", "Please override onNotificationClicked in order to handle the event.")
    }
    fun onNotificationDismissed() {
        //TODO Add stop method
    }
}