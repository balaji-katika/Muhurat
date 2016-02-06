package com.tr.muhurath.app.muhurat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
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
    private LocationManager locationManager;
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
                location = locationManager.getLastKnownLocation(provider);
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
                locationManager.requestLocationUpdates(provider, AppConstants.LOC_MIN_TIME_INTERVAL, AppConstants.LOC_MIN_DISTANCE, this);
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
            locationManager.removeUpdates(this);
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
            if (DateUtils.isIndianTimeZone()) {
                showLastKnownPermissionToast();
            } else {
                showLocationPermissionAlert();
            }
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
                showLocationDisableAlert();
            }
        }
    }
    /**
     * Gather Location Details using Location Service
     */
    private void gatherLocationDetails() {
        // Get the location manager
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        } catch (Exception exception) {
            //IllegalArgumentsException could occur here
            Log.e(TAG, "gatherLocationDetails - " + exception.getMessage());
        }

        if (locationManager != null) {
            // Define the criteria how to select the location provider -> use default
            Criteria criteria = new Criteria();
            Log.d(TAG, "Enabled - " + LocationUtil.isLocationEnabled(locationManager));
            if(!LocationUtil.isLocationEnabled(locationManager)) {
                displayLocationSettingsAlert();
            }

            provider = locationManager.getBestProvider(criteria, false);
            try {
                Location location = null;
                if (provider != null) {
                    //getBestProvider is returning null on Marshmallow
                    location = locationManager.getLastKnownLocation(provider);
                } else {
                    displayLocationSettingsAlert();
                    location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                }
                if (location != null) {
                    Log.d(TAG, "gatherLocationDetails - LocationProvider is " + location.getProvider());
                    AppConfiguration.setLocation(location.getLongitude(), location.getLatitude());
                } else {
                    displayLocationSettingsAlert();
                    Log.d(TAG, "gatherLocationDetails - Unable to retrieve the last known location");
                    AppConfiguration.setLocation(AppConstants.DEF_LONGITUDE, AppConstants.DEF_LATITUDE);
                }
            } catch (SecurityException securityException) {
                displayLocationPermissionsAlert();
                Log.w(TAG, "gatherLocationDetails - User has not given permission for Location Service");
                AppConfiguration.setLocation(AppConstants.DEF_LONGITUDE, AppConstants.DEF_LATITUDE);
            } catch (NullPointerException nullPointerException) {
                Log.w(TAG, "gatherLocationDetails - NullPointerException received for Location Service");
                AppConfiguration.setLocation(AppConstants.DEF_LONGITUDE, AppConstants.DEF_LATITUDE);
            } catch (Exception exception) {
                Log.w(TAG, "gatherLocationDetails - Exception received for Location Service");
                AppConfiguration.setLocation(AppConstants.DEF_LONGITUDE, AppConstants.DEF_LATITUDE);
            }
        }
        else {
            Log.w(TAG, "gatherLocationDetails - LocationManager Service not enabled");
            AppConfiguration.setLocation(AppConstants.DEF_LONGITUDE, AppConstants.DEF_LATITUDE);
            showLastKnownPermissionToast();
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
                .setNegativeButton(AppMessages.MSG_ENABLE_PERMISSION, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent manageAppIntent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        startActivity(manageAppIntent);
                    }
                })
                .setPositiveButton(AppMessages.MSG_IGNORE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        showLocationPermissionAlert();
                    }
                });
        dialog.show();
        AppConfiguration.getInstance().setLocationPermissionAlertShown(Boolean.TRUE);
    }
    /**
     * Display Location Settings alert
     */
    private void showLocationDisableAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(AppMessages.MSG_LOC_ENABLE)
                .setMessage(AppMessages.MSG_LOC_DLG_MSG)
                .setNegativeButton(AppMessages.MSG_LOC_ENABLE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(locationIntent);
                    }
                })
                .setPositiveButton(AppMessages.MSG_LOC_LAST_KNOWN, new DialogInterface.OnClickListener() {
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
