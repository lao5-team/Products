package com.pineapple.mobilecraft.data.message;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobObject;

/**
 * Created by yihao on 15/3/12.
 */
public class TreasureMessage extends BmobObject {
    public static int TYPE_TREASURE = 0; //新的宝物
    public static int TYPE_COMMENT = 1; //新的评论或者鉴定
    public static int TYPE_ACTIVITY = 2; //新的活动
  
    public String mUsername = "";
    public ArrayList<String> mMessages = new ArrayList<String>();
    
    public static TreasureMessage NULL = new TreasureMessage();
    
    private TreasureMessage()
    {
    	
    }
    
    public TreasureMessage(String username)
    {
    	mUsername = username;
    }
    
    public ArrayList<String> getMessages()
    {
    	return (ArrayList<String>) mMessages.clone();
    }
    
    public void addMessage(int type, String id)
    {
    	JSONObject object = new JSONObject();
    	try {
			object.put("type", type);
			object.put("targetId", id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	mMessages.add(object.toString());
    }
    
    public void clearMessage()
    {
    	mMessages.clear();
    }
    
    public static int getType(String content)
    {
    	try {
			JSONObject object = new JSONObject(content);
			return object.getInt("type");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}   	
    }
    
  public static String getTargetId(String content)
  {
  	try {
			JSONObject object = new JSONObject(content);
			return object.getString("targetId");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}   	
  }
  
  
    
//    /**
//     * @param username 消息发送的用户名
//     * @param type 消息类型
//     * @param targetId 消息对象id
//     */
//    public TreasureMessage(String username, int type, String targetId)
//    {
//    	mUsername = username;
//    	
//    	JSONObject object = new JSONObject();
//    	try {
//			object.put("type", type);
//			object.put("targetId", type);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	mContent.add(object.toString());
//    }
    
    
//    /**
//     * 返回消息类型，-1表示非法
//     * @return
//     */
//    public int getType()
//    {
//    	try {
//			JSONObject object = new JSONObject(mContent);
//			return object.getInt("type");
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return -1;
//		}
//    }
//    
//    public String getTargetId()
//    {
//    	try {
//			JSONObject object = new JSONObject(mContent);
//			return object.getString("targetId");
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return "";
//		}   	
//    }
    
    
}
