package com.pineapple.mobilecraft.tumcca.mediator;

/**
 * Created by yihao on 15/6/3.
 */
public interface ICalligraphyDetail {
    public void addTitleView();

    public void addDescribeView();

    public void addCreatorView();

    public void addPictureView();

    public void addBackView();

    public void addCommentView();

    /**
     * 打开创作者的详情
     */
    public void openCreator();

    /**
     * 打开图片详情
     * @param id
     */
    public void openPicture(String id);

    /**
     *
     * @param id User 的id，如果为null则直接对作品进行评论
     */
    public void setCommentTarget(String id);

    /**
     * 发表评论
     */
    public void submitComment();

    /**
     * 返回上一级页面
     */
    public void returnToPrev();


}
