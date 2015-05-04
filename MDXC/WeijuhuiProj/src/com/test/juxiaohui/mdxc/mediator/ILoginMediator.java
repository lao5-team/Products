package com.test.juxiaohui.mdxc.mediator;

/**
 * Created by yihao on 15/3/4.
 * 登录时要选择国家区号，输入电话号码
 * 登录成功后，会将区号和电话号码，密码记录下来，方便下次自动登录。
 * 密码记录时要加密存储
 * 注销后，缓存的密码将被清除，下次登录时只会自动填好电话号码，国家区号。
 *
 * User needs to select the country code, input phone number when login.
 * It will cache the code, phone number, password to login automatically next time.
 * The password will be encrypted when cache.
 * It will clear the cached password after user log out, country code and phone number will
 * be set automatically in next login.
 */
public interface ILoginMediator {
    public void addUsernameView();

    public void addPasswordView();

    /**
     * 当用户登录过一次后，会缓存用户名和密码
     * 下次就会自动从缓存中取出用户名和密码进行登录
     */
    public void loginFromCache();

    public void confirm();

    public void cancel();

    public void showProgress();

    public void hideProgress();

    /**
     * 添加国家代码选择
     */
    public void addCountryCodeView();

    /**
     * 若登录成功，存储国家码，电话号码，密码
     * 密码要加密保存
     * @param countryCode
     * @param phoneNumber
     */
    public void saveLoginInfo(String countryCode, String phoneNumber);

    /**
     * 读取存储信息
     */
    public void loadLoginInfo();


}
