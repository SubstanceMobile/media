package mobile.substance.sdk.music.playback.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import mobile.substance.sdk.music.playback.MusicPlaybackOptions
import mobile.substance.sdk.music.playback.PlaybackRemote

/**
 * Audio noisy receiver. register() and unregister() to enable and disable this. For example, if you are currently playing music this should be registered
 */
object HeadsetPlugReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) = PlaybackRemote.pause()

    infix fun register(context: Context) = context.registerReceiver(this, IntentFilter(if (MusicPlaybackOptions.useHeadsetPlugInsteadOfAudioNoisy) Intent.ACTION_HEADSET_PLUG else AudioManager.ACTION_AUDIO_BECOMING_NOISY))

    infix fun unregister(context: Context) = context.unregisterReceiver(this)

}