package com.pineapple.mobilecraft.tumcca.server;

import android.test.AndroidTestCase;
import junit.framework.Assert;

import java.io.File;

/**
 * Created by yihao on 15/6/12.
 */
public class PictureServerTest extends AndroidTestCase{
    PictureServer mPictureServer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mPictureServer = PictureServer.getInstance();
    }

    public void testUpload(){
        UserServer userServer = UserServer.getInstance();
        IUserServer.LoginResult loginResult = userServer.login("100", "100");
        int picId = -1;
        Assert.assertTrue((picId = mPictureServer.uploadPicture(loginResult.token, new File("mnt/sdcard/1.png"))) >= 0);
    }
}
