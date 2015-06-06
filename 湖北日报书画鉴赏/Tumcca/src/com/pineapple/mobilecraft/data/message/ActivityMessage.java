package com.pineapple.mobilecraft.data.message;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.data.MyUser;

/**
 * @author yh
 * 活动邀请，申请，接受，拒绝相关消息
 */
public class ActivityMessage extends MyMessage {

	public static class MessageBuilder{
		ActivityMessage mMessage;
		public MessageBuilder()
		{
			mMessage = new ActivityMessage();
			mMessage.mType = MyMessage.TYPE_ACTIVITY;
		}
		
		public MessageBuilder setAction(String action)
		{
			if(null == action)
			{
				throw new IllegalArgumentException("setAction aciton can not be null");
			}
			mMessage.mAction = action;
			return this;
		}
		
		public MessageBuilder setActivityID(String id)
		{
			if(null == id)
			{
				throw new IllegalArgumentException("setActivityID id can not be null");
			}
			mMessage.mActivityID = id;
			return this;
		}

		public MessageBuilder setActivityTitle(String title)
		{
			if(null == title)
			{
				throw new IllegalArgumentException("setActivityTitle title can not be null");
			}
			mMessage.mActivityName = title;
			return this;
		}		
		
		public MessageBuilder setFromUser(MyUser user)
		{
			if(null == user)
			{
				throw new IllegalArgumentException("setFromUser user can not be null");
			}
			mMessage.mFromUser = user;
			return this;			
		}
		
		public ActivityMessage create()
		{
			return mMessage;
		}
	}
	
	private static final long serialVersionUID = -6511827765134217999L;
	public static final String ACTION_INVITE = "invite";
	public static final String ACTION_ACCEPT_APPLY = "accept_apply";
	public static final String ACTION_REFUSE_APPLY = "refuse_apply";
	public static final String ACTION_DISMISS = "dismiss";
	public static final String ACTION_APPLY = "apply";
	public static final String ACTION_ACCEPT_INVITE = "accept_invite";
	public static final String ACTION_REFUSE_INVITE = "refuse_invite";
	public static final String ACTION_QUIT = "quit";
	public static final String ACTION_FINISH = "finish";
	/**
	 * 定义消息行为 创建者可以使用"invite","accept_apply","refuse_apply","dismiss","finish"
	 *             分别表示邀请好友，接受陌生人申请，拒绝申请，解散活动，完成活动
                                  参与者可以使用"apply","accept_invite","refuse_invite","quit"
     *             分别表示申请加入聚会，接受创建者邀请，拒绝邀请，退出活动          
	 */
	public String mAction;
	
	/**
	 * 聚会ID
	 */
	public String mActivityID;
	
	/**
	 * 聚会名称，为了直观显示用
	 */
	public String mActivityName;
	
	public ActivityMessage()
	{
		mAction = null;
		mType = MyMessage.TYPE_ACTIVITY;
	}
	
	/** 消息转化为字符串
	 * @param context
	 * @return
	 */
	public String toString(Context context)
	{
		if(null != mAction && mAction.length()>0)
		{
			if(mAction.equals(ACTION_INVITE))
			{
				return context.getString(R.string.invite_message, mFromUser.mName, mActivityName);
			}
			if(mAction.equals(ACTION_ACCEPT_INVITE))
			{
				return context.getString(R.string.accept_invite_message, mFromUser.mName);
			}
			if(mAction.equals(ACTION_REFUSE_INVITE))
			{
				return context.getString(R.string.refuse_invite_message, mFromUser.mName);
			}			
			if(mAction.equals(ACTION_APPLY))
			{
				return context.getString(R.string.apply_message, mFromUser.mName, mActivityName);
			}
			if(mAction.equals(ACTION_ACCEPT_APPLY))
			{
				return context.getString(R.string.accept_apply_message, mFromUser.mName);
			}
			if(mAction.equals(ACTION_REFUSE_APPLY))
			{
				return context.getString(R.string.refuse_apply_message, mFromUser.mName);
			}	
			if(mAction.equals(ACTION_FINISH))
			{
				return context.getString(R.string.finish_activity_message, mActivityName);
			}			
		}
		else
		{
			throw new NullPointerException("ActivityMessage's mAction is null or is empty!");
		}

		return "";
		
	}
	
	
	
	/** 从JSONObject转化为ActivityMessage
	 * @param JSONObject 将要转化的JSONObject
	 * @return
	 */
	public static ActivityMessage fromJSON(JSONObject json)
	{
		try {
			ActivityMessage message = new ActivityMessage();
			message.mType = json.getString("type");
			message.mFromUser = MyUser.fromJSON(json.getJSONObject("from"));
			message.mAction = json.getString("action");
			message.mActivityID = json.getString("activity_id");
			message.mActivityName = json.getString("activity_name");
			return message;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**从ActivityMessage转化为JSONObject
	 * @param message
	 * @return
	 */
	@Override
	public JSONObject toJSON()
	{
		JSONObject obj = new JSONObject();
		try {
			obj.put("type", this.mType);
			obj.put("from", MyUser.toJSON(this.mFromUser));
			obj.put("action", this.mAction);
			obj.put("activity_id", this.mActivityID);
			obj.put("activity_name", this.mActivityName);
			//obj.put("action", msg.mAction);
			return obj;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
