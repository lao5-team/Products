package com.pineapple.mobilecraft.tumcca.server;

import com.pineapple.mobilecraft.tumcca.data.Calligraphy;

import java.util.List;

/**
 * Created by yihao on 15/5/28.
 */
public interface ICalligraphyServer {
    public List<String> getCalligraphyList(String type, String firstId, int count);

    public String uploadCalligraphy(Calligraphy calligraphy);

    public Calligraphy getCalligraphy(String id);

    public List<Calligraphy> getCalligraphyList(List<String> idList);



}
