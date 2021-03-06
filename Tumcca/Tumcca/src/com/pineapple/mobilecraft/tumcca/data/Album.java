package com.pineapple.mobilecraft.tumcca.data;

import com.google.gson.Gson;
import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by yihao on 15/7/1.
 */
public class Album {

//    public static class AlbumPrarams {
//        public String toString(){
//
//        }
//    }
//
//    public static class AlbumResult{
//        public String toString(){
//
//        }
//    }

    public String title = "";
    public String description = "";
    //public int sampleImageId = -1;

    public List<Integer> cover = null;

    /**
     * id 为0表示默认专辑
     */
    public transient long id = -1;

    public transient long author = -1;

    public transient List<WorksInfo> worksInfoList = null;

    public transient boolean isLiked = false;

    public transient boolean isCollected = false;

    public static Album NULL = new Album();

    public static long DEFAULT_ID = 0;

    public static Album DEFAULT_ALBUM = new Album(TumccaApplication.applicationContext.getString(R.string.default_album),
            "", DEFAULT_ID);



    public Album(){

    }

    public Album(String title, String description, long id){
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public static Album fromJSON(JSONObject jsonObject){
        Gson gson = new Gson();
        Album album = gson.fromJson(jsonObject.toString(), Album.class);
        try {
            album.id = jsonObject.getLong("id");
            album.author = jsonObject.getInt("author");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return album;
    }

    public static JSONObject toJSON(Album album){
        Gson gson = new Gson();
        try {
            JSONObject jsonObject = new JSONObject(gson.toJson(album));
            if(album.id!=-1){
                jsonObject.put("id", album.id);
            }
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //public static
}
