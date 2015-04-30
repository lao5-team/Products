package com.test.juxiaohui.mdxc.app.view;

import com.test.juxiaohui.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

public class FragmentHomeView extends RelativeLayout{

	Context mContext;
	RelativeLayout mContentView;
	private LayoutInflater mInflater;
	
	public FragmentHomeView(Context context) {
		super(context);
		mContext = context;
		initView();
		// TODO Auto-generated constructor stub
	}
	
	public void initView()
	{
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContentView = (RelativeLayout) mInflater.inflate(R.layout.fragment_home, null);
		this.addView(mContentView);
	}

}
