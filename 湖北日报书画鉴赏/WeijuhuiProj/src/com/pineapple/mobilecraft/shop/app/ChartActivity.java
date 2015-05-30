package com.pineapple.mobilecraft.shop.app;

import com.pineapple.mobilecraft.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ChartActivity extends FragmentActivity {
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chart_1);
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new ChartFragment(), null).commit();
	}

}
