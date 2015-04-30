package com.test.juxiaohui.mdxc.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.test.juxiaohui.R;
import com.test.juxiaohui.mdxc.manager.ServerManager;
import com.test.juxiaohui.mdxc.adapter.FlightDetailListAdapter;
import com.test.juxiaohui.mdxc.data.FlightData;
import com.test.juxiaohui.mdxc.mediator.IFlightDetailMediator;

public class FlightDetailActivity extends Activity implements
		IFlightDetailMediator {

	LinearLayout mMainLayout;
	LayoutInflater mInflater;	
	ListView mDetailListView;
	LinearLayout mBottomBar;	
	FlightDetailListAdapter mAdapter;
	FlightData mData;
	public static void startActivity(String id, Context context)
	{
		Intent intent = new Intent();
		intent.putExtra("id", id);
		context.startActivity(intent);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		initData();
		initView();
	}
	
	private void initData()
	{
		String id = getIntent().getStringExtra("id");
		mData = ServerManager.getInstance().getFlightData(id);		
		mAdapter = new FlightDetailListAdapter(this,null);
	}
	
	private void initView()
	{
		mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMainLayout = (LinearLayout) mInflater.inflate(R.layout.activity_flight_detail,null);
		addFlightDetailView();
		addBottomBar();
		setContentView(mMainLayout);
	}
	


	@Override
	public void addBottomBar() {
		// TODO Auto-generated method stub
		mBottomBar = (LinearLayout) mInflater.inflate(R.layout.view_bottom_bar, null);
		TextView tvBottomTip = (TextView)mBottomBar.findViewById(R.id.tv_bottom_tip);
		tvBottomTip.setText("价格（含税费）");
		
		TextView tvBottomCurrency = (TextView)mBottomBar.findViewById(R.id.tv_bottom_currency);
		tvBottomCurrency.setText("CNY");
		
		TextView tvBottomPrice = (TextView)mBottomBar.findViewById(R.id.tv_bottom_price);
		tvBottomPrice.setText("7,673");
		
		Button btnBottomSelect = (Button)mBottomBar.findViewById(R.id.btn_bottom_submit);
		btnBottomSelect.setText("预定");
		btnBottomSelect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				book();
			}
		});
		
		mMainLayout.addView(mBottomBar);
	}

	@Override
	public void book() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addFlightDetailView() {
		// TODO Auto-generated method stub
		mDetailListView = (ListView)mMainLayout.findViewById(R.id.flight_container);
		mDetailListView.setAdapter(mAdapter);
	}

}
