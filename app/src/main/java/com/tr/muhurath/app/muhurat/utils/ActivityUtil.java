package com.tr.muhurath.app.muhurat.utils;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

/**
 * @author Balaji Katika (balaji.katika@gmail.com) on 2/7/16.
 */
public class ActivityUtil {
    /**
     * Start System Activity.
     * Display Toast with the message if activity not available
     * @param activity- {@link Activity} instance
     * @param action - String representing {@link Settings}
     * @param altToastMessage - Message to be displayed in Toast if {@link Activity} not available
     */
    public static void startSystemActivity(Activity activity, String action, String altToastMessage) {
        Intent locationIntent = new Intent(action);
        if (locationIntent.getAction() != null) {
            activity.startActivity(locationIntent);
        } else {
            if (altToastMessage != null) {
                showToast(activity, altToastMessage);
            }
        }
    }

    /**
     * Display Toast with the input message
     * @param activity - {@link Activity} instance
     * @param message - Message to be displayed in Toast
     */
    public static void showToast(Activity activity, String message) {
        Toast.makeText(activity, message,
                Toast.LENGTH_LONG).show();
    }
}
