package com.test.juxiaohui.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;
import android.test.InstrumentationTestRunner;
import android.util.Log;

import com.test.juxiaohui.data.ActivityData;
import com.test.juxiaohui.data.MyUser;
import com.test.juxiaohui.data.DianpingDao.ComplexBusiness;


public class ActivityDataTest extends AndroidTestCase {

	//String testString = "";
	String testString;
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	

	
	public void testFromToJSON()
	{
		ComplexBusiness testCB = new ComplexBusiness();
		testCB.mName = "东来顺";
		testCB.mImgUrl = "http://i2.dpfile.com//pc//e6801a8a0b89fa2dd93e582c69d2e7cd(700x700)//thumb.jpg";
		testCB.mPhoneNumber = "12345678";
		
		MyUser testUser = new MyUser();
		testUser.mName = "testCreator";
		
		ArrayList<MyUser> users = new ArrayList<MyUser>();
		MyUser testGuest = new MyUser();
		testGuest.mName = "test0";
		users.add(testGuest);
		
		testGuest = new MyUser();
		testGuest.mName = "test1";
		users.add(testGuest);
		
		testGuest = new MyUser();
		testGuest.mName = "test2";
		users.add(testGuest);	
		
		try {
			ActivityData data = new ActivityData.ActivityBuilder().setBeginTime(new SimpleDateFormat(ActivityData.dataPattern).parse("2014年9月20日16时")
					).setComplexBusiness(testCB).setCreator(testUser).setUsers(users).create();
			
			testString = ActivityData.toJSON(data).toString();
			
			Log.v("weijuhui", testString);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			ActivityData data = ActivityData.fromJSON(new JSONObject(testString));
			assertNotNull(data);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
