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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
public abstract class SyncHTTPCaller<T> {
	String mURL;
	String mToken = "";
	String mEntity = "";
	List mParamsList = new ArrayList();
	public SyncHTTPCaller(String URL)
	{
		mURL = URL;
	}
	
	public SyncHTTPCaller(String URL, String token)
	{
		mURL = URL;
		mToken = token;
	}
	public SyncHTTPCaller(String URL, String cookie, String entity)
	{
		mURL = URL;
		mToken = cookie;
		mEntity = entity;
	}

	public SyncHTTPCaller(String URL, String cookie, List params)
	{
		mURL = URL;
		mToken = cookie;
		mParamsList = params;
	}


	public abstract T postExcute(String result);
	//public abstract T postExcute(int code, String result);

	public T execute()
	{
		return null;
	}
}
