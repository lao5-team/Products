package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.data.User;
import com.pineapple.mobilecraft.tumcca.mediator.IUserInfo;
import com.squareup.picasso.Picasso;
import org.xbill.DNS.RelativeNameException;

/**
 * Created by yihao on 15/6/8.
 */
public class UserInfoActivity extends FragmentActivity implements IUserInfo, View.OnClickListener{
    private RelativeLayout avatarLay;
    private RelativeLayout phoneLay;
    private RelativeLayout emailLay;
    private RelativeLayout pseudonymLay;
    private RelativeLayout hobbyLay;
    private RelativeLayout introLay;
    private RelativeLayout forteLay;
    private RelativeLayout regionLay;
    private ImageView mIvAvatar;
    private TextView mTvGender;
    private TextView mTvPseudonym;
    private TextView mTvTitle;
    private TextView mTvIntro;
    private TextView mTvHobby;
    private TextView mTvForte;

    private UserInfoPhone userInfoPhone;
    private UserInfoEmail userInfoEmail;
    private UserInfoPseudonym userInfoPseudonym;
    private UserInfoIntro userInfoIntro;
    private UserInfoHobby userInfoHobby;
    private UserInfoForte userInfoForte;

    private TextView mTvCountry;
    private TextView mTvProvince;
    private TextView mTvCity;
    private User mUser;

    public static void startActivity(Activity activity){
        activity.startActivity(new Intent(activity, UserInfoActivity.class));

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        initView();
        addAvatarView();
        addGenderView();
        addPseudonymView();
        addTitleView();
        addIntroView();
        addHobbyView();
        addForteView();
        addRegionView();
    }



    @Override
    public void addAvatarView() {
        avatarLay = (RelativeLayout)findViewById(R.id.avatarLay);
        mIvAvatar = (ImageView)findViewById(R.id.imageView_avatar);
        avatarLay.setOnClickListener(this);
    }

    private void initView() {
        phoneLay = (RelativeLayout)this.findViewById(R.id.layout_phone);
        emailLay = (RelativeLayout)this.findViewById(R.id.layout_email);
        pseudonymLay = (RelativeLayout)this.findViewById(R.id.pseudonymLay);
        introLay = (RelativeLayout)this.findViewById(R.id.introLay);
        hobbyLay= (RelativeLayout)this.findViewById(R.id.hobbyLay);
        forteLay = (RelativeLayout)this.findViewById(R.id.forteLay);

        phoneLay.setOnClickListener(this);
        emailLay.setOnClickListener(this);
        pseudonymLay.setOnClickListener(this);
        introLay.setOnClickListener(this);
        hobbyLay.setOnClickListener(this);
        forteLay.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.avatarLay:

                break;
            case R.id.layout_phone:
                if(userInfoPhone == null)
                {
                    userInfoPhone = new UserInfoPhone();
                }
                userInfoPhone.show(getSupportFragmentManager(), "UserInfoPhone");
                break;
            case R.id.layout_email:
                if(userInfoEmail == null)
                {
                    userInfoEmail = new UserInfoEmail();
                }
                userInfoEmail.show(getSupportFragmentManager(), "UserInfoEmail");
                break;
            case R.id.pseudonymLay:
                if(userInfoPseudonym == null)
                {
                    userInfoPseudonym = new UserInfoPseudonym();
                }
                userInfoPseudonym.show(getSupportFragmentManager(), "UserInfoPseudonym");
                break;
            case R.id.introLay:
                if(userInfoIntro == null)
                {
                    userInfoIntro = new UserInfoIntro();
                }
                userInfoIntro.show(getSupportFragmentManager(), "UserInfoIntro");
                break;
            case R.id.hobbyLay:
                if(userInfoIntro == null)
                {
                    userInfoIntro = new UserInfoIntro();
                }
                userInfoIntro.show(getSupportFragmentManager(), "UserInfoIntro");
                break;
            case R.id.forteLay:
                if(userInfoForte == null)
                {
                    userInfoForte = new UserInfoForte();
                }
                userInfoForte.show(getSupportFragmentManager(), "UserInfoIntro");
                break;
        }
    }


    public void addGenderView() {
        mTvGender = (TextView)findViewById(R.id.textView_gender);
    }

    @Override
    public void addPseudonymView() {
        mTvPseudonym = (TextView)findViewById(R.id.textView_pseudonym);
    }

    @Override
    public void addTitleView() {
        mTvTitle = (TextView)findViewById(R.id.textView_title);
    }

    @Override
    public void addIntroView() {
        mTvIntro = (TextView)findViewById(R.id.textView_intro);
    }

    @Override
    public void addHobbyView() {
        mTvHobby = (TextView)findViewById(R.id.textView_hobbies);
    }

    @Override
    public void addForteView() {
        mTvForte = (TextView)findViewById(R.id.textView_forte);
    }

    @Override
    public void addRegionView() {
        mTvCountry = (TextView)findViewById(R.id.textView_country);
        mTvProvince = (TextView)findViewById(R.id.textView_province);
        mTvCity = (TextView)findViewById(R.id.textView_city);
    }

    @Override
    public void setAvatar(int avatarId) {
        //Picasso.with(this).load(imgUrl).into(mIvAvatar);
        mUser.avatar = avatarId;
        updateUser(mUser);
    }

    @Override
    public void setGender(int genderIndex) {
        if(genderIndex == 1){
            mTvGender.setText(getString(R.string.male));
            mUser.gender = 1;
        }
        else if(genderIndex == 0){
            mTvGender.setText(getString(R.string.female));
            mUser.gender = 0;
        }
        else{
            mTvGender.setText(getString(R.string.male));
            mUser.gender = 1;
        }
        updateUser(mUser);
    }

    @Override
    public void setPseudonym(String pseudonym) {
        mTvPseudonym.setText(pseudonym);
        mUser.pseudonym = pseudonym;
        updateUser(mUser);
    }

    @Override
    public void setTitle(String title) {
        mTvTitle.setText(title);
        mUser.title = title;
        updateUser(mUser);
    }

    @Override
    public void setIntro(String intro) {
        mTvIntro.setText(intro);
        mUser.introduction = intro;
        updateUser(mUser);

    }

    @Override
    public void setHobbies(String[] hobbies) {
        if(hobbies.length>0){
            String str = hobbies[0];
            for (int i=1; i<hobbies.length; i++){
                str += "," + hobbies[i];
            }
            mTvHobby.setText(str);
            mUser.hobbies = str;
            updateUser(mUser);
        }

    }

    @Override
    public void setForte(String[] forte) {
        if(forte.length>0){
            String str = forte[0];
            for (int i=1; i<forte.length; i++){
                str += "," + forte[i];
            }
            mTvForte.setText(str);
            mUser.forte = str;
            updateUser(mUser);
        }

    }

    @Override
    public void setRegion(String country, String province, String city) {
        if(!TextUtils.isEmpty(country)){
            mTvCountry.setText(country);
            mUser.country = country;
        }
        if(!TextUtils.isEmpty(province)){
            mTvProvince.setText(province);
            mUser.province = province;
        }
        if(!TextUtils.isEmpty(city)){
            mTvCity.setText(city);
            mUser.city = city;
        }
        updateUser(mUser);
    }

    @Override
    public void setUser(User user) {
        mUser = user;
    }

    @Override
    public void updateUser(User user) {

    }
}