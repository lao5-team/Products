package com.pineapple.mobilecraft.tumcca.server;

import android.test.AndroidTestCase;
import android.util.Log;
import com.pineapple.mobilecraft.tumcca.data.Notification;
import junit.framework.Assert;

import java.util.List;

/**
 * Created by yihao on 15/6/24.
 */
public class NotificationServerTest extends AndroidTestCase {

    public void testNotify(){
        IUserServer.LoginResult result = UserServer.getInstance().login("999", "999");
        NotificationServer.sendFollowNotify(result.token, 118, 119);

        UserServer.getInstance().logout(118, result.token);

        result = UserServer.getInstance().login("998", "998");
        List<Notification> listNotification = NotificationServer.getFollowNotification(result.token);
        Assert.assertTrue(listNotification.size()>0);

        UserServer.getInstance().logout(119, result.token);
        result = UserServer.getInstance().login("999", "999");
        NotificationServer.sendUnFollowNotify(result.token, 118, 119);

    }
}
