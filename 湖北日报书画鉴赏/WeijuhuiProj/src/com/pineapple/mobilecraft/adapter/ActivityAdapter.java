package com.pineapple.mobilecraft.adapter;

import java.util.List;

import com.squareup.picasso.Picasso;
import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.data.ActivityData;
import com.pineapple.mobilecraft.domain.activity.IActivityLoader;
import com.pineapple.mobilecraft.R;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ActivityAdapter extends BaseAdapter {

	private Fragment mFragment = null;
	private IActivityLoader mLoader = null;
	private List<ActivityData> mActivityList;
	private static class SBItemViewHolder
	{
		public TextView mTvName;
		public TextView mFromUser;
		public TextView mTvState;
		public ImageView mImgPic;
		
		public void createFromView(View view)
		{
			mTvName = (TextView)view.findViewById(R.id.textView_activity);
			mFromUser = (TextView)view.findViewById(R.id.textView_from);
			mTvState = (TextView)view.findViewById(R.id.textView_state);
			mImgPic = (ImageView)view.findViewById(R.id.imageView_pic);
		}
		
	}
	
	public ActivityAdapter(Fragment context, IActivityLoader loader)
	{
		mFragment = context;
		mLoader = loader;
		if(null != mLoader)
		{
			mActivityList = mLoader.getActivityList();
		}
		
	}
	
	public void refresh()
	{
		if(null != mLoader)
		{
			mActivityList = mLoader.getActivityList();
		}
		this.notifyDataSetChanged();
	}
	
	
	@Override
	public int getCount() {
		//return ActivityManager.getInstance().getActivitySize();
		if(null == mActivityList)
		{
			return 0;
		}
		else
		{
			return mActivityList.size();
		}
		
	}

	@Override
	public Object getItem(int position) {
		//return ActivityManager.getInstance().getActivity(position);
		if(null == mActivityList)
		{
			return null;
		}
		else
		{
			return mActivityList.get(position);
		}
		
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.v(DemoApplication.TAG, "getView()");
		SBItemViewHolder holder;
		if(null == convertView)
		{
			RelativeLayout layout = (RelativeLayout)mFragment.getActivity().getLayoutInflater().inflate(R.layout.listitem_activity, null);
			holder = new SBItemViewHolder();
			holder.createFromView(layout);
			convertView = layout;
			convertView.setTag(holder);
		}
		else
		{
			holder = (SBItemViewHolder)convertView.getTag();
		}
		ActivityData data = (ActivityData)getItem(position);
		holder.mTvName.setText(data.mTitle);
		if(null!=data.mImgUrl&&data.mImgUrl.length()>0)
		{
			Picasso.with(mFragment.getActivity()).load(data.mImgUrl).into(holder.mImgPic);
		}

		//temp delete
		holder.mFromUser.setText("组织者: " + data.mCreator);
		holder.mTvState.setText(data.mContent);
		return convertView;
	}

	public void setData(List<ActivityData> datas)
	{
		if(null != datas)
		{
			mActivityList = datas;
		}

	}

}
