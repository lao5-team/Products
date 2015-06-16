package com.pineapple.mobilecraft.tumcca.mediator;

import android.graphics.Bitmap;
import com.pineapple.mobilecraft.tumcca.data.User;

/**
 * Created by yihao on 15/6/3.
 */
public interface IUserInfo {
    public void addAvatarView();

    public void addGenderView();

    public void addPseudonymView();

    public void addTitleView();

    public void addIntroView();

    public void addHobbyView();

    public void addForteView();

    public void addRegionView();

    public void setAvatar(int avatarId);

    public void setGender(int genderIndex);

    public void setPseudonym(String pseudonym);

    public void setTitle(String title);

    public void setIntro(String intro);

    public void setHobbies(String[]hobbies);

    public void setForte(String []forte);

    public void setRegion(String country, String province, String city);

    public void setUser(User user);

    public void updateUser(User user);




}
