package com.tr.muhurath.app.muhurat;

/**
 * Container for Application Configuration
 *
 * Created by Balaji Katika (balaji.katika@gmail.com) on 1/31/16.
 */
public class AppConfiguration {
    public static double longitude;
    public static double latitude;

    /**
     * Set the location for the application
     * @param longi - Longitue
     * @param lati - Latitude
     */
    public static void setLocation(double longi, double lati) {
        longitude = longi;
        latitude = lati;
    }
}
