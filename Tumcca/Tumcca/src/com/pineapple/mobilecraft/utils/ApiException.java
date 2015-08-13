package com.pineapple.mobilecraft.utils;

/**
 * Created by yihao on 8/13/15.
 */
public class ApiException extends RuntimeException {
    public int mErrorCode;
    //public String mErrorMessage;

    public ApiException(int errorCode){
        mErrorCode = errorCode;
    }
}
