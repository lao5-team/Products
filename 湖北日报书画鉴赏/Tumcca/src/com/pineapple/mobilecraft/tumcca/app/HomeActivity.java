package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.os.Bundle;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.mediator.IHome;

/**
 * Created by yihao on 15/6/4.
 */
public class HomeActivity extends Activity implements IHome{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    public void addHotTab() {

    }

    @Override
    public void addTrendsTab() {

    }

    @Override
    public void onTabSelect() {

    }
}