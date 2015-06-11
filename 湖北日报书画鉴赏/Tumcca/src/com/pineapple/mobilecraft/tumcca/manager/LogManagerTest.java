package com.pineapple.mobilecraft.tumcca.manager;

import android.test.AndroidTestCase;
import android.util.Log;
import com.pineapple.mobilecraft.utils.SyncHttpGet;

import java.util.concurrent.ExecutorService;

/**
 * Created by yihao on 15/6/7.
 */
public class LogManagerTest extends AndroidTestCase{
    public void setUp() throws Exception{

    }

    public void testLog(){
        for(int i=0; i<10; i++){
            LogManager.log("v", "hello", "test log upload " + i, true);
        }


        SyncHttpGet<String> get = new SyncHttpGet<String>("http://120.26.202.114/api/logs/android/size/10", null) {
            @Override
            public String postExcute(String result) {
                Log.v("tumcca", result);
                return null;
            }
        };
        get.execute();

    }
}
