package com.pineapple.mobilecraft.tumcca.mediator;

import android.view.View;
import com.pineapple.mobilecraft.tumcca.data.Picture;

import java.util.List;

/**
 * Created by yihao on 15/6/3.
 */
public interface ICalligraphyCreate {

    public void addDescribeView(View view);

    public void addPictureChooseView(View view);

    public void addPictureDisplayView(View view);

    public void setPictures(List<Picture> pictureList);

    public void submit();

    public void returnBack();
}
