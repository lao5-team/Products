package com.example.sendsms;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    EditText etxPhone;
    EditText etxMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etxPhone = (EditText)findViewById(R.id.editText_Phone);
        etxMessage = (EditText)findViewById(R.id.editText_Message);
        Button btnSend = (Button)findViewById(R.id.button_Send);
        btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendMessage(etxPhone.getEditableText().toString(), etxMessage.getEditableText().toString());
			}
		});
        //testThread.start();
        messageThread.start();
    }
    
    public void sendMessage(String destPhone, String message)
    {
    	/* 建构一取得default instance的 SmsManager对象 */
		SmsManager smsManager = SmsManager.getDefault();
		
		PendingIntent SendPI = PendingIntent.getBroadcast(
				MainActivity.this, 0, new Intent(), 0);
		// 发送短信
		smsManager.sendTextMessage(destPhone, null,
				"Your checkcode is " + message, SendPI, null);
		
		Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
		
    }
    
    Thread testThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true)
			{
				HttpPost post = new HttpPost("http://64.251.7.148/user/app/register/checkcode.json");
				HttpResponse httpResponse;
				List params = new ArrayList();
				params.add(new BasicNameValuePair("userName", "15510472558"));


				try {
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
					post.setEntity(entity);
					try {
						httpResponse = new DefaultHttpClient().execute(post);

						Log.v("SMS", httpResponse.getStatusLine().getStatusCode() + "");
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
				Thread.currentThread().sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
//					httpResponse = new DefaultHttpClient().execute(post);
//					if (httpResponse.getStatusLine().getStatusCode() == 200) {
//						String str = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
//						try {
//							JSONObject json = new JSONObject(str);
//							Iterator<String> keys = json.keys();
//							while(keys.hasNext())
//							{
//								String number = keys.next();
//								Log.v("SMS", number);
//								String checkCode = json.getString(number);
//								sendMessage(number, checkCode);
//								try {
//									Thread.currentThread().sleep(100);
//								} catch (InterruptedException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//							}
//
//						} catch (JSONException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						
//					}
//				} catch (ClientProtocolException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		}
	});
    
    
    Thread messageThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			Looper.prepare();
			while(true)
			{
				HttpPost post = new HttpPost("http://64.251.7.148/user/app/register/pullcheckcode.json");
				HttpResponse httpResponse;
				try {
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						String str = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
						try {
							JSONObject json = new JSONObject(str);
							Iterator<String> keys = json.keys();
							while(keys.hasNext())
							{
								final String number = keys.next();
								Log.v("SMS", number);
								final String checkCode = json.getString(number);
								runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										sendMessage(number, checkCode);
										
									}
								});
								
								try {
									Thread.currentThread().sleep(10000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
			}
		}
	});
}
