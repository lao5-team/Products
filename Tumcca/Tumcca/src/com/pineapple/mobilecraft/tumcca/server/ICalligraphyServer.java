package com.pineapple.mobilecraft.tumcca.server;

import com.pineapple.mobilecraft.tumcca.data.Works;

import java.util.List;

/**
 * Created by yihao on 15/5/28.
 */
public interface ICalligraphyServer {
    public List<String> getCalligraphyList(String type, String firstId, int count);

    public String uploadCalligraphy(Works calligraphy);

    public Works getCalligraphy(String id);

    public List<Works> getCalligraphyList(List<String> idList);



}
