package mobile.substance.sdk.music.playback

import android.app.ActivityManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import mobile.substance.sdk.music.playback.service.MusicService
import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder
import java.util.*

/**
 * The utility class for the playback library
 */
object MusicPlaybackUtil {

    val SERVER_PORT_AUDIO: Int by lazy {
        Random().nextInt(10000)
    }

    val SERVER_PORT_ARTWORK: Int by lazy {
        Random().nextInt(10000)
    }

    const val SERVER_TYPE_ARTWORK = 1
    const val SERVER_TYPE_AUDIO = 2

    @JvmStatic fun getServerPortForType(type: Int): Int {
        when (type) {
            SERVER_TYPE_ARTWORK -> return SERVER_PORT_ARTWORK
            SERVER_TYPE_AUDIO -> return SERVER_PORT_AUDIO
            else -> return 0
        }
    }

    fun isServiceRunning(context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.className == MusicService::class.java.name) {
                return true
            }
        }
        return false
    }

    fun getIpAddressString(context: Context): String? {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var mIpAddress = wifiManager.connectionInfo.ipAddress
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            mIpAddress = Integer.reverseBytes(mIpAddress)
        }
        val ipByteArray = BigInteger.valueOf(mIpAddress.toLong()).toByteArray()
        val ipAddressString: String?
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).hostAddress
        } catch (ex: UnknownHostException) {
            ipAddressString = null
        }

        return ipAddressString
    }

    ///////////////////////////////////////////////////////////////////////////
    // For getting intents
    ///////////////////////////////////////////////////////////////////////////

    enum class Action {
        PLAY, PAUSE, SKIP_FORWARD, SKIP_BACKWARD, SEEK, STOP, NOTIFICATION
    }

    fun getAction(context: Context, action: Action): String {
        val packageName = context.applicationContext.packageName + ".action"
        when (action) {
            Action.PLAY -> return packageName + ".PLAY"
            Action.PAUSE -> return packageName + ".PAUSE"
            Action.SKIP_FORWARD -> return packageName + ".skip.FORWARD"
            Action.SKIP_BACKWARD -> return packageName + ".skip.BACKWARD"
            Action.SEEK -> return packageName + ".SEEK"
            Action.STOP -> return packageName + ".STOP"
            Action.NOTIFICATION -> return packageName + ".NOTIFICATION"
            else -> return packageName + ".NOTFOUND"
        }
    }

    fun getPendingIntent(context: Context, action: Action): PendingIntent {
        return PendingIntent.getService(context, MusicService.UNIQUE_ID, Intent(context, MusicService::class.java).setAction(getAction(context, action)),
                PendingIntent.FLAG_CANCEL_CURRENT)
    }

}