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
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.mediator.IRegister;
import com.pineapple.mobilecraft.tumcca.server.IUserServer;
import com.pineapple.mobilecraft.tumcca.server.UserServer;
import com.pineapple.mobilecraft.utils.PATextUtils;

/**
 * 注册页
 * 测试用例：
 * 1 用户名的手机号或者邮箱格式不正确，要注册失败，且弹出提示。
 * 2 注册重复的用户名，要弹出提示
 * 3 注册完成后要能够自动登录，登录后首页显示默认头像和用户填写的/斋号昵称
 */
public class RegisterActivity extends Activity implements IRegister, TextWatcher {
    private EditText userNameEditText;
    private EditText passwordEditText;
    private EditText pseudonymEditText;
    private Button registerButton;

    public static void startActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        addUsernameView();
        addPasswordView();
        addPseudonymView();
        registerButton = (Button) findViewById(R.id.button_register);
        registerButton.setEnabled(false);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
    }

    /**
     * 注册
     */
    public void register() {
        final String username = userNameEditText.getText().toString().trim();
        final String pwd = passwordEditText.getText().toString().trim();
        final String pseudonym = pseudonymEditText.getEditableText().toString();
        //String confirm_pwd = confirmPwdEditText.getText().toString().trim();
        if (!PATextUtils.isValidEmail(username) && !PATextUtils.isValidPhoneNumber(username)) {
            Toast.makeText(this, getString(R.string.please_enter_correct_phone_or_email), Toast.LENGTH_SHORT).show();
            userNameEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, getString(R.string.password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            passwordEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(pseudonym)) {
            Toast.makeText(this, getString(R.string.pseudonym_cannot_be_empty), Toast.LENGTH_SHORT).show();
            passwordEditText.requestFocus();
            return;
        }

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.registering));
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    IUserServer.RegisterResult registerResult = IUserServer.RegisterResult.getFailResult();
                    if (PATextUtils.isValidEmail(username)) {
                        registerResult = UserManager.getInstance().register(null, username, pwd);
                    } else if (PATextUtils.isValidPhoneNumber(username)) {
                        registerResult = UserManager.getInstance().register(username, null, pwd);
                    }
                    if (registerResult.message.equals(IUserServer.REGISTER_SUCCESS)) {
                        loginAfterRegister(username, pwd);
                        uploadPseudonym(pseudonym);
                        toastMessage(getString(R.string.register_success));
                        setResult(RESULT_OK);
                        finish();
                    } else if (registerResult.message.equals(IUserServer.REGISTER_ACCOUNT_EXIST)) {
                        toastMessage(getString(R.string.account_exist));
                    } else if (registerResult.message.equals(IUserServer.REGISTER_FAILED)) {
                        toastMessage(getString(R.string.register_failed));
                    }

                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!RegisterActivity.this.isFinishing())
                                pd.dismiss();

                        }
                    });
                } catch (final Exception e) {
                }
            }
        }).start();

    }

    public void back(View view) {
        finish();
    }

    @Override
    public void addUsernameView() {
        userNameEditText = (EditText) findViewById(R.id.username);
        userNameEditText.addTextChangedListener(this);
    }

    @Override
    public void addPasswordView() {
        passwordEditText = (EditText) findViewById(R.id.password);
        passwordEditText.addTextChangedListener(this);

    }

    public void addPseudonymView() {
        pseudonymEditText = (EditText) findViewById(R.id.pseudonym);
        pseudonymEditText.addTextChangedListener(this);


    }

    @Override
    public void confirm() {
        register();
    }

    @Override
    public void cancel() {
        finish();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if ((userNameEditText.getEditableText().length() > 0) && (passwordEditText.getEditableText().length() > 0)
                && (pseudonymEditText.getEditableText().length() > 0)) {
            registerButton.setEnabled(true);
        } else {
            registerButton.setEnabled(false);
        }
    }

    private void toastMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginAfterRegister(String username, String password){
        UserManager.getInstance().login(username, password);
        UserManager.getInstance().saveLoginInfo(username, password);
    }

    private void uploadPseudonym(String pseudonym){
        Profile profile = Profile.createDefaultProfile();
        profile.pseudonym = pseudonym;
        UserServer.getInstance().updateUser(profile, UserManager.getInstance().getCurrentToken(null));
    }
}
