package com.pineapple.mobilecraft.utils;

import java.util.regex.Pattern;

/**
 * Created by yihao on 15/6/7.
 */
public class PATextUtils  {
    public static boolean isValidPhoneNumber(String str){
        return Pattern.compile("^[1][3,4,5,8][0-9]{9}$").matcher(str).matches(); // 验证手机号
    }

    public static boolean isValidEmail(String str){
        return Pattern.compile("[\\w]+@[\\w]+\\.[\\w]+").matcher(str).matches();
    }
}
