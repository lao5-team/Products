package com.pineapple.mobilecraft.tumcca.server;

import android.util.Log;
import com.pineapple.mobilecraft.utils.SyncHttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by yihao on 15/6/12.
 */
public class PictureServer {
    public static final int INVALID_PICTURE_ID = -1;
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

                return INVALID_PICTURE_ID;
            }
        };
        return post.execute("works", file);
    }

    public int uploadAvatar(String token, File file)
    {
        String url =  mHost + "/api/avatars/upload";
        SyncHttpPost<Integer> post = new SyncHttpPost<Integer>(url, token, null) {
            @Override
            public Integer postExcute(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    return jsonObject.getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        return post.execute("avatar", file);
    }

    /**
     *
     * @param id
     * @return 图片的url
     */
    public String getPictureUrl(int id){

        return  mHost + "/api/pictures/download/" + id;
    }

    public String getPictureUrl(int id, int width, int height){
        if(width == 0){
            Log.v("Tumcca", Log.getStackTraceString(new RuntimeException()));
        }
        return  mHost + "/api/pictures/download/" + id + "/thumb/" + width + "/" + height;
    }

    public void deletePicture(String token, File file){

    }


}
