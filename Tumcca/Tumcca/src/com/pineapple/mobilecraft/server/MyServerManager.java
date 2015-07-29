package com.pineapple.mobilecraft.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.data.ActivityData;
import com.pineapple.mobilecraft.data.MyUser;
import com.pineapple.mobilecraft.data.comment.ActivityComment;
import com.pineapple.mobilecraft.data.comment.Comment;
import com.pineapple.mobilecraft.data.message.MyMessage;

import android.util.Log;

public class MyServerManager {
	public static class ServerException extends RuntimeException
	{
		public ServerException(String detailMessage)
		{
			super(detailMessage);
		}
	}
	
	protected static MyServerManager mInstance = null;
	String IP_ADDRESS = "http://103.31.201.252";//"http://117.78.3.87:80";
	final String IP_ADDRESS_UPLOAD = "http://117.78.3.87:81/upload";
	public final String EXCEPTION_NOT_LOGIN = "You have not sign_in!";
	String mToken;
	String mUserName;
	boolean LOCAL_DEBUG = false;
	protected MyServerManager() {
		if(LOCAL_DEBUG)
		{
			IP_ADDRESS = "http://192.168.1.102:80";
		}
	}

	public static MyServerManager getInstance() {
		if (mInstance == null) {
			mInstance = new MyServerManager();
		}
		return mInstance;
	}

