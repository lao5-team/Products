package com.test.juxiaohui.common.data;

import org.json.JSONObject;

/**
 * Created by yihao on 15/3/4.
 */
public class User {
    protected String mUsername = "";
    protected String mPassword = "";
    protected String mSex = "";
    protected String mNickname = "";
    protected String mPhoneNumber = "";
    protected String mId = "18";
    public static User NULL = new User()
    {

    };


    public String getDisplayName()
    {
        return mNickname;
    }

    public String getInnerName()
    {
        return mUsername;
    }

    public void setUsername(String username)
    {
        mUsername = username;
    }

    public void setPassword(String password)
    {
        mPassword = password;
    }

    public String getPassword()
    {
        return mPassword;
    }

    public void setNickname(String nickname)
    {
        mNickname = nickname;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        mPhoneNumber = phoneNumber;
    }

    public String getPhoneNumber()
    {
        return mPhoneNumber;
    }

    public void setSex(String sex)
    {
        mSex = sex;
    }

    public String getSex()
    {
        return mSex;
    }

    public JSONObject toJSON()
    {
        return null;
    }

    public User fromJSON(JSONObject json)
    {
        return null;
    }

    public String getId()
    {
        return mId;
    }

    public void setId(String id)
    {
        mId = id;
    }


}
