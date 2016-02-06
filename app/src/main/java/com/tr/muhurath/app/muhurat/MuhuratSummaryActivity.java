package com.tr.muhurath.app.muhurat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.tr.muhurath.app.muhurat.kaal.GuliKaal;
import com.tr.muhurath.app.muhurat.kaal.Kaal;
import com.tr.muhurath.app.muhurat.kaal.RahuKaal;
import com.tr.muhurath.app.muhurat.kaal.YamaGandaKaal;
import com.tr.muhurath.app.muhurat.utils.DateUtils;
import com.tr.muhurath.app.muhurat.utils.SunRiseSetUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import android.support.v7.widget.ShareActionProvider;

/**
 * Activity displaying the Muhurat Summary
 * Created by Balaji Katika (balaji.katika@gmail.com) on 1/30/16.
 */
public class MuhuratSummaryActivity extends AppCompatActivity {

    public static final String LBL_DATE_DISPLAY = "E dd-MMM-yyyy";
    public static final String TAG = MuhuratSummaryActivity.class.getName();

    private StringBuffer summaryText = new StringBuffer();
    private ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muhurat_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initShareBtnListener();
        //Parse the date passed to the activity
        Intent intent = getIntent();
        String inputDate = intent.getStringExtra(IntentConstants.DATE_DDMMYYYY);
        Date selectedDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            selectedDate = sdf.parse(inputDate);
        }
        catch (ParseException parseException) {
            //Set the current date
            selectedDate = new Date();
        }

        //Calculate Sunrise and SunSet
        //Date sunRise= SunRiseSetUtil.getSunRise(selectedDate);
        //Date sunSet=SunRiseSetUtil.getSunSet(selectedDate);
        Location loc = new Location(AppConfiguration.latitude, AppConfiguration.longitude);
        Log.d(TAG, TimeZone.getDefault().getID());
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(loc, TimeZone.getDefault().getID());
        Date sunRise = SunRiseSetUtil.getSunRiseLocationBased(selectedDate, calculator);
        Date sunSet = SunRiseSetUtil.getSunSetLocationBased(selectedDate, calculator);

        //Generate label with the selected date
        SimpleDateFormat displayDateFormat = new SimpleDateFormat(LBL_DATE_DISPLAY);
        TextView dateHolder = (TextView) findViewById(R.id.txtMuhuratDateHolder);
        String msg = null;
        msg = "Muhurat for " + displayDateFormat.format(selectedDate);
        dateHolder.setText(msg);
        summaryText.append(msg + "<br>");

        Kaal kaal = null;
        TextView kalHolder = null;

        //Display Guli Kaal
        kaal = new GuliKaal();
        kalHolder = (TextView) findViewById(R.id.txtMuhuratGuli);
        msg = "Gulika : " + kaal.getMuhuratForDisplay(sunRise, sunSet);
        kalHolder.setText(msg);
        summaryText.append(msg + "<br>");

        //Display Rahu Kaal
        kaal = new RahuKaal();
        kalHolder = (TextView) findViewById(R.id.txtMuhuratRahu);
        msg = "Rahu Kaalam : " + kaal.getMuhuratForDisplay(sunRise, sunSet);
        kalHolder.setText(msg);
        summaryText.append(msg + "<br>");

        //Display Yama Kaal
        kaal = new YamaGandaKaal();
        kalHolder = (TextView) findViewById(R.id.txtMuhuratYama);
        msg = "Yama Gandam : " + kaal.getMuhuratForDisplay(sunRise, sunSet);
        kalHolder.setText(msg);
        summaryText.append(msg + "<br>");

        kalHolder = (TextView) findViewById(R.id.txtMuhuraShoolam);
        msg = "Shoolam : " + DateUtils.getShoolamDirection(selectedDate);
        kalHolder.setText(msg);
        summaryText.append(msg + "<br>");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);

        //Display Sun Rise
        kalHolder = (TextView) findViewById(R.id.txtMuhuraSunRise);
        msg = "Sun Rise : " + DateUtils.get12HourFormat(calculator.getOfficialSunriseForDate(calendar));
        kalHolder.setText(msg);
        summaryText.append(msg + "<br>");

        //Display Sun Set
        kalHolder = (TextView) findViewById(R.id.txtMuhuraSunSet);
        msg = "Sun Set   : " + DateUtils.get12HourFormat(calculator.getOfficialSunsetForDate(calendar));
        kalHolder.setText(msg);
        summaryText.append(msg + "<br>");

        //Set back button on Tool bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initShareBtnListener() {
        Button shareBtn = (Button) findViewById(R.id.shareButton);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, Html.fromHtml("<p>Muhurath</p>"));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p>" + summaryText.toString() + "</p>"));
                startActivity(Intent.createChooser(sharingIntent, getResources().getText(R.string.send_to)));
            }
        });
    }

    /**
     * Set Share Intent for Share toolbar button
     */
    private void setShareIntent(Intent shareIntent){
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(shareIntent);
            Log.d(TAG, "setShareIntent - Share Action Provider is not null");
        }
        else {
            Log.d(TAG, "setShareIntent - Share Action Provider is null");
        }
    }

    /**
     * Create the Share Intent
     * @return
     */
    private Intent createShareIntent() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, Html.fromHtml("<p>Muhurath</p>"));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p>" + summaryText.toString() + "</p>"));
        return sharingIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "Dummy2");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_muhurat_summary, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        //shareActionProvider = (ShareActionProvider)item.getActionProvider();
        setShareIntent(createShareIntent());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        else if (id == R.id.action_faq) {
            startActivity(new Intent(this, FAQActivity.class));
            return true;
        }
        else if (id == R.id.menu_item_share) {
            Log.d(TAG,"onOptionsItemSelected - share selected");
            setShareIntent(createShareIntent());
        }

        return super.onOptionsItemSelected(item);
    }
}
