package com.test.juxiaohui.mdxc.mediator;

/**
 * Created by yihao on 15/3/4.
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
}
