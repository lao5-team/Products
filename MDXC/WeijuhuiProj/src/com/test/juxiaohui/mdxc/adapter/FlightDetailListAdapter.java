package com.test.juxiaohui.mdxc.adapter;

import java.util.List;
import com.test.juxiaohui.R;
import com.test.juxiaohui.mdxc.data.FlightData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FlightDetailListAdapter extends BaseAdapter {

	LayoutInflater mInflater;
	Context mContext;
	public FlightDetailListAdapter(Context context , List<FlightData> dataList)
	{
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return initItemView(position);
	}
	
	private View initItemView(int positon)
	{
		LinearLayout itemView = (LinearLayout)mInflater.inflate(R.layout.view_flight_detail, null);
		LinearLayout detailHeader = (LinearLayout) itemView.findViewById(R.id.flight_detail_header_layout);
		LinearLayout detailContent = (LinearLayout) itemView.findViewById(R.id.flight_detail_content_layout);
		TextView tvPolicy = (TextView) itemView.findViewById(R.id.tv_policy_title);
		final LinearLayout detailPolicy = (LinearLayout) itemView.findViewById(R.id.flight_detail_policy_layout);
		
		//header view
		TextView htvAirCodeDepart = (TextView) detailHeader.findViewById(R.id.tv_airport_code_depart);
		TextView htvCityNameDepart = (TextView) detailHeader.findViewById(R.id.tv_city_name_depart);
		TextView htvAirCodeArrival = (TextView) detailHeader.findViewById(R.id.tv_airport_code_arrival);
		TextView htvCityNameArrival = (TextView) detailHeader.findViewById(R.id.tv_city_name_arrival);
		TextView htvDataDepart = (TextView) detailHeader.findViewById(R.id.tv_date_depart);
		
		htvAirCodeDepart.setText("PVG");
		htvCityNameDepart.setText("上海");
		htvAirCodeArrival.setText("LAX");
		htvCityNameArrival.setText("洛杉矶");
		htvDataDepart.setText("03月19日，周四");
		
		//content view 
		LinearLayout flightSeciton = (LinearLayout) detailContent.findViewById(R.id.ll_flight_section);
		
		RelativeLayout viewSection2 = (RelativeLayout) mInflater.inflate(R.layout.view_section_layout_2, null);
		flightSeciton.addView(viewSection2);
		
		RelativeLayout viewSection3 = (RelativeLayout) mInflater.inflate(R.layout.view_section_layout_3, null);
		flightSeciton.addView(viewSection3);
		
		TextView ctvDurationTime = (TextView)detailContent.findViewById(R.id.tv_duration_time);
		ctvDurationTime.setText("20H50M");
		
		LinearLayout airlineDetail = (LinearLayout) detailContent.findViewById(R.id.ll_airline_detail);
		RelativeLayout airlineOverview1 = (RelativeLayout) mInflater.inflate(R.layout.view_flight_overview_layout, null);
		airlineDetail.addView(airlineOverview1);
		RelativeLayout airlineOverview2 = (RelativeLayout) mInflater.inflate(R.layout.view_flight_overview_layout, null);
		airlineDetail.addView(airlineOverview2);
		
		//policy view
		tvPolicy.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(detailPolicy.getVisibility() == View.GONE)
					detailPolicy.setVisibility(View.VISIBLE);
				else if(detailPolicy.getVisibility() == View.VISIBLE)
					detailPolicy.setVisibility(View.GONE);			
			}
		});
		return itemView;
	}

}
