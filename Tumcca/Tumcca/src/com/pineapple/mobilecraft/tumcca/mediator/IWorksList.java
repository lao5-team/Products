package com.pineapple.mobilecraft.tumcca.mediator;

import android.view.View;
import com.pineapple.mobilecraft.tumcca.data.Works;
import com.pineapple.mobilecraft.tumcca.data.WorksInfo;

import java.util.List;

/**
 * Created by yihao on 15/6/3.
 */
public interface IWorksList {

    public static interface OnScrollListener{

        public void onTop();

        public void onBottom();
    }


    /**
     * 添加作品到头部
     * @param worksInfoList
     */
    public int addWorksHead(List<WorksInfo> worksInfoList);

    /**
     * 添加作品到底部
     * @param worksInfoList
     */
    public int addWorksTail(List<WorksInfo> worksInfoList);

    /**
     * 清空作品
     */
    public void clearWorks();

    /**
     * 添加作品显示列表
     */
    public void addWorksListView(View view);

    /**
     * 打开作品详情
      * @param calligraphy
     */
    public void openWorks(WorksInfo calligraphy);


    public void openAuthor(int authroId);
}
