package com.pineapple.mobilecraft.tumcca.data;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 15/5/26.
 *               "category": 9,
 "tags": "string",
 "title": "string",
 "description": "string",
 "pictures": [
 1
 ]
 */
public class Works {
    public int category = -1;
    public String tags = "";
    public String title = "";
    public String description = "";
    public int albumId = -1;
    public List<Integer> pictures = new ArrayList<Integer>();

    public static final Works NULL = new Works();

    public static Works fromJSON(JSONObject jsonObject){
        if(null!=jsonObject){
            Gson gson = new Gson();
            return gson.fromJson(jsonObject.toString(), Works.class);
        }
        else{
            return Works.NULL;
        }

    }

    public static JSONObject toJSON(Works works){
        if(null!=works&&NULL!=works){
            Gson gson = new Gson();
            try {
                return new JSONObject(gson.toJson(works));
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
