package com.pineapple.mobilecraft.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by yihao on 15/6/5.
 */
public abstract class SyncHttpDelete<T> extends SyncHTTPCaller<T> {
    public SyncHttpDelete(String URL) {
        super(URL);
    }

    public SyncHttpDelete(String URL, String token) {
        super(URL, token);
    }

    public T execute()
    {
        Callable<T> callable = new Callable<T>() {
            @Override
            public T call() throws Exception {
                T result = null;
                HttpDelete delete = new HttpDelete(mURL);
                if(null!=mToken){
                    delete.addHeader("Authorization", "Bearer " + mToken);
                }
                HttpResponse httpResponse;
                try {
                    httpResponse = new DefaultHttpClient().execute(delete);
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
                    postExcute("");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    postExcute("");
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
