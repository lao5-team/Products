package com.pineapple.mobilecraft.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pineapple.mobilecraft.Constant;
import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.adapter.ActivityMemberSelectAdapter;
import com.pineapple.mobilecraft.adapter.ActivityMemberStateAdapter;
import com.pineapple.mobilecraft.data.MyUser;
import com.pineapple.mobilecraft.manager.ActivityManager;
import com.pineapple.mobilecraft.widget.Sidebar;
import com.pineapple.mobilecraft.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author yh
 * 聚会成员选择Activity,记得intent.getSerializable("members")
 */
public class ActivityMembersActivity extends FragmentActivity {

	private ArrayList<MyUser> mFriends;//
	private Button mBtnOK;
	private Button mBtnCancel;
	private TextView mTvMembers;
	private ActivityMemberSelectAdapter mAdapter;
	private ActivityMemberStateAdapter mStateAdapter;
	private List<com.pineapple.mobilecraft.domain.User> mContactList = new ArrayList<com.pineapple.mobilecraft.domain.User>();;
	private ListView listView;
	private Sidebar sidebar;
	private ActivityManager.DataChangedListener mActivityChangeListener;
	@Override
	public void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		
		initUI();	
		
		initData();
		mActivityChangeListener = new ActivityManager.DataChangedListener() {
			
			@Override
			public void onDataChanged() {
				
			}
		};

	}
	
//	public void addFriend(com.pineapple.mobilecraft.domain.User user)
//	{
//		for(int i=0; i<mFriends.size(); i++)
//		{
//			if(mFriends.get(i).mName.equals(user.getNick()))
//			{
//				return;
//			}
//		}
//		User user1 = new User();
//		user1.mName = user.getNick();
//		mFriends.add(user1);
//		String buffer = "";
//		for(int i=0; i<mFriends.size(); i++)
//		{
//			if(i==mFriends.size() - 1)
//			{
//				buffer += mFriends.get(i).mName;
//			}
//			else
//			{
//				buffer += mFriends.get(i).mName + " , ";
//			}
//		}
//		mTvMembers.setText(getString(R.string.select_activity_members) + " : " + buffer);
//		
//	}
//	
//	public void removeFriend(com.pineapple.mobilecraft.domain.User user)
//	{
//		for(int i=0; i<mFriends.size(); i++)
//		{
//			if(mFriends.get(i).mName.equals(user.getNick()))
//			{
//				mFriends.remove(i);
//			}
//		}
//		String buffer = "";
//		for(int i=0; i<mFriends.size(); i++)
//		{
//			if(i==mFriends.size() - 1)
//			{
//				buffer += mFriends.get(i).mName;
//			}
//			else
//			{
//				buffer += mFriends.get(i).mName + " , ";
//			}
//		}
//		mTvMembers.setText(getString(R.string.select_activity_members) + " : " + buffer);
//		
//	}	
	private void getContactList() {
		mContactList.clear();
		Map<String, com.pineapple.mobilecraft.domain.User> users = DemoApplication.getInstance().getContactList();
		Iterator<Entry<String, com.pineapple.mobilecraft.domain.User>> iterator = users.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, com.pineapple.mobilecraft.domain.User> entry = iterator.next();
			if (!entry.getKey().equals(Constant.NEW_FRIENDS_USERNAME) && !entry.getKey().equals(Constant.GROUP_USERNAME))
				mContactList.add(entry.getValue());
		}
		// 排序
		Collections.sort(mContactList, new Comparator<com.pineapple.mobilecraft.domain.User>() {

			@Override
			public int compare(com.pineapple.mobilecraft.domain.User lhs, com.pineapple.mobilecraft.domain.User rhs) {
				return lhs.getUsername().compareTo(rhs.getUsername());
			}
		});
	}
	
	public void initData()
	{
		Intent intent = getIntent();
		//Parent Activity 可能会传递已选择的用户
		if(null != intent&&intent.hasExtra("members"))
		{
			mFriends = (ArrayList<MyUser>) intent.getSerializableExtra("members");
		}
		else
		{
			mFriends = new ArrayList<MyUser>( );
		}
		getContactList();
//		if(getIntent().getIntExtra("state", 0) == ActivityData.BEGIN)
//		{
			mAdapter = new ActivityMemberSelectAdapter(this, R.layout.row_contact, mContactList, mFriends, sidebar);
			listView.setAdapter(mAdapter);
//		}
//		else if(getIntent().getIntExtra("state", 0) == ActivityData.BEGIN)
//		{
//			mStateAdapter = new ActivityMemberStateAdapter(this, R.layout.row_contact, mFriends);
//			listView.setAdapter(mStateAdapter);
//		}
	}
	
	public void initUI()
	{
		this.setContentView(R.layout.activity_members_activity);
		mTvMembers = (TextView)this.findViewById(R.id.textView_members);
		mBtnOK = (Button) this.findViewById(R.id.button_ok);
		mBtnCancel = (Button) this.findViewById(R.id.button_cancel);
		listView = (ListView) findViewById(R.id.list);
		sidebar = (Sidebar) findViewById(R.id.sidebar);
		sidebar.setListView(listView);
		
		
		
		mBtnOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
//				Bundle bundle = new Bundle();
//				bundle.putSerializable("members", mFriends);
				intent.putExtra("members", mFriends);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		
		mBtnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
