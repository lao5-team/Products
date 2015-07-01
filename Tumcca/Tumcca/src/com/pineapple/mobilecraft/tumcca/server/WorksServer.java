package com.pineapple.mobilecraft.tumcca.server;

import com.pineapple.mobilecraft.tumcca.data.Album;
import com.pineapple.mobilecraft.tumcca.data.Works;
import com.pineapple.mobilecraft.utils.SyncHttpPost;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

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

}
