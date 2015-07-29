package com.pineapple.mobilecraft.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.squareup.picasso.Picasso;
import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.app.CreateActivityActivity2.IntentBuilder;
import com.pineapple.mobilecraft.adapter.CommentAdapter;
import com.pineapple.mobilecraft.data.ActivityData;
import com.pineapple.mobilecraft.data.MyUser;
import com.pineapple.mobilecraft.data.DianpingDao.ComplexBusiness;
import com.pineapple.mobilecraft.data.comment.ActivityComment;
import com.pineapple.mobilecraft.data.comment.IActivityCommentLoader;
import com.pineapple.mobilecraft.server.MyServerManager;
import com.pineapple.mobilecraft.manager.UserManager;
import com.pineapple.mobilecraft.manager.ActivityManager;
import com.pineapple.mobilecraft.mediator.IActivityDetailMediator;
import com.pineapple.mobilecraft.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @author yh
 * 1 显示活动的详细内容和地点
 * 2 抽取活动内容和地点（包括经纬度）
 * 3 添加好友
 * 4 发起聚会，向好友发送邀请单
 */

public class ActivityDetailActivity extends FragmentActivity {
	/** 
	 * 用来对intent数据进行规范式处理
	 * 
	 * @param setUseType 决定是用来创建一个活动还是用来处理一个活动，可以是USE_CREATE,USE_EDIT
	 * @param setActivityID 设置活动的ID
	 */
	public static class IntentWrapper
	{
		Intent mIntent;

		public IntentWrapper(Intent intent)
		{
			if(null == intent)
				throw new IllegalArgumentException("intent cannot be null!");
			mIntent = intent;
		}
		
		public void setUseType(int useType)
		{
			mIntent.putExtra("use", useType);
		}
		
		public void setActivityID(String activityID)
		{
			mIntent.putExtra("activity_id", activityID);
		}
		
		public int getUseType()
		{
			return mIntent.getIntExtra("use", USE_CREATE);
		}
		
		public String getActivityID()
		{
			return mIntent.getStringExtra("activity_id");
		}
	};
	
	public static final int USE_CREATE = 4;
	public static final int USE_EDIT = 5;
	
	private IActivityDetailMediator mMediator = new IActivityDetailMediator() {
		
		@Override
		public void showTitle(String title) {
			mTvName.setText(mData.mTitle);
		}
		
		@Override
		public void showTime(Date date) {
			mBtnTime.setText( new SimpleDateFormat(ActivityData.dataPattern).format(date));
		}
		
		@Override
		public void showMembers(ArrayList<MyUser> users) {
			String result = "聚会参与人员: ";
			if(null != users)
			{
				for(int i=0; i<users.size(); i++)
				{
					if(i==users.size()-1)
					{
						result += users.get(i).mName;
					}
					else
					{
						result += users.get(i).mName + " , ";
					}
				}				
			}

		}
		
		@Override
		public void showContent(String content) {
			mTvContent.setText(mData.mContent);
		}
		
		@Override
		public void setActivityData(ActivityData data, MyUser creator) {
			mData = data;
			showTitle(data.mTitle);
			showContent(data.mContent);
			showTime(data.mBeginDate);
			showCreator(creator);
			if(data.mImgUrl!=null&&data.mImgUrl.length()>0)
			{
				Picasso.with(ActivityDetailActivity.this).load(data.mImgUrl).into(mIvXuanchuan);
			}


		}
		
		@Override
		public void changeMembers(ArrayList<MyUser> users) {
			mData.mUsers.clear();
			for(MyUser user:users)
			{
				mData.mUsers.add(user.mName);
			}
			showMembers(users);
		}

		@Override
		public void showPayType(int type) {

		}

		@Override
		public void onOKClicked() {
			MyUser currentUser = UserManager.getInstance().getCurrentUser();
			if(currentUser.getMyRoleType(mData).equals(MyUser.CREATOR))
			{
				currentUser.finishActivity(mData);
			}
			else if(currentUser.getMyRoleType(mData).equals(MyUser.STRANGER))
			{
				currentUser.applyActivity(mData);
			}
			else if(currentUser.getMyRoleType(mData).equals(MyUser.JOINER))
			{
				;			
			}
		}

		@Override
		public void onCancelClicked() {
			if(UserManager.getInstance().getCurrentUser().getMyRoleType(mData).endsWith(MyUser.CREATOR))
			{
				UserManager.getInstance().getCurrentUser().dismissActivity(mData);
			}
			else if(UserManager.getInstance().getCurrentUser().getMyRoleType(mData).endsWith(MyUser.JOINER))
			{
				UserManager.getInstance().getCurrentUser().quitActivity(mData);
			}
		}

		@Override
		public void postComment(ActivityComment comment) {
			if(null!=comment.getReplyTo())
			{
				UserManager.getInstance().getCurrentUser().replyComment(comment);
			}
			else
			{
				UserManager.getInstance().getCurrentUser().postComment(comment);
			}
			mCommentAdapter.refresh();
		}

		@Override
		public void showCreator(MyUser user) {
			mTvUser.setText(user.mName);
			if(null!=user.mImgUrl&&user.mImgUrl.length()>0)
			{
				Picasso.with(ActivityDetailActivity.this).load(user.mImgUrl).into(mIvUser);
			}
		}


	};
	//data
	private ComplexBusiness mCBData;
	private int mActivityIndex;
	private CommentAdapter mCommentAdapter;
	
