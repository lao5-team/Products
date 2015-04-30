package com.test.juxiaohui.mdxc.app.view;

import com.test.juxiaohui.R;
import com.test.juxiaohui.mdxc.app.EntryActivity;
import com.test.juxiaohui.mdxc.widget.CommonTitleBar;

import android.content.Context;

import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class SettingsView extends LinearLayout {
	
	private LinearLayout mContentLayout;
	private Context mContext;
	
	private LayoutInflater mInflater;
	
	private CommonTitleBar mTitleBar;
	
	public SettingsView(Context context) {
		super(context);
		mContext = context;
		initView();
		// TODO Auto-generated constructor stub
	}
	
	private void initView()
	{
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.setOrientation(VERTICAL);
		
		//add titlebar
		mTitleBar = new CommonTitleBar(mContext);
		TextView titleTextView = new TextView(mContext);
		titleTextView.setGravity(Gravity.CENTER);
		titleTextView.setTextColor(getResources().getColor(R.color.white));
		titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.font_size_36));
		titleTextView.setText(mContext.getText(R.string.settings));
		titleTextView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mTitleBar.setMarkLayout(titleTextView);
		this.addView(mTitleBar);
		
		mContentLayout = (LinearLayout) mInflater.inflate(R.layout.view_settings, null);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mContentLayout.setLayoutParams(params);
		this.addView(mContentLayout);
	}

	public void setTileBarBackIconListener(OnClickListener listener)
	{
		mTitleBar.setBackIconListener(listener);
	}
	
}
