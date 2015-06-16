package com.pineapple.mobilecraft.tumcca.server;

import com.pineapple.mobilecraft.utils.SyncHttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by yihao on 15/6/12.
 */
public class PictureServer {

    private static PictureServer mInstance = null;
    private final String mHost = "http://120.26.202.114";
    public static PictureServer getInstance(){
        if(mInstance==null){
            mInstance = new PictureServer();
        }
        return mInstance;
    }

    public int uploadPicture(String token, File file){
        String url = mHost + "/api/pictures/upload";
        SyncHttpPost<Integer> post = new SyncHttpPost<Integer>(url, token, null) {
            @Override
            public Integer postExcute(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    return jsonObject.getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return -1;
            }
        };
        return post.execute("works", file);
    }

    public String getPicture(String token, String id){

        return  mHost + "/api/pictures/download/" + id;
    }

    public void deletePicture(String token, File file){

    }


}