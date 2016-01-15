package com.proptiger.uberhackathon.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Created by rohitgarg on 1/15/16.
 */
public class JsonParser {


    public static <T> Object parseJson(String jsonString, Class<T> model) {
        try{
            Gson gson = new Gson();
            return gson.fromJson(jsonString, model);
        }catch(JsonSyntaxException e){
            return null;
        }
    }
}
