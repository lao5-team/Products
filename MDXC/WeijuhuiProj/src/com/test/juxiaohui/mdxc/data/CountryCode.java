package com.test.juxiaohui.mdxc.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 15/5/5.
 */
public class CountryCode
{
    public String mCountry;
    public String mCode;

    public CountryCode(String country, String code)
    {
        mCountry = country;
        mCode = code;
    }


public static List<CountryCode> getDefaultCodes()
{
//    +95 Myanmar
//        +86 China
//        +65 Singapore
//        +66 Thailand
//        +81 Japan
//        +82 Korea
//        +852 Hong Kong
//    +1 US&Canada
//        +44 United Kindom
//    +61 Australia
    List<CountryCode> codeList = new ArrayList<CountryCode>();

    codeList.add(new CountryCode("Myanmar", "+95"));
    codeList.add(new CountryCode("China", "+86"));
    codeList.add(new CountryCode("Singapore", "+65"));
    codeList.add(new CountryCode("Thailand", "+66"));
    codeList.add(new CountryCode("Japan", "+81"));
    codeList.add(new CountryCode("Korea", "+82"));
    codeList.add(new CountryCode("Hong Kong", "+852"));
    codeList.add(new CountryCode("US&Canada", "+1"));
    codeList.add(new CountryCode("United Kindom", "+44"));
    codeList.add(new CountryCode("Australia", "+61"));
    return codeList;
}

public static List<String> convertCodeListToString(List<CountryCode> codeList)
{
    List<String> listStr = new ArrayList<String>();
    for (CountryCode code:codeList)
    {
        listStr.add(code.mCode + "(" + code.mCountry + ")");
    }
    return listStr;
}

};
