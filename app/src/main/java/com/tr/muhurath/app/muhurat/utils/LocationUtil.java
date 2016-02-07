package com.tr.muhurath.app.muhurat.utils;

import android.location.Criteria;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.util.Log;

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

    /**
     * Get the best known provider as per the application
     *
     * Priority as below
     *
     * 1) Best known provider as per {@link LocationManager}
     * 2) Network Provider if enabled
     * 3) GPS Provider if enabled
     * 4) Passive Provider
     *
     * @param locationManager - {@link LocationManager} instance
     * @return - String representing the location provider
     */
    public static String getBestProvider(LocationManager locationManager) {
        if (locationManager == null) {
            return null;
        }
        Criteria criteria = new Criteria();
        String provider = null;

        provider = locationManager.getBestProvider(criteria, false);
        if (provider != null) {
            return provider;
        }
        //Best Provider is not available. Alert as per Application preferred order
        else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;
        }
        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            return LocationManager.GPS_PROVIDER;
        }
        else {
            return LocationManager.PASSIVE_PROVIDER;
        }
    }
}
