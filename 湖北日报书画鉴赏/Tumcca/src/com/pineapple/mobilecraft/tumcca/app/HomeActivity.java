package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.mediator.IHome;

/**
 * Created by yihao on 15/6/4.
 */
public class HomeActivity extends Activity implements IHome{
    Button mBtnLogin = null;
    Button mBtnRegister = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        addAccountView();
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

    @Override
    public void addAccountView() {
        mBtnLogin = (Button)findViewById(R.id.button_login);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.startActivity(HomeActivity.this);
            }
        });

        mBtnRegister = (Button)findViewById(R.id.button_register);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.startActivity(HomeActivity.this);
            }
        });
    }
}