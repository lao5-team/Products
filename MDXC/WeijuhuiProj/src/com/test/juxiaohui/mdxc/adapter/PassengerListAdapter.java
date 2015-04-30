package com.test.juxiaohui.mdxc.adapter;

import java.util.List;


import com.test.juxiaohui.R;
import com.test.juxiaohui.mdxc.adapter.CityStickyListHeadersListAdapter.ItemViewHoler;
import com.test.juxiaohui.mdxc.app.PassengerEditorActivity;
import com.test.juxiaohui.mdxc.app.PassengerListActivity.PassengerSelector;
import com.test.juxiaohui.mdxc.data.CityData;
import com.test.juxiaohui.mdxc.data.FlightData;
import com.test.juxiaohui.mdxc.data.Passenger;
import com.test.juxiaohui.mdxc.mediator.IPassengerListMediator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PassengerListAdapter extends BaseAdapter {

	LayoutInflater mInflater;
	Context mContext;
	List<PassengerSelector> mPassengersList;
	IPassengerListMediator mPassengersActivity;
	
	public PassengerListAdapter(Context context, List<PassengerSelector> allPassengers,IPassengerListMediator passengersActivity)
	{
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//mSelecktedList = selectPassengers;
		mPassengersList = allPassengers;
		mPassengersActivity = passengersActivity;
		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(mPassengersList == null)
			return 0;
		return mPassengersList.size();
	}

	@Override
	public Passenger getItem(int arg0) {
		// TODO Auto-generated method stub
		if(mPassengersList == null)
			return null;
		return mPassengersList.get(arg0).mPassenger;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		PassengerHoler holder;
		Passenger data;
		data = (Passenger)getItem(position);

		if(data == null || ! (data instanceof Passenger))
			return null;
		if (convertView == null) 
		{
			holder = new PassengerHoler();
			convertView = mInflater.inflate(R.layout.view_guests_item,parent, false);
			holder.title =  (TextView) convertView.findViewById(R.id.tv_title);
			holder.subTitle = (TextView) convertView.findViewById(R.id.tv_subtitle);
			holder.checkView = (ImageView) convertView.findViewById(R.id.iv_check);
			holder.checkSection = (LinearLayout) convertView.findViewById(R.id.view_check_section);
			holder.checkSection.setTag(position);

			holder.checkSection.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					int p = (Integer) arg0.getTag();
					mPassengersList.get(p).mIsSelected = !mPassengersList.get(p).mIsSelected;
					mPassengersActivity.setPassengerSelect(mPassengersList.get(p).mPassenger, mPassengersList.get(p).mIsSelected);
					notifyDataSetChanged();

				}
			});
			holder.editView = (ImageView) convertView.findViewById(R.id.iv_edit);
			holder.editView.setTag(position);
			holder.editView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					int p = (Integer) arg0.getTag();
					mPassengersActivity.editPassenger(getItem(p));
				}
			});
			
			
			convertView.setTag(holder);
		} 
		else 
		{
			holder = (PassengerHoler) convertView.getTag();
		}
		
		holder.title.setText(data.mName);
		if(data.mIdType.equals(Passenger.ID_TYPE_ID))
		{
			holder.subTitle.setText(mContext.getText(R.string.chinese_id_card) + " " + data.mIdNo);
		}
		else if(data.mIdType.equals(Passenger.ID_TYPE_PASSPORT))
		{
			holder.subTitle.setText(mContext.getText(R.string.passport) + data.mIdNo);
		}
		
		if(mPassengersList.get(position).mIsSelected)
			holder.isSelected =true;
		else
			holder.isSelected =false;
		
		if(holder.isSelected)
			holder.checkView.setImageResource(R.drawable.icon_check_pressed);
		else
			holder.checkView.setImageResource(R.drawable.icon_check_normal);
			

		return convertView;
	}
	
	class PassengerHoler
	{
		TextView title;
		TextView subTitle;
		ImageView checkView;
		ImageView editView;
		LinearLayout checkSection;
		boolean isSelected = false;
		
	}
		
	public void refresh()
	{
		this.notifyDataSetChanged();
	}
}
