package com.pineapple.mobilecraft.tumcca.data;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yihao on 15/7/2.
 */
public class WorksInfo {
    public int id = -1;
    public int category = -1;
    public String tags = "";
    public String title = "";
    public String description = "";
    public String createTimeString = "";
    public int author = 0;
    public int likes = 0;
    public int collects = 0;
    public int comments = 0;
    //transient 的属性需要我们自己解析
    public transient Date createTime = null;
    public transient PictureInfo picInfo = null;
    public transient Profile profile =Profile.NULL;
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss.SSS");

    public static final WorksInfo NULL = new WorksInfo();

    public static WorksInfo fromJSON(JSONObject jsonObject){
        if(null!=jsonObject){
            Gson gson = new Gson();
            WorksInfo result =  gson.fromJson(jsonObject.toString(), WorksInfo.class);
//            try {
                //result.createTime = DATE_FORMAT.parse(result.createTimeString);
                try {
                    result.picInfo = PictureInfo.fromJSON(jsonObject.getJSONObject("picture"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            return result;
        }
        else{
            return WorksInfo.NULL;
        }

    }

    public static JSONObject toJSON(WorksInfo worksinfo){
        if(null!=worksinfo&&WorksInfo.NULL!=worksinfo){
            Gson gson = new Gson();
            try {
                JSONObject jsonObject = new JSONObject(gson.toJson(worksinfo));
                jsonObject.put("picture", PictureInfo.toJSON(worksinfo.picInfo));
                //jsonObject.put("createTime", DATE_FORMAT.format(worksinfo.createTime));
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
}
