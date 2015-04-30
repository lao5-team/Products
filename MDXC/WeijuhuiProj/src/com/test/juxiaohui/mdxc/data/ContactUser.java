package com.test.juxiaohui.mdxc.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yihao on 15/4/18.
 * 用来表示联系人,既可以给机票使用，也可以给酒店使用
 */
public class ContactUser {
    //public String mFirstName = "";
    //public String mLastName = "";
    //public String mEmail = "";
    //public String mPhoneNumber = "";
    public String contactName = "";
    public String contCountryCode = "";
    public String contPhone = "";
    public String contEmail = "";
    public String recipient = "";
    public String reciPhone = "";
    public String reciAddress = "";
    public String reciPostalCode = "";
    public String pickUpTime = "";

    public static ContactUser NULL = new ContactUser();

    public static JSONObject toJSON(ContactUser contactUser)
    {
        if(null!=contactUser)
        {
            JSONObject obj = new JSONObject();
            try {
//                obj.put("firstName", contactUser.mFirstName);
//                obj.put("lastName", contactUser.mLastName);
                obj.put("contactName", contactUser.contactName);
                obj.put("contEmail", contactUser.contEmail);
                obj.put("contPhone", contactUser.contPhone);
                obj.put("contCountryCode", contactUser.contCountryCode);
                return obj;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static ContactUser fromJSON(JSONObject jsonObject)
    {
        if(null!=jsonObject)
        {
            ContactUser contactUser = new ContactUser();
            try {
                contactUser.contactName = jsonObject.getString("contactName");
                contactUser.contEmail = jsonObject.getString("contEmail");
                contactUser.contPhone = jsonObject.getString("contPhone");
                contactUser.contCountryCode = jsonObject.getString("contCountryCode");
                return contactUser;
            } catch (JSONException e) {
                e.printStackTrace();
                return ContactUser.NULL;
            }
        }
        return ContactUser.NULL;
    }
}
