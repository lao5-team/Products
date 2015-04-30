package com.test.juxiaohui.mdxc.widget;

import com.test.juxiaohui.R;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CommonTitleBar extends LinearLayout 
{

	private ImageView mBackIcon;
	private LinearLayout mMarkLayout;
	private LayoutInflater mInflater;
	private RelativeLayout mSelf;
	private LinearLayout mTitleIconLayout;
	
	private Context mContext;
	
	public CommonTitleBar(Context context) {
		super(context);
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		initView();
		// TODO Auto-generated constructor stub
	}
	
	public void initView()
	{
		this.setOrientation(VERTICAL);
		mSelf = (RelativeLayout) mInflater.inflate(R.layout.common_title_bar, null);
		this.addView(mSelf);
		mBackIcon = (ImageView) mSelf.findViewById(R.id.title_back);
		mTitleIconLayout = (LinearLayout) mSelf.findViewById(R.id.title_icon);
		mMarkLayout = (LinearLayout) mSelf.findViewById( R.id.title_mark_view);
		
	}
	
	public void setBackIconListener(OnClickListener listener)
	{
		mBackIcon.setOnClickListener(listener);
	}
	
	public void setMarkLayout(View v)
	{
		mMarkLayout.addView(v);
	}
	
	public void setTitleIcon(View v)
	{
		mTitleIconLayout.addView(v);
	}

}
