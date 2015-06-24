package com.pineapple.mobilecraft.tumcca.server;

import com.pineapple.mobilecraft.tumcca.data.Notification;
import com.pineapple.mobilecraft.utils.SyncHttpGet;
import com.pineapple.mobilecraft.utils.SyncHttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 15/6/24.
 */
public class NotificationServer {

    /**
     * 发送关注
     * @param token
     * @param followerId
     * @param toFollowId
     */
    public static void sendFollowNotify(String token, int followerId, int toFollowId){
        String url = "http://120.26.202.114/api/follow/notify";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("follower", followerId);
            jsonObject.put("toFollow", toFollowId);
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


    }

    /**
     * 获取关于关注的通知
     * @param token
     * @return
     */
    public static List<Notification> getFollowNotification(String token){
        String url = "http://120.26.202.114/api/notifications";
        SyncHttpGet<List<Notification>> get = new SyncHttpGet<List<Notification>>(url, token) {
            @Override
            public List<Notification> postExcute(String result) {
                List<Notification> listNotification = new ArrayList<Notification>();
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Notification notification = Notification.fromJSON(jsonObject);
                        listNotification.add(notification);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return listNotification;
            }
        };
        List<Notification> listNotification = get.execute();
        for(Notification notification:listNotification){
            String url2 = "http://120.26.202.114/api/notifications/" + notification.id + "/read";
            SyncHttpPost<String> post = new SyncHttpPost<String>(url2, token, "") {
                @Override
                public String postExcute(String result) {
                    return null;
                }
            };
            post.execute();
        }
        return listNotification;
    }


    /**
     * 取消关注
     * @param token
     * @param followerId 关注者id
     * @param followingId 被关注者id
     */
    public static void sendUnFollowNotify(String token, int followerId, int followingId){
        String url = "http://120.26.202.114/api/unfollow/notify";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("follower", followerId);
            jsonObject.put("following", followingId);
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


    }

}
