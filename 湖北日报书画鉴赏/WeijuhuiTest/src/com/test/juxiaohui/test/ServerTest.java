package com.test.juxiaohui.test;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import junit.framework.Assert;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.test.juxiaohui.data.ActivityData;
import com.test.juxiaohui.data.MyUser;
import com.test.juxiaohui.data.DianpingDao.ComplexBusiness;
import com.test.juxiaohui.data.comment.ActivityComment;
import com.test.juxiaohui.data.comment.Comment;
import com.test.juxiaohui.data.message.ActivityMessage;
import com.test.juxiaohui.data.message.MyMessage;
import com.test.juxiaohui.domain.MyServerManager;
import com.test.juxiaohui.domain.UserManager;

import android.test.AndroidTestCase;
import android.util.Log;

public class ServerTest extends AndroidTestCase {

	String IP_ADDRESS = "http://117.78.3.87:80";
	final String TAG = "weijuhuitest";
	boolean LOCAL_DEBUG = false;
	String cookie = "";
	protected void setUp() throws Exception {
		super.setUp();
		if(LOCAL_DEBUG)
		{
			IP_ADDRESS = "http://127.0.0.1:80";
		}
		UserManager.getInstance().login("yh", "yh");
//		MyServerManager.getInstance().login("test");
//		MyServerManager.getInstance().getUserInfo("test");
	}
	
//	public void testLogin()
//	{
//		String userName = "yihao";
//		String url = String.format("%s/login?user=%s", IP_ADDRESS, userName);//"http://192.168.1.103:8080/login?user=" + userName;
//        //  第1步：创建HttpGet对象  
//        HttpGet httpGet = new HttpGet(url);  
//        //  第2步：使用execute方法发送HTTP GET请求，并返回HttpResponse对象  
//        HttpResponse httpResponse;
//		try {
//			httpResponse = new DefaultHttpClient().execute(httpGet);
//	        //  判断请求响应状态码，状态码为200表示服务端成功响应了客户端的请求  
//	        if (httpResponse.getStatusLine().getStatusCode() == 200)  
//	        {  
//	            //  第3步：使用getEntity方法获得返回结果  
//	            String result = EntityUtils.toString(httpResponse.getEntity());  
//	            Log.v("weijuhuiTest", result);
//	            Header[] headers = httpResponse.getHeaders("Set-Cookie");
//	            if(null != headers)
//	            {
//		            for(int i=0; i<headers.length; i++)
//		            {
//		            	Log.v("weijuhuiTest", headers[i].getValue());
//		            	String str = headers[i].getValue();
//		            	if(str.contains("token="))
//		            	{
//			            	int first = str.indexOf("token=");
//			            	int last = str.indexOf(';');	
//			            	String cookie = str.substring(first + "token=".length(), last);
//			            	Log.v("weijuhuiTest", "cookie " + cookie);
//			            	String id = testGetID(userName, cookie);
//			            	
//			            	
//			        		ComplexBusiness testCB = new ComplexBusiness();
//			        		testCB.mName = "东来顺";
//			        		testCB.mImgUrl = "http://i2.dpfile.com//pc//e6801a8a0b89fa2dd93e582c69d2e7cd(700x700)//thumb.jpg";
//			        		testCB.mPhoneNumber = "12345678";
//			        		
//			        		User testUser = new User();
//			        		testUser.mName = "testCreator";
//			        		
//			        		ArrayList<User> users = new ArrayList<User>();
//			        		User testGuest = new User();
//			        		testGuest.mName = "test0";
//			        		users.add(testGuest);
//			        		
//			        		testGuest = new User();
//			        		testGuest.mName = "test1";
//			        		users.add(testGuest);
//			        		
//			        		testGuest = new User();
//			        		testGuest.mName = "test2";
//			        		users.add(testGuest);	
//			        		
//			        		try {
//			        			ActivityData data = new ActivityData.ActivityBuilder().setBeginTime(new SimpleDateFormat(ActivityData.dataPattern).parse("2014年9月20日16时")
//			        					).setComplexBusiness(testCB).setCreator(testUser).setUsers(users).create();
//			        			testSaveActivity(userName, cookie, id, data);
//			        			testGetActivity(userName, cookie, id);
//			        			testGetActivity(userName, cookie, "54282ad0e13823452d8a16c7");
//			        		}catch (ParseException e) {
//			        			// TODO Auto-generated catch block
//			        			e.printStackTrace();
//			        		}
//		            	}
//		            }	            	
//	            }
//
//	            //  去掉返回结果中的"\r"字符，否则会在结果字符串后面显示一个小方格  
//	        } 
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  
// 
//	}
//	
//	public void testUploadFile()
//	{
//        String end ="\r\n";
//        String twoHyphens ="--";
//        String boundary ="*****";
//	      try
//	        {
//	    	  HttpClient httpClient = new DefaultHttpClient();
//	          HttpPost post = new HttpPost("http://117.78.3.87:81/upload");
//	          post.setEntity(new StringEntity(String.format("wo_ri_o.txt\r\n%s\r\n", "55So5LmQ6Zif5ZCN5a2X5YGa5qG25ZCN5LiA54K56YO95LiN6YW3\r\n5a+56Z+z5LmQ55qE5omn552A6L+95rGC5L2g5LiN5aaC5oiR")));
//	          HttpResponse response = httpClient.execute(post);
//	          if(response.getStatusLine().getStatusCode()==200){
//	        	  Log.v("weijuhuitest", "Upload Success");
//	          }
//	        }
//	      catch(Exception e)
//	      {
//	    	  e.printStackTrace();
//	      }
//	}
//	
//	private String testGetID(String user, String token)
//	{
//	    HttpGet httpGet = new HttpGet();
//	    httpGet.addHeader("Cookie", String.format("user=%s; token=%s", user, token));
//	    try {
//			httpGet.setURI(new URI(String.format("%s/newid", IP_ADDRESS)));//"http://192.168.1.103:8080/newid"
//			HttpResponse httpResponse;
//			try {
//				httpResponse = new DefaultHttpClient().execute(httpGet);
//		        if (httpResponse.getStatusLine().getStatusCode() == 200)  
//		        {
//		            String result = EntityUtils.toString(httpResponse.getEntity());  
//		            try {
//						JSONObject obj = new JSONObject(result);
//						Log.v("weijuhuiTest", "newid = " + obj.getString("id"));
//						return obj.getString("id");
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//		        }
//			} catch (ClientProtocolException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//			return null;	
//				
//	}
//	
//	private void testSaveActivity(String user, String token, String id, ActivityData data)
//	{
//		 HttpPost httpPost = new HttpPost(String.format("%s/saveid", IP_ADDRESS));//"http://192.168.1.103:8080/saveid"
//		 httpPost.addHeader("Cookie", String.format("user=%s; token=%s", user, token));
//		 try {
//			HttpEntity entity = new StringEntity(String.format("%s\r\n%s\r\n", id, ActivityData.toJSON(data).toString()));
//			httpPost.setEntity(entity);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			HttpResponse httpResp;
//			httpResp = new DefaultHttpClient().execute(httpPost);
//			if (httpResp.getStatusLine().getStatusCode() != 200) {
//				assertFalse(true);
//			}
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		 
//
//	}
//	
//	private String testGetActivity(String user, String token, String id)
//	{
//		//user:= token=
//	    HttpGet httpGet = new HttpGet();
//	    httpGet.addHeader("Cookie", String.format("user=%s; token=%s", user, token));
//	    try {
//			httpGet.setURI(new URI(String.format("%s/loadid?id=%s", IP_ADDRESS, id)));//"http://192.168.1.103:8080/newid"
//			HttpResponse httpResponse;
//			try {
//				httpResponse = new DefaultHttpClient().execute(httpGet);
//		        if (httpResponse.getStatusLine().getStatusCode() == 200)  
//		        {
//		            String result = EntityUtils.toString(httpResponse.getEntity());  
//		            try {
//						JSONObject obj = new JSONObject(result);
//						Log.v("weijuhuiTest", "getActivity = " + obj.getString("data"));
//						return obj.getString("activity");
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//		        }
//			} catch (ClientProtocolException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//			return null;	
//				
//	}
	
//	public void testLogin()
//	{
//		String userName = "yihao";
//		String url = String.format("%s/login?user=%s", IP_ADDRESS, userName);//"http://192.168.1.103:8080/login?user=" + userName;
//        HttpGet httpGet = new HttpGet(url);  
//        HttpResponse httpResponse;
//		try {
//			httpResponse = new DefaultHttpClient().execute(httpGet);
//	        if (httpResponse.getStatusLine().getStatusCode() == 200)  
//	        {  
//	            String result = EntityUtils.toString(httpResponse.getEntity());  
//	            Log.v("weijuhuiTest", result);
//	            Header[] headers = httpResponse.getHeaders("Set-Cookie");
//	            if(null != headers)
//	            {
//		            for(int i=0; i<headers.length; i++)
//		            {
//		            	Log.v("weijuhuiTest", headers[i].getValue());
//		            	String str = headers[i].getValue();
//		            	if(str.contains("token="))
//		            	{
//			            	int first = str.indexOf("token=");
//			            	int last = str.indexOf(';');	
//			            	cookie = str.substring(first + "token=".length(), last);
//			            	break;
//		            	}
//		            }
//	            }
//	        }
//	        else
//	        {
//	        	Assert.fail("login failed");
//	        }
//		}
//		catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		//testAddData(cookie);
//	}
	
//	public void testAddData(String cookie)
//	{
//  
//		String id = "";
//		try {
//			HttpClient httpClient = new DefaultHttpClient();
//			HttpPost post = new HttpPost(String.format("http://117.78.3.87:80/db?action=set&table=%s", "ut"));
//			post.addHeader("Cookie", String.format("token=%s", cookie));
//			JSONObject obj = new JSONObject();
//			obj.put("data", "论对音乐的执着追求");
//			post.setEntity(new StringEntity(obj.toString(), "utf-8"));
//			HttpResponse response = httpClient.execute(post);
//			if (response.getStatusLine().getStatusCode() == 200) {
//				String result = EntityUtils.toString(response.getEntity()); 
//				JSONObject jsonResult = new JSONObject(result);
//				id = jsonResult.getString("id");
//			}
//			else
//			{
//				Assert.fail();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		//testGetData(cookie, id);
//		//testDelData(cookie, id);
//		
//	}
	
