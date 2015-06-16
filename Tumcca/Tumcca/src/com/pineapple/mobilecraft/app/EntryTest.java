package com.pineapple.mobilecraft.app;

import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.shop.app.ShopMainActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class EntryTest extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry_test);
		
		Button btn_Social = (Button)findViewById(R.id.button_social);
		btn_Social.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EntryTest.this, LoginActivity.class);
				startActivity(intent);
			}
		});
		
		Button btn_Shop = (Button)findViewById(R.id.button_shop);
		btn_Shop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EntryTest.this, ShopMainActivity.class);
				startActivity(intent);				
			}
		});
	}

}
