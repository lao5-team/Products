package com.pineapple.mobilecraft.tumcca.manager;

import android.util.Log;
import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.data.Works;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.utility.JSONCache;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 15/7/2.
 */
public class WorksManager {
    private static WorksManager mInstance = null;
    JSONCache mAlbumWorksCache = new JSONCache(TumccaApplication.applicationContext, "album_works");
    JSONCache mWorksCache = new JSONCache(TumccaApplication.applicationContext, "works");
    JSONCache mAlbumCache = new JSONCache(TumccaApplication.applicationContext, "album");
    JSONCache mMyAlbumsCache = new JSONCache(TumccaApplication.applicationContext, "myAlbums");
    public static WorksManager getInstance(){
        if(mInstance==null){
            mInstance = new WorksManager();
        }
        return mInstance;
    }

    public void putWorks(WorksInfo works){
        mWorksCache.putItem(String.valueOf(works.id), WorksInfo.toJSON(works));
    }

    public WorksInfo getWorks(int id){
        WorksInfo worksInfo = WorksInfo.fromJSON(mWorksCache.getItem(String.valueOf(id)));
        if(worksInfo.id!=id){
            return WorksInfo.NULL;
        }
        else
        {
            return worksInfo;
        }
    }

    public void putWorksList(List<WorksInfo> worksInfoList){

    }


    public void putAlbumWorks(long albumId, List<WorksInfo> listWorksInfo){
        JSONArray jsonArray = new JSONArray();
        for(WorksInfo worksInfo:listWorksInfo){
            jsonArray.put(WorksInfo.toJSON(worksInfo));

        }
        Log.v("Tumcca", jsonArray.toString());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("albumWorks", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAlbumWorksCache.putItem(String.valueOf(albumId), jsonObject);
    }


    /**
     * 根据albumId 返回一个WorksInfo的列表
     * @param albumId
     * @return 如果数据不存在，列表的个size为0
     */
    public List<WorksInfo> getAlbumWorks(long albumId){
        List<WorksInfo> result = new ArrayList<WorksInfo>();
        JSONObject jsonObject = mAlbumWorksCache.getItem(String.valueOf(albumId));
        if(null != jsonObject){
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("albumWorks");
                for(int i=0; i<jsonArray.length(); i++){
                    WorksInfo worksInfo = WorksInfo.fromJSON(jsonArray.getJSONObject(i));
                    result.add(worksInfo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return result;

    }

    public void setLatestAlbum(Album album){
        mAlbumCache.putItem("latest", Album.toJSON(album));
    }

    public Album getLatestAlbum(){
        JSONObject jsonObject = mAlbumCache.getItem("latest");
        if(null!=jsonObject){
            return Album.fromJSON(jsonObject);
        }
        else{
            return Album.DEFAULT_ALBUM;
        }
    }

    public void clearCache(){
        mAlbumWorksCache.clear();
        mWorksCache.clear();
        mAlbumCache.clear();
        mMyAlbumsCache.clear();
    }

    public List<Album> getMyAlbumList(){
        List<JSONObject> jsonObjectList = mMyAlbumsCache.getAllItems();
        List<Album> albumList = new ArrayList<Album>();
        for(JSONObject jsonObject:jsonObjectList){
            albumList.add(Album.fromJSON(jsonObject));
        }
        return albumList;
    }

    public void setMyAlbumList(List<Album> albumList){
        try{
            mMyAlbumsCache.clear();
            List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
            List<String> idList = new ArrayList<String>();
            for(Album album:albumList){
                idList.add(String.valueOf(album.id));
                jsonObjectList.add(Album.toJSON(album));
            }
            mMyAlbumsCache.putItems(idList, jsonObjectList);
        }
        catch (NullPointerException exp){
            exp.printStackTrace();
        }

    }

    public void addMyAlbum(Album album){
        if(null!=album&&Album.NULL!=album){
            mMyAlbumsCache.putItem(String.valueOf(album.id), Album.toJSON(album));
        }
    }

    public void removeMyAlbum(Album album){
        if(null!=album&&Album.NULL!=album){
            mMyAlbumsCache.remove(String.valueOf(album.id));
        }
    }

//    List<WorksInfo> mHomeWorks;
//    int mHomePageIndex = 1;
//    public void setHomeWorks(List<WorksInfo> works, int currentPage){
//        mHomeWorks = works;
//        mHomePageIndex = currentPage;
//    }





}
