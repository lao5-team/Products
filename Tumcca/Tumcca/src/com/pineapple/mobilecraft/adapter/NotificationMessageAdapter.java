package com.pineapple.mobilecraft.adapter;

import java.util.ArrayList;

import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.app.ActivityDetailActivity;
import com.pineapple.mobilecraft.data.ActivityData;
import com.pineapple.mobilecraft.data.MyUser;
import com.pineapple.mobilecraft.data.comment.ActivityComment;
import com.pineapple.mobilecraft.data.message.ActivityMessage;
import com.pineapple.mobilecraft.data.message.CommentMessage;
import com.pineapple.mobilecraft.data.message.MyMessage;
import com.pineapple.mobilecraft.server.MyServerManager;
import com.pineapple.mobilecraft.manager.UserManager;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class NotificationMessageAdapter extends BaseAdapter {

	Context mContext = null;
	ArrayList<MyMessage> mMessages = new ArrayList<MyMessage>();
	
	public NotificationMessageAdapter(Context context)
	{
		if(null == context)
		{
			throw new IllegalArgumentException("context can not be null");
		}
		mContext = context;
	}
	
	public void addMessages(ArrayList<MyMessage> messages)
	{
		mMessages.addAll(messages);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(null != mMessages)
		{
			return mMessages.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = View.inflate(mContext, R.layout.item_message, null);
		TextView tv = (TextView) view.findViewById(R.id.textView_content);
		Button button = (Button)view.findViewById(R.id.button_confirm);
		Button btn_refuse = (Button)view.findViewById(R.id.button_refuse);
		final MyMessage message = mMessages.get(position);
		tv.setText(message.toString(mContext));
		tv.setClickable(true);
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(message.mType.equals(MyMessage.TYPE_ACTIVITY))
				{
					ActivityMessage aMessage = (ActivityMessage)message;
					Intent intent = new Intent();
					ActivityDetailActivity.IntentWrapper ib = new ActivityDetailActivity.IntentWrapper(intent);
					ib.setUseType(ActivityDetailActivity.USE_EDIT);
					ib.setActivityID(aMessage.mActivityID);
					intent.setClass(mContext, ActivityDetailActivity.class);
					mContext.startActivity(intent);
				}
				if(message.mType.equals(MyMessage.TYPE_COMMENT))
				{
					CommentMessage cMessage = (CommentMessage)message;
					ActivityComment comment = MyServerManager.getInstance().getComment(cMessage.mCommentID);
					Intent intent = new Intent();
					ActivityDetailActivity.IntentWrapper ib = new ActivityDetailActivity.IntentWrapper(intent);
					ib.setUseType(ActivityDetailActivity.USE_EDIT);
					ib.setActivityID(comment.getActivityID());
					intent.setClass(mContext, ActivityDetailActivity.class);
					mContext.startActivity(intent);
				}
			}
		});
		if(message.mType.equals(MyMessage.TYPE_ACTIVITY))
		{
			final ActivityData data = MyServerManager.getInstance().getActivity(((ActivityMessage)message).mActivityID);
			ActivityMessage activityMessage = (ActivityMessage)message;
			if(activityMessage.mAction.equals(ActivityMessage.ACTION_INVITE))
			{
				button.setText("接受");
				button.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						UserManager.getInstance().getCurrentUser().acceptInviteActivity(data);
						mMessages.remove(message);
						((android.app.Activity)mContext).finish();
					}
				});
				btn_refuse.setText("拒绝");
				btn_refuse.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						UserManager.getInstance().getCurrentUser().refuseInvite(data);
						mMessages.remove(message);
						((android.app.Activity)mContext).finish();
					}
				});				
			}
			else if(activityMessage.mAction.equals(ActivityMessage.ACTION_APPLY))
			{
				final MyUser fromUser = activityMessage.mFromUser;
				button.setText("接受");
				button.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						UserManager.getInstance().getCurrentUser().acceptApplyActivity(data, fromUser);
						mMessages.remove(message);
						((android.app.Activity)mContext).finish();
					}
				});
				btn_refuse.setText("拒绝");
				btn_refuse.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						UserManager.getInstance().getCurrentUser().refuseApply(data, fromUser);
						mMessages.remove(message);
						((android.app.Activity)mContext).finish();
					}
				});
			}
			
		}
		if(message.mType.equals(MyMessage.TYPE_COMMENT))
		{
			button.setText("确认");
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mMessages.remove(message);
					((android.app.Activity)mContext).finish();
				}
			});
			btn_refuse.setVisibility(View.GONE);
		}
		return view;
	}

}
