package com.proptiger.uberhackathon;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.proptiger.uberhackathon.network.HackNetworkClient;
import com.proptiger.uberhackathon.util.RandomString;

/**
 * Created by rohitgarg on 1/15/16.
 */
public class HackApplication extends Application {

    public static Gson gson;

    public static RandomString randomString = new RandomString(6);

    @Override
    public void onCreate() {
        super.onCreate();
        gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
        HackNetworkClient.init(this);
    }
}
