package com.proptiger.uberhackathon.util;

import com.proptiger.uberhackathon.HackApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rohitgarg on 1/15/16.
 */
public class JsonBuilder {

    /**
     * A method to convert a model object to json string
     * @param payloadObject model Object
     *
     * @return jObject the JSONObject
     * @throws JSONException
     * */
    public static final JSONObject toJson(Object payloadObject) throws JSONException {
        if(payloadObject == null){
            throw new IllegalArgumentException("Invalid argument");
        }

        String payload = HackApplication.gson.toJson(payloadObject);
        JSONObject jObject = new JSONObject(payload);
        return jObject;
    }


    /**
     * A method to convert a model object to json string
     * @param payloadObject model Object
     *
     * @return jObject the JSONObject
     * @throws JSONException
     * */
    public static final JSONArray toJsonArray(Object payloadObject) throws JSONException {
        if(payloadObject == null){
            throw new IllegalArgumentException("Invalid argument");
        }

        String payload = HackApplication.gson.toJson(payloadObject);
        JSONArray jArray = new JSONArray(payload);
        return jArray;
    }
}
