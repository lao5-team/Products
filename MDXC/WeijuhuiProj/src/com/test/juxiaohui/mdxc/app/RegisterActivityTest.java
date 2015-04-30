package com.test.juxiaohui.mdxc.app;

import junit.framework.Assert;

import com.easemob.chat.EMChat;
import com.test.juxiaohui.R;

import android.test.ActivityInstrumentationTestCase2;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivityTest extends ActivityInstrumentationTestCase2<RegisterActivity> {

	private EditText mPhoneNumberEditText;
	private EditText mPasswordEditText;
	private EditText mConfirmPwdEditText;
	private Button mRegiserButton;
	private RegisterActivity mActivity;
	
	public RegisterActivityTest() {
		super(RegisterActivity.class);
		// TODO Auto-generated constructor stub
	}
	
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
        EMChat.getInstance().init(mActivity);
        mPhoneNumberEditText = (EditText) mActivity.findViewById(R.id.et_phone_number);
		mPhoneNumberEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		mPasswordEditText = (EditText) mActivity.findViewById(R.id.et_password);
		mConfirmPwdEditText = (EditText) mActivity.findViewById(R.id.et_confirm_password);
		mRegiserButton = (Button) mActivity.findViewById(R.id.button_register);

    }
    
    public void testUI()
    {
    	//测试不填写手机号码
    	getInstrumentation().waitForIdleSync();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
            	mPhoneNumberEditText.requestFocus();
            }
        });
        
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("");
        getInstrumentation().waitForIdleSync();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
            	mRegiserButton.requestFocus();
            	mRegiserButton.performClick();
            }
        });
        getInstrumentation().waitForIdleSync();
        Assert.assertTrue("name_is_null".equalsIgnoreCase(mActivity.getRegisterResult()));
        
        //测试手机号码不合法
        getInstrumentation().waitForIdleSync();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
            	mPhoneNumberEditText.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("123456");
        getInstrumentation().waitForIdleSync();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
            	mRegiserButton.requestFocus();
            	mRegiserButton.performClick();
            }
        });
        getInstrumentation().waitForIdleSync();
        Assert.assertTrue("user_name_invalid".equalsIgnoreCase(mActivity.getRegisterResult()));
        
        //测试密码不一致
        getInstrumentation().waitForIdleSync();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
            	mPhoneNumberEditText.requestFocus();
            	mPhoneNumberEditText.setText("");
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("13888888888");
        getInstrumentation().waitForIdleSync();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
            	mPasswordEditText.requestFocus();
            	mPasswordEditText.setText("");
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("123456");
        getInstrumentation().waitForIdleSync();
        
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
            	mConfirmPwdEditText.requestFocus();
            	mConfirmPwdEditText.setText("");
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("654321");
        getInstrumentation().waitForIdleSync();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
            	mRegiserButton.requestFocus();
            	mRegiserButton.performClick();
            }
        });
        getInstrumentation().waitForIdleSync();
        Assert.assertTrue("password_confirm_error".equalsIgnoreCase(mActivity.getRegisterResult()));
        
        //测试不写确认密码
        getInstrumentation().waitForIdleSync();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
            	mConfirmPwdEditText.requestFocus();
            	mConfirmPwdEditText.setText("");
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("");
        getInstrumentation().waitForIdleSync();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
            	mRegiserButton.requestFocus();
            	mRegiserButton.performClick();
            }
        });
        getInstrumentation().waitForIdleSync();
        Assert.assertTrue("password_is_null".equalsIgnoreCase(mActivity.getRegisterResult()));
        
        //测试所有信息填写无误
        getInstrumentation().waitForIdleSync();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
            	mConfirmPwdEditText.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("123456");
        getInstrumentation().waitForIdleSync();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
            	mRegiserButton.requestFocus();
            	mRegiserButton.performClick();
            }
        });
        
        getInstrumentation().waitForIdleSync();
        Assert.assertTrue("Success".equalsIgnoreCase(mActivity.getRegisterResult()));


    }


}
