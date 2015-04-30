package com.test.juxiaohui.utils;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import com.test.juxiaohui.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

public class CrashLogHandler implements UncaughtExceptionHandler {
	
	private Activity mActivity = null;
	
	public static CrashLogHandler createHandler(Activity activity)
	{
		
		CrashLogHandler handler = new CrashLogHandler();
		handler.mActivity = activity;
		Thread.setDefaultUncaughtExceptionHandler(handler); 
		return handler;
	}
	
	@Override
	public void uncaughtException(Thread arg0, Throwable arg1) {
		final Throwable throwable = arg1;
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(
					writer);
			throwable.printStackTrace(printWriter);
			String[] subs = {"yhchinabest@163.com"};
			Intent data=new Intent(Intent.ACTION_SEND); 
			data.setType("plain/text");
			data.putExtra(Intent.EXTRA_EMAIL, subs); 
			data.putExtra(Intent.EXTRA_SUBJECT, "应用崩溃"); 
			data.putExtra(Intent.EXTRA_TEXT, writer.toString()); 
			mActivity.startActivity(Intent.createChooser(data, mActivity.getString(R.string.application_crashed))); 
			mActivity.finish();
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(10);
//		}
//		else
//		{
//			Thread.getDefaultUncaughtExceptionHandler().uncaughtException(thread, throwable);    
//		}
	}

}
