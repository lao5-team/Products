package com.pineapple.mobilecraft.manager;

import com.pineapple.mobilecraft.data.Treasure;
import com.pineapple.mobilecraft.data.comment.TreasureComment;
import com.pineapple.mobilecraft.data.message.TreasureMessage;
import com.pineapple.mobilecraft.server.BmobServerManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yihao on 15/3/12.
 */
public class TreasureManager {

    public static TreasureManager mInstance = null;
    public static TreasureManager getInstance()
    {
        if(null == mInstance)
        {
            mInstance = new TreasureManager();
        }

        return mInstance;
    }

    public String uploadTreasure(Treasure treasure){
        return BmobServerManager.getInstance().uploadTreasure(treasure);
    }

    /**
     * 通过id获取宝物
     * @param ids id数组
     * @return
     */
    public List<Treasure> getTreasuresByIds(List<String> ids)
    {
        return BmobServerManager.getInstance().getTreasuresByIds(ids);
    }

    public Treasure getTreasureById(String id)
    {
        ArrayList<String> ids = new ArrayList<String>();
        ids.add(id);
        List<Treasure> treasures = BmobServerManager.getInstance().getTreasuresByIds(ids);
        return treasures.get(0);
    }

    /**
     * 返回所有宝物id
     * @return
     */
    public List<Treasure> getAllTreasureIds()
    {
        return BmobServerManager.getInstance().getAllTreasure();
    }

    /**
     *
     * @param username 用户名
     * @return
     */
    public List<String> getUserTreasureIds(String username)
    {
        return null;
    }

    /**
     * 获取所有未鉴定的宝物id
     * @return
     */
    public List<String> getUnidentifiedIds()
    {
        return null;
    }

    /**
     * 对宝物进行鉴定
     * @param comment 鉴宝信息
     */
    public void identify(TreasureComment comment)
    {

    }

    /**
     * 对宝物进行评价
     * @param comment 评论信息
     */
    public void sendComment(TreasureComment comment)
    {
        BmobServerManager.getInstance().sendTreasureComment(comment);
        Treasure treasure = getTreasureById(comment.mTreasureId);
        TreasureMessage messageToOwner, messageToOther;
        
        if(!treasure.mOwnerName.equals(UserManager.getInstance().getCurrentUser().mName))
        {
        	messageToOwner = BmobServerManager.getInstance().getTreasureMessage(treasure.mOwnerName);
        	messageToOwner.addMessage(TreasureMessage.TYPE_COMMENT, comment.getObjectId());
        	BmobServerManager.getInstance().sendMessage(messageToOwner);
        }
        if(comment.mReplayTo.length()>0)
        {
        	messageToOther = BmobServerManager.getInstance().getTreasureMessage(comment.mReplayTo);
        	messageToOther.addMessage(TreasureMessage.TYPE_COMMENT, comment.getObjectId());
        	BmobServerManager.getInstance().sendMessage(messageToOther);
        }
                
    }
    
    public TreasureMessage getUserMessage(String username)
    {
    	return BmobServerManager.getInstance().getTreasureMessage(username);
    }
    
    public void clearUserMessage(String username)
    {
    	TreasureMessage message = BmobServerManager.getInstance().getTreasureMessage(username);
    	message.clearMessage();
    	BmobServerManager.getInstance().sendMessage(message);
    }
    
    


}
