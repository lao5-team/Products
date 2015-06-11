package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.mediator.IHome;

/**
 * Created by yihao on 15/6/4.
 */
public class HomeActivity extends FragmentActivity implements IHome{
    Button mBtnLogin = null;
    Button mBtnRegister = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_home);
        if(UserManager.getInstance().isLogin()){
            setTitle(UserManager.getInstance().getCachedUsername());
        }
        else{
            setTitle("书法+");
            addAccountView();
        }




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
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout_account);
        layout.setVisibility(View.VISIBLE);
        mBtnLogin = (Button)findViewById(R.id.button_login);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.startActivity(HomeActivity.this);
//                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
//
//                builder.setView(getLayoutInflater().inflate(R.layout.dialog_register, null));
//
//                builder.create().show();
            }
        });

        mBtnRegister = (Button)findViewById(R.id.button_register);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.startActivity(HomeActivity.this);
                //RegisterFragment fragment = new RegisterFragment();
                //getFragmentManager().beginTransaction().add(fragment, "register").commit();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == LoginActivity.REQ_LOGIN&&resultCode == RESULT_OK){
            setTitle(UserManager.getInstance().getCachedUsername());
            RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout_account);
            layout.setVisibility(View.GONE);
        }
        if(requestCode == RegisterActivity.REQ_REGISTER&&resultCode == RESULT_OK){
            setTitle(UserManager.getInstance().getCachedUsername());
            RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout_account);
            layout.setVisibility(View.GONE);


        }
        //super.onActivityResult();
    }
}