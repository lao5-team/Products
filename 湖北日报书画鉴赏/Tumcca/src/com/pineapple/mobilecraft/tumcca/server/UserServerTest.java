package com.pineapple.mobilecraft.tumcca.server;

import android.content.res.AssetManager;
import android.test.AndroidTestCase;
import com.pineapple.mobilecraft.shop.mediator.IShopEntryMediator;
import com.pineapple.mobilecraft.tumcca.data.Account;
import com.pineapple.mobilecraft.utils.PATextUtils;
import junit.framework.Assert;

import java.io.File;

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
        Assert.assertTrue(PATextUtils.isValidPhoneNumber("15510472558"));
        Assert.assertFalse(PATextUtils.isValidPhoneNumber("1551047255"));

        Assert.assertTrue(PATextUtils.isValidEmail("yh@163.com"));
        Assert.assertFalse(PATextUtils.isValidEmail("yh@163com"));
        IUserServer.RegisterResult result = mUserServer.register("15510472559", null, "123456");
        Assert.assertEquals(result.message, IUserServer.REGISTER_SUCCESS);
        mUserServer.deleteUser(result.uid);
        result = mUserServer.register(null, "yh@126.com", "123456");
        Assert.assertEquals(result.message, IUserServer.REGISTER_SUCCESS);

        mUserServer.deleteUser(result.uid);

        /*register two same account*/
        result = mUserServer.register("15510472559", null, "123456");
        String uid = result.uid;
        result = mUserServer.register("15510472559", null, "123456");
        Assert.assertEquals(result.message, IUserServer.REGISTER_ACCOUNT_EXIST);
        mUserServer.deleteUser(result.uid);

        /*register invalid account*/
        result = mUserServer.register(null, null, "123456");
        Assert.assertEquals(result.message, IUserServer.REGISTER_FAILED);

    }

    public void testLogin(){
        mUserServer.register("15510472559", null, "123456");
        IUserServer.LoginResult loginResult = mUserServer.login("15510472559", "123456");
        Assert.assertNotNull(loginResult);
        Account account = mUserServer.getAccount(loginResult.token);
        Assert.assertEquals(account.mobile, "15510472559");
        mUserServer.deleteUser(loginResult.uid);

    }

    public void testAvatar(){
        int avatarid = -1;
        //getContext().getAssets().
        Assert.assertTrue((avatarid = mUserServer.uploadAvatar(new File("mnt/sdcard/yh.jpg")))>=0);



    }

}
