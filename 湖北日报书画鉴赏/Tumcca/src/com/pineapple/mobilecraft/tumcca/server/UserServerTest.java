package com.pineapple.mobilecraft.tumcca.server;

import android.test.AndroidTestCase;
import com.pineapple.mobilecraft.shop.mediator.IShopEntryMediator;
import junit.framework.Assert;

/**
 * Created by yihao on 15/6/6.
 */
public class UserServerTest extends AndroidTestCase{
    UserServer mUserServer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mUserServer = UserServer.getInstance();
    }

    public void testRegister(){
        IUserServer.RegisterResult result = mUserServer.register("15510472557", null, "123456");
        Assert.assertEquals(result.message, IUserServer.REGISTER_SUCCESS);
        result = mUserServer.register(null, "yh@163.com", "123456");
        //delete account

        /*register two same account*/
        result = mUserServer.register("15510472557", null, "123456");
        result = mUserServer.register("15510472557", null, "123456");
        Assert.assertEquals(result, IUserServer.REGISTER_ACCOUNT_EXIST);
        //delete account

        /*register invalid account*/
        result = mUserServer.register(null, null, "123456");
        Assert.assertEquals(result.message, IUserServer.REGISTER_FAILED);

    }

    public void testLogin(){
        IUserServer.LoginResult loginResult = mUserServer.login("15510472557", "123456");
        Assert.assertNotNull(loginResult);
    }

}
