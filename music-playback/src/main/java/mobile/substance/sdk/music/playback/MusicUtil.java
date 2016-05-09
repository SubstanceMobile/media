package mobile.substance.sdk.music.playback;

import android.app.ActivityManager;
import android.content.Context;
import android.net.wifi.WifiManager;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

/**
 * Created by Julian Os on 07.05.2016.
 */
public class MusicUtil {
    public static final int PLAY = 1;
    public static final int PAUSE = 2;
    public static final int RESUME = 3;
    public static final int SKIP_FORWARD = 4;
    public static final int SKIP_BACKWARD = 5;
    public static final int SEEK = 6;
    public static final int STOP = 7;
    public static final int NOTIFICATION = 8;

    public static final int FILE_PORT = 12345;
    public static final int ARTWORK_PORT = 23456;

    public static boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().equals(MusicService.class.getName())) {
                return true;
            }
        }
        return false;
    }

    public static String getIP(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int mIpAddress = wifiManager.getConnectionInfo().getIpAddress();
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            mIpAddress = Integer.reverseBytes(mIpAddress);
        }
        byte[] ipByteArray = BigInteger.valueOf(mIpAddress).toByteArray();
        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            ipAddressString = null;
        }
        return ipAddressString;
    }

    public static String getAction(Context context, int action) {
        String packageName = context.getApplicationContext().getPackageName() + ".action";

        switch (action) {
            case PLAY:
                return packageName + ".PLAY";
            case PAUSE:
                return packageName + ".PAUSE";
            case RESUME:
                return packageName + ".RESUME";
            case SKIP_FORWARD:
                return packageName + ".skip.FORWARD";
            case SKIP_BACKWARD:
                return packageName + ".skip.BACKWARD";
            case SEEK:
                return packageName + ".SEEk";
            case STOP:
                return packageName + ".STOP";
            case NOTIFICATION:
                return packageName + ".NOTIFICATION";
            default:
                return packageName + ".NOTFOUND";
        }
    }


}