	private void testGetData(String cookie, String id)
	{
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost post = new HttpPost(String.format("http://117.78.3.87:80/db?action=get&table=ut&id=%s", id));
			post.addHeader("Cookie", String.format("token=%s", cookie));
			HttpResponse response = httpClient.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(response.getEntity(), "utf-8"); 
				JSONObject jsonResult = new JSONObject(result);
				String data = jsonResult.getString("data");
				Log.v(TAG, data);
			}
			else
			{
				Assert.fail();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
//	private void testDelData(String cookie, String id)
//	{
//		try {
//			HttpClient httpClient = new DefaultHttpClient();
//			HttpPost post = new HttpPost(String.format("http://117.78.3.87:80/db?action=del&table=ut&id=%s", id));
//			post.addHeader("Cookie", String.format("token=%s", cookie));
//			HttpResponse response = httpClient.execute(post);
//			if (response.getStatusLine().getStatusCode() == 200) {
//				String result = EntityUtils.toString(response.getEntity()); 
//				JSONObject jsonResult = new JSONObject(result);
//				String data = jsonResult.getString("id");
//				Log.v(TAG, data);
//			}
//			else
//			{
//				Assert.fail();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}		
//	}
	private String activityID;
	public void testActivity()
	{
		//create activity
		ActivityData data = ActivityData.createTestData();
		activityID = MyServerManager.getInstance().createActivity(data);
		Assert.assertNotNull(activityID);
		
		//get activity
		data = MyServerManager.getInstance().getActivity(activityID);
		Assert.assertNotNull(data);
		
		//update activity
		Assert.assertEquals(true, MyServerManager.getInstance().updateActivity(data, activityID));
		
//		MyServerManager.getInstance().addUserActivity("545f496d1c63a0f5756c7bd0", activityID, "doing_activity");		
//		ArrayList list = MyServerManager.getInstance().getUserActivity("545f496d1c63a0f5756c7bd0");
//		Assert.assertTrue(list.size()>0);
//		Log.v("juxiaohuitest", list.size() + "");
		
		//get all activity
		ArrayList<ActivityData> activityList = MyServerManager.getInstance().getAllActivity();
		Assert.assertTrue(activityList.size() > 0);
	}
	

	
	public void testMessage()
	{
		ActivityMessage message = new ActivityMessage();
		MyUser user = new MyUser();
		user.mName = "kimi";
		message.mFromUser = user;
		message.mAction = "invite";
		message.mActivityName = "war3 2v2";
		
		//发送消息，查询消息
		MyUser me = MyServerManager.getInstance().getUserInfo("yh");
		MyServerManager.getInstance().sendMessage(me.mID, message);
		ArrayList<MyMessage> messageList = MyServerManager.getInstance().getMessages(me.mID);
		Assert.assertTrue(messageList.size() > 0);
		Log.v("juxiaohuitest", message.toJSON().toString());
		
		//删除消息
		MyServerManager.getInstance().removeMessages(me.mID, messageList);
		messageList = MyServerManager.getInstance().getMessages(me.mID);
		Assert.assertTrue(messageList.size() == 0);
	}
	
	public void testUser()
	{
		//新增一个用户和查询一个用户
		MyUser newUser = new MyUser();
		newUser.mName = "test_local";
		newUser.mSex = "female";
		MyServerManager.getInstance().updateUserInfo(newUser);
		MyUser getUser = MyServerManager.getInstance().getUserInfo("test_local");
		Assert.assertNotNull(getUser);
		Assert.assertNotNull(getUser.mID);
		Assert.assertTrue(getUser.mName.equals(newUser.mName));
		Assert.assertTrue(getUser.mSex.equals(newUser.mSex));
		
		//删除一个用户
		MyServerManager.getInstance().removeUser(getUser.mID);
		getUser = MyServerManager.getInstance().getUserInfo("test_local");
		Assert.assertNull(getUser);
	}
	
	public void testComment()
	{
		ActivityComment comment = new ActivityComment();
		comment.mUserName = UserManager.getInstance().getCurrentUser().mName;
		comment.mActivityID = "547b49162d3a8c110032669a";
		comment.mContent = "What a good party!";
		//对一个Activity进行评论
		String commentID = UserManager.getInstance().getCurrentUser().postComment(comment);
		ActivityComment getComment = MyServerManager.getInstance().getComment(commentID);
		Assert.assertNotNull(getComment);
		ArrayList<String> activity_comments = MyServerManager.getInstance().getActivityComment("547b49162d3a8c110032669a");
		Assert.assertTrue(activity_comments.size()>0);
		for(String id:activity_comments)
		{
			Log.v(TAG, "activity's comment_id is " + id);
		}
		//对上一个评论进行评论
		
		//删除一个有权限的评论
		 
		//删除一个没有权限的评论
	}
	
	

//	private void testGetData()
//	{
//		
//	}
//	
//	private void testSetData()
//	{
//		
//	}
//	
//	private void testDelData()
//	{
//		
//	}
}
