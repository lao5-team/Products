package com.pineapple.mobilecraft.tumcca.data;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yihao on 15/6/24.
 *
 *              "action" : "FOLLOW",
                "id" : 2,
                 "message" : "{\"follower\":3,\"toFollow\":4}",
                 "createTime" : "2015-06-22 20:48:46.000"
 */
public class Notification {
    public static String DATE_FORMAT = "yyyy-mm-dd HH:mm:ss";

    public String action;
    public String id;
    public String message;
    public String createTime;

    public static Notification fromJSON(JSONObject jsonObject){
        Gson gson = new Gson();
        return gson.fromJson(jsonObject.toString(), Notification.class);
    }

    public static JSONObject toJSON(Notification notification){
        Gson gson = new Gson();
        try {
            return new JSONObject(gson.toJson(notification));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }


}
