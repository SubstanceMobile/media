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

package mobile.substance.media.utils

import android.app.ActivityManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.wifi.WifiManager
import android.support.annotation.WorkerThread
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import jp.wasabeef.glide.transformations.BlurTransformation
import mobile.substance.media.audio.playback.service.AudioService
import mobile.substance.media.options.AudioCoreOptions
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder

/**
 * The utility class for the playback library
 */
object AudioPlaybackUtil {

    const val SERVER_PORT = 1234
    const val URL_PATH_PART_AUDIO = "audio"
    const val URL_PATH_PART_ARTWORK = "artwork"

    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE).any { it.service.className == serviceClass.name }
    }

    fun getIpAddressString(context: Context): String? {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var ipAddress = wifiManager.connectionInfo.ipAddress
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            ipAddress = Integer.reverseBytes(ipAddress)
        }
        val ipByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray()
        var ipAddressString: String? = null
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).hostAddress
        } catch (ex: UnknownHostException) {
            ex.printStackTrace()
        }

        return ipAddressString
    }

    /**
     * Bitmap blurring
     *
     * @param song song to get artwork of
     *
     * @param context required for the default artwork drawable fallback
     *
     * @return The retrieved Bitmap; null if a NetworkOnMainThreadException has been caught
     */
    @WorkerThread
    fun blurBitmap(bitmap: Bitmap, context: Context): Bitmap? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Glide.with(context)
                .load(bytes)
                .asBitmap()
                .error(AudioCoreOptions.defaultArtResId)
                .transform(BlurTransformation(context))
                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get()
    }

    ///////////////////////////////////////////////////////////////////////////
    // For getting intents
    ///////////////////////////////////////////////////////////////////////////

    enum class Action {
        PLAY, PAUSE, SKIP_FORWARD, SKIP_BACKWARD, SEEK, STOP, NOTIFICATION
    }

    fun getAction(context: Context, action: Action): String {
        val packageName = context.applicationContext.packageName + ".action."
        when (action) {
            Action.PLAY -> return packageName + "PLAY"
            Action.PAUSE -> return packageName + "PAUSE"
            Action.SKIP_FORWARD -> return packageName + "SKIP_FORWARD"
            Action.SKIP_BACKWARD -> return packageName + "SKIP_BACKWARD"
            Action.SEEK -> return packageName + "SEEK"
            Action.STOP -> return packageName + "STOP"
            Action.NOTIFICATION -> return packageName + "NOTIFICATION"
            else -> return packageName + "NOT_FOUND"
        }
    }

    fun getPendingIntent(context: Context, action: Action, serviceClass: Class<*>): PendingIntent {
        return PendingIntent.getService(context, AudioService.UNIQUE_ID, Intent(context, serviceClass).setAction(getAction(context, action)),
                PendingIntent.FLAG_CANCEL_CURRENT)
    }

}