package com.pineapple.mobilecraft.tumcca.data;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yihao on 15/7/1.
 */
public class Album {
    public String title = "";
    public String description = "";

    /**
     * id 为0表示默认专辑
     */
    public String id = "";

    public static Album NULL = new Album();
    public static Album fromJSON(JSONObject jsonObject){
        Gson gson = new Gson();
        return gson.fromJson(jsonObject.toString(), Album.class);
    }

    public static JSONObject toJSON(Album album){
        Gson gson = new Gson();
        try {
            return new JSONObject(gson.toJson(album));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
