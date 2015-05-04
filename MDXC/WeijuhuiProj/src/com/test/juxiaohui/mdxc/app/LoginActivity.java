package com.test.juxiaohui.mdxc.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.test.juxiaohui.DemoApplication;
import com.test.juxiaohui.R;
import com.test.juxiaohui.mdxc.manager.ServerManager;
import com.test.juxiaohui.mdxc.manager.UserManager;
import com.test.juxiaohui.mdxc.mediator.ILoginMediator;

import java.util.ArrayList;
import java.util.List;

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
    ExpandableListView mElvCountryCode;
    private int mSelectedChildPos = -1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdxc_activity_login);
        mLayoutSplash = (RelativeLayout) findViewById(R.id.rl_splash);
        mLayoutContent = (LinearLayout) findViewById(R.id.ll_content);

        addUsernameView();
        addPasswordView();
        addCountryCodeView();

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

    /**
     * 添加国家代码选择
     */
    @Override
    public void addCountryCodeView() {
        final List<String> coutryCodeList = new ArrayList<String>();
        coutryCodeList.add("+0086(China)");
        coutryCodeList.add("+0065(Singapore)");
        coutryCodeList.add("+0033(France)");
        mElvCountryCode = (ExpandableListView)findViewById(R.id.expandableListView_countryCode);
        mElvCountryCode.setAdapter(new BaseExpandableListAdapter() {

            @Override
            public int getGroupCount() {
                return 1;
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                return coutryCodeList.size();
            }

            @Override
            public Object getGroup(int groupPosition) {
                return null;
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                return null;
            }

            @Override
            public long getGroupId(int groupPosition) {
                return 0;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                long pos = mElvCountryCode.getSelectedPosition();
                View view = getLayoutInflater().inflate(R.layout.item_country_code, null);
                TextView tv = (TextView) view.findViewById(R.id.textView_country_code);
                if(mSelectedChildPos >= 0)
                {
                    tv.setText(coutryCodeList.get(mSelectedChildPos));
                }
                else
                {
                    tv.setText(getResources().getText(R.string.select_countryCode));
                }

                return view;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                View view = getLayoutInflater().inflate(R.layout.item_country_code, null);
                TextView tv = (TextView) view.findViewById(R.id.textView_country_code);
                tv.setText(coutryCodeList.get(childPosition));
                return view;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return true;
            }
        });

        mElvCountryCode.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                mSelectedChildPos = childPosition;
                mElvCountryCode.collapseGroup(0);
                return false;
            }
        });
    }

    /**
     * 若登录成功，存储国家码，电话号码，密码
     * 密码要加密保存
     *
     * @param countryCode
     * @param phoneNumber
     */
    @Override
    public void saveLoginInfo(String countryCode, String phoneNumber) {

    }

    /**
     * 读取存储信息
     */
    @Override
    public void loadLoginInfo() {

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