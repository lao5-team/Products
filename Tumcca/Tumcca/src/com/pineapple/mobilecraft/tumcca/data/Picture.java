package com.pineapple.mobilecraft.tumcca.data;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yihao on 15/6/3.
 */
public class Picture {
    public String url;
    public String localPath;
    public float rotArc = 0;
    public int id;
    private static int ID_COUNT = 0;
    public Picture(String url, String localPath){
        this.id = ID_COUNT++;
        this.url = url;
        this.localPath = localPath;
    }

    public static JSONObject toJSON(Picture picture){
        Gson gson = new Gson();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(gson.toJson(picture));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  jsonObject;
    }

    public static Picture fromJSON(JSONObject jsonObject){
        Gson gson = new Gson();
        return gson.fromJson(jsonObject.toString(), Picture.class);
    }
}
