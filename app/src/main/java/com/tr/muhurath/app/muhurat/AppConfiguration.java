package com.tr.muhurath.app.muhurat;

/**
 * Container for Application Configuration
 *
 * Created by Balaji Katika (balaji.katika@gmail.com) on 1/31/16.
 */
public class AppConfiguration {
    private static AppConfiguration _instance = null;
    public static double longitude;
    public static double latitude;
    private static final Object _lock = new Object();
    private boolean locationAlertShown = false;
    private boolean locationPermissionAlertShown = false;
    /**
     * Private constructor
     */
    private AppConfiguration() {

    }

    /**
     * Get Singleton instance of AppConfiguration
     * @return - {@link AppConfiguration} instance
     */
    public static AppConfiguration getInstance() {
        if (_instance != null) {
            return _instance;
        }
        else {
            synchronized (_lock) {
                if (_instance == null) {
                    _instance = new AppConfiguration();
                }
            }
        }
        return _instance;
    }

    /**
     * Setter for {@link AppConfiguration#locationAlertShown}
     * @param shown - True/False indicating Location Alert shown for the user
     */
    public void setLocationAlertShown(boolean shown) {
        locationAlertShown = shown;
    }

    /**
     * Getter for {@link AppConfiguration#locationAlertShown}
     * @return - True if locationAlertShown. False otherwise
     */
    public boolean getLocationAlertShown() {
        return locationAlertShown;
    }

    /**
     * Getter for {@link AppConfiguration#locationPermissionAlertShown}
     * @return - True if permissions alert shown. False otherwise
     */
    public boolean getLocationPermissionAlertShown() {
        return locationPermissionAlertShown;
    }

    /**
     * Setter for {@link AppConfiguration#locationPermissionAlertShown}
     * @param shown - True if permission Alert shown. False otherwise
     */
    public void setLocationPermissionAlertShown(boolean shown) {
        this.locationPermissionAlertShown = shown;
    }

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
