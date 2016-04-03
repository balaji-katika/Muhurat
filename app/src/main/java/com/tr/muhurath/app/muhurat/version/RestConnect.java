package com.tr.muhurath.app.muhurat.version;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by geddam on 3/12/2016.
 */
public class RestConnect<T> extends AsyncTask<Void, Void, VersionInfo> {

    private CallBack<T> callBack;

    public RestConnect(CallBack<T> callBack) {
        this.callBack = callBack;
    }

    public void execute() {
        super.execute();
    }


    @Override
    protected VersionInfo doInBackground(Void... params) {
        try {
            final String url = "http://54.238.238.162:3001/muhurat/version";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            return restTemplate.getForObject(url, VersionInfo.class);
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(VersionInfo eggPrices) {
        callBack.onSuccess((T)eggPrices);
    }

}

