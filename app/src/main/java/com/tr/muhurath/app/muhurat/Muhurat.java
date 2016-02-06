package com.tr.muhurath.app.muhurat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PermissionInfo;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.Button;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.Toast;

import com.tr.muhurath.app.muhurat.utils.AppConstants;
import com.tr.muhurath.app.muhurat.utils.AppMessages;
import com.tr.muhurath.app.muhurat.utils.DateUtils;
import com.tr.muhurath.app.muhurat.utils.LocationUtil;

/**
 * Main Activity for the Application
 *
 * Created by Balaji Katika (balaji.katika@gmail.com) on 1/30/16.
 */
public class Muhurat extends AppCompatActivity implements LocationListener {
    Button button;
    private LocationManager locationService;
    private String provider;
    private String TAG = "Muhurat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muhurat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addListenterOnCalcButton();
        gatherLocationDetails();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Location location = null;
        if (provider != null && (LocationManager.GPS_PROVIDER.equals(provider) || LocationManager.NETWORK_PROVIDER.equals(provider))) {
            try {
                location = locationService.getLastKnownLocation(provider);
                AppConfiguration.setLocation(location.getLongitude(), location.getLatitude());
                Log.d(TAG, "onProviderEnabled - Location Provider enable. New location - " + location);
            } catch (SecurityException securityException) {
                Log.w(TAG, "onProviderEnabled - User has not given permission for Location Service");
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        if (provider != null) {
            try {
                locationService.requestLocationUpdates(provider, AppConstants.LOC_MIN_TIME_INTERVAL, AppConstants.LOC_MIN_DISTANCE, this);
            } catch (SecurityException securityException) {
                Log.w(TAG, "onResume - User has not given permission for Location Service");
            }
        }
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        try {
            locationService.removeUpdates(this);
        }
        catch (SecurityException securityException) {
            Log.w(TAG, "onPause - User has not given permission for Location Service");
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location!=null) {
            AppConfiguration.setLocation(location.getLongitude(), location.getLatitude());
        }
        else {
            Log.d(TAG, "onLocationChanged - Unable to retrieve the last known location");
            AppConfiguration.setLocation(AppConstants.DEF_LONGITUDE, AppConstants.DEF_LATITUDE);
        }
    }

    /**
     * Display Enable location permissions alert
     */
    private void displayLocationPermissionsAlert() {
        if (!AppConfiguration.getInstance().getLocationPermissionAlertShown()) {
           showLastKnownPermissionToast();
        }
    }

    /**
     * Display Location Settings dialog/toast based on the timezone
     */
    private void displayLocationSettingsAlert() {
        if (!AppConfiguration.getInstance().getLocationAlertShown()) {
            if (DateUtils.isIndianTimeZone()) {
                showLastKnowLocationToast();
            } else {
                showLocationSettingsDialog();
            }
        }
    }
    /**
     * Gather Location Details using Location Service
     */
    private void gatherLocationDetails() {
        // Get the location manager
        try {
            locationService = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        } catch (Exception exception) {
            //IllegalArgumentsException could occur here for invalid service name
            Log.e(TAG, "gatherLocationDetails - " + exception.getMessage());
        }

        if (locationService == null) {
            //Happens for Marshmallow. By default access to system service are disabled
            Log.w(TAG, "gatherLocationDetails - Location Service not accessible");
            displayLocationPermissionsAlert();
            AppConfiguration.setLocation(AppConstants.DEF_LONGITUDE, AppConstants.DEF_LATITUDE);
        }
        else {
            Location location = null;
            provider = LocationUtil.getBestProvider(locationService);
            if (provider == null) {
                //Location Settings are Off
                displayLocationSettingsAlert();
            }
            else {
                try {
                    location = locationService.getLastKnownLocation(provider);
                }
                catch (SecurityException securityException) {
                    displayLocationPermissionsAlert();
                }
                catch (Exception exception){
                    //Nothing to do
                    location = null;
                }
            }
            if (location == null) {
                //Pass an empty string for the provider
                location = new Location("");
                //Set the default location as configured in the App
                location.setLatitude(AppConstants.DEF_LATITUDE);
                location.setLongitude(AppConstants.DEF_LONGITUDE);

            }
            AppConfiguration.setLocation(location.getLongitude(), location.getLatitude());
        }
    }

    /**
     * Show the Toast with permission not given message
     */
    private void showLastKnownPermissionToast() {
        Toast.makeText(this, "Permission to use location settings missing for Muhurat. Kindly give permissions",
                Toast.LENGTH_LONG).show();
        AppConfiguration.getInstance().setLocationPermissionAlertShown(Boolean.TRUE);
    }

    private void showUnableToAccessLocationSettingsToast() {
        Toast.makeText(this, "Unable to access location settings in your phone",
                Toast.LENGTH_LONG).show();
        AppConfiguration.getInstance().setLocationAlertShown(Boolean.TRUE);
    }

    /**
     * Show the Toast with Location not enabled message
     */
    private void showLastKnowLocationToast() {
        Toast.makeText(this, "Muhurat uses your last known location by default. Enable Location Settings for accurate result",
                Toast.LENGTH_LONG).show();
        AppConfiguration.getInstance().setLocationAlertShown(Boolean.TRUE);
    }

    /**
     * Display the dialog with permissions not given message
     */
    private void showLocationPermissionAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(AppMessages.MSG_PERMISSION_DLG_TITLE)
                .setMessage(AppMessages.MSG_PERMISSION_DLG_MSG)
                .setPositiveButton(AppMessages.MSG_ENABLE_PERMISSION, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent manageAppIntent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        if (manageAppIntent.getAction() != null) {
                            startActivity(manageAppIntent);
                        } else {
                            //When application settings are not available
                            showLastKnownPermissionToast();
                        }
                    }
                })
                .setNegativeButton(AppMessages.MSG_IGNORE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        showLastKnownPermissionToast();
                    }
                });
        dialog.show();
        AppConfiguration.getInstance().setLocationPermissionAlertShown(Boolean.TRUE);
    }
    /**
     * Display Location Settings alert
     */
    private void showLocationSettingsDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(AppMessages.MSG_LOC_ENABLE)
                .setMessage(AppMessages.MSG_LOC_DLG_MSG)
                .setPositiveButton(AppMessages.MSG_LOC_ENABLE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        if (locationIntent.getAction() != null) {
                            startActivity(locationIntent);
                        } else {
                            showUnableToAccessLocationSettingsToast();
                        }
                    }
                })
                .setNegativeButton(AppMessages.MSG_LOC_LAST_KNOWN, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        showLastKnowLocationToast();
                    }
                });
        dialog.show();
        AppConfiguration.getInstance().setLocationAlertShown(Boolean.TRUE);
    }


    /**
     * Adds the listener for 'Calculate' Button
     */
    public void addListenterOnCalcButton() {
        final Context context = this;

        button = (Button) findViewById(R.id.btnCalc);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MuhuratSummaryActivity.class);
                DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
                StringBuilder dateBuilder = new StringBuilder();
                dateBuilder.append(datePicker.getDayOfMonth() + "-");
                int month = datePicker.getMonth() + 1;
                if (month < 10) {
                    dateBuilder.append("0");
                }
                dateBuilder.append(month + "-");
                dateBuilder.append(datePicker.getYear());
                intent.putExtra(IntentConstants.DATE_DDMMYYYY, dateBuilder.toString());
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_muhurat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
           // startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }*/
        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        else if (id == R.id.action_faq) {
            startActivity(new Intent(this, FAQActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
