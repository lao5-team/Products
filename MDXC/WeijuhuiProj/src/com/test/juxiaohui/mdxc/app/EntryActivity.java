package com.test.juxiaohui.mdxc.app;

import com.test.juxiaohui.R;
import com.test.juxiaohui.mdxc.app.view.FragmentHomeView;
import com.test.juxiaohui.mdxc.app.view.SliderContentView;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EntryActivity extends FragmentActivity {

	
	LayoutInflater mInflater;
	
	FrameLayout mRootLayout;
	RelativeLayout mNavigationLayout;
	SliderContentView mContentView;

	AnimationSet mAnimationSet = new AnimationSet(true);
	private TextView mTvHotel;
	private TextView mTvFlights;
	
	//navigation item
	private View mMyOrderItem;
	private View mViewedHotelsItem;
	private View mFlightStatusItem;
	private View mSearchHistoryItem;
	private View mSettingItem;
	private View mFeedbackItem;
	
	//navigation TAG
	public static final int MY_ODERS = 101;
	public static final int VIEWED_HOTELS = 102;
	public static final int FLIGHT_STATUS = 103;
	public static final int SEARCH_HISTORY = 104;
	public static final int SETTINGS = 105;
	public static final int FEED_BACK = 106;
	
	private static EntryActivity mInstance;
	
	public static EntryActivity getInstance()
	{
		return mInstance;
	}
	
	public static void startActivity(Context context)
	{
		Intent intent = new Intent(context, EntryActivity.class);
		context.startActivity(intent);
	}
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initView();	
	}
	
	public void initView()
	{
		mInflater = LayoutInflater.from(this);
		mRootLayout = (FrameLayout) mInflater.inflate(R.layout.fragment_root, null);
		mNavigationLayout = (RelativeLayout) mRootLayout.findViewById(R.id.navigation_layout);
		mContentView = new SliderContentView(this);
		mRootLayout.addView(mContentView);
		setContentView(mRootLayout);
		addFlightView();
		addNavigationItem();

		mInstance = this;
	}
	
	 @Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(ev);
	}
	 
	public void addFlightView()
	{
		mTvFlights = (TextView)findViewById(R.id.tv_flight_button);
		mTvFlights.setClickable(true);
		mTvFlights.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FlightSearchActivity.startActivity(EntryActivity.this);
				
			}
		});
	}
	 
	public void addNavigationItem()
	{
		mMyOrderItem = mNavigationLayout.findViewById(R.id.channel_navigation_my_orders);
		mViewedHotelsItem = mNavigationLayout.findViewById(R.id.channel_navigation_item_viewed_hotels);
		mFlightStatusItem = mNavigationLayout.findViewById(R.id.channel_navigation_item_flight_status);
		mSearchHistoryItem = mNavigationLayout.findViewById(R.id.channel_navigation_item_search_history);
		mSettingItem = mNavigationLayout.findViewById(R.id.channel_navigation_item_settings);
		mFeedbackItem = mNavigationLayout.findViewById(R.id.channel_navigation_item_feed_back);
		mMyOrderItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mContentView.startContent(MY_ODERS);
			}
		});
		mViewedHotelsItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mContentView.startContent(VIEWED_HOTELS);
			}
		});
		mSearchHistoryItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mContentView.startContent(SEARCH_HISTORY);
			}
		});
		mSettingItem.setClickable(true);
		mSettingItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mContentView.startContent(SETTINGS);
			}
		});
		mFeedbackItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mContentView.startContent(FEED_BACK);
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(mContentView.startContent(FLIGHT_STATUS))
			return false;
		else 
			return super.onKeyDown(keyCode, event);
	}
	
}
