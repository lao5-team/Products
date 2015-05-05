package com.test.juxiaohui.mdxc.manager;

import android.test.AndroidTestCase;
import com.test.juxiaohui.common.data.User;
import com.test.juxiaohui.mdxc.data.ContactUser;
import com.test.juxiaohui.mdxc.data.Passenger;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 15/4/19.
 */
public class UserManagerTest extends AndroidTestCase {
    UserManager mUserManager;
    protected void setUp() throws Exception {
        super.setUp();
        mUserManager = UserManager.getInstance();
    }

    public void testSetContactUser()
    {
        mUserManager.logout();

        ContactUser contactUser;
        contactUser = mUserManager.getContactUser();
        Assert.assertEquals(contactUser, ContactUser.NULL);

        mUserManager.login("+86", "15510472558", "123456");
        contactUser  = new ContactUser();
        contactUser.contactName = "Yi";
        //contactUser.mLastName = "Hao";
        contactUser.contEmail = "yhchinabest@163.com";
        contactUser.contPhone = "15510472558";
        mUserManager.setContactUser(contactUser);

        contactUser = mUserManager.getContactUser();
        Assert.assertEquals("Yi", contactUser.contactName);

        mUserManager.logout();
        mUserManager.login("+86", "15510472558", "123456");
        contactUser = mUserManager.getContactUser();
        Assert.assertEquals("Yi", contactUser.contactName);
    }

    public void testSetPassenger()
    {
        mUserManager.logout();
        Assert.assertNotNull(mUserManager.getPassengerList());

        mUserManager.login("+86", "15510472558", "123456");
        List<Passenger> listPassenger = new ArrayList<Passenger>();
        Passenger passenger = new Passenger();
        passenger.mId = "1";
        passenger.mName = "a";
        listPassenger.add(passenger);
        passenger = new Passenger();
        passenger.mId = "2";
        passenger.mName = "b";
        listPassenger.add(passenger);

        mUserManager.setPassengerList(listPassenger);
        Assert.assertEquals(mUserManager.getPassengerById("1").mName, "a");

        mUserManager.logout();
        Assert.assertEquals(mUserManager.getPassengerList().size(), 0);

        mUserManager.login("+86", "15510472558", "123456");
        Assert.assertEquals(mUserManager.getPassengerList().size(), 2);
        Assert.assertEquals(mUserManager.getPassengerById("1").mName, "a");


    }

    public void testLogin()
    {
        Assert.assertEquals(mUserManager.login("+86", "15510472558", "123456"), UserManager.LOGIN_SUCCESS);

        Assert.assertEquals(mUserManager.login("+86", "15510472558", "123456"), UserManager.ALREADY_LOGIN);

        Assert.assertEquals(mUserManager.logout(), UserManager.LOGOUT_SUCCESS);

        Assert.assertEquals(mUserManager.login("+86", "15510472558", "1234567"), UserManager.INVALID_USERNAME_PASSWORD);
    }

    public void testGetUser()
    {
       Assert.assertTrue(User.NULL!=mUserManager.getUserInfo("+86", "15510472558"));
    }

}
