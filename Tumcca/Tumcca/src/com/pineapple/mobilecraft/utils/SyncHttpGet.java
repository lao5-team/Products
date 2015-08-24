package com.pineapple.mobilecraft.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by yihao on 15/6/5.
 */
public abstract class SyncHttpGet<T> extends SyncHTTPCaller<T> {
    /**
     *
     * @param URL
     * @param token
     */
    public SyncHttpGet(String URL, String token) {
        super(URL, token);
    }

    public T execute()
    {
        Callable<T> callable = new Callable<T>() {
            @Override
            public T call() throws Exception {
                T result = null;
                HttpGet get = new HttpGet(mURL);
                get.addHeader("Content-Type",  "application/json");
                if(null!=mToken){
                    get.addHeader("Authorization", "Bearer " + mToken);
                }
                HttpResponse httpResponse;
                try {
                    httpResponse = new DefaultHttpClient().execute(get);
                    if(httpResponse.getStatusLine().getStatusCode()==200){
                        String str = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                        result = postExcute(str);
                    }
                    else{
                        String str = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                        reportError(str);
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    result = postExcute(e.getMessage());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    result = postExcute(e.getMessage());
                }
                return result;
            }

        };

        Future<T> future = Executors.newSingleThreadExecutor().submit(callable);
        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    protected void reportError(String message){
        try {
            JSONObject jsonObject = new JSONObject(message);
            throw new ApiException(jsonObject.getInt("code"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
