package com.pineapple.mobilecraft.adapter;

import com.pineapple.mobilecraft.R;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 给活动分类展示用，点击某一个分类时，会仅显示该分类的活动列表
 */
public class ActivityCategoryAdapter extends BaseAdapter {

	String[] mCategories = {};
	Context mContext;
	public ActivityCategoryAdapter(Context context)
	{
		mContext = context;
	}
	int mSelectIndex = -1;
	public void setData(String []categories)
	{
		mCategories = categories;
		this.notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mCategories.length;
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
		TextView tv = new TextView(mContext);
		if(position == mSelectIndex)
		{
			tv.setBackgroundColor(0xffff0000);
		}
		else
		{
			tv.setBackgroundColor(0xffffffff);
		}
		//tv.setBackgroundResource(R.drawable.bg_activity_categories);
		tv.setText(mCategories[position]);
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(20);

		return tv;
	}

	public void setSelectIndex(int index)
	{
		mSelectIndex = index;
	}

}
