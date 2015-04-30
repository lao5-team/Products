package com.test.juxiaohui.mdxc.app;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easemob.chat.EMChat;
import com.test.juxiaohui.R;
import com.test.juxiaohui.mdxc.manager.ServerManager;
import com.test.juxiaohui.mdxc.manager.UserManager;

import junit.framework.Assert;

/**
 * Created by yihao on 15/3/4.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    LoginActivity mActivity;
    TextView mTvUsername;
    TextView mPassword;
    EditText mEtxUsername;
    EditText mEtxPassword;
    Button mBtnOK;
    Button mBtnCancel;

    public LoginActivityTest()
    {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
        EMChat.getInstance().init(mActivity);
        UserManager.getInstance().logout();
        mTvUsername = (TextView) mActivity.findViewById(R.id.textView_username);
        mEtxUsername = (EditText) mActivity.findViewById(R.id.editText_username);
        mEtxPassword = (EditText) mActivity.findViewById(R.id.editText_password);
        mBtnOK = (Button)mActivity.findViewById(R.id.button_OK);
        mBtnCancel = (Button)mActivity.findViewById(R.id.button_Cancel);

    }

    public void testUI()
    {
        //Assert.assertEquals(mTvUsername.getText(), "Username");

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mEtxUsername.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("mdxc");
        getInstrumentation().waitForIdleSync();

        //填写一个错误的密码
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mEtxPassword.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("yhyh");
        getInstrumentation().waitForIdleSync();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mBtnOK.requestFocus();
                mBtnOK.performClick();

            }
        });
        getInstrumentation().waitForIdleSync();
        Assert.assertFalse(mActivity.getLoginResult().contains("Success"));

        //填写一个正确的密码
        getInstrumentation().waitForIdleSync();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mEtxPassword.requestFocus();
                mEtxPassword.setText("");
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("mdxc");
        getInstrumentation().waitForIdleSync();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mBtnOK.requestFocus();
                mBtnOK.performClick();

            }
        });
        getInstrumentation().waitForIdleSync();
        Assert.assertTrue(mActivity.getLoginResult().contains("Success"));


    }

    @Override
    public void tearDown()
    {
        try {
            UserManager.getInstance().logout();
            super.tearDown();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
