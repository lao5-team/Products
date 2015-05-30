package com.pineapple.mobilecraft.mediator;

import com.pineapple.mobilecraft.data.Treasure;

import java.util.List;

/**
 * Created by yihao on 15/3/13.
 * 用来显示所有宝物
 */
public interface IAllTreasureMediator {
    public void setTreasures(List<Treasure> treasures);

    public void addTreasuresView();

    public void onSelectOneTreasure(Treasure treasure);
}
