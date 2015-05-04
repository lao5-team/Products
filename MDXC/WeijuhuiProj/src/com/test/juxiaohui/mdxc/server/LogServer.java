package com.test.juxiaohui.mdxc.server;

import cn.bmob.v3.Bmob;
import com.test.juxiaohui.DemoApplication;
import com.test.juxiaohui.mdxc.data.LogData;

/**
 * Created by yihao on 15/4/30.
 */
public class LogServer {
    private static LogServer mInstance = null;

    public static LogServer getInstance()
    {
        if(null == mInstance)
        {
            mInstance = new LogServer();
        }
        return mInstance;
    }

    public void uploadLog(String tag, LogData log)
    {
        log.save(DemoApplication.applicationContext);
    }

    private LogServer()
    {
        Bmob.initialize(DemoApplication.applicationContext, "2bcee996bebe106a29ac7c8cde15078a");
    }


}
