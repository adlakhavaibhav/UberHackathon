package com.proptiger.uberhackathon.network;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.proptiger.uberhackathon.HackApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;

import cache.LruBitmapCache;
import constant.ResponseConstants;

/**
 * Created by rohitgarg on 1/15/16.
 */
public class HackNetworkClient {

    public static final String TAG = HackNetworkClient.class.getSimpleName();

    private RequestQueue makaanGetRequestQueue;
    private ImageLoader mImageLoader;


    private AssetManager assetManager;
    private static HackNetworkClient instance;

    private HashMap<String, String> requestUrlToTag = new HashMap<>();

    private HackNetworkClient(Context appContext) {
        makaanGetRequestQueue = Volley.newRequestQueue(appContext);

        assetManager = appContext.getAssets();
    }

    public static void init(Context appContext) {
        instance = new HackNetworkClient(appContext);
    }


    public static HackNetworkClient getInstance() {
        return instance;
    }

    public void get(String url, final JSONGetCallback jsonGetCallback) {
        get(url, jsonGetCallback, null, null);
    }

    public void get(String url, final JSONGetCallback jsonGetCallback, String mockFile) {
        get(url, jsonGetCallback, mockFile, null);
    }

    public void get(String url, final Type type, final ObjectGetCallback objectGetCallback, String mockFile) {
        get(url, type, objectGetCallback, mockFile, null, false);
    }

    public void get(String url, final StringRequestCallback stringRequestCallback) {
        get(url, stringRequestCallback, null);
    }


    @SuppressWarnings("unchecked")
    public void get(final String url, final JSONGetCallback jsonGetCallback, String mockFile, String tag) {

        if (null != mockFile) {

            try {
                JSONObject mockFileResponse = readFromMockFile(mockFile);
                //JSONObject response = mockFileResponse.getJSONObject(ResponseConstants.DATA);
                jsonGetCallback.onSuccess(mockFileResponse);
            } catch (Exception e) {
                Log.e(TAG, "Exception", e);
            }

        } else {
            final JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, appendSourceDomain(url), null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            completeRequestInQueue(url);
                            jsonGetCallback.onSuccess(response);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            completeRequestInQueue(url);
                            jsonGetCallback.onError();
                            Log.e(TAG, "Network error", error);
                        }
                    });
            addToRequestQueue(jsonRequest, tag);
        }

    }

    public void get(final String url, final Type type, final ObjectGetCallback objectGetCallback) {
        get(url, type, objectGetCallback, null, null, false);
    }

    public void get(final String url, final Type type, final ObjectGetCallback objectGetCallback, boolean isDataArr) {
        get(url, type, objectGetCallback, null, null, isDataArr);
    }

    public void get(final String url, final Type type, final ObjectGetCallback objectGetCallback, String mockFile, String tag, final boolean isDataArr) {

        if (null != mockFile) {
            try {
                JSONObject mockFileResponse = readFromMockFile(mockFile);
                Object objResponse;
                if (isDataArr) {
                    JSONArray response = mockFileResponse.getJSONArray(ResponseConstants.DATA);
                    objResponse = HackApplication.gson.fromJson(response.toString(), type);
                } else {
                    JSONObject response = mockFileResponse.getJSONObject(ResponseConstants.DATA);
                    objResponse = HackApplication.gson.fromJson(response.toString(), type);
                }

                objectGetCallback.onSuccess(objResponse);

            } catch (Exception e) {
                Log.e(TAG, "Exception", e);
            }
        } else {

            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, appendSourceDomain(url), null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            completeRequestInQueue(url);
                            try {
                                Object objResponse;
                                if (isDataArr) {
                                    JSONArray tempResponse = response.getJSONArray(ResponseConstants.DATA);
                                    objResponse = HackApplication.gson.fromJson(tempResponse.toString(), type);
                                } else {
                                    response = response.getJSONObject(ResponseConstants.DATA);
                                    objResponse = HackApplication.gson.fromJson(response.toString(), type);
                                }


                                objectGetCallback.onSuccess(objResponse);

                            } catch (JSONException e) {
                                Log.e(TAG, "JSONException", e);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            completeRequestInQueue(url);
                            objectGetCallback.onError();
                            Log.e(TAG, "Network error", error);
                        }
                    });

            addToRequestQueue(jsonRequest, tag);

        }
    }


    public void get(final String url, final StringRequestCallback stringRequestCallback, String tag) {

        StringRequest stringRequest = new StringRequest
                (Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        completeRequestInQueue(appendSourceDomain(url));
                        stringRequestCallback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        completeRequestInQueue(url);
                        stringRequestCallback.onError();
                        Log.e(TAG, "Network error", error);
                    }
                });
        addToRequestQueue(stringRequest, tag);
    }

    public void post(final String url, JSONObject jsonObject,
                     final StringRequestCallback stringRequestCallback, String tag) {

        /*StringRequest stringRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        completeRequestInQueue(url);
                        stringRequestCallback.onSuccess(response);

                    }
                }, jsonObject, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        completeRequestInQueue(url);
                        stringRequestCallback.onError();
                    }
                });
        addToRequestQueue(stringRequest, tag);*/
    }

    private void addToRequestQueue(Request req, String tag) {
        if (null == tag) {
            tag = requestUrlToTag.get(req.getUrl());
            if (null == tag) {
                tag = HackApplication.randomString.nextString();
                requestUrlToTag.put(req.getUrl(), tag);
            }
            req.setTag(tag);
        } else {
            req.setTag(tag);
        }

        cancelFromRequestQueue(req, tag);
        Log.e(TAG, "URL: -> " + req.getUrl());

        makaanGetRequestQueue.add(req);
    }


    private String appendSourceDomain(String url) {

//        if (!url.contains(RequestConstants.SOURCE_DOMAIN_MAKAAN)) {
//            if (url.contains("?")) {
//                url = url.concat("&").concat(RequestConstants.SOURCE_DOMAIN_MAKAAN);
//            } else {
//                url = url.concat("?").concat(RequestConstants.SOURCE_DOMAIN_MAKAAN);
//            }
//        }

        return url;
    }

    private void cancelFromRequestQueue(Request req, String tag) {
        if (null != makaanGetRequestQueue) {
            if (tag == null && null != req) {
                tag = requestUrlToTag.get(req.getUrl());
                makaanGetRequestQueue.cancelAll(tag);
            } else if (null != tag) {
                makaanGetRequestQueue.cancelAll(tag);
            }
        }

    }

    private void completeRequestInQueue(String url) {
        requestUrlToTag.remove(url);
    }


    /**
     * Retrieves volley image loader
     */
    public ImageLoader getImageLoader() {
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.makaanGetRequestQueue, new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    private JSONObject readFromMockFile(String mockFile) throws JSONException, IOException {
        BufferedReader br = null;
        StringBuilder json = new StringBuilder();


        br = new BufferedReader(new InputStreamReader(assetManager.open(mockFile)));
        String line = null;
        while ((line = br.readLine()) != null) {
            json.append(line);
        }
        return new JSONObject(json.toString());

    }
}
