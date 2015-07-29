package com.pineapple.mobilecraft.tumcca.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.pineapple.mobilecraft.cache.temp.IListCache;
import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by yihao on 7/19/15.
 * 一个缓存JSON数据的类
 */
public class PrefsCache implements IListCache<String, JSONObject>{

    SharedPreferences mPrefs = null;
    /**
     *
     * @param context
     * @param name Cache的名称
     */
    public PrefsCache(Context context, String name){
        try{
            mPrefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        }
        catch (NullPointerException exp){
            exp.printStackTrace();
        }
    }

    /**
     * 缓存一个 {@link org.json.JSONObject}
     * @param name
     * @param jsonObject
     */
//    public void putJSON(String name, JSONObject jsonObject){
//        if(null != mPrefs){
//            mPrefs.edit().putString(name, jsonObject.toString()).commit();
//        }
//    }

    /**
     * 缓存一个 {@link org.json.JSONObject}列表
     * @param names
     * @param jsonObjects
     */
//    public void putJSONList(List<String> names, List<JSONObject> jsonObjects){
//
//    }

    /**
     * 读取一个 {@link org.json.JSONObject}
     * @param name
     * @return 如果name存在，则返回一个有效的{@link org.json.JSONObject}，否则返回null
     */
//    public JSONObject getJSON(String name){
//        if(null != mPrefs){
//            if(mPrefs.contains(name)){
//                try {
//                    JSONObject jsonObject = new JSONObject(mPrefs.getString(name, ""));
//                    return jsonObject;
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    return null;
//                }
//            }
//            else{
//                return null;
//            }
//        }
//        else{
//            return null;
//        }
//
//    }

    /**
     * 获取一个{@link org.json.JSONObject}列表
     * @param names
     * @return
     */

    public List<JSONObject> getItems(Set<String> names){
        List<JSONObject> list = new ArrayList<JSONObject>();
        if(null!=names){
            for(String name:names){
                JSONObject jsonObject = getItem(name);
                if(null!=jsonObject){
                    list.add(jsonObject);
                }
            }
        }
        return list;
    }

    /**
     * 移除一个{@link org.json.JSONObject}
     * @param name
     */
    @Override
    public void remove(String name){
        if(mPrefs!=null){
            mPrefs.contains(name);
            mPrefs.edit().remove(name).commit();
        }
    }

    /**
     * 移除一个{@link org.json.JSONObject}列表
     * @param names
     */
    @Override
    public void removeList(Set<String> names){
        if(mPrefs!=null){
            SharedPreferences.Editor editor = mPrefs.edit();
            for(String name:names){
                mPrefs.contains(name);
                editor.remove(name);
            }
            editor.commit();
        }
    }

    @Override
    public List<String> getKeysBeforeItem(String key, int count) {
        return null;
    }

    @Override
    public List<String> getKeysAfterItem(String key, int count) {
        return null;
    }


    @Override
    public void putItems(Set<String> keySet, List<JSONObject> valueList) {
        Assert.assertEquals(keySet.size(), valueList.size());
        if(null != mPrefs){
            SharedPreferences.Editor editor = mPrefs.edit();
            Iterator<String> iteratorKey = keySet.iterator();
            Iterator iteratorValue = keySet.iterator();
            while(iteratorKey.hasNext()){
                editor.putString(iteratorKey.next(), iteratorValue.next().toString());
            }
            editor.commit();
        }
    }

    @Override
    public void putItem(String key, JSONObject value) {
        if(null != mPrefs){
            mPrefs.edit().putString(key, value.toString()).commit();
        }
    }

    @Override
    public JSONObject getItem(String key) {
        if(null != mPrefs){
            if(mPrefs.contains(key)){
                try {
                    JSONObject jsonObject = new JSONObject(mPrefs.getString(key, ""));
                    return jsonObject;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            else{
                return null;
            }
        }
        else{
            return null;
        }
    }


    @Override
    public List<JSONObject> getAllItems() {
        Map<String, ?> map = mPrefs.getAll();
        List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();

        Collection values = map.values();
        Iterator iterator = values.iterator();
        while (iterator.hasNext()){
            try {
                jsonObjectList.add(new JSONObject((String)iterator.next()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObjectList;
    }

    @Override
    public Set<String> getAllKeys() {
        Map<String, ?> map = mPrefs.getAll();
        return map.keySet();
    }

    @Override
    public boolean hasKey(String key) {
        return mPrefs.contains(key);
    }

    public void clear(){
        mPrefs.edit().clear().commit();

    }
}
