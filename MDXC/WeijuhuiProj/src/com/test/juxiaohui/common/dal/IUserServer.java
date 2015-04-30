package com.test.juxiaohui.common.dal;

import com.test.juxiaohui.common.data.User;

/**
 * Created by yihao on 15/3/4.
 */
public interface IUserServer {
	/**
	 * 注册
	 * @param username 
	 * @param password
	 * @param checkcode 手机校验码
	 * @return
	 */
    public String register(String username, String password, String checkcode);

    /**
     * 登陆接口，登陆的结果用String返回
     * @param username
     * @param password
     * @param user 用来将登录后的用户信息返回，主要是为了返回用户id
     * @return 返回登陆结果
     */
    public String login(String username, String password, User user);

    /**
     * 获取当前登录的用户
     * @return
     */
    public User getCurrentUser();

    public String logout();
    
    public void sendCheckcode(String phoneNumber);

    public User getUserInfo(String username);
}
