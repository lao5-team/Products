package com.pineapple.mobilecraft.tumcca.mediator;

import com.pineapple.mobilecraft.tumcca.data.Picture;

import java.util.List;

/**
 * Created by yihao on 15/6/3.
 */
public interface ICalligraphyCreate {

    public void addTitleView();

    public void addDescribeView();

    public void addCreatorView();

    public void addPictureChooseView();

    public void addPictureDisplayView();

    public void setPictures(List<Picture> pictureList);

    public void submit();

    public void returnBack();
}
