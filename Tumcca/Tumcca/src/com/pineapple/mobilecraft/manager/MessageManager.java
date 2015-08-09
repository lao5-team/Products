package com.pineapple.mobilecraft.manager;

import java.util.ArrayList;

import com.pineapple.mobilecraft.server.BmobServerManager;
import junit.framework.Assert;
import android.util.Log;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.exceptions.EaseMobException;
import com.pineapple.mobilecraft.TumccaApplication;
import com.pineapple.mobilecraft.data.message.MyMessage;
import com.pineapple.mobilecraft.data.message.TreasureMessage;

/**
 * @author yh
 * 微聚会自定义消息发送和接受的管理者
 * @see MyMessage
 */
public class MessageManager {

	private static MessageManager mInstance = null;
	private ArrayList<MessageListener> mMsgListeners;
	
	public static abstract class MessageListener
	{
		public String filterType = null;
		abstract public void onReceiveMessage(MyMessage msg);
	}
	
	private MessageManager()
	{
		mMsgListeners = new ArrayList<MessageManager.MessageListener>();
	}
	
	public static MessageManager getInstance()
	{
		if(null == mInstance)
		{
			mInstance = new MessageManager();
		}
		return mInstance;
	}
	
	public boolean sendMessagetoSingle(MyMessage msg, String username)
	{
		Assert.assertNotNull(username);
		Assert.assertNotNull(msg);
		EMConversation conversation = EMChatManager.getInstance().getConversation(username);
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
		String str = msg.toJSON().toString();
		Log.v(TumccaApplication.TAG, "Message to single: " + str);
		TextMessageBody txtBody = new TextMessageBody(str);
		message.addBody(txtBody);
		message.setReceipt(username);
		conversation.addMessage(message);
		try {
			EMChatManager.getInstance().sendMessage(message);
			return true;
		} catch (EaseMobException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * @param msg
	 * @param groupID 
	 * @return
	 */
	public boolean sendMessagetoGroup(MyMessage msg, String groupID)
	{
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
		message.setChatType(ChatType.GroupChat);
		String str = msg.toJSON().toString();
		Log.v(TumccaApplication.TAG, "Message to group ID " + groupID + " " + str);
		TextMessageBody txtBody = new TextMessageBody(str);
		message.addBody(txtBody);
		message.setReceipt(groupID);
		try {
			EMChatManager.getInstance().sendMessage(message);
			return true;
		} catch (EaseMobException e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	public void receiveMessage(MyMessage msg)
	{
		for(MessageListener listener:mMsgListeners)
		{
			if(msg.mType.equals(listener.filterType))
			{
				listener.onReceiveMessage(msg);
			}
		}
	}
	
	public void addMessageListener(MessageListener listener)
	{
		mMsgListeners.add(listener);
	}
	
	public void removeMessageListener(MessageListener listener)
	{
		mMsgListeners.remove(listener);
	}
	
	public String sendTreasureMessage(TreasureMessage message)
	{
		return BmobServerManager.getInstance().sendMessage(message);
	}
	
}
