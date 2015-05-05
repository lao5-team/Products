package com.test.juxiaohui.mdxc.app;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

import com.test.juxiaohui.R;
import com.test.juxiaohui.mdxc.data.CountryCode;
import com.test.juxiaohui.mdxc.manager.ServerManager;
import com.test.juxiaohui.mdxc.manager.UserManager;
import com.test.juxiaohui.mdxc.mediator.IRegisterMediator;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends Activity implements IRegisterMediator {

	private EditText mPhoneNumberEditText;
	private EditText mPasswordEditText;
	private EditText mConfirmPwdEditText;
	private Button mRegiserButton;
	private Context mContext;
	private String mRegisterResult;
	private Button mCheckcodeButton;
	private EditText mCheckcodeEditText;
	private UserManager mUserManager;
	ExpandableListView mElvCountryCode;
	private int mSelectedChildPos = -1;
	private String mCountryCode = "+86";
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mUserManager = UserManager.getInstance();
		mContext = this;
		initView();
	}
	
	public void initView()
	{
		setContentView(R.layout.mdxc_activity_register);
		addPhoneNumberView();
		addPasswordView();
		addConfirmPasswordView();
		addRegisterButton();
		addGetCheckcodeView();
		addCountryCodeView();
	}
	
	@Override
	public void addPhoneNumberView() {
		// TODO Auto-generated method stub
		mPhoneNumberEditText = (EditText) findViewById(R.id.et_phone_number);
		mPhoneNumberEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
	}

	@Override
	public void addPasswordView() {
		// TODO Auto-generated method stub
		mPasswordEditText = (EditText) findViewById(R.id.et_password);
	}


	@Override
	public void addConfirmPasswordView() {
		// TODO Auto-generated method stub
		mConfirmPwdEditText = (EditText) findViewById(R.id.et_confirm_password);
	}

	@Override
	public void addRegisterButton() {
		// TODO Auto-generated method stub
		mRegiserButton = (Button) findViewById(R.id.button_register);
		mRegiserButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				confirm();

			}
		});
	}

	/**
	 * 添加国家代码选择
	 */
	@Override
	public void addCountryCodeView() {
		final List<String> countryCodeList = CountryCode.convertCodeListToString(CountryCode.getDefaultCodes());

		mElvCountryCode = (ExpandableListView)findViewById(R.id.expandableListView_countryCode);
		mElvCountryCode.setAdapter(new BaseExpandableListAdapter() {

			@Override
			public int getGroupCount() {
				return 1;
			}

			@Override
			public int getChildrenCount(int groupPosition) {
				return countryCodeList.size();
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
					tv.setText(countryCodeList.get(mSelectedChildPos));
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
				tv.setText(countryCodeList.get(childPosition));
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


	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		String name;
		String password;
		String confirmPassword;
		String checkcode;
		name = mPhoneNumberEditText.getText().toString();
		password = mPasswordEditText.getText().toString();
		confirmPassword = mConfirmPwdEditText.getText().toString();
		checkcode = mCheckcodeEditText.getText().toString();
		if(name == null || name.equalsIgnoreCase(""))
		{
			showErrorMessage(mContext.getString(R.string.name_is_null));
			mRegisterResult = "name_is_null";
			return;
		}
		
		if(name.length() != 11)
		{
			showErrorMessage(mContext.getString(R.string.user_name_invalid));
			mRegisterResult = "user_name_invalid";
			return;
		}
		
		if(password == null || confirmPassword  == null || password.equalsIgnoreCase("") || confirmPassword.equalsIgnoreCase(""))
		{
			showErrorMessage(mContext.getString(R.string.password_is_null));
			mRegisterResult = "password_is_null";
			return;
		}
		if(!password.equalsIgnoreCase(confirmPassword))
		{
			showErrorMessage(mContext.getString(R.string.password_confirm_error));
			mRegisterResult = "password_confirm_error";
			return;
		}
		String[] params = {mCountryCode, name,password,checkcode};
		new RegisterTask().execute(params);
	}
	
	//注册调用不要在主线程中，否则会抛异常。
	private class RegisterTask extends AsyncTask<String, Integer, String>
	{

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return mUserManager.register(arg0[0], arg0[1], arg0[2], arg0[3]);
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			mRegisterResult = result;
			if(result.equals("Success"))
			{
				showErrorMessage(getString(R.string.reg_success));
				finish();
			}
			else
			{
				showErrorMessage(getString(R.string.reg_fail));
			}

			super.onPostExecute(result);
		}
		
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		finish();

	}
	
    public void showErrorMessage(final String message)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, message,Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    protected void onDestroy()
    {
    	mConfirmPwdEditText = null;
    	mContext = null;
    	mPasswordEditText = null;
    	mPhoneNumberEditText = null;
    	mRegiserButton = null;
    	super.onDestroy();
    }
    
    public static void startActivity(Context context)
    {
    	Intent intent = new Intent(context,RegisterActivity.class);
    	context.startActivity(intent);
    }
    
    public String getRegisterResult()
    {
    	return mRegisterResult;
    }

	@Override
	public void addGetCheckcodeView() {
		mCheckcodeButton = (Button)findViewById(R.id.button_checkcode);
		mCheckcodeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getCheckcode();
			}
		});
		mCheckcodeEditText = (EditText)findViewById(R.id.et_checkcode);
	}

	@Override
	public void getCheckcode() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				mUserManager.sendCheckcode(mCountryCode, mPhoneNumberEditText.getText().toString());
			}
		});
		t.start();
		
	}

}
