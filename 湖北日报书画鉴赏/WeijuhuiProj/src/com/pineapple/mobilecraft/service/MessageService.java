package com.pineapple.mobilecraft.service;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobRealTimeData;

import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.app.MessagesActivity;
import com.pineapple.mobilecraft.app.TreasureDetailActivity;
import com.pineapple.mobilecraft.data.comment.TreasureComment;
import com.pineapple.mobilecraft.data.message.MyMessage;
import com.pineapple.mobilecraft.data.message.TreasureMessage;
import com.pineapple.mobilecraft.server.BmobServerManager;
import com.pineapple.mobilecraft.manager.UserManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;

/**
 * 用户消息服务，应该在用户登录成功后启动
 * @author yh
 *
 */
public class MessageService extends Service {

	ArrayList<MyMessage> mMessages = new ArrayList<MyMessage>();
	public class LocalService extends Binder
	{
		public MessageService getService()
		{
			return MessageService.this;
		}
	}
	
	LocalService mBinder = new LocalService();
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public void onCreate()
	{
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				while(true)
				{
					final BmobRealTimeData rtd = new BmobRealTimeData();
					TreasureMessage message = BmobServerManager
							.getInstance()
							.getTreasureMessage(
									UserManager.getInstance().getCurrentUser().mName);	
					List<String> messages= message.getMessages();
					for(String str:messages)
					{
						int type = TreasureMessage.getType(str);
						if(type == TreasureMessage.TYPE_COMMENT)
						{
							NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
							TreasureComment comment = BmobServerManager.getInstance().getTreasureComment(TreasureMessage.getTargetId(str));
							Intent intent = new Intent(MessageService.this, TreasureDetailActivity.class);
							intent.putExtra("treasureID", comment.mTreasureId);
							intent.putExtra("fromNotification", true);
							 PendingIntent pi = PendingIntent.getActivity(MessageService.this, MessagesActivity.REQ_NOTIFICATION, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
							 Notification noti = new Notification.Builder(MessageService.this)
					         .setContentTitle("您有新的评论")
					         .setContentText("@"+comment.mFromUserName + ":" + comment.mContent)
					         .setSmallIcon(R.drawable.icon)
					         .setTicker("您有新的评论")
					         .setDefaults(Notification.DEFAULT_SOUND)
					         .setContentIntent(pi)
					         .setAutoCancel(true)
					         .build();
							 nm.notify("comment", 0, noti);
						}
						 						
					}
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				

					


			}
		});
		t.start();
		
//		Thread t = new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				while(true)
//				{
//					ArrayList<MyMessage> messages;
//					MyUser user = UserManager.getInstance().getCurrentUser();
//					/**/
//					if(null != user)
//					{
//						messages = MyServerManager.getInstance().getMessages(user.mID);
//						if(null!=messages)
//						{
//							mMessages = messages;
//							//MyServerManager.getInstance().removeMessages(user, messages);		
//							NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//							for(MyMessage message : messages)
//							{
//								 Intent intent = new Intent(MessageService.this, MessagesActivity.class);
//								 
//								 intent.putExtra("Message", message.toJSON().toString());
//								 PendingIntent pi = PendingIntent.getActivity(MessageService.this, MessagesActivity.REQ_NOTIFICATION, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
//								 Notification noti = new Notification.Builder(MessageService.this)
//						         .setContentTitle("new Message")
//						         .setContentText(message.toString(MessageService.this))
//						         .setSmallIcon(R.drawable.icon)
//						         .setTicker("new Message")
//						         .setDefaults(Notification.DEFAULT_SOUND)
//						         .setContentIntent(pi)
//						         .setAutoCancel(true)
//						         .build();
//								 nm.notify("test", 0, noti);
//							}
//
//						}						
//					}
//					try {
//						Thread.sleep(30000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}				
//				}				// TODO Auto-generated method stub
//				
//			}
//		});
//		t.start();
		
	}
	
	public ArrayList<MyMessage> getMessages()
	{
		return mMessages;
	}

}
