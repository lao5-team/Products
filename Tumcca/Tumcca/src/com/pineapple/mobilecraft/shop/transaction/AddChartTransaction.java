package com.pineapple.mobilecraft.shop.transaction;

import com.pineapple.mobilecraft.shop.data.Chart;

/**
 * Created by yihao on 15/2/6.
 */
public class AddChartTransaction
{
    private String mId;
    private int mCount;
    public AddChartTransaction(String id, int count)
    {
        mId = id;
        mCount = count;
    }

    public void execute()
    {
        Chart.getInstance().addGoods(mId, mCount);
    }
}
