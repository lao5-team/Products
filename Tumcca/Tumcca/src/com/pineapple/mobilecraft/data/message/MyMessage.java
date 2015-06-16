package com.pineapple.mobilecraft.data.message;

import java.io.Serializable;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.pineapple.mobilecraft.data.ActivityData;
import com.pineapple.mobilecraft.data.MyUser;

/**
 * @author yh
 * 
 * 本应用使用的消息数据
 */
public abstract class MyMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1130070227842759145L;

	public static final String TYPE_ACTIVITY = "activity";
	public static final String TYPE_COMMENT = "comment";
	/**
	 * 定义消息类型，TYPE_ACTIVITY(对应ActivityMessage), TYPE_COMMENT(对应CommentMessage)“conversation”（数据用于对话，暂时不需要）
	 */
	public String mType;
	
	/**
	 * 消息的发送者
	 */
	public MyUser mFromUser; 
	

	
	protected MyMessage()
	{
		mType = null;
	}
	
	abstract public JSONObject toJSON();
	
    /**  <p>
     *   从JSONObject转化MyMessage对象，该方法会自动识别MyMessage的type，并向相应的子类型进行转换
     *   </p>
     *   <p>
     * 		
     *      if(json.getString("type").equals("activity"))
			{
				return ActivityMessage.fromJSON(json);
			}
		  </p>
     * @param json
     * @return
     */
    public static MyMessage fromJSON(JSONObject json)
	{
    	try {
			if(json.getString("type").equals(TYPE_ACTIVITY))
			{
				return ActivityMessage.fromJSON(json);
			}
			if(json.getString("type").equals(TYPE_COMMENT))
			{
				return CommentMessage.fromJSON(json);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
	}
	
	public String getNotifyString()
	{
		if(mType.equals("activity"))
		{
//			if(mAction.equals("create"))
//			{
//				return String.format("你的好友%s邀请您参加活动 %s", data.mCreator.mName, data.mTitle );
//			}
//			else if(mAction.equals("update"))
//			{
//				return String.format("活动%s有更新", data.mTitle);
//			}
		}
		return "未知消息";
	}
	
	 abstract public String toString(Context context);
	 
	 

	
//	public static class MessageBuilder
//	{
//		private MyMessage mMessage;
//		public MessageBuilder()
//		{
//			//mMessage = new MyMessage();
//		}
//		
//		public MessageBuilder setType(String type)
//		{
//			mMessage.mType = type;
//			return this;
//		}
//		
////		public MessageBuilder setAction(String action)
////		{
////			mMessage.mAction = action;
////			return this;
////		}
//		
//		public MessageBuilder setData(JSONObject data)
//		{
//			//mMessage.mData = data;
//			return this;
//		}
//		
//		public MyMessage create()
//		{
//			Assert.assertNotNull(mMessage.mType);
//			//Assert.assertNotNull(mMessage.mAction);
//			//Assert.assertNotNull(mMessage.mData);
//			return mMessage;
//		}
//	}
	
}
