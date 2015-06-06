package com.pineapple.mobilecraft.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

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
public abstract class SyncHttpPost<T> extends SyncHTTPCaller<T> {
    private String mToken = "";
    public SyncHttpPost(String URL, String token, String entity) {
        super(URL);
        mToken = token;
        mEntity = entity;
    }

    public T execute()
    {
        Callable<T> callable = new Callable<T>() {
            @Override
            public T call() throws Exception {
                T result = null;
                HttpPost post = new HttpPost(mURL);
                post.addHeader("Content-Type",  "application/json");
                post.addHeader("Authorization", "Bearer " + mToken);
                post.setEntity(new StringEntity(mEntity, HTTP.UTF_8));
                HttpResponse httpResponse;
                try {
                    httpResponse = new DefaultHttpClient().execute(post);
                    String str = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                    result = postExcute(str);
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
}
