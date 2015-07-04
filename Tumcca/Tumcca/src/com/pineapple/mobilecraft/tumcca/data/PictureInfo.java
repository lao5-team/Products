package com.pineapple.mobilecraft.tumcca.data;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yihao on 15/7/2.
 */
public class PictureInfo {
    public int id;
    public int width;
    public int height;

    public static PictureInfo fromJSON(JSONObject json){
        Gson gson = new Gson();
        return gson.fromJson(json.toString(), PictureInfo.class);
    }

    public static JSONObject toJSON(PictureInfo pictureInfo){
        Gson gson = new Gson();
        try {
            return new JSONObject(gson.toJson(pictureInfo));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
