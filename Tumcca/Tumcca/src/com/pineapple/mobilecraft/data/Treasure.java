package com.pineapple.mobilecraft.data;

import cn.bmob.v3.BmobObject;

import java.util.ArrayList;

/**
 * Created by yihao on 15/3/12.
 */
public class Treasure extends BmobObject {

    public static Treasure NULL = new Treasure();


    public String mName = "";
    public String mDesc = "";
    public String mOwnerName = "";
    public ArrayList<String> mImgs = new ArrayList<String>();
    public ArrayList<String> mCommentIds = new ArrayList<String>();
    public ArrayList<String> mIdentifies = new ArrayList<String>();
    public Boolean mIsIdentified = false;

}
