package com.pineapple.mobilecraft.data.comment;

import cn.bmob.v3.BmobObject;

/**
 * Created by yihao on 15/3/12.
 */
public class TreasureComment extends BmobObject {
    public static int TYPE_COMMENT = 0;
    public static int TYPE_IDENTIFY = 1;
    public String mFromUserName = ""; //评论人
    public int mType = 0;
    public String mContent = ""; //评论内容
    public String mReplayTo = ""; //评论的用户名字
    public String mTreasureId = ""; //评论的宝物id
    public Boolean mIdentifyResult = false;
    
    public static TreasureComment NULL = new TreasureComment();

    public static TreasureComment createUserComment()
    {
        TreasureComment comment  = new TreasureComment();
        comment.mType = TYPE_COMMENT;
        return comment;
    }

    public static TreasureComment createProfComment()
    {
        TreasureComment comment  = new TreasureComment();
        comment.mType = TYPE_IDENTIFY;
        return comment;
    }

}
