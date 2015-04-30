package com.test.juxiaohui.mdxc.app.view;

import com.test.juxiaohui.R;
import com.test.juxiaohui.mdxc.app.FlightSearchActivity.ICabinClassListener;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

public class CabinClassDialog extends Dialog 
{

	private Context mContext;
	private ICabinClassListener mListener;
	
	private LinearLayout mMainView;
	private RelativeLayout mEconomyLayout, mBusinessLayout, mFirstLayout;
	private ImageView mIvEconomySelected, mIvBusinessSelected, mIvFirstSelected;
	private LayoutInflater mInflater;
	private Button mBtnCancel;
	
	public static final int ECONOMY = 0;
	public static final int BUSINESS = 1;
	public static final int FIRST = 2;
	
	private static final int CANCEL = 3;
	
	private CabinClassDialog mSelf;
	
	private int mCurrentClass;
	
	public CabinClassDialog(Context context,ICabinClassListener listener) 
	{
		// TODO Auto-generated constructor stub
		super(context,R.style.cabin_class_dialog);
		mContext = context;
		mListener = listener;	
		mSelf = this;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//window.setType(Window.FEATURE_NO_TITLE);
		initView();
	}
	

	
	private void initView()
	{
		mInflater= LayoutInflater.from(mContext);
		LayoutParams mainViewlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);		
		mMainView = (LinearLayout) mInflater.inflate(R.layout.dialog_cabin_class, null);
		mMainView.setLayoutParams(mainViewlp);
		mEconomyLayout = (RelativeLayout) mMainView.findViewById(R.id.economy_layout);
		mEconomyLayout.setTag(ECONOMY);
		mEconomyLayout.setOnClickListener(mOnClickListener);
		mBusinessLayout = (RelativeLayout) mMainView.findViewById(R.id.business_layout);
		mBusinessLayout.setTag(BUSINESS);
		mBusinessLayout.setOnClickListener(mOnClickListener);
		mFirstLayout = (RelativeLayout) mMainView.findViewById(R.id.first_layout);
		mFirstLayout.setTag(FIRST);
		mFirstLayout.setOnClickListener(mOnClickListener);
		mIvEconomySelected = (ImageView) mMainView.findViewById(R.id.iv_economy_selected);
		mIvEconomySelected.setVisibility(View.INVISIBLE);
		mIvBusinessSelected = (ImageView) mMainView.findViewById(R.id.iv_business_selected);
		mIvBusinessSelected.setVisibility(View.INVISIBLE);
		mIvFirstSelected = (ImageView) mMainView.findViewById(R.id.iv_first_selected);
		mIvFirstSelected.setVisibility(View.INVISIBLE);
		mBtnCancel = (Button) mMainView.findViewById(R.id.btn_cancle);
		mBtnCancel.setTag(CANCEL);
		mBtnCancel.setOnClickListener(mOnClickListener);

		switch(mCurrentClass)
		{
		case ECONOMY:
			mIvEconomySelected.setVisibility(View.VISIBLE);
			mIvBusinessSelected.setVisibility(View.INVISIBLE);
			mIvFirstSelected.setVisibility(View.INVISIBLE);
			break;
		case BUSINESS:
			mIvEconomySelected.setVisibility(View.INVISIBLE);
			mIvBusinessSelected.setVisibility(View.VISIBLE);
			mIvFirstSelected.setVisibility(View.INVISIBLE);
			break;
		case FIRST:
			mIvEconomySelected.setVisibility(View.INVISIBLE);
			mIvBusinessSelected.setVisibility(View.INVISIBLE);
			mIvFirstSelected.setVisibility(View.VISIBLE);
			break;
		}
		
		setContentView(mMainView);
	}
	
	private int getScreenWidth()
	{
		DisplayMetrics dm = mContext.getApplicationContext().getResources().getDisplayMetrics();
		if (dm.widthPixels >= dm.heightPixels)
		{
			return dm.heightPixels;
		}
		else
		{
			return dm.widthPixels;
		}
	}
	
    public void show(int flag)
    {
		
    	if(mIvEconomySelected != null && mIvBusinessSelected !=null && mIvFirstSelected != null)
    	{
    		switch(flag)
    		{
    		case ECONOMY:
    			mIvEconomySelected.setVisibility(View.VISIBLE);
    			mIvBusinessSelected.setVisibility(View.INVISIBLE);
    			mIvFirstSelected.setVisibility(View.INVISIBLE);
    			break;
    		case BUSINESS:
    			mIvEconomySelected.setVisibility(View.INVISIBLE);
    			mIvBusinessSelected.setVisibility(View.VISIBLE);
    			mIvFirstSelected.setVisibility(View.INVISIBLE);
    			break;
    		case FIRST:
    			mIvEconomySelected.setVisibility(View.INVISIBLE);
    			mIvBusinessSelected.setVisibility(View.INVISIBLE);
    			mIvFirstSelected.setVisibility(View.VISIBLE);
    			break;
    		}
    	}
    	else
    		mCurrentClass = flag;
    	this.show();
	
    	DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.dimAmount = 0.25f;
		lp.alpha = 0.95f;
		lp.width = dm.widthPixels;
		getWindow().setAttributes(lp);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		Window window = this.getWindow();
		window.getDecorView().setPadding(0, 0, 0, 0);
		window.setGravity(Gravity.BOTTOM);

    }
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

			switch((Integer)(arg0.getTag()))
			{
			case ECONOMY:
				mListener.onChangeCabinClass(ECONOMY);
				break;
			case BUSINESS:
				mListener.onChangeCabinClass(BUSINESS);
				break;
			case FIRST:
				mListener.onChangeCabinClass(FIRST);
				break;
			case CANCEL:
				break;
			}
			
			mSelf.dismiss();
		}

	};
	
}
