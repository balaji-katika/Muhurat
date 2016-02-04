package com.tr.muhurath.app.muhurat.utils;

import android.location.LocationManager;

/**
 * Created by Balaji Katika (balaji.katika@gmail.com) on 2/2/16.
 */
public class LocationUtil {
    /**
     * Returns true if Location Services are enabled
     * @param locationManager
     * @return
     */
    public static boolean isLocationEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
