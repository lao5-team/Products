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
package com.pineapple.mobilecraft.tumcca.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.easemob.util.HanziToPinyin;
import com.pineapple.mobilecraft.Constant;
import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.domain.User;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.server.IUserServer;
import com.pineapple.mobilecraft.utils.AccessTokenKeeper;
import com.pineapple.mobilecraft.utils.CommonUtils;
import com.pineapple.mobilecraft.utils.LoginButton;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;

import java.text.SimpleDateFormat;

/**
 * 登陆页面
 * 
 */
public class LoginActivity extends Activity {
	public static final int REQ_REG = 0;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private boolean progressShow;
	private LoginButton mLoginButton;

	private AuthListener mLoginListener = new AuthListener();


	public static void startActivity(Activity activity, int requestCode){
		Intent intent = new Intent(activity, LoginActivity.class);
		activity.startActivityForResult(intent, requestCode);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);

		//mAuthInfo = new AuthInfo(this, WeiboUtils.APP_KEY, WeiboUtils.REDIRECT_URL, WeiboUtils.SCOPE);

	}

	/**
	 * 登陆
	 * 
	 * @param view
	 */
	public void login(View view) {
		if (!CommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
			return;
		}
		final String username = usernameEditText.getText().toString();
		final String password = passwordEditText.getText().toString();
		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
			progressShow = true;
			final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
			pd.setCanceledOnTouchOutside(false);
			pd.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					progressShow = false;
				}
			});
			pd.setMessage(getString(R.string.logining));
			pd.show();

			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					final IUserServer.LoginResult loginResult = UserManager.getInstance().login(username, password);
					try {
						Thread.currentThread().sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							if (null != loginResult) {
								//Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
								UserManager.getInstance().saveLoginInfo(username, password);
								setResult(RESULT_OK);
								finish();
								pd.dismiss();
							} else {
								Toast.makeText(LoginActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
							}

						}
					});

				}
			});
			t.start();

		}
	}

	/**
	 * 注册
	 * 
	 * @param view
	 */
	public void register(View view) {
		RegisterActivity.startActivity(LoginActivity.this, REQ_REG);
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (DemoApplication.getInstance().getUserName() != null) {
			usernameEditText.setText(DemoApplication.getInstance().getUserName());
		}
	}

	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 * 
	 * @param username
	 * @param user
	 */
	protected void setUserHearder(String username, User user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}

	/**
	 * 登入按钮的监听器，接收授权结果。
	 */
	private class AuthListener implements WeiboAuthListener {
		@Override
		public void onComplete(Bundle values) {
			Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
			if (accessToken != null && accessToken.isSessionValid()) {
				String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
						new java.util.Date(accessToken.getExpiresTime()));
				//String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
				//mTokenView.setText(String.format(format, accessToken.getToken(), date));

				AccessTokenKeeper.writeAccessToken(getApplicationContext(), accessToken);
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel() {
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQ_REG&&resultCode==RESULT_OK){
			setResult(RESULT_OK);
			finish();
		}
	}

}