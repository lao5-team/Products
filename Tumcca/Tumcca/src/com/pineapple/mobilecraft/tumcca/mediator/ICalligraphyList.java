package com.pineapple.mobilecraft.tumcca.mediator;

import com.pineapple.mobilecraft.tumcca.data.Works;

import java.util.List;

/**
 * Created by yihao on 15/6/3.
 */
public interface ICalligraphyList {

    public void setCalligraphyList(List<Works> calligraphyList);

    public void addCalligraphyListView();

    public void openCalligraphy(Works calligraphy);
}
