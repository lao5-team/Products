package com.pineapple.mobilecraft.utils;

import android.util.Log;
import com.pineapple.mobilecraft.TumccaApplication;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by yihao on 15/6/5.
 */
public abstract class SyncHttpPost<T> extends SyncHTTPCaller<T> {
    private String mToken = "";

    /**
     *
     * @param URL
     * @param token
     * @param entity
     */
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
                if(null!=mToken){
                    post.addHeader("Authorization", "Bearer " + mToken);
                }
                if(null!=mEntity)
                {
                    post.setEntity(new StringEntity(mEntity, HTTP.UTF_8));
                }
                HttpResponse httpResponse;
                try {
                    httpResponse = new DefaultHttpClient().execute(post);
                    if(httpResponse.getStatusLine().getStatusCode()==200){
                        String str = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                        result = postExcute(str);
                    }
                    else{
                        String str = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                        Log.v(TumccaApplication.TAG, "HttpPost error " + str);
                        reportError(str);
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    result = postExcute("");
                } catch (IOException e) {
                    e.printStackTrace();
                    result = postExcute("");
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

    public T execute(final String payload, final File fileEntity){
        Callable<T> callable = new Callable<T>() {
            @Override
            public T call() throws Exception {
                T result = null;
                HttpPost post = new HttpPost(mURL);

                post.addHeader("User-Agent", "Mozilla/5.0");
                post.addHeader("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryf6LDyP9jaAlS571g");
                if(null!=mToken){
                    post.addHeader("Authorization", "Bearer " + mToken);
                }
                if(null!=mEntity)
                {
                    post.setEntity(new StringEntity(mEntity, HTTP.UTF_8));
                }
                if(null!=fileEntity){
                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    builder.addBinaryBody(payload, fileEntity, ContentType.create("image/jpeg"), fileEntity.getName());
                    builder.setLaxMode().setBoundary("----WebKitFormBoundaryf6LDyP9jaAlS571g").setCharset(Charset.forName("UTF-8"));
                    post.setEntity(builder.build());
                }

                HttpResponse httpResponse;
                try {
                    httpResponse = new DefaultHttpClient().execute(post);
                    if(httpResponse.getStatusLine().getStatusCode()==200){
                        String str = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                        result = postExcute(str);
                    }
                    else{
                        String str = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                        Log.v(TumccaApplication.TAG, "HttpPost error " + str);
                        reportError(str);
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    result = postExcute("");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    result = postExcute("");
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

    private void reportError(String message){
        try {
            JSONObject jsonObject = new JSONObject(message);
            throw new ApiException(jsonObject.getInt("code"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