	/**登录到自己的服务器，若成功，会在MyServerManager中生成token
	 * @param username 用户名
	 * @return 登录是否成功
	 */
	public boolean login(String username) {
		mUserName = username;
		
		Callable<Boolean> callable = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Boolean result = false;
				String url = String.format("%s/sign_in?user=%s", IP_ADDRESS,
						mUserName);
				HttpGet httpGet = new HttpGet(url);
				HttpResponse httpResponse;
				try {
					httpResponse = new DefaultHttpClient().execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						EntityUtils.toString(httpResponse
								.getEntity());
						Header[] headers = httpResponse
								.getHeaders("Set-Cookie");
						if (null != headers) {
							for (int i = 0; i < headers.length; i++) {
								String str = headers[i].getValue();
								if (str.contains("token=")) {
									int first = str.indexOf("token=");
									int last = str.indexOf(';');
									mToken = str.substring(
											first + "token=".length(), last);
								}
							}
						}
						result = true;
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}	
				return result;
			}
		};
		
		Future<Boolean>future = Executors.newCachedThreadPool().submit(callable);
		try {
			return future.get().booleanValue();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Deprecated
	public String getNewActivityID(String username) {
		final String fUsername = username;
		final String ftoken = mToken;
		Callable<String> callable = new Callable<String>() {
			@Override
			public String call() throws Exception {
				String newID = null;
				HttpGet httpGet = new HttpGet();
				httpGet.addHeader("Cookie",
						String.format("user=%s; token=%s", fUsername, ftoken));
				try {
					httpGet.setURI(new URI(String
							.format("%s/newid", IP_ADDRESS)));// "http://192.168.1.103:8080/newid"
					HttpResponse httpResponse;
					try {
						httpResponse = new DefaultHttpClient().execute(httpGet);
						if (httpResponse.getStatusLine().getStatusCode() == 200) {
							String result = EntityUtils.toString(httpResponse
									.getEntity());
							try {
								JSONObject obj = new JSONObject(result);
								newID = obj.getString("id");
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				return newID;
			}
		};
		Future<String>future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	/** 向华为云存储上传文件，如果上传成功，则会返回对应文件的一个url
	 * @param file
	 * @return String 上传文件的url
	 */
	public String uploadImage(File file) {
		if(null == file)
		{
			throw new IllegalArgumentException("uploadImage File can not be null!");
		}
		String fileName = file.getName();
		String fileContent;
		try {
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte buffer[] = new byte[1024 * 100];
			int count = 0;
			count = fis.read(buffer);
			while (-1 != count) {

				baos.write(buffer, 0, count);
				count = fis.read(buffer);
			}
			;
			fileContent = new String(baos.toByteArray(), "ISO-8859-1");

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost post = new HttpPost(IP_ADDRESS_UPLOAD);
			post.setEntity(new StringEntity(String.format("%s\r\n%s\r\n",
					fileName, fileContent), "ISO-8859-1"));
			HttpResponse response = httpClient.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				Log.v(DemoApplication.TAG, "Upload Image Success");
				String result = EntityUtils.toString(response.getEntity());
				try {
					JSONObject obj = new JSONObject(result);
					return obj.getString("url");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * 更新用户信息，如果该用户信息不存在，则会新增一个用户信息
	 * 
	 * @param user
	 * @return
	 */
	public boolean updateUserInfo(MyUser user) {
		if(null == user)
		{
			throw new IllegalArgumentException("updateUserInfo user can not be null");
		}
		
		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}
		
		final MyUser fUser = user;
		Callable<Boolean> callable = new Callable<Boolean>() {
			
			@Override
			public Boolean call() throws Exception {
				Boolean result = false;
				String url = String.format(
						"%s/db?action=set_user&table=user&username=%s",
						IP_ADDRESS, fUser.mName);
				HttpPost post = new HttpPost(url);
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							fUser.mName, mToken));
					post.setEntity(new StringEntity(MyUser.toJSON(fUser)
							.toString(), "utf-8"));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						result = true;
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				return result;
			}
		};
		
		Future<Boolean>future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get().booleanValue();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 获取用户信息
	 * 
	 * @param username
	 * @return MyUser
	 */
	public MyUser getUserInfo(String username) {
		if(null == username)
		{
			throw new IllegalArgumentException("getUserInfo username can not be null");
		}
		
		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}
		final String fUserName = username;
		Callable<MyUser> callable = new Callable<MyUser>() {
			@Override
			public MyUser call() throws Exception {
				MyUser user = MyUser.NULL;
				String url = String.format(
						"%s/db?action=get_user&table=user&username=%s",
						IP_ADDRESS, fUserName);
				HttpPost post = new HttpPost(url);
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							fUserName, mToken));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						try {
							JSONObject jsonObj = new JSONObject(
									EntityUtils.toString(
											httpResponse.getEntity(), "utf-8"));
							user = MyUser.fromJSON(jsonObj
									.getJSONObject("data"));
							user.mID = jsonObj.getString("_id");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				Log.v(DemoApplication.TAG,
						"getUserInfo result is " + MyUser.toJSON(user).toString());
				return user;
			}
		};
		
		Future<MyUser>future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}			
	}


	/**
	 * 在服务器端创建一个活动数据
	 * 
	 * @param data
	 *            活动数据
	 * @return 活动id
	 */
	public String createActivity(ActivityData data) {
		if (null == data) {
			throw new IllegalArgumentException("ActivityData can not be null");
		}
		
		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}
		final ActivityData fData = data;
		Callable<String> callable = new Callable<String>() {
			@Override
			public String call() throws Exception {
				String activityID = null;
				String url = String.format("%s/db?action=set&table=activity",
						IP_ADDRESS);
				HttpPost post = new HttpPost(url);
				// 第2步：使用execute方法发送HTTP GET请求，并返回HttpResponse对象
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							mUserName, mToken));
					String str = ActivityData.toJSON(fData)
							.toString();
					post.setEntity(new StringEntity(str, "utf-8"));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						try {
							JSONObject jsonObj = new JSONObject(
									EntityUtils.toString(
											httpResponse.getEntity(), "utf-8"));
							activityID = jsonObj.getString("id");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 	
				return activityID;
			}
		};
		
		Future<String>future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}		
	}

	public String createActivity2(ActivityData data) {
		data.save(DemoApplication.applicationContext);
		return "";
	}

	/**
	 * 更新一个活动数据
	 * 
	 * @param data 活动数据
	 *            
	 * @param id 活动id
	 *            
	 * @return
	 */
	public boolean updateActivity(ActivityData data, String id) {
		if (null == data || null == id) {
			throw new IllegalArgumentException(
					"ActivityData or id can not be null");
		}
		
		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}
		final ActivityData fData = data;
		final String fId = id;
		Callable<Boolean> callable = new Callable<Boolean>() {
			
			@Override
			public Boolean call() throws Exception {
				Boolean result = false;
				String url = String.format(
						"%s/db?action=set&table=activity&id=%s", IP_ADDRESS,
						fId);
				HttpPost post = new HttpPost(url);
				// 第2步：使用execute方法发送HTTP GET请求，并返回HttpResponse对象
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							mUserName, mToken));
					post.setEntity(new StringEntity(ActivityData.toJSON(fData)
							.toString(), "utf-8"));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						result = true;
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				return result;
			}
		};

		Future<Boolean>future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}	
	}

	/**
	 * 获取一条活动数据
	 * 
	 * @param id 活动id
	 *            
	 * @return 活动数据，如果此id不存在，则返回null
	 */

	public ActivityData getActivity(String id) {
		if (null == id) {
			throw new IllegalArgumentException("id can not be null");
		}
		
		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}
		final String fId = id;
		Callable<ActivityData> callable = new Callable<ActivityData>() {
			@Override
			public ActivityData call() throws Exception {
				ActivityData activityData = null;
				String url = String.format(
						"%s/db?action=get&table=activity&id=%s", IP_ADDRESS,
						fId);
				HttpPost post = new HttpPost(url);
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							mUserName, mToken));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						try {
							JSONObject jsonObj = new JSONObject(
									EntityUtils.toString(
											httpResponse.getEntity(), "utf-8"));
							activityData = ActivityData.fromJSON(jsonObj
									.getJSONObject("data"));
							activityData.mID = fId;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				return activityData;
			}
		};
		Future<ActivityData>future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}			
	}

	/**
	 * 返回全部活动数据
	 * 
	 *            
	 * @return 活动数据，如果此id不存在，则返回null
	 */

	public ArrayList<ActivityData> getAllActivity() {
		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}
		Callable<ArrayList<ActivityData>> callable = new Callable<ArrayList<ActivityData>>() {
			@Override
			public ArrayList<ActivityData> call() throws Exception {
				ActivityData activityData = null;
				ArrayList<ActivityData> result = null;
				String url = String.format(
						"%s/db?action=get_all_activity&table=activity", IP_ADDRESS);
				HttpPost post = new HttpPost(url);
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							mUserName, mToken));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						result = new ArrayList<ActivityData>();
						try {
							JSONObject jsonObj = new JSONObject(
									EntityUtils.toString(
											httpResponse.getEntity(), "utf-8"));
							JSONArray jsonArr = jsonObj
									.getJSONArray("data");
							for(int i=0; i<jsonArr.length(); i++)
							{
								JSONObject jsonData = jsonArr.getJSONObject(i);
								activityData = ActivityData.fromJSON(jsonData.getJSONObject("data"));
								activityData.mID = jsonData.getString("_id");		
								result.add(activityData);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				return result;
			}
		};
		Future<ArrayList<ActivityData>>future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}			
	}
	
	/**
	 * @param userId
	 *            用户id
	 * @param activityId
	 *            活动id
	 * @param field
	 *            活动的field 正在进行中的活动"doing_activity", 完成的活动"finish_activity"
	 * @return 是否成功
	 */
	public boolean addUserActivity(String userId, String activityId,
			String field) {
		if (null == userId || null == activityId || null == field) {
			throw new IllegalArgumentException(
					"userId or activityId or field can not be null");
		}
		
		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}

		final String fUserId = userId;
		final String fActivityID = activityId;
		final String fField = field;
		
		Callable<Boolean> callable = new Callable<Boolean>() {
			
			@Override
			public Boolean call() throws Exception {
				Boolean result = false;
				String url = String
						.format("%s/db?action=add_user_activity&table=user_activity&user_id=%s&field=%s&activity_id=%s",
								IP_ADDRESS, fUserId, fField, fActivityID);
				HttpPost post = new HttpPost(url);
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							mUserName, mToken));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						result = true;
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				return result;
			}
		};
		Future<Boolean>future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get().booleanValue();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
		
	}

	/**
	 * 获取一条活动数据
	 * 
	 * @param id 活动id
	 *            
	 * @return 活动数据，如果此id不存在，则返回null
	 */

	public boolean removeActivity(String id) {
		if (null == id) {
			throw new IllegalArgumentException("id can not be null");
		}
		
		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}
		final String fId = id;
		Callable<Boolean> callable = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Boolean result = false;
				String url = String.format(
						"%s/db?action=del&table=activity&id=%s", IP_ADDRESS,
						fId);
				HttpPost post = new HttpPost(url);
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							mUserName, mToken));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						result = true;
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				return result;
			}
		};
		Future<Boolean>future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}			
	}

	/**
	 * 获取某个用户的活动列表
	 * 
	 * @param userId
	 * @return 该ArrayList有两个元素，第一个元素为doingActivity的ArrayList，
	 *         第二个为finishActivity的ArrayList
	 */
	public ArrayList<ArrayList<String>> getUserActivity(String userId) {
		if (null == userId) {
			throw new IllegalArgumentException("userId can not be null");
		}
		
		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}
		final String fUserId = userId;
		Callable<ArrayList<ArrayList<String>>> callable = new Callable<ArrayList<ArrayList<String>>>() {
			
			@Override
			public ArrayList<ArrayList<String>> call() throws Exception {
				ArrayList<ArrayList<String>> activityList = null;
				String url = String
						.format("%s/db?action=get_user_activity&table=user_activity&user_id=%s",
								IP_ADDRESS, fUserId);
				HttpPost post = new HttpPost(url);
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							mUserName, mToken));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						try {
							activityList = new ArrayList<ArrayList<String>>();
							JSONObject jsonObj = new JSONObject(
									EntityUtils.toString(
											httpResponse.getEntity(), "utf-8"));
							JSONArray jDoingActivity = jsonObj
									.getJSONArray("doing_activity");
							ArrayList<String> doingActivity = new ArrayList<String>();
							for (int i = 0; i < jDoingActivity.length(); i++) {
								doingActivity.add(jDoingActivity.getString(i));
							}
							activityList.add(doingActivity);

							JSONArray jFinishActivity = jsonObj
									.getJSONArray("finish_activity");
							ArrayList<String> finishActivity = new ArrayList<String>();
							for (int i = 0; i < jFinishActivity.length(); i++) {
								finishActivity.add(jFinishActivity.getString(i));
							}
							activityList.add(finishActivity);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return activityList;
			}
		};
		Future<ArrayList<ArrayList<String>>> future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/** 向user发送消息
	 * @param user
	 * @param message
	 */
	public boolean sendMessage(String user, MyMessage message)
	{
		if (null == user || null == message) {
			throw new IllegalArgumentException(
					"userId or message can not be null");
		}
		
		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}
		final String fUserId = user;
		final MyMessage fMessage = message;
		
		Callable<Boolean> callable = new Callable<Boolean>() {
			
			@Override
			public Boolean call() throws Exception {
				Boolean result = false;
				String url = String
						.format("%s/db?action=add_user_message&table=user_message&user_id=%s",
								IP_ADDRESS, fUserId);
				HttpPost post = new HttpPost(url);
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							mUserName, mToken));
					JSONArray jsonArray = new JSONArray();
					post.setEntity(new StringEntity(fMessage.toJSON().toString(), "utf-8"));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						result = true;
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
				}
				return result;
			}
		};
		
		Future<Boolean> future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get().booleanValue();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**获取user的消息，该接口可以用于作为通知提示用户阅读消息
	 * @param user
	 * @return
	 */
	public ArrayList<MyMessage> getMessages(String userId)
	{
		if (null == userId) {
			throw new IllegalArgumentException("userId can not be null");
		}
		
		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}
		final String fUserId = userId;
		Callable <ArrayList<MyMessage>> callable = new Callable<ArrayList<MyMessage>>() {
			
			@Override
			public ArrayList<MyMessage> call() throws Exception {
				ArrayList<MyMessage> messages = null;
				String url = String
						.format("%s/db?action=get_user_message&table=user_message&user_id=%s",
								IP_ADDRESS, fUserId);
				HttpPost post = new HttpPost(url);
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							mUserName, mToken));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						try {
							messages = new ArrayList<MyMessage>();
							JSONObject jsonObj = new JSONObject(
									EntityUtils.toString(
											httpResponse.getEntity(), "utf-8"));
							JSONArray jMessages = jsonObj
									.getJSONArray("data");
							for (int i = 0; i < jMessages.length(); i++) {
								JSONObject obj = new JSONObject(jMessages.getString(i));
								messages.add(MyMessage.fromJSON(obj));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return messages;
			}
		};
		
		Future<ArrayList<MyMessage>> future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * 删除用户消息，该接口可用于阅读完消息后，删除未读的消息
	 * @param messages
	 */
	public boolean removeMessages(String userID, ArrayList<MyMessage> messages)
	{
		if (null == userID || null == messages) {
			throw new IllegalArgumentException("userID or messages can not be null");
		}
		
		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}
		final String fUserID = userID;
		final ArrayList<MyMessage> fMessages  = messages;
		Callable<Boolean> callable = new Callable<Boolean>() {
			
			@Override
			public Boolean call() throws Exception {
				Boolean result = false;
				String url = String
						.format("%s/db?action=remove_user_message&table=user_message&user_id=%s",
								IP_ADDRESS, fUserID);
				HttpPost post = new HttpPost(url);
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							mUserName, mToken));
					JSONArray jsonArray = new JSONArray();
					String data = "[";
					for(MyMessage message : fMessages)
					{
						data += String.format("\'%s\',", message.toJSON().toString());
						jsonArray.put(message.toJSON().toString());
					}
					data = data.substring(0, data.length()-1);
					data += "]";
					//post.setEntity(new StringEntity(fMessages.get(0).toJSON().toString(), "utf-8"));
					post.setEntity(new StringEntity(jsonArray.toString()));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						result = true;				
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return result;
			}
		};
		Future<Boolean> future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get().booleanValue();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 在用户的Activity列表中删除某一个活动
	 * @param userId
	 * @param activityId
	 * @param sourceField
	 * @return
	 */
	public boolean removeUserActivity(String userId, String activityId, String field)
	{
		if (null == userId || null == activityId || null == field) {
			throw new IllegalArgumentException(
					"userId or activityId or field can not be null");
		}

		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}
		final String fUserId = userId;
		final String fActivityID = activityId;
		final String fField = field;
		Callable<Boolean> callable = new Callable<Boolean>() {
			
			@Override
			public Boolean call() throws Exception {
				Boolean result = false;
				String url = String
						.format("%s/db?action=remove_user_activity&table=user_activity&user_id=%s&field=%s&activity_id=%s",
								IP_ADDRESS, fUserId, fField, fActivityID);
				HttpPost post = new HttpPost(url);
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							mUserName, mToken));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						result = true;
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				return result;
			}
		};
		Future<Boolean>future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get().booleanValue();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
		
	}
	
	/**
	 * 将用户的Activity从一个列表中移到另一个列表里面
	 * @param userId
	 * @param activityId
	 * @param sourceField
	 * @param destField
	 * @return
	 */
	public boolean moveActivity(String userId, String activityId, String sourceField, String destField)
	{
		if (null == userId || null == activityId || null == sourceField || null == destField) {
			throw new IllegalArgumentException(
					"userId or activityId or field can not be null");
		}

		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}
		final String fUserId = userId;
		final String fActivityID = activityId;
		final String fSourceField = sourceField;
		final String fDestField = destField;
		Callable<Boolean> callable = new Callable<Boolean>() {
			
			@Override
			public Boolean call() throws Exception {
				Boolean result = false;
				String url = String
						.format("%s/db?action=move_user_activity&table=user_activity&user_id=%s&field_source=%s&field_dest=%s&activity_id=%s",
								IP_ADDRESS, fUserId, fSourceField, fDestField, fActivityID);
				HttpPost post = new HttpPost(url);
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							mUserName, mToken));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						result = true;
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				return result;
			}
		};
		Future<Boolean>future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get().booleanValue();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
		
	}
	
	/** 删除一个用户
	 * @param id
	 * @return
	 */
	public boolean removeUser(String id) {
		if (null == id) {
			throw new IllegalArgumentException("id can not be null");
		}
		
		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}
		final String fId = id;
		Callable<Boolean> callable = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Boolean result = false;
				String url = String.format(
						"%s/db?action=del&table=user&id=%s", IP_ADDRESS,
						fId);
				HttpPost post = new HttpPost(url);
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							mUserName, mToken));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						result = true;
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				return result;
			}
		};
		Future<Boolean>future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}			
	}
	
	/**
	 * 登出用户
	 */
	public void logout()
	{
		mToken = null;
		mUserName = null;
	}
	
	/**向服务器增加一条评论
	 * @param comment
	 * @return
	 */
	public String addComment(Comment comment)
	{
		if (null == comment) {
			throw new IllegalArgumentException("Comment can not be null");
		}
		
		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}
		final Comment fComment = comment;
		Callable<String> callable = new Callable<String>() {
			@Override
			public String call() throws Exception {
				String commentID = null;
				String url = String.format("%s/db?action=addComment&table=comment",
						IP_ADDRESS);
				HttpPost post = new HttpPost(url);
				// 第2步：使用execute方法发送HTTP GET请求，并返回HttpResponse对象
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							mUserName, mToken));
					String str = fComment.toJSON().toString();
					post.setEntity(new StringEntity(str, "utf-8"));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						try {
							JSONObject jsonObj = new JSONObject(
									EntityUtils.toString(
											httpResponse.getEntity(), "utf-8"));
							commentID = jsonObj.getString("id");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 	
				return commentID;
			}
		};
		
		Future<String>future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	
	}
	
	/**读取一条评论，注意当获取多条评论时，应该使用{@link #getCommentList}
	 * @param id
	 * @return
	 */
	public ActivityComment getComment(String id) {
		if (null == id) {
			throw new IllegalArgumentException("id can not be null");
		}
		
		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}
		final String fId = id;
		Callable<ActivityComment> callable = new Callable<ActivityComment>() {
			@Override
			public ActivityComment call() throws Exception {
				ActivityComment comment = null;
				String url = String.format(
						"%s/db?action=getComment&table=comment&id=%s", IP_ADDRESS,
						fId);
				HttpPost post = new HttpPost(url);
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							mUserName, mToken));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						try {
							String str = EntityUtils.toString(
									httpResponse.getEntity(), "utf-8");
							JSONObject jsonObj = new JSONObject(
									str);
							comment = ActivityComment.fromJSON(jsonObj
									.getJSONObject("data"));
							//comment.mID = fId;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				return comment;
			}
		};
		Future<ActivityComment>future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}			
	}

	/** 获取一组评论
	 * @param ids 包含评论id的数组
	 * @return
	 */
	public ArrayList<ActivityComment> getCommentList(ArrayList<String> ids) {
		if (null == ids) {
			throw new IllegalArgumentException("ids can not be null");
		}
		
		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}
		final ArrayList<String> fIds = ids;
		Callable<ArrayList<ActivityComment>> callable = new Callable<ArrayList<ActivityComment>>() {
			@Override
			public ArrayList<ActivityComment> call() throws Exception {
				ActivityComment comment = null;
				ArrayList<ActivityComment> commentList = null;
				String url = String.format(
						"%s/db?action=getCommentList&table=comment", IP_ADDRESS);
				HttpPost post = new HttpPost(url);
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							mUserName, mToken));
					/*拼一个comment id 数组的json*/
					JSONObject jsonObj = new JSONObject();
					JSONArray jsonArray = new JSONArray();
					for(String id:fIds)
					{
						jsonArray.put(id);
					}
					jsonObj.put("ids", jsonArray);
					String str = jsonObj.toString();
					
					post.setEntity(new StringEntity(str, "utf-8"));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						try {
							commentList = new ArrayList<ActivityComment>();
							str = EntityUtils.toString(
									httpResponse.getEntity(), "utf-8");
							jsonObj = new JSONObject(str);
							jsonArray = jsonObj.getJSONArray("data");
							for(int i=0; i<jsonArray.length(); i++)
							{
								JSONObject jsonItem = (JSONObject) jsonArray.get(i);
								comment = ActivityComment.fromJSON(jsonItem);
								commentList.add(comment);
							}
						} catch (Exception e) {
							e.printStackTrace();
							commentList = null;
						}
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				return commentList;
			}
		};
		Future<ArrayList<ActivityComment>>future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}			
	}	
	
	
	/** 删除评论
	 * @param id
	 * @return
	 */
	public boolean removeComment(String id)
	{
		if (null == id) {
			throw new IllegalArgumentException("id can not be null");
		}
		
		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}
		final String fId = id;
		Callable<Boolean> callable = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Boolean result = false;
				String url = String.format(
						"%s/db?action=removeComment&table=comment&id=%s", IP_ADDRESS,
						fId);
				HttpPost post = new HttpPost(url);
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							mUserName, mToken));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						result = true;
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				return result;
			}
		};
		Future<Boolean>future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}	
	}

	/**获取某个活动的所有评论的id
	 * @param activityID
	 * @return
	 */
	public ArrayList<String> getActivityComment(String activityID) {
		if (null == activityID) {
			throw new IllegalArgumentException("activityID can not be null");
		}
		
		if(!checklogin())
		{
			throw new ServerException(EXCEPTION_NOT_LOGIN);
		}
		final String fActivityId = activityID;
		Callable<ArrayList<String>> callable = new Callable<ArrayList<String>>() {
			
			@Override
			public ArrayList<String> call() throws Exception {
				ArrayList<String> commentList = null;
				String url = String
						.format("%s/db?action=get_activity_comment&table=activity&activity_id=%s",
								IP_ADDRESS, fActivityId);
				HttpPost post = new HttpPost(url);
				HttpResponse httpResponse;
				try {
					post.addHeader("Cookie", String.format("user=%s;token=%s",
							mUserName, mToken));
					httpResponse = new DefaultHttpClient().execute(post);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						try {
							commentList = new ArrayList<String>();
							JSONObject jsonObj = new JSONObject(
									EntityUtils.toString(
											httpResponse.getEntity(), "utf-8"));
							JSONArray jComments = jsonObj
									.getJSONArray("comment_ids");						
							for (int i = 0; i < jComments.length(); i++) 
							{
								commentList.add(jComments.getString(i));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return commentList;
			}
		};
		Future<ArrayList<String>> future = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return future.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**检查是否已经登录
	 * @return
	 */
	boolean checklogin()
	{
		if(mToken == null|| mUserName == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	
}
