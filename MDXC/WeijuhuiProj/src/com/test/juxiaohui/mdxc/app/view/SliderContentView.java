package com.test.juxiaohui.mdxc.app.view;

import com.test.juxiaohui.R;
import com.test.juxiaohui.mdxc.app.EntryActivity;
import com.test.juxiaohui.mdxc.app.FlightSearchActivity;
import com.test.juxiaohui.mdxc.widget.CommonTitleBar;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebStorage.Origin;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderContentView extends RelativeLayout {

	
	private long mDownActionTime = 0l;
	private float lastX = 0;
	private float mDownActionX = 0;
	
	//add view
	private LayoutInflater mInflater;
	private RelativeLayout mSelf;
	private FragmentHomeView mFragmentHomeView;
	private SettingsView mSettingsView;
	private FlightOrdersView mFlightOrdersView;
	private final int MINI_SCREEN = 0;
	private final int FULL_SCREEN = 1;
	
	
	private Context mContext;

	private ImageView mLeftDrawer;
	
	private float maxDistanceX;
	private float maxShrinkY;
	private float orginHeight = 0;
	private float orginWidth = 0;
	private float moveScale;
	private float widthScale;
	private float heightScale;
	private boolean init = false;
	
	private Bitmap mScreenShot;
	private ImageView mTempContentView;
	
	public boolean isRoot = true;
	
	public SliderContentView(Context context) {
		super(context);
		mContext = context;
		mSelf = this;
		initView();
		// TODO Auto-generated constructor stub
	}

	private void initScrollData()
	{
		WindowManager wm = (WindowManager) getContext() 
                .getSystemService(Context.WINDOW_SERVICE); 
		orginWidth = this.getWidth();	
        orginHeight = this.getHeight();
        widthScale = (2.0f/3.0f);
        heightScale = (2.0f/3.0f);
        maxDistanceX = orginWidth * 0.8f;
        maxShrinkY = (orginHeight * (1 -heightScale))/2;
        moveScale = maxDistanceX/maxShrinkY;
        mFragmentHomeView.setDrawingCacheEnabled(true);
        mFragmentHomeView.buildDrawingCache();
		mScreenShot = mFragmentHomeView.getDrawingCache().copy(Bitmap.Config.ARGB_4444, true);
		mTempContentView.setImageBitmap(mScreenShot);
		this.addView(mTempContentView);
		mTempContentView.setVisibility(View.GONE);
	}
	private void initView()
	{
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
		//add 
		mFragmentHomeView = new FragmentHomeView(mContext);
		mLeftDrawer = (ImageView) mFragmentHomeView.findViewById(R.id.rl_left_drawer);
		mLeftDrawer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub		
				
				if(!init)
				{
					initScrollData();
					init = true;
				}
				transformToMiniView(mFragmentHomeView.getLeft(),mFragmentHomeView.getTop(),orginWidth,orginHeight);
			}
		});
		mTempContentView = new ImageView(mContext);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mTempContentView.setLayoutParams(params);
		this.addView(mFragmentHomeView);
		mTempContentView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!init)
				{
					initScrollData();
					init = true;
				}
				transformToFullView(mSelf.getLeft(),mSelf.getTop(),mSelf.getRight() - mSelf.getLeft(),mSelf.getBottom() - mSelf.getTop());
				mTempContentView.setClickable(false);			
			}
		});
		mTempContentView.setClickable(false);
		


	}
	
	

	
	private void transformToFullView(final float cx, final float cy,float cw, float ch)
	{

		
		final AnimationSet mAnimationSet = new AnimationSet(true);

		TranslateAnimation translateAnimation = new TranslateAnimation(0, -cx, 0, 0);
		ScaleAnimation scaleAnimation =new ScaleAnimation(1f, orginWidth/cw, 1f, orginHeight/ch,   
				Animation.ABSOLUTE, -cx, Animation.RELATIVE_TO_SELF, 0.5f); 

		mAnimationSet.addAnimation(translateAnimation);
		mAnimationSet.addAnimation(scaleAnimation);
		mAnimationSet.setFillAfter(false);
		mAnimationSet.setDuration(200);
		mAnimationSet.setAnimationListener(new AnimationListener() {	
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub		
			}		
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub		
			}
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub	
				
				Message msg = new Message();
				msg.what = FULL_SCREEN;
				mHandler.sendMessageDelayed(msg, 10);				
				mTempContentView.setClickable(false);
				
			}
		});
		mSelf.startAnimation(mAnimationSet);
		
	}

	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == MINI_SCREEN)
			{
				
				mSelf.layout(Math.round(maxDistanceX), Math.round(maxShrinkY + 0.5f) , Math.round(maxDistanceX + orginWidth * widthScale), Math.round( maxShrinkY + 0.5f + orginHeight*heightScale));
				mTempContentView.layout(0, 0,Math.round(orginWidth * widthScale), Math.round(orginHeight*heightScale));
				mFragmentHomeView.layout(0, 0,Math.round(orginWidth * widthScale), Math.round(orginHeight*heightScale));
				mTempContentView.setClickable(true);						
			}
			else if(msg.what == FULL_SCREEN)
			{
				mSelf.layout(0, 0, Math.round(orginWidth), Math.round(orginHeight));
				mTempContentView.layout(0, 0,Math.round(orginWidth), Math.round(orginHeight));
				mFragmentHomeView.layout(0, 0,Math.round(orginWidth), Math.round(orginHeight));

			}
				
		};
	};
	
	private void transformToMiniView(float cx, float cy,float cw, float ch)
	{

		
		final AnimationSet mAnimationSet = new AnimationSet(true);
		mTempContentView.setVisibility(VISIBLE);
		TranslateAnimation translateAnimation = new TranslateAnimation(0,(maxDistanceX - cx) , 0,0);
		final ScaleAnimation scaleAnimation =new ScaleAnimation(1f, (orginWidth * widthScale)/cw, 1f, (orginHeight * heightScale)/ch,   
				Animation.ABSOLUTE, (maxDistanceX - cx), Animation.RELATIVE_TO_SELF, 0.5f); 
		
		mAnimationSet.addAnimation(translateAnimation);
		mAnimationSet.addAnimation(scaleAnimation);
		
		mAnimationSet.setFillAfter(false);
		mAnimationSet.setDuration(200);
		mAnimationSet.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = MINI_SCREEN;
				mHandler.sendMessageDelayed(msg, 10);	
				
			}
		});
		this.startAnimation(mAnimationSet);
	}
	
	
	
	 @Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(ev);
	}
	 
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.onInterceptTouchEvent(ev);
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		
		if(!isRoot)
			return true;
			
		float x = ev.getRawX();
		if(!init)
		{
			initScrollData();
			init = true;
		}
		Log.d("MDXC", "lastTop : " + this.getTop() + "     lastLeft :" +  this.getLeft());
		switch (ev.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			mDownActionTime = System.currentTimeMillis();
			mDownActionX = x;	
			mTempContentView.setVisibility(VISIBLE);
			break;
		case MotionEvent.ACTION_MOVE:
			float distanceX = x - lastX;
			if(Math.abs(distanceX) < 3)
				return true;
			float leftbound = this.getLeft() + distanceX;

			float topbound = this.getTop() + (distanceX/moveScale);
			if((leftbound/maxDistanceX) > 1 && distanceX > 0)
			{
				lastX = x;
				return true;
			}
			else if(leftbound < 0)
			{
				this.layout(0,0,Math.round(orginWidth), Math.round(orginHeight));
				mTempContentView.layout(0,0,Math.round(orginWidth), Math.round(orginHeight));
				
			}
			else
			{
				float currentWidthScale = (1 - (leftbound/maxDistanceX)*(1 - widthScale));
				float currentHeightScale = (1 - (topbound/maxShrinkY)*(1 - heightScale));
				this.layout(Math.round(leftbound), Math.round(topbound), Math.round(leftbound + orginWidth * currentWidthScale), Math.round( topbound + orginHeight*currentHeightScale));
				mTempContentView.layout(0, 0, Math.round(orginWidth * currentWidthScale), Math.round(orginHeight*currentHeightScale));
				mFragmentHomeView.layout(0, 0, Math.round(orginWidth * currentWidthScale), Math.round(orginHeight*currentHeightScale));
				Log.d("MDXC", "distanceX: " + distanceX + " distanceY:  " + (distanceX/moveScale) +"   l:" + leftbound + " t:" + topbound + " r:" +Math.round(leftbound + orginWidth * currentWidthScale) + " b" + Math.round( topbound + orginHeight*currentHeightScale));
			}

			break;
		case MotionEvent.ACTION_UP:
			Log.e("MDXC", "x - mDownActionX: " + (x - mDownActionX) + " time  " + (System.currentTimeMillis() - mDownActionTime) );
			if(x - mDownActionX > 50 && (System.currentTimeMillis() - mDownActionTime) < 100)
				transformToMiniView(mSelf.getLeft(), mSelf.getTop(), (mSelf.getRight() - mSelf.getLeft()), (mSelf.getBottom() - mSelf.getTop()));
			else if(mSelf.getLeft() <= (orginWidth/2))
				transformToFullView(mSelf.getLeft(), mSelf.getTop(), (mSelf.getRight() - mSelf.getLeft()), (mSelf.getBottom() - mSelf.getTop()));
			else 
				transformToMiniView(mSelf.getLeft(), mSelf.getTop(), (mSelf.getRight() - mSelf.getLeft()), (mSelf.getBottom() - mSelf.getTop()));
			
			break;
		default:
			break;
		}

		lastX = x;

		return true;
	}
	
	public boolean startContent(int tag)
	{
		switch(tag)
		{
		case EntryActivity.MY_ODERS:
			mSelf.removeAllViews();
			if(mFlightOrdersView == null)
			{
				mFlightOrdersView = new FlightOrdersView(mContext);
				LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				mFlightOrdersView.setLayoutParams(params);
				mFlightOrdersView.setTag(EntryActivity.MY_ODERS);
				mFlightOrdersView.setTileBarBackIconListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						startContent(EntryActivity.FLIGHT_STATUS);
					}
				});
			}
			
			mSelf.addView(mFlightOrdersView);
			mSelf.layout(0, 0, Math.round(orginWidth), Math.round(orginHeight));
			isRoot = false;
			break;
		case EntryActivity.VIEWED_HOTELS:
			break;
		case EntryActivity.FLIGHT_STATUS:
			
			if(isRoot)
				return false;
			if(!init)
			{
				initScrollData();
				init = true;
			}
			mSelf.removeAllViews();
			mSelf.addView(mFragmentHomeView);
			mSelf.addView(mTempContentView);
			transformToMiniView(mFragmentHomeView.getLeft(),mFragmentHomeView.getTop(),orginWidth,orginHeight);
			isRoot = true;
			break;
		case EntryActivity.SEARCH_HISTORY:
			break;
		case EntryActivity.SETTINGS:
			mSelf.removeAllViews();
			if(mSettingsView == null)
			{
				mSettingsView = new SettingsView(mContext);
				LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				mSettingsView.setLayoutParams(params);
				mSettingsView.setTag(EntryActivity.SETTINGS);
				mSettingsView.setTileBarBackIconListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						startContent(EntryActivity.FLIGHT_STATUS);
					}
				});
			}
			mSelf.addView(mSettingsView);
			mSelf.layout(0, 0, Math.round(orginWidth), Math.round(orginHeight));
			isRoot = false;
			break;			
		case EntryActivity.FEED_BACK:
			break;
		}
		requestLayout();
		return true;
	}

}
