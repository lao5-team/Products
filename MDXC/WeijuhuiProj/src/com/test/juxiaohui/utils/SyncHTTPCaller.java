package com.test.juxiaohui.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.LogManager;

import com.test.juxiaohui.mdxc.data.LogData;
import com.test.juxiaohui.mdxc.server.LogServer;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
public abstract class SyncHTTPCaller<T> {
	public static int TYPE_POST = 0;
	public static int TYPE_GET = 1;
	String mURL;
	String mCookie = "";
	//String mEntity = null;
	UrlEncodedFormEntity mEntity = null;
	int mType = TYPE_POST;
	T mDefaultResult = null;
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
		//mEntity = entity;
	}
	

	public SyncHTTPCaller(String URL, String cookie, List entity)
	{
		mURL = URL;
		mCookie = cookie;
		if(null!=entity&&entity.size()>0)
		{
			try {
				mEntity = new UrlEncodedFormEntity(entity, HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public SyncHTTPCaller(String URL, String cookie, List entity, int type)
	{
		this(URL, cookie, entity);
		mType = type;

	}

	public SyncHTTPCaller(String URL, String cookie, List entity, int type, T defaultValue)
	{
		this(URL, cookie, entity, type);
		mDefaultResult = defaultValue;

	}

	public T execute()
	{
		Callable<T> callable = new Callable<T>() {
			@Override
			public T call() throws Exception {
				HttpRequestBase httpRequest = null;
				if(mType == TYPE_POST)
				{
					httpRequest = new HttpPost(mURL);
					((HttpPost)httpRequest).setEntity(mEntity);
				}
				else if(mType == TYPE_GET)
				{
					httpRequest = new HttpGet(mURL);
				}

				HttpResponse httpResponse;
					try {
						httpResponse = new DefaultHttpClient().execute(httpRequest);
						int statusCode = httpResponse.getStatusLine().getStatusCode();
						String statusString = httpResponse.getStatusLine().toString();
						String str = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
						String log = "url " + mURL + "\n" +
									 "statusCode " + statusCode + "\n" +
									 "statusString " + statusString + "\n" +
								     "entity " + str + "\n";
						LogData logData = new LogData();
						logData.log = log;
						LogServer.getInstance().uploadLog("", logData);
						if (statusCode == 200) {
							mDefaultResult = postExcute(str);
						}
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return mDefaultResult;
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
