package mobile.substance.sdk.music.playback.cast

import android.content.Context
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider
import mobile.substance.sdk.music.playback.MusicPlaybackOptions

//TODO: Julian explain this
class CastOptionsProvider : OptionsProvider {

    companion object {
        const val MEDIA_NAMESPACE = "urn:x-cast:com.google.cast.media"
    }

    override fun getAdditionalSessionProviders(p0: Context?): MutableList<SessionProvider>? {
        return null
    }

    override fun getCastOptions(p0: Context?): CastOptions? {
        return CastOptions.Builder()
                .setReceiverApplicationId(MusicPlaybackOptions.castApplicationId)
                .setEnableReconnectionService(true)
                .setResumeSavedSession(true)
                // .setSupportedNamespaces(listOf(MEDIA_NAMESPACE))
                .build()
    }

}