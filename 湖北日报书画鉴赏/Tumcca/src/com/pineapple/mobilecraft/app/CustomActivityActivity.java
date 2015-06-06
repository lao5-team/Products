package com.pineapple.mobilecraft.app;

import com.pineapple.mobilecraft.Constant;
import com.pineapple.mobilecraft.data.CustomBusiness;
import com.pineapple.mobilecraft.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CustomActivityActivity extends Activity {
	//data
	private CustomBusiness mCB;
	
	//UI controls
	EditText mEtxContent;
	Button mBtnOK;
	Button mBtnCancel;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	    initUI();
	    initData();
	}
	
	private void initUI()
	{
		setContentView(R.layout.activity_customactivity);
		mBtnOK = (Button)findViewById(R.id.button_confirm);
		mBtnCancel = (Button)findViewById(R.id.button_cancel);
		mEtxContent = (EditText)findViewById(R.id.editText_content);
		
		mBtnOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCB.content = mEtxContent.getEditableText().toString();
				Intent intent = new Intent();
				intent.putExtra(Constant.CUSTOM_BUSINESS, mCB);
				setResult(RESULT_OK);
				finish();
			}
		});
		
		mBtnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();				
			}
		});
		
	}
	
	private void initData()
	{
		mCB = new CustomBusiness();
	}
}
