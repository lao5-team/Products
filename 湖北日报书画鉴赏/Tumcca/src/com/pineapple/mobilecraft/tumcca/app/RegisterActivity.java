/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.app.BaseActivity;
import com.pineapple.mobilecraft.data.MyUser;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.mediator.IRegister;
import com.pineapple.mobilecraft.tumcca.server.IUserServer;

/**
 * 注册页
 * 
 */
public class RegisterActivity extends Activity implements IRegister {
	private EditText userNameEditText;
	private EditText passwordEditText;
	private EditText confirmPwdEditText;

	public static final int REQ_REGISTER = 0;
	public static void startActivity(Activity activity){
		Intent intent = new Intent(activity, RegisterActivity.class);
		activity.startActivityForResult(intent, REQ_REGISTER);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		addUsernameView();
		addPasswordView();
		addConfirmPasswordView();

		Button btn_register = (Button)findViewById(R.id.button_register);
		btn_register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				confirm();
			}
		});
	}

	/**
	 * 注册
	 * 
	 */
	public void register() {
		final String username = userNameEditText.getText().toString().trim();
		final String pwd = passwordEditText.getText().toString().trim();
		String confirm_pwd = confirmPwdEditText.getText().toString().trim();
		if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
			userNameEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
			passwordEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(confirm_pwd)) {
			Toast.makeText(this, "确认密码不能为空！", Toast.LENGTH_SHORT).show();
			confirmPwdEditText.requestFocus();
			return;
		} else if (!pwd.equals(confirm_pwd)) {
			Toast.makeText(this, "两次输入的密码不一致，请重新输入！", Toast.LENGTH_SHORT).show();
			return;
		}

		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage("正在注册...");
			pd.show();
			new Thread(new Runnable() {
				public void run() {
					try {
						final IUserServer.RegisterResult registerResult;
						if(username.contains("@")&&username.contains(".")){
							registerResult = UserManager.getInstance().register(null, username, pwd);
						}
						else if(Integer.valueOf(username)>0){
							registerResult = UserManager.getInstance().register(username, null, pwd);
						}
						else {
							registerResult = new IUserServer.RegisterResult();
						}
						runOnUiThread(new Runnable() {
							public void run() {
								if (!RegisterActivity.this.isFinishing())
									pd.dismiss();
								// 保存用户名
								if(registerResult.message.equals(IUserServer.REGISTER_SUCCESS)){
									Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
									finish();
								}
								else if(registerResult.message.equals(IUserServer.REGISTER_ACCOUNT_EXIST)){
									Toast.makeText(getApplicationContext(), "该用户已存在", Toast.LENGTH_SHORT).show();
								}
								else if(registerResult.message.equals(IUserServer.REGISTER_FAILED)){
									Toast.makeText(getApplicationContext(), "注册不成功", Toast.LENGTH_SHORT).show();

								}


							}
						});
					} catch (final Exception e) {
					}
				}
			}).start();

		}
	}

	public void back(View view) {
		finish();
	}

	@Override
	public void addUsernameView() {
		userNameEditText = (EditText) findViewById(R.id.username);
	}

	@Override
	public void addPasswordView() {
		passwordEditText = (EditText) findViewById(R.id.password);
	}

	@Override
	public void addConfirmPasswordView() {
		confirmPwdEditText = (EditText) findViewById(R.id.confirm_password);
	}

	@Override
	public void confirm() {
		register();
	}

	@Override
	public void cancel() {
		finish();
	}
}
