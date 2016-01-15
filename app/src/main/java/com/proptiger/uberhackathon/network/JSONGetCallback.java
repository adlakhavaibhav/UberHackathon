package com.proptiger.uberhackathon.network;


import org.json.JSONObject;

/**
 * Created by rohitgarg on 1/15/16.
 */
public abstract class JSONGetCallback extends GetCallback {
    public abstract void onSuccess(JSONObject responseObject);
}
