package com.pineapple.mobilecraft.tumcca.data;

import com.google.gson.Gson;
import org.json.JSONObject;

/**
 * Created by yihao on 15/5/30.
 */
public class TestData {
    public String getId() {
        return id;
    }

    private String id;

    public String getTitle() {
        return title;
    }

    private String title;

    public static TestData fromJSON(JSONObject json) {
        Gson gson = new Gson();
        TestData testData = gson.fromJson(json.toString(), TestData.class);
        return testData;
    }
}


