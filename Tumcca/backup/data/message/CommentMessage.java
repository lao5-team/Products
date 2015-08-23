package com.pineapple.mobilecraft.data.message;

import org.json.JSONException;
import org.json.JSONObject;

import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.data.MyUser;
import com.pineapple.mobilecraft.data.comment.ActivityComment;
import com.pineapple.mobilecraft.server.MyServerManager;

import android.content.Context;

public class CommentMessage extends MyMessage {
	/**
	 * 评论ID
	 */
	public String mCommentID;
	/**
	 * 评论行为，可以是ACTION_POST,ACTION_REPLY
	 */
	public String mAction;
	/**
	 * 发表评论
	 */
	public static final String ACTION_POST = "post";
	/**
	 * 回复评论
	 */
	public static final String ACTION_REPLY = "reply";
	
	public CommentMessage()
	{
		mType = MyMessage.TYPE_COMMENT;
		mAction = null;
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("type", this.mType);
			obj.put("from", MyUser.toJSON(this.mFromUser));
			obj.put("comment_id", this.mCommentID);
			obj.put("action", this.mAction);
			return obj;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static CommentMessage fromJSON(JSONObject json)
	{
		try {
			CommentMessage message = new CommentMessage();
			message.mType = json.getString("type");
			message.mFromUser = MyUser.fromJSON(json.getJSONObject("from"));
			message.mCommentID = json.getString("comment_id");
			message.mAction = json.getString("action");
			return message;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	}

	@Override
	public String toString(Context context) {
		
		if(null != mAction && mAction.length()>0)
		{
			ActivityComment comment = MyServerManager.getInstance().getComment(mCommentID);
			if(null != comment)
			{
				if(mAction.equals(ACTION_POST))
				{
					return context.getString(R.string.post_comment_message, mFromUser.mName, comment.getContent());
				}
				else if(mAction.equals(ACTION_REPLY))
				{
					return context.getString(R.string.reply_comment_message, mFromUser.mName, comment.getContent());
				}				
			}

		}
		else
		{
			throw new NullPointerException("CommentMessage's mAction is null or is empty!");
		}
		return "";
	}

}
