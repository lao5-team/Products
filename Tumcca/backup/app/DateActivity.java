package com.pineapple.mobilecraft.app;

import java.util.Date;

import com.pineapple.mobilecraft.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class DateActivity extends Activity {
	private DatePicker mDatePicker;
	private TimePicker mTimePicker;
	private Button mBtnOK;
	private Button mBtnCancel;
	private Date mDate;
	@Override
	protected void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		initData();
		initUI();
	}
	
	public void initData()
	{
		mDate = new Date();
	}
	
	public void initUI()
	{
		setContentView(R.layout.activity_date);
		mDatePicker = (DatePicker) findViewById(R.id.datePicker);
		mTimePicker = (TimePicker) findViewById(R.id.timePicker); 
		mTimePicker.setIs24HourView(true);
		mDatePicker.setCalendarViewShown(false);
		mBtnOK = (Button)findViewById(R.id.button_ok);
		mBtnOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDate.setYear(mDatePicker.getYear() - 1900);
				mDate.setMonth(mDatePicker.getMonth());
				mDate.setDate(mDatePicker.getDayOfMonth());
				mDate.setHours(mTimePicker.getCurrentHour());
				mDate.setMinutes(mTimePicker.getCurrentMinute());
				Intent intent = new Intent();
				intent.putExtra("date", mDate);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		
		mBtnCancel = (Button)findViewById(R.id.button_cancel);
		mBtnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(RESULT_CANCELED, intent);		
				finish();
			}
		});
	}
}
