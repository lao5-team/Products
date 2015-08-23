package com.pineapple.mobilecraft.data.comment;

import org.json.JSONException;
import org.json.JSONObject;

import com.pineapple.mobilecraft.data.ActivityData;
import com.pineapple.mobilecraft.server.MyServerManager;

public class ActivityComment extends Comment {

	/**
	 * 评论针对的活动ID
	 */
	protected String mActivityID = null;
	/**
	 * 评论回复的对象名称
	 */
	protected String mReplyTo = null; 
	
	/**
	 * @param userName 发表评论的用户名称
	 * @param Content  评论内容
	 * @param activityID 评论针对的活动ID
	 * @param replyTo 评论回复的对象名称，可以为null，如果是回复别人的评论，则要输入别人的名字
	 */
	public ActivityComment(String userName, String content, String activityID, String replyTo)
	{
		if(null == userName)
		{
			throw new IllegalArgumentException("userName can not be null!");
		}
		
		if(null == content)
		{
			throw new IllegalArgumentException("content can not be null!");
		}
		
		if(null == activityID)
		{
			throw new IllegalArgumentException("activityID can not be null!");
		}
		mUserName = userName;
		mContent = content;
		mActivityID = activityID;
		mReplyTo = replyTo;
	}
	
	private ActivityComment()
	{
		
	}
	
	/** 返回评论针对的活动ID
	 * @return
	 */
	public String getActivityID()
	{
		return mActivityID;
	}
	
	/**返回评论回复的对象名称
	 * @return
	 */
	public String getReplyTo()
	{
		return mReplyTo;
	}
	
	public static ActivityComment fromJSON(JSONObject json)	
	{
		ActivityComment comment = new ActivityComment();
		try {
			if(json.has("id"))
			{
				comment.mID = json.getString("id");
			}
			comment.mContent = json.getString("content");
			comment.mActivityID = json.getString("activity_id");
			comment.mUserName = json.getString("user_name");
			if(json.has("replyTo"))
			{
				comment.mReplyTo = json.getString("replyTo");
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			comment = null;
		}		
		return comment;
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		try {
			if(null != this.mID)
			{
				obj.put("id", this.mID);
			}
			obj.put("content", this.mContent);
			obj.put("activity_id", this.mActivityID);
			obj.put("user_name", this.mUserName);
			if(null != this.mReplyTo)
			{
				obj.put("replyTo", this.mReplyTo);
			}
			
			return obj;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.pineapple.mobilecraft.data.comment.Comment#checkDeleteAuthority(java.lang.String)
	 * 检查是否有权限进行删除评论，如果评论是该用户发的，则有权限。如果活动是该用户创建的，也有权限
	 */
	@Override
	public boolean checkDeleteAuthority(String userName) {
		ActivityData activityData = MyServerManager.getInstance().getActivity(mActivityID);
		if(mUserName.equals(userName))
			return true;
		//temp delete
		//else if(activityData.mCreator.mID.equals(userName))
		//	return true;
		else 
			return false;
	}
	
	

}
