package com.tr.muhurath.app.muhurat.utils;

import android.util.Log;

/**
 * Utility for Application Logging
 * @author  Balaji Katika (balaji.katika@gmail.com) on 2/7/16.
 */
public class AppLogUtils {
    private static String TAG = AppLogUtils.class.getName();
    private static StringBuilder debugBuilder;
    private static boolean debugEnabled = false;
    private static String DELIMITER = "; ";

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
            if (debugBuilder == null) {
                resetDebug();
            }
            debugBuilder.append(msg);
            debugBuilder.append(DELIMITER);
        }
        else {
            Log.d(TAG, msg + " not being tracked since debug not enabled");
        }
    }

    /**
     * Reset the debug. Internally re-initialization
     */
    public static void resetDebug() {
        debugBuilder = new StringBuilder();
    }

    /**
     * Return the debug message
     * @return - Message string
     */
    public static String getDebugMessage() {
        if (debugBuilder != null) {
            return debugBuilder.toString();
        }
        return null;
    }
}
