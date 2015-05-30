package com.pineapple.mobilecraft.adapter;

import com.squareup.picasso.Picasso;
import com.pineapple.mobilecraft.app.ActivityDetailActivity;
import com.pineapple.mobilecraft.app.CreateActivityActivity;
import com.pineapple.mobilecraft.data.DianpingDao.SimpleBusiness;
import com.pineapple.mobilecraft.R;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SimpleBusinessAdapter extends BaseAdapter {

	private SimpleBusiness[] mData = null;
	private Activity mActivity = null;
	public SimpleBusinessAdapter(Activity activity)
	{
		mActivity = activity;
	}
	
	private static class SBItemViewHolder
	{
		public ImageView mImgView;
		public TextView mTvName;
		public TextView mTvAddress;
		
		public void createFromView(View view)
		{
			mImgView = (ImageView)view.findViewById(R.id.imageView_img);
			mTvName = (TextView)view.findViewById(R.id.textView_treasure_name);
			mTvAddress = (TextView)view.findViewById(R.id.textView_address);
		}
		
	}
	
	public void setBusiness(SimpleBusiness[] data)
	{
		mData = data;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(null!=mData)
		{
			return mData.length; 
		}
		else
		{
			return 0;
		}
		
	}

	@Override
	public Object getItem(int position) {
		if(null!=mData)
		{
			return mData[position]; 
		}
		else
		{
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(null == mData)
		{
			return null;
		}
		
		SBItemViewHolder holder;
		if(null == convertView)
		{
			RelativeLayout layout = (RelativeLayout)mActivity.getLayoutInflater().inflate(R.layout.item_simple_business, null);
			holder = new SBItemViewHolder();
			holder.createFromView(layout);
			convertView = layout;
			convertView.setTag(holder);
			final int pos = position;
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("businessID", mData[pos].mBusinessID);
					intent.setClass(mActivity, ActivityDetailActivity.class);
					mActivity.startActivityForResult(intent, CreateActivityActivity.INTENT_DETAIL);
				}
			});
		}
		else
		{
			holder = (SBItemViewHolder)convertView.getTag();
		}
		//holder.mImgView
		Picasso.with(mActivity).load(mData[position].mSmallImgUrl).into(holder.mImgView);
		holder.mTvName.setText(mData[position].mName);
		holder.mTvAddress.setText(mData[position].mAddress);
		return convertView;
	}

}
