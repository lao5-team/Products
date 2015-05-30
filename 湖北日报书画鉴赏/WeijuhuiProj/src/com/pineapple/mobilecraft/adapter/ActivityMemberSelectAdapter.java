/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pineapple.mobilecraft.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pineapple.mobilecraft.Constant;
import com.pineapple.mobilecraft.app.ActivityMembersActivity;
import com.pineapple.mobilecraft.domain.User;
import com.pineapple.mobilecraft.widget.Sidebar;
import com.pineapple.mobilecraft.R;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;


/**
 * 简单的好友Adapter实现
 *
 */
public class ActivityMemberSelectAdapter extends ArrayAdapter<User>  implements SectionIndexer{

	private LayoutInflater layoutInflater;
	private EditText query;
	private ImageButton clearSearch;
	private SparseIntArray positionOfSection;
	private SparseIntArray sectionOfPosition;
	private Sidebar sidebar;
	private int res;
	private ArrayList<com.pineapple.mobilecraft.data.MyUser> mSelectedMembers = new ArrayList<com.pineapple.mobilecraft.data.MyUser>();
	private HashMap<String, Boolean> mSelectedMaps = new  HashMap<String, Boolean>();
	/**
	 * @param context
	 * @param resource
	 * @param objects
	 * @param sidebar
	 * @param type  联系人的使用类型 ContactlistFragment.CONTACTS ,ContactlistFragment.ACTIVITY 
	 */
	public ActivityMemberSelectAdapter(Context context, int resource, List<User> objects, ArrayList<com.pineapple.mobilecraft.data.MyUser> selectMembers, Sidebar sidebar) {
		super(context, resource, objects);
		this.res = resource;
		this.sidebar=sidebar;
		layoutInflater = LayoutInflater.from(context);
		if(null!=selectMembers)
		{
			mSelectedMembers = selectMembers;
			for(com.pineapple.mobilecraft.data.MyUser user: mSelectedMembers)
			{
				mSelectedMaps.put(user.mName, true);
			}
		}
	}
	
	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public int getItemViewType(int position) {
		return 1;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(res, null);
		}

		ImageView avatar = (ImageView) convertView.findViewById(R.id.avatar);
		TextView unreadMsgView = (TextView) convertView
				.findViewById(R.id.unread_msg_number);
		TextView nameTextview = (TextView) convertView.findViewById(R.id.name);
		TextView tvHeader = (TextView) convertView.findViewById(R.id.header);
		CheckBox cbSelect = (CheckBox) convertView
				.findViewById(R.id.checkBox_selected);

		User user = getItem(position);
		// 设置nick，demo里不涉及到完整user，用username代替nick显示
		String username = user.getUsername();
		String header = user.getHeader();
		if (position == 0 || header != null
				&& !header.equals(getItem(position - 1).getHeader())) {
			if ("".equals(header)) {
				tvHeader.setVisibility(View.GONE);
			} else {
				tvHeader.setVisibility(View.VISIBLE);
				tvHeader.setText(header);
			}
		} else {
			tvHeader.setVisibility(View.GONE);
		}
		// 显示申请与通知item
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			nameTextview.setText(user.getNick());
			avatar.setImageResource(R.drawable.new_friends_icon);
			if (user.getUnreadMsgCount() > 0) {
				unreadMsgView.setVisibility(View.VISIBLE);
				unreadMsgView.setText(user.getUnreadMsgCount() + "");
			} else {
				unreadMsgView.setVisibility(View.INVISIBLE);
			}
			cbSelect.setVisibility(View.INVISIBLE);
		} else if (username.equals(Constant.GROUP_USERNAME)) {
			// 群聊item
			nameTextview.setText(user.getNick());
			avatar.setImageResource(R.drawable.groups_icon);
			cbSelect.setVisibility(View.INVISIBLE);
		} else {
			nameTextview.setText(username);
			if (unreadMsgView != null)
				unreadMsgView.setVisibility(View.INVISIBLE);
			avatar.setImageResource(R.drawable.default_avatar);
			final int fpos = position;
			cbSelect.setVisibility(View.VISIBLE);
			Boolean selected = mSelectedMaps.get(getItem(position).getNick());
			if (null != selected && true == selected) {
				cbSelect.setChecked(true);
			}
			cbSelect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (getContext() instanceof ActivityMembersActivity) {
						if (isChecked) {
							addSelectedMember(getItem(fpos));
						} else {
							removeSelectedMember(getItem(fpos));
						}
					}

				}
			});
		}
//		}
		return convertView;
	}
	
	

	public int getPositionForSection(int section) {
		return positionOfSection.get(section);
	}

	public int getSectionForPosition(int position) {
		return sectionOfPosition.get(position);
	}

	@Override
	public Object[] getSections() {
		positionOfSection = new SparseIntArray();
		sectionOfPosition = new SparseIntArray();
		int count = getCount();
		List<String> list = new ArrayList<String>();
		list.add(getContext().getString(R.string.search_header));
		positionOfSection.put(0, 0);
		sectionOfPosition.put(0, 0);
		for (int i = 1; i < count; i++) {

			String letter = getItem(i).getHeader();
			System.err.println("contactadapter getsection getHeader:" + letter + " name:" + getItem(i).getUsername());
			int section = list.size() - 1;
			if (list.get(section) != null && !list.get(section).equals(letter)) {
				list.add(letter);
				section++;
				positionOfSection.put(section, i);
			}
			sectionOfPosition.put(i, section);
		}
		return list.toArray(new String[list.size()]);
	}
	
	public ArrayList<User> getSelectedMembers()
	{
		return (ArrayList<User>)mSelectedMembers.clone();
	}
	
	private void addSelectedMember(User user)
	{
		for(int i=0; i<mSelectedMembers.size(); i++)
		{
			if(mSelectedMembers.get(i).mName.equals(user.getNick()))
			{
				return;
			}
		}
		com.pineapple.mobilecraft.data.MyUser user1 = new com.pineapple.mobilecraft.data.MyUser();
		user1.mName = user.getNick();
		mSelectedMembers.add(user1);
		mSelectedMaps.put(user1.mName, true);
	}
	
	private void removeSelectedMember(User user)
	{
		for(int i=0; i<mSelectedMembers.size(); i++)
		{
			if(mSelectedMembers.get(i).mName.equals(user.getNick()))
			{
				mSelectedMembers.remove(i);
			}
		}	
		mSelectedMaps.put(user.getNick(), false);
	}
	
	

}