	//UI

	private ImageView mIvUser;
	private TextView mTvUser;
	private TextView mTvName;
	private TextView mTvDesc;
	private ImageView mIvXuanchuan;
	private Button mBtnTime;
	private Button mBtnAddress;


	private TextView mTvContent;

	//Handler
	private ArrayList<MyUser> mFriends = new ArrayList<MyUser>();
	private ActivityData mData;

	private void updateFriendsUI()
	{

	}
	
	@Override
	protected void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		
		initUI();
		initData();
	}
	
	private void initData()
	{
		/*test code begin*/
		Intent intent = getIntent();
		final IntentBuilder ib = new IntentBuilder(intent);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				mData = ActivityManager.getInstance().getActivity(ib.getActivityID());
				final MyUser user = MyServerManager.getInstance().getUserInfo(mData.mCreator);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (mData == null) {
							Toast.makeText(ActivityDetailActivity.this, "找不到活动内容", Toast.LENGTH_SHORT).show();
							ActivityDetailActivity.this.finish();
						} else {
							mMediator.setActivityData(mData, user);
						}
					}
				});


			}
		});
		t.start();



		
		mCommentAdapter = new CommentAdapter(this, new IActivityCommentLoader() {
			
			@Override
			public ArrayList<ActivityComment> getCommentList() {
				ArrayList<ActivityComment> commentList = new ArrayList<ActivityComment>();
				ArrayList<String> commentID_list = MyServerManager.getInstance().getActivityComment(ib.getActivityID());
				if(null!= commentID_list)
				{
					ArrayList<String> ids = new ArrayList<String>();
					for(String id:commentID_list)
					{
						//ActivityComment comment = MyServerManager.getInstance().getComment(id);
						//commentList.add_photo(comment);
						ids.add(id);
					}
					commentList = MyServerManager.getInstance().getCommentList(ids);
						
				}
				return commentList;				
			}
		});
		
	}
	
	private void initUI()
	{
		RelativeLayout layout = (RelativeLayout) this.getLayoutInflater().inflate(R.layout.fragment_activity_detail, null);

		setContentView(layout);
		
		mTvName = (TextView) layout.findViewById(R.id.textView_title);

		mTvContent = (TextView)layout.findViewById(R.id.editText_content_sub);
		mIvUser = (ImageView)findViewById(R.id.imageView_user);
		mTvUser = (TextView)findViewById(R.id.textView_user);
		mIvXuanchuan = (ImageView)findViewById(R.id.imageView_xuanchuan);
		mBtnTime = (Button)findViewById(R.id.button_date);
		mBtnAddress = (Button)findViewById(R.id.button_address);
		
	}
	
	
	private void sendActivityToSingle(ActivityData data)
	{
		EMConversation conversation = EMChatManager.getInstance().getConversation(data.mUsers.get(0));
		
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
		// 如果是群聊，设置chattype,默认是单聊
		
		Log.v(DemoApplication.TAG, ActivityData.toJSON(data).toString());
		TextMessageBody txtBody = new TextMessageBody(ActivityData.toJSON(data).toString());
		message.addBody(txtBody);
		message.setReceipt(mFriends.get(0).mName);
		conversation.addMessage(message);
		try {
			EMChatManager.getInstance().sendMessage(message);
		} catch (EaseMobException e) {
			e.printStackTrace();
		}
	}
	
	private void sendActivityToGroup(ActivityData data)
	{
		EMGroup group;
		try {
			String[] names = new String[data.mUsers.size()];
			for(int i=0; i<data.mUsers.size(); i++)
			{
				names[i] = data.mUsers.get(i);
			}
			group = EMGroupManager.getInstance().createPrivateGroup("", "", names, false);
			EMConversation conversation = EMChatManager.getInstance().getConversation(group.getGroupId());										
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
			message.setChatType(ChatType.GroupChat);
			TextMessageBody txtBody = new TextMessageBody(ActivityData.toJSON(data).toString());
			message.addBody(txtBody);
		    message.setReceipt(group.getGroupId());
		    conversation.addMessage(message);
			try {
				EMChatManager.getInstance().sendMessage(message);
			} catch (EaseMobException e) {
				e.printStackTrace();
			}
		} catch (EaseMobException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(data!=null)
		{
			Bundle bundle = data.getBundleExtra("members");
			mFriends = (ArrayList<MyUser>)bundle.getSerializable("members");
			updateFriendsUI();			
		}

	}
}
