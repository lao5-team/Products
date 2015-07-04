package com.pineapple.mobilecraft.tumcca.data;

import com.google.gson.Gson;
import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by yihao on 15/7/1.
 */
public class Album {
    public String title = "";
    public String description = "";

    /**
     * id 为0表示默认专辑
     */
    public transient int id = -1;

    public transient int author = -1;

    public transient List<WorksInfo> worksInfoList = null;


    public static Album NULL = new Album();

    public static Album DEFAULT_ALBUM = new Album(DemoApplication.applicationContext.getString(R.string.default_album),
            "", 0);

    public Album(){

    }

    public Album(String title, String description, int id){
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public static Album fromJSON(JSONObject jsonObject){
        Gson gson = new Gson();
        Album album = gson.fromJson(jsonObject.toString(), Album.class);
        try {
            album.id = jsonObject.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return album;
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
