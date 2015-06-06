package com.pineapple.mobilecraft.utils;

/**
 * Created by yihao on 15/3/10.
 */
public abstract class SyncCallback<T> {
    private Object mLock = new Object();
    T mResult = null;
    public T executeBegin()
    {
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
//        t.start();
//
//        try {
//            Thread.currentThread().sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        executeImpl();
        synchronized (mLock)
        {
            try {
                mLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mResult;
    }

    public abstract void executeImpl();

    /**
     * 记得在executeImpl的回调中调用onResult方法，否则会卡住
     * @param result
     */
    protected void onResult(T result)
    {
        mResult = result;
        synchronized (mLock)
        {
            mLock.notify();
        }
    }


}
