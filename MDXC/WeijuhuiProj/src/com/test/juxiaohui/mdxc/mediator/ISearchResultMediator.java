package com.test.juxiaohui.mdxc.mediator;


import java.util.List;

import com.test.juxiaohui.mdxc.data.FlightData;
import com.test.juxiaohui.mdxc.data.FlightSearchRequest;

/**
 * Created by yihao on 15/3/13.
 *
 */
public interface ISearchResultMediator {
	public void setRequest(FlightSearchRequest request);
	
    public void setResult(List<FlightData> results);

    public void search();
    
    public void addResultView();

    public void selectOneFlight(FlightData data);
}
