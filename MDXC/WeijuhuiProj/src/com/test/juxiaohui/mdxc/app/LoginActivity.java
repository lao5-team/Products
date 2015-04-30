package com.test.juxiaohui.mdxc.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.test.juxiaohui.DemoApplication;
import com.test.juxiaohui.R;
import com.test.juxiaohui.mdxc.manager.ServerManager;
import com.test.juxiaohui.mdxc.manager.UserManager;
import com.test.juxiaohui.mdxc.mediator.ILoginMediator;

/**
 * Created by yihao on 15/3/4.
 */
public class LoginActivity extends Activity implements ILoginMediator{

    EditText mEtxUsername;
    EditText mEtxPassword;
    Button mBtnOK;
    Button mBtnCancel;
    Button mBtnRegister;
    String mLoginResult = "";
    RelativeLayout mLayoutSplash;
    LinearLayout mLayoutContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdxc_activity_login);
        mLayoutSplash = (RelativeLayout) findViewById(R.id.rl_splash);
        mLayoutContent = (LinearLayout) findViewById(R.id.ll_content);

        addUsernameView();
        addPasswordView();

        mBtnOK = (Button)findViewById(R.id.button_OK);
        mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });

        mBtnCancel = (Button)findViewById(R.id.button_Cancel);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        //当前未登录
        showProgress();
        if(!UserManager.getInstance().isLogin())
        {
            //之前登录过，从缓存中登录
            if(isHasLoginCache()){

                Thread t= new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loginFromCache();
                        if(UserManager.getInstance().isLogin())
                        {
                            gotoNext();
                        }
                        else
                        {
                            hideProgress();
                        }

                    }
                });
                t.start();
            }
            else
            {
                //手动登录
                hideProgress();
            }

        }
        else{
            gotoNext();
        }
        
    }

    @Override
    public void addUsernameView() {
        mEtxUsername  = (EditText)findViewById(R.id.editText_username);
    }

    @Override
    public void addPasswordView() {
        mEtxPassword = (EditText)findViewById(R.id.editText_password);
    }

    /**
     * 当用户登录过一次后，会缓存用户名和密码
     * 下次就会自动从缓存中取出用户名和密码进行登录
     */
    @Override
    public void loginFromCache() {
        if(!UserManager.getInstance().isLogin())
        {
            String username = DemoApplication.getInstance().getUserName();
            String password = DemoApplication.getInstance().getPassword();
            if(null!=username && null!=password)
            {
                mLoginResult = UserManager.getInstance().login(username, password);
                if(!mLoginResult.equals(UserManager.LOGIN_SUCCESS))
                {
                    showErrorMessage(mLoginResult);
                }
            }
        }
    }

    @Override
    public void confirm() {
        boolean isValid = true;
        if(mEtxUsername.getEditableText().toString().length()==0)
        {
            showErrorMessage("Username is empty!");
            isValid = false;
        }

        if(mEtxPassword.getEditableText().toString().length()==0)
        {
            showErrorMessage("Password is empty!");
            isValid = false;
        }

        if(isValid)
        {
            showErrorMessage("Waiting for login...");
            final String username = mEtxUsername.getEditableText().toString();
            final String password = mEtxPassword.getEditableText().toString();
    		DemoApplication.getInstance().setUserName(username);
    		DemoApplication.getInstance().setPassword(password);
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    mLoginResult = UserManager.getInstance().login(username, password);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mLoginResult.equals(UserManager.LOGIN_SUCCESS))
                            {
                                showErrorMessage("Login Success");
                                EntryActivity.startActivity(LoginActivity.this);
                                finish();
                            }
                            else
                            {
                                showErrorMessage(mLoginResult);
                            }
                        }
                    });
                }
            });
            t.start();

        }

    }

    @Override
    public void cancel() {
        finish();
    }

    @Override
    public void showProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLayoutSplash.setVisibility(View.VISIBLE);
                mLayoutContent.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public void hideProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLayoutSplash.setVisibility(View.INVISIBLE);
                mLayoutContent.setVisibility(View.VISIBLE);
            }
        });

    }

    public void showErrorMessage(final String message)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getLoginResult()
    {
        Log.v(DemoApplication.TAG, "Login Result = " + mLoginResult);
        return mLoginResult;
    }


    public void onClickRegister(View view)
    {
    	Intent intent = new Intent(this, RegisterActivity.class);
    	startActivity(intent);
    }

    private boolean isHasLoginCache()
    {
        String username = DemoApplication.getInstance().getUserName();
        String password = DemoApplication.getInstance().getPassword();
        return (null!=username&&null!=password);
    }

    private void gotoNext(){
        EntryActivity.startActivity(LoginActivity.this);
        finish();
    }

}