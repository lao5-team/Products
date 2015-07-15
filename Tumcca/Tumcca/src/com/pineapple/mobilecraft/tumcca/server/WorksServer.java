package com.pineapple.mobilecraft.tumcca.server;

import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.data.Works;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.utils.SyncHttpDelete;
import com.pineapple.mobilecraft.utils.SyncHttpGet;
import com.pineapple.mobilecraft.utils.SyncHttpPost;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 15/6/26.
 */
public class WorksServer {

    public static final int INVALID_WORKS_ID = -1;
    static final String host = "http://120.26.202.114";

    /**
     * 发布作品，发布前需要用户的profile已经完善
     *
     * @param token
     * @param works picture, description, works, category和album为必填项
     * @return
     */
    public static int uploadWorks(String token, Works works){
        SyncHttpPost<Integer> post = new SyncHttpPost<Integer>(host + "/api/works", token, Works.toJSON(works).toString()) {
            @Override
            public Integer postExcute(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    return jsonObject.getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return INVALID_WORKS_ID;
                }
            }
        };

        return post.execute();
    }

    /**
     * 上传用户专辑
     * @param token
     * @param album
     * @return
     */
    public static int uploadAlbum(String token ,Album album){
        SyncHttpPost<Integer> post = new SyncHttpPost<Integer>(host + "/api/album", token, Album.toJSON(album).toString()) {
            @Override
            public Integer postExcute(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    return jsonObject.getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return INVALID_WORKS_ID;
                }
            }
        };

        return post.execute();
    }


    /**
     * 获取当前用户发布的专辑
     * @param token
     * @return
     */
    public static List<Album> getMyAlbumList(String token){
        String url = host + "/api/album";
        SyncHttpGet<List<Album>> get = new SyncHttpGet<List<Album>>(url, token) {
            @Override
            public List<Album> postExcute(String result) {
                List<Album> albumList = new ArrayList<Album>();
                try {
                    JSONArray array = new JSONArray(result);

                    for(int i=0; i<array.length(); i++){
                        albumList.add(Album.fromJSON(array.getJSONObject(i)));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return albumList;
            }
        };
        return get.execute();
    }

    /**
     * 获取专辑下面的作品
     * @param token
     * @param albumId 专辑id
     * @param page 页数
     * @param size 数量
     * @param width 宽度
     * @return
     */
    public static List<WorksInfo> getWorksOfAlbum(String token, final int albumId, int page, int size, int width){
        String url = host + "/api/album/" + albumId + "/workses/page/" + page + "/size/" +
                size + "/width/" + width;
        SyncHttpPost<List<WorksInfo>> post = new SyncHttpPost<List<WorksInfo>>(url, token, null) {
            @Override
            public List<WorksInfo> postExcute(String result) {
                List<WorksInfo> worksInfoList = new ArrayList<WorksInfo>();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray array = jsonObject.getJSONArray("results");
                    for(int i=0; i<array.length(); i++){
                        worksInfoList.add(WorksInfo.fromJSON(array.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return worksInfoList;
            }
        };
        return post.execute();
    }

    public static List<WorksInfo> getWorksInHome(int page, int size, int width){
        String url = host + "/api/works/homepage/page/" + page + "/size/" + size + "/width/" + width;
        String token = UserManager.getInstance().getCurrentToken();
        SyncHttpGet<List<WorksInfo>> post = new SyncHttpGet<List<WorksInfo>>(url, token) {
            @Override
            public List<WorksInfo> postExcute(String result) {
                List<WorksInfo> worksInfoList = new ArrayList<WorksInfo>();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray array = jsonObject.getJSONArray("results");
                    for(int i=0; i<array.length(); i++){
                        worksInfoList.add(WorksInfo.fromJSON(array.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return worksInfoList;
            }
        };
        return post.execute();
    }

    public static boolean likeWorks(String token, String worksID, String userID){
        String url = host + "/api/like";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("works", worksID);
            jsonObject.put("admirer", userID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        SyncHttpPost<String> post = new SyncHttpPost<String>(url, token, jsonObject.toString()) {
            @Override
            public String postExcute(String result) {
                return null;
            }
        };
        post.execute();
        return true;
    }

    public static boolean cancellikeWorks(String token, String worksID, String userID){
//        String url = host + "/api/like";
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("works", worksID);
//            jsonObject.put("admirer", userID);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        SyncHttpDelete<String> delete = new SyncHttpDelete<String>(url, token, jsonObject.toString()) {
//            @Override
//            public String postExcute(String result) {
//                return null;
//            }
//        };
//        post.execute();
//        return true;
        return false;
    }

}
