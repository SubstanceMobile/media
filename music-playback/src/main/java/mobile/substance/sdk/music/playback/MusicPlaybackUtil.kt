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

package mobile.substance.sdk.music.playback

import android.app.ActivityManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager

import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder

/**
 * Created by Julian Os on 07.05.2016.
 */
object MusicPlaybackUtil {
    val PLAY = 1
    val PAUSE = 2
    val RESUME = 3
    val SKIP_FORWARD = 4
    val SKIP_BACKWARD = 5
    val SEEK = 6
    val STOP = 7
    val NOTIFICATION = 8
    val FILE_PORT = 12345
    val ARTWORK_PORT = 23456

    fun isServiceRunning(context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.className == MusicService::class.java.name) {
                return true
            }
        }
        return false
    }

    fun getIP(context: Context): String? {
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

    enum class Action {
        PLAY, PAUSE, RESUME, SKIP_FORWARD, SKIP_BACKWARD, SEEK, STOP, NOTIFICATION
    }

    fun getAction(context: Context, action: Action): String {
        val packageName = context.applicationContext.packageName + ".action"
        when (action.ordinal) {
            PLAY -> return packageName + ".PLAY"
            PAUSE -> return packageName + ".PAUSE"
            RESUME -> return packageName + ".RESUME"
            SKIP_FORWARD -> return packageName + ".skip.FORWARD"
            SKIP_BACKWARD -> return packageName + ".skip.BACKWARD"
            SEEK -> return packageName + ".SEEk"
            STOP -> return packageName + ".STOP"
            NOTIFICATION -> return packageName + ".NOTIFICATION"
            else -> return packageName + ".NOTFOUND"
        }
    }

    fun getPendingIntent(context: Context, action: Action): PendingIntent {
        return PendingIntent.getService(context, MusicService.UNIQUE_ID, Intent(context, MusicService::class.java).setAction(getAction(context, action)),
                PendingIntent.FLAG_CANCEL_CURRENT)
    }

}
