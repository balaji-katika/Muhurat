package com.tr.muhurath.app.muhurat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tr.muhurath.app.muhurat.utils.ActivityUtil;
import com.tr.muhurath.app.muhurat.utils.AppConstants;
import com.tr.muhurath.app.muhurat.utils.AppLogUtils;
import com.tr.muhurath.app.muhurat.utils.AppMessages;
import com.tr.muhurath.app.muhurat.utils.DateUtils;
import com.tr.muhurath.app.muhurat.utils.LocationUtil;
import com.tr.muhurath.app.muhurat.version.CallBack;
import com.tr.muhurath.app.muhurat.version.RestConnect;
import com.tr.muhurath.app.muhurat.version.VersionInfo;

/**
 * Main Activity for the Application
 * <p>
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
        if (isNetworkAvailable(this)) {
            checkVersionUpdate();
        }
        setContentView(R.layout.activity_muhurat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addListenterOnCalcButton();
        gatherLocationDetails();
        showDebugToast();

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void checkVersionUpdate() {
        try {
            final PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            RestConnect restCall = new RestConnect(new CallBack<VersionInfo>() {
                @Override
                public void onSuccess(VersionInfo versionInfo) {
                    if (Integer.parseInt(versionInfo.getLatestVersion()) > pInfo.versionCode) {
                        showAlertDialog();
                    }
                }
            });

            restCall.execute();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Muhurat.this);
        builder.setMessage("There is newer version of this application available, click OK to upgrade now?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    //if the user agrees to upgrade
                    public void onClick(DialogInterface dialog, int id) {
                        //start downloading the file using the download manager
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            anfe.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Remind Later", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                })
                .setTitle("Muhurat");
        //show the alert message
        builder.create().show();
    }

    //check for internet connection
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        } catch (SecurityException securityException) {
            Log.w(TAG, "onPause - User has not given permission for Location Service");
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            AppConfiguration.setLocation(location.getLongitude(), location.getLatitude());
        } else {
            Log.d(TAG, "onLocationChanged - Unable to retrieve the last known location");
            AppConfiguration.setLocation(AppConstants.DEF_LONGITUDE, AppConstants.DEF_LATITUDE);
        }
    }

    /**
     * Display Enable location permissions alert
     */
    private void displayLocationPermissionsAlert() {
        if (!AppConfiguration.getInstance().getLocationPermissionAlertShown()) {
            ActivityUtil.showToast(this, "Permission to use location settings missing for Muhurat. Kindly give permissions");
            AppConfiguration.getInstance().setLocationPermissionAlertShown(Boolean.TRUE);
        }
    }

    /**
     * Display Location Settings dialog/toast based on the timezone
     */
    private void displayLocationSettingsAlert() {
        if (!AppConfiguration.getInstance().getLocationAlertShown()) {
            ActivityUtil.showToast(this, "Muhurat uses your last known location by default. Enable Location Settings for accurate result");
            AppConfiguration.getInstance().setLocationAlertShown(Boolean.TRUE);
        }
    }

    /**
     * Gather Location Details using Location Service
     */
    private void gatherLocationDetails() {
        Location location = null;
        if (!DateUtils.isIndianTimeZone()) {
            displayLocationSettingsAlert();
        }
        // Get the location manager
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        } catch (Exception exception) {
            //IllegalArgumentsException could occur here for invalid service name
            Log.e(TAG, "gatherLocationDetails - " + exception.getMessage());
        }

        if (locationManager == null) {
            //Happens for Marshmallow. By default access to system service are disabled
            Log.w(TAG, "gatherLocationDetails - Location Service not accessible");
            displayLocationPermissionsAlert();
        } else {
            provider = LocationUtil.getBestProvider(locationManager);
            if (provider != null) {
                try {
                    location = locationManager.getLastKnownLocation(provider);
                } catch (SecurityException securityException) {
                    displayLocationPermissionsAlert();
                } catch (Exception exception) {
                    //Nothing to do
                    location = null;
                }
            }
        }
        if (location == null) {
            if (!DateUtils.isIndianTimeZone()) {
                showLocationUnavailableToast();
            }
            //Pass an empty string for the provider
            location = new Location("");
            //Set the default location as configured in the App
            location.setLatitude(AppConstants.DEF_LATITUDE);
            location.setLongitude(AppConstants.DEF_LONGITUDE);
        }
        //Calling here to log Timezone debug message
        DateUtils.isIndianTimeZone();
        AppLogUtils.appendDebug("Long = " + location.getLongitude() + " Lat = " + location.getLatitude());
        AppConfiguration.setLocation(location.getLongitude(), location.getLatitude());
    }

    private void showDebugToast() {
        if (AppLogUtils.isDebugEnabled() && AppLogUtils.getDebugMessage() != null) {
            Toast.makeText(this, AppLogUtils.getDebugMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Display India Timezone as the assumed location
     */
    private void showLocationUnavailableToast() {
        if (!AppConfiguration.getInstance().isUnavailableToastShown()) {
            ActivityUtil.showToast(this, "Unable to get location information. Muhurat shall assume Indian Timezone");
            AppConfiguration.getInstance().setUnavailableToastShown(true);
        }
    }

    /**
     * Show the Toast with permission not given message
     */
    private void showLastKnownPermissionToast() {

    }

    private void showUnableToAccessLocationSettingsToast() {
        Toast.makeText(this, "Unable to access location settings in your phone",
                Toast.LENGTH_LONG).show();
        //AppConfiguration.getInstance().setLocationAlertShown(Boolean.TRUE);
    }

    /**
     * Show the Toast with Location not enabled message
     */
    private void showLastKnowLocationToast() {
        ActivityUtil.showToast(this, "Muhurat uses your last known location by default. Enable Location Settings for accurate result");
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
        if (id == R.id.action_loc_settings) {
            ActivityUtil.startSystemActivity(this,
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS,
                    AppMessages.MSG_LOC_SETTINGS_UNACCESSIBLE);
            return true;
        } else if (id == R.id.action_faq) {
            startActivity(new Intent(this, FAQActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
