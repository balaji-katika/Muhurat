package com.tr.muhurath.app.muhurat.utils;

import android.util.Log;

/**
 * Created by bkatika on 2/7/16.
 */
public class AppLogUtils {
    private static String TAG = AppLogUtils.class.getName();
    private static StringBuilder debugBuilder = new StringBuilder();
    private static boolean debugEnabled = true;
    private static String NEW_LINE = "\n";

    /**
     * Control application debugging
     * @param enabled - True to enable. False otherwise
     */
    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
    }

    public static boolean isDebugEnabled() {
        return debugEnabled;
    }

    /**
     * Log the debug message
     * @param msg - Input message string
     */
    public static void appendDebug(String msg) {
        if (debugEnabled) {
            debugBuilder.append(msg);
            debugBuilder.append(NEW_LINE);
        }
        else {
            Log.d(TAG, msg + " not being tracked since debug not enabled");
        }
    }
    public static void resetDebug() {
        debugBuilder = new StringBuilder();
    }

    public static String getDebugMessage() {
        return debugBuilder.toString();
    }
}
