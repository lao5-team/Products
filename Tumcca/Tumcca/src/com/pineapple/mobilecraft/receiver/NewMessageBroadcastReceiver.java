package com.pineapple.mobilecraft.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.pineapple.mobilecraft.DemoApplication;
import com.pineapple.mobilecraft.data.message.MyMessage;
import com.pineapple.mobilecraft.manager.MessageManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NewMessageBroadcastReceiver extends BroadcastReceiver {

//	@Override
//	public void onReceive(Context context, Intent intent) {
//		String username = intent.getStringExtra("from");
//		String msgid = intent.getStringExtra("msgid");
//		// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
//		EMMessage message = EMChatManager.getInstance().getMessage(msgid);
//		// 如果是群聊消息，获取到group id
//		if (message.getChatType() == ChatType.GroupChat) {
//			username = message.getTo();
//		}
//		ActivityData data;
//        if(message.getType() == EMMessage.Type.TXT)
//        {
//        	try {
//				JSONObject obj = new JSONObject(((TextMessageBody)message.getBody()).getMessage());
//            	data = ActivityData.fromJSON(obj);
//    			ActivityManager.getInstance().addActivity(data);
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			abortBroadcast();
//        }
//
//	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String username = intent.getStringExtra("from");
		String msgid = intent.getStringExtra("msgid");
		EMMessage message = EMChatManager.getInstance().getMessage(msgid);
        if(message.getType() == EMMessage.Type.TXT)
        {
        	try {
				JSONObject obj = new JSONObject(((TextMessageBody)message.getBody()).getMessage());
				Log.v(DemoApplication.TAG, "receive message " + obj.toString());
            	MyMessage msg = MyMessage.fromJSON(obj);
    			MessageManager.getInstance().receiveMessage(msg);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			abortBroadcast();
        }
	}

}
