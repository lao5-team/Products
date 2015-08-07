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
import java.util.HashMap;
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

    public static void removeWork(String token, long id){
        String url = host + "/api/works/trash/" + id;
        SyncHttpDelete<String> post = new SyncHttpDelete<String>(url, token) {
            @Override
            public String postExcute(String result) {
                return null;
            }
        };
        post.execute();
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

    public static void removeAlbum(String token, long id){
        String url = host + "/api/album/" + id;
        SyncHttpDelete<String> post = new SyncHttpDelete<String>(url, token) {
            @Override
            public String postExcute(String result) {
                return null;
            }
        };
        post.execute();
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

    public static List<Album> getAuthorAlbumList(int authorId){
        String url = host + "/api/album/author/" + authorId;
        SyncHttpGet<List<Album>> get = new SyncHttpGet<List<Album>>(url, null) {
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
    public static List<WorksInfo> getWorksOfAlbum(String token, final long albumId, final long authorId, int page, int size, int width){
        String url = host + "/api/album/" + albumId + "/author/" + authorId + "/workses/page/" + page + "/size/" +
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

    public static boolean[] isWorksLiked(String token,final long [] ids){
        String url = host + "/api/like/works/islike";
        SyncHttpPost<boolean[]> post = new SyncHttpPost<boolean[]>(url, token, null) {
            @Override
            public boolean[] postExcute(String result) {
                boolean[] results = {};
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    results = sortResult("id", "isLike", jsonArray, ids);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return results;
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

    public static boolean dislikeWork(String token, String workId){
        String url = host + "/api/like/" + workId;
        SyncHttpDelete<String> post = new SyncHttpDelete<String>(url, token) {
            @Override
            public String postExcute(String result) {
                return null;
            }
        };
        post.execute();
        return true;
    }




    /**
     *
     * @param token
     * @param page
     * @param size
     * @param width
     * @return
     */
    public static List<WorksInfo> getMyLikeWorks(String token, int page, int size, int width){
        //http://120.26.202.114:80/api/like/works/page/1/size/5/width/400
        String url = host + "/api/like/works/page/" + page + "/size/" + size + "/width/" + width;
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

    public static boolean[] isWorksCollected(String token,final long [] ids){
        String url = host + "/api/collect/works/iscollect";
        SyncHttpPost<boolean[]> post = new SyncHttpPost<boolean[]>(url, token, null) {
            @Override
            public boolean[] postExcute(String result) {
                boolean[] results = {};
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    results = sortResult("id", "isCollect", jsonArray, ids);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return results;
            }
        };
        return post.execute();
    }

    public static boolean collectWorks(String token, long worksID, long userID){
        String url = host + "/api/collect";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("works", worksID);
            jsonObject.put("collector", userID);

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

    public static boolean discollectWork(String token, long workId){
        String url = host + "/api/collect/" + workId;
        SyncHttpDelete<String> post = new SyncHttpDelete<String>(url, token) {
            @Override
            public String postExcute(String result) {
                return null;
            }
        };
        post.execute();
        return true;
    }

    public static boolean[] isAlbumsCollected(String token, final long [] ids){
        String url = host + "/api/collect/album/iscollect";
        JSONArray params = new JSONArray();
        for(long id:ids){
            params.put(id);
        }
        SyncHttpPost<boolean[]> post = new SyncHttpPost<boolean[]>(url, token, params.toString()) {
            @Override
            public boolean[] postExcute(String result) {
                boolean[] results = {};
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    results = sortResult("id", "isCollect", jsonArray, ids);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return results;
            }
        };
        return post.execute();
    }

    public static boolean collectAlbum(String token, long albumID, long userID){
        String url = host + "/api/collectalbum";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("album", albumID);
            jsonObject.put("collector", userID);

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

    public static boolean discollectAlbum(String token, long albumID){
        String url = host + "/api/collectalbum/" + albumID;
        SyncHttpDelete<String> post = new SyncHttpDelete<String>(url, token) {
            @Override
            public String postExcute(String result) {
                return null;
            }
        };
        post.execute();
        return true;
    }


    public static boolean[] isAlbumsLiked(String token, final long [] ids){
        String url = host + "/api/like/album/islike";
        JSONArray params = new JSONArray();
        for(long id:ids){
            params.put(id);
        }
        SyncHttpPost<boolean[]> post = new SyncHttpPost<boolean[]>(url, token, params.toString()) {
            @Override
            public boolean[] postExcute(String result) {
                boolean[] results = {};
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    results = sortResult("id", "isLike", jsonArray, ids);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return results;
            }
        };
        return post.execute();
    }

    public static boolean likeAlbum(String token, long albumID, long userID){
        String url = host + "/api/likealbum";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("album", albumID);
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

    public static boolean dislikeAlbum(String token, long albumID){
        String url = host + "/api/likealbum/" + albumID;
        JSONObject jsonObject = new JSONObject();
        SyncHttpDelete<String> post = new SyncHttpDelete<String>(url, token) {
            @Override
            public String postExcute(String result) {
                return null;
            }
        };
        post.execute();
        return true;
    }
    /**
     *
     * @param token
     * @param page
     * @param size
     * @param width
     * @return
     */
    public static List<WorksInfo> getCollectWorks(int authorId, int page, int size, int width){
        //http://120.26.202.114:80/api/like/works/page/1/size/5/width/400
        //String url = host + "/api/like/works/page/" + page + "/size/" + size + "/width/" + width;
        String url = host + "/api/collect/works/author/" + authorId + "/page/" + page + "/size/" + size + "/width/" + width;
        SyncHttpGet<List<WorksInfo>> post = new SyncHttpGet<List<WorksInfo>>(url, null) {
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

    /**
     *
     * @param token
     * @param page
     * @param size
     * @param width
     * @return
     */
    public static List<WorksInfo> getLikeWorks(int authorId, int page, int size, int width){
        //http://120.26.202.114:80/api/like/works/page/1/size/5/width/400
        //String url = host + "/api/like/works/page/" + page + "/size/" + size + "/width/" + width;
        String url = host + "/api/like/works/author/" + authorId + "/page/" + page + "/size/" + size + "/width/" + width;
        SyncHttpGet<List<WorksInfo>> post = new SyncHttpGet<List<WorksInfo>>(url, null) {
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

    /**
     *
     * @param token
     * @param page
     * @param size
     * @param width
     * @return
     */
    public static List<Album> getLikeAlbums(int authorId, int page, int size){
        //http://120.26.202.114:80/api/like/works/page/1/size/5/width/400
        //String url = host + "/api/like/works/page/" + page + "/size/" + size + "/width/" + width;
        String url = host + "/api/like/album/author/" + authorId + "/page/" + page + "/size/" + size;
        SyncHttpGet<List<Album>> post = new SyncHttpGet<List<Album>>(url, null) {
            @Override
            public List<Album> postExcute(String result) {
                List<Album> worksInfoList = new ArrayList<Album>();
                try {
                    //JSONObject jsonObject = new JSONObject(result);
                    JSONArray array = new JSONArray(result);
                    for(int i=0; i<array.length(); i++){
                        worksInfoList.add(Album.fromJSON(array.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return worksInfoList;
            }
        };
        return post.execute();
    }

    public static List<Album> getCollectAlbums(int authorId, int page, int size){
        //http://120.26.202.114:80/api/like/works/page/1/size/5/width/400
        //String url = host + "/api/like/works/page/" + page + "/size/" + size + "/width/" + width;
        String url = host + "/api/collect/album/author/" + authorId + "/page/" + page + "/size/" + size;
        SyncHttpGet<List<Album>> post = new SyncHttpGet<List<Album>>(url, null) {
            @Override
            public List<Album> postExcute(String result) {
                List<Album> worksInfoList = new ArrayList<Album>();
                try {
                    //JSONObject jsonObject = new JSONObject(result);
                    JSONArray array = new JSONArray(result);
                    for(int i=0; i<array.length(); i++){
                        worksInfoList.add(Album.fromJSON(array.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return worksInfoList;
            }
        };
        return post.execute();
    }


    public static void parseAlbumList(String token, List<Album> albumList){
        long[] ids = new long[albumList.size()];
        for(int i=0; i<albumList.size(); i++){
            ids[i] = albumList.get(i).id;
        }
        boolean[] isLike = WorksServer.isAlbumsLiked(token, ids);
        boolean[] isCollect = WorksServer.isAlbumsCollected(token, ids);
        for(int i=0; i<albumList.size(); i++){
            albumList.get(i).isLiked = isLike[i];
            albumList.get(i).isCollected = isCollect[i];
        }
    }

    public static boolean[] sortResult(String idName, String valueName, JSONArray array, long[]ids){
        HashMap<Long, Boolean> map = new HashMap<Long, Boolean>();
        for(int i=0; i<array.length(); i++){
            try {
                JSONObject object = array.getJSONObject(i);
                map.put(object.getLong(idName), object.getBoolean(valueName));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        boolean[] result = new boolean[ids.length];
        for(int i=0; i<result.length; i++){
            result[i] = map.get(ids[i]);
        }
        return result;
    }
}
