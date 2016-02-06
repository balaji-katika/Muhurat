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
     * 1) Best known provider that is enabled
     * 2)
     * @param locationService
     * @return
     */
    public static String getBestProvider(LocationManager locationService) {
        if (locationService == null) {
            return null;
        }
        Criteria criteria = new Criteria();
        String provider = null;

        provider = locationService.getBestProvider(criteria, true);
        if (provider != null) {
            return provider;
        }

        //Best Provider is not available. Alert as per Application preferred order
        if(!LocationUtil.isLocationEnabled(locationService)) {
            return null;
        }
        else {
            if (locationService.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                return LocationManager.NETWORK_PROVIDER;
            }
            else {
                return LocationManager.GPS_PROVIDER;
            }
        }
    }
}
