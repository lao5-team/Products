package com.pineapple.mobilecraft.tumcca.manager;

import android.util.Log;
import com.pineapple.mobilecraft.utils.SyncHttpPost;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yihao on 15/6/7.
 */
public class LogManager {
    /**
     *
     * @param level "v", "d", "e"
     * @param filter 和andrid的log一致
     * @param content
     * @param remoteFlag 是否需要远程
     */
    public static void log(String level, String filter, final String content, boolean remoteFlag){
        if(level.equals("v")){
            Log.v(filter, content);
        }
        else if(level.equals("d")){
            Log.d(filter, content);
        }
        else if(level.equals("e")){
            Log.e(filter, content);
        }
        else {
            Log.v(filter, content);
        }

        if(remoteFlag){
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    String url = "http://120.26.202.114/api/logs/android";
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("description", content);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SyncHttpPost<String> post = new SyncHttpPost<String>(url, null, jsonObject.toString()) {
                        @Override
                        public String postExcute(String result) {
                            return null;
                        }
                    };
                    post.execute();
                }
            });
            t.start();
        }
    }
}
