package com.pineapple.mobilecraft.shop.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Choreographer.FrameCallback;
import android.widget.Button;
import android.support.v4.app.Fragment;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.shop.mediator.IShopEntryMediator;
import com.pineapple.mobilecraft.shop.server.ShopServer;

/**
 * Created by yihao on 15/2/14.
 */
public class ShopMainActivity extends FragmentActivity implements IShopEntryMediator{
	Fragment[] mFragments = new Fragment[3];
	ShopFragment mShopFragment;
	ChartFragment mChartFragment;
	OrderListFragment mOrderListFragment;
	Fragment mCurrentFragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopServer.getInstance().login("yh", "yhtest");
        setContentView(R.layout.activity_shop_main);

        Button btnShop = (Button) findViewById(R.id.btn_shop);
        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	getSupportFragmentManager().beginTransaction().remove(mCurrentFragment).commit();
            	mCurrentFragment = mShopFragment;
            	getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mCurrentFragment).show(mCurrentFragment).commit();
            }
        });

        Button btnChart = (Button) findViewById(R.id.btn_chart);
        btnChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	getSupportFragmentManager().beginTransaction().remove(mCurrentFragment).commit();
            	mCurrentFragment = mChartFragment;
            	getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mCurrentFragment).show(mCurrentFragment).commit();
            }
        });

        Button btnOrder = (Button) findViewById(R.id.btn_my);
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	getSupportFragmentManager().beginTransaction().remove(mCurrentFragment).commit();
            	mCurrentFragment = mOrderListFragment;
            	getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mCurrentFragment).show(mCurrentFragment).commit();
            	
            }
        });
        mShopFragment = new ShopFragment();
        mChartFragment = new ChartFragment();
        mOrderListFragment = new OrderListFragment();
        
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mShopFragment).show(mShopFragment).commit();
        mCurrentFragment = mShopFragment;
        
    }

	@Override
	public void setTabNum(int num) {
		
	}

	@Override
	public void setTabActivity(Class<?> cls, int index) {
		
	}

	@Override
	public void setTabFragment(Fragment fragment, int index) {
		
	}

	@Override
	public void onTabSwitched(int curIndex, int prevIndex) {
		
	}
}