package com.pineapple.mobilecraft.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
public abstract class SyncHTTPCaller<T> {
	
	String mURL;
	String mCookie = "";
	String mEntity = "";
	public SyncHTTPCaller(String URL)
	{
		mURL = URL;
	}
	
	public SyncHTTPCaller(String URL, String cookie)
	{
		mURL = URL;
		mCookie = cookie;
	}
	public SyncHTTPCaller(String URL, String cookie, String entity)
	{
		mURL = URL;
		mCookie = cookie;
		mEntity = entity;
	}
	public T execute()
	{
		Callable<T> callable = new Callable<T>() {
			@Override
			public T call() throws Exception {
				T result = null;
				HttpPost post = new HttpPost(mURL);
				//post.addHeader("Cookie", mCookie);
				//post.setEntity(new StringEntity("test order"));

				if(mEntity.length()>0)
				{
					List params=new ArrayList();
					params.add(new BasicNameValuePair("products", mEntity));
					post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
/*					HttpParams params = new BasicHttpParams();
					params.setParameter("products", mEntity);
					params.setParameter("authid", mCookie);
					post.setParams(params);*/
				}

				HttpResponse httpResponse;
					try {
						httpResponse = new DefaultHttpClient().execute(post);
						if (httpResponse.getStatusLine().getStatusCode() == 200) {
							String str = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
							result = postExcute(str);
						}
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return result;
				}

		};
		
		Future<T>future = Executors.newSingleThreadExecutor().submit(callable);
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
	
	public abstract T postExcute(String result);
	
}
