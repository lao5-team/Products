package com.test.juxiaohui.mdxc.app;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import com.test.juxiaohui.R;
import com.test.juxiaohui.mdxc.adapter.PassengerListAdapter;
import com.test.juxiaohui.mdxc.data.Passenger;
import com.test.juxiaohui.mdxc.manager.ServerManager;
import com.test.juxiaohui.mdxc.manager.UserManager;
import com.test.juxiaohui.mdxc.mediator.IPassengerListMediator;
import com.test.juxiaohui.mdxc.widget.CommonTitleBar;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author yh Create at 2015-3-23 passenger select for FlightOrderActivity
 */
public class PassengerListActivity extends Activity implements
		IPassengerListMediator {
	
	private RelativeLayout mMainView;
	private CommonTitleBar mTitleBar;
	private ListView mPassengerListView;
	private PassengerListAdapter mPassengerListAdapter;
	private RelativeLayout mConfirmButtonLayout;
	private Button mBtnConfirm;

	private Context mContext;

	public static class PassengerSelector {
		public Passenger mPassenger;
		public boolean mIsSelected = false;
	}

	private List<Passenger> mPassengers = new ArrayList<Passenger>();
	private List<PassengerSelector> mSelectors = new ArrayList<PassengerSelector>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		initData();
		initView();
	}

	public void initData() {
		for (Passenger _p : UserManager.getInstance().getPassengerList()) 
		{
			addPassenger(_p);
		}
	}

	private void initView()
	{
		
		mMainView = new RelativeLayout(this);
		
		RelativeLayout.LayoutParams mainParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	//	mMainView.setOrientation(LinearLayout.VERTICAL);
		//mMainView.setLayoutParams(mainParams);
		
		mTitleBar = new CommonTitleBar(mContext);
		TextView titleTextView = new TextView(mContext);
		titleTextView.setGravity(Gravity.CENTER);
		titleTextView.setTextColor(getResources().getColor(R.color.white));
		titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.font_size_36));
		titleTextView.setText("Passenger info");
		titleTextView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mTitleBar.setMarkLayout(titleTextView);
		mTitleBar.setId(10010);
		mTitleBar.setBackIconListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		ImageView addPassengersView = new ImageView(mContext);
		addPassengersView.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_icon_add));
		addPassengersView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext,PassengerEditorActivity.class);
				startActivityForResult(intent, 0);
			}
		});
		mTitleBar.setTitleIcon(addPassengersView);
		mMainView.addView(mTitleBar);
		
		
		mPassengerListView = new ListView(mContext);
		mPassengerListAdapter = new PassengerListAdapter(mContext , mSelectors, this);
		mPassengerListView.setAdapter(mPassengerListAdapter);
		mPassengerListView.setBackgroundColor(getResources().getColor(R.color.color_gray_12));
		mPassengerListView.setId(10086);
	//	RelativeLayout.LayoutParams listViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		RelativeLayout.LayoutParams listViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		listViewParams.addRule(RelativeLayout.BELOW, mTitleBar.getId());
		mMainView.addView(mPassengerListView,listViewParams);
		
		RelativeLayout.LayoutParams confirmLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, Math.round(getResources().getDimension(R.dimen.passengerlist_confirmlayout_height)));
		confirmLayoutParams.addRule(RelativeLayout.BELOW, mPassengerListView.getId());
		confirmLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
		confirmLayoutParams.alignWithParent = true;
		mConfirmButtonLayout = new RelativeLayout(mContext);
		mConfirmButtonLayout.setBackgroundColor(getResources().getColor(R.color.color_gray_12));
		mConfirmButtonLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		
		RelativeLayout.LayoutParams confirmButtonParams = new RelativeLayout.LayoutParams(Math.round(getResources().getDimension(R.dimen.passengerlist_confirmbutton_width)),Math.round(getResources().getDimension(R.dimen.passengerlist_confirmbutton_height)));
		confirmButtonParams.setMargins(0, 0, 0, Math.round(getResources().getDimension(R.dimen.size_20dip)));
		confirmButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		mBtnConfirm = new Button(this);
		mBtnConfirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_btn_1));
		mBtnConfirm.setText(R.string.done);
		mBtnConfirm.setGravity(Gravity.CENTER);
		mBtnConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<String> passengers_ids = new ArrayList<String>();
				for (Passenger passenger:getSelectedPassengers())
				{
					passengers_ids.add(passenger.mId);
				}
				Intent intent = new Intent();
				intent.putStringArrayListExtra("passenger_ids", passengers_ids);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		mConfirmButtonLayout.addView(mBtnConfirm,confirmButtonParams);
		mMainView.addView(mConfirmButtonLayout,confirmLayoutParams);
		
		
		setContentView(mMainView,mainParams);
		
		
	}

	@Override
	public void addPassenger(Passenger passenger) {
		// TODO Auto-generated method stub
		mPassengers.add(passenger);
		PassengerSelector selector = new PassengerSelector();
		selector.mPassenger = passenger;
		mSelectors.add(selector);
	}

	@Override
	public void editPassenger(Passenger passenger) {
		// TODO Auto-generated method stub
		// open Passenger Edit Activity
		
		Intent intent = new Intent(mContext,PassengerEditorActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("passenger", passenger);
		intent.putExtras(bundle);
		startActivityForResult(intent, 0);
	}

	@Override
	public void setPassengerSelect(Passenger passenger, boolean selected) {
		// TODO Auto-generated method stub
		for (PassengerSelector selector : mSelectors) {
			if (passenger.equals(selector.mPassenger)) {
				selector.mIsSelected = selected;
			}
		}
	}

	@Override
	public List<Passenger> getSelectedPassengers() {
		// TODO Auto-generated method stub
		ArrayList<Passenger> passengers = new ArrayList<Passenger>();
		for (PassengerSelector selector : mSelectors) {
			if (selector.mIsSelected == true) {
				passengers.add(selector.mPassenger);
			}
		}

		return passengers;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		updatePassengerList();
		mPassengerListAdapter.refresh();
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void updatePassengerList()
	{
		mPassengers.clear();
		mSelectors.clear();
		initData();
		
	}
	/*
	 * public List<PassengerSelector> getPassengerSelector(List<Passenger> list)
	 * { List<PassengerSelector> ret = new
	 * ArrayList<PassengerListActivity.PassengerSelector>(); for(Passenger
	 * data:list) { PassengerSelector p = new PassengerSelector(); p.mPassenger
	 * = data;
	 * 
	 * for(PassengerSelector temp:mSelectors) { if(temp.mPassenger.equals(data))
	 * p.mIsSelected = true; } ret.add(p); } return ret;
	 * 
	 * }
	 */

}
