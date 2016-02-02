package com.tr.muhurath.app.muhurat;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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

import com.tr.muhurath.app.muhurat.utils.AppConstants;

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

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        try {
            locationManager.requestLocationUpdates(provider, AppConstants.LOC_MIN_TIME_INTERVAL, AppConstants.LOC_MIN_DISTANCE, this);
        }
        catch (SecurityException securityException) {
            Log.w(TAG, "onResume - User has not given permission for Location Service");
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
            Log.w(TAG, "onResume - User has not given permission for Location Service");
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
            Log.i(TAG, "onLocationChanged - Unable to retrieve the last known location");
            AppConfiguration.setLocation(AppConstants.DEF_LONGITUDE, AppConstants.DEF_LATITUDE);
        }
    }

    /**
     * Gather Location Details using Location Service
     */
    private void gatherLocationDetails() {
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        try {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location!=null) {
                AppConfiguration.setLocation(location.getLongitude(), location.getLatitude());
            }
            else {
                Log.i(TAG, "gatherLocationDetails - Unable to retrieve the last known location");
                AppConfiguration.setLocation(AppConstants.DEF_LONGITUDE, AppConstants.DEF_LATITUDE);
            }
        } catch (SecurityException securityException) {
            Log.w(TAG, "gatherLocationDetails - User has not given permission for Location Service");
            AppConfiguration.setLocation(AppConstants.DEF_LONGITUDE, AppConstants.DEF_LATITUDE);
        }
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
        if (id == R.id.action_settings) {
           // startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
