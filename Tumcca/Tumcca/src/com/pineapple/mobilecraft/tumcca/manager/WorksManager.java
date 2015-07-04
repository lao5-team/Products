package com.pineapple.mobilecraft.tumcca.manager;

import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.cache.temp.JSONCache;
import com.pineapple.mobilecraft.tumcca.data.Works;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;
import com.pineapple.mobilecraft.tumcca.server.WorksServer;
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
    JSONCache mAlbumWorksCache = new JSONCache(DemoApplication.applicationContext, "album_works");
    JSONCache mWorksCache = new JSONCache(DemoApplication.applicationContext, "works");

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

    public void putAlbumWorks(int albumId, List<WorksInfo> listWorksInfo){
        JSONArray jsonArray = new JSONArray();
        for(WorksInfo worksInfo:listWorksInfo){
            jsonArray.put(WorksInfo.toJSON(worksInfo));
        }
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
    public List<WorksInfo> getAlbumWorks(int albumId){
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



}
