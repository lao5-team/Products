package com.test.juxiaohui.mdxc.adapter;

import com.test.juxiaohui.mdxc.data.FlightOrder;
import com.test.juxiaohui.widget.CommonAdapter;
import com.test.juxiaohui.widget.IAdapterItem;

import java.util.List;

/**
 * Created by yihao on 15/4/27.
 */
public class FlightOrderAdapter extends CommonAdapter<FlightOrder> {
    public FlightOrderAdapter(List<FlightOrder> dataList, IAdapterItem<FlightOrder> item) {
        super(dataList, item);
    }
}
