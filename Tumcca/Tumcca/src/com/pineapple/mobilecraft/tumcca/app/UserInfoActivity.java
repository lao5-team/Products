package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.Utility.Utility;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.mediator.IUserInfo;
import com.pineapple.mobilecraft.tumcca.server.PictureServer;
import com.pineapple.mobilecraft.tumcca.server.UserServer;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.LogRecord;

/**
 * Created by yihao on 15/6/8.
 */
public class UserInfoActivity extends FragmentActivity implements IUserInfo, View.OnClickListener {


    public static final int CROP_REQUEST_CODE = 2;

    private static final int MSG_SHOW_AVATAR = 0;

    private RelativeLayout avatarLay;
    private RelativeLayout phoneLay;
    private RelativeLayout mGenderLay;
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

    private UserInfoPhone mUserInfoPhone;
    private UserInfoGender mUserInfoGender;
    private PhotoChoose userInfoPhotoChoose;
    private UserInfoPhone userInfoPhone;
    private UserInfoEmail userInfoEmail;
    private UserInfoPseudonym userInfoPseudonym;
    private UserInfoIntro userInfoIntro;
    private UserInfoHobby userInfoHobby;
    private UserInfoForte userInfoForte;
    private TextView mTvCountry;
    private TextView mTvProvince;
    private TextView mTvCity;

    private Profile mProfile;

    public Uri mUri;
    public Uri mCropUri;
    private Handler mHandler;
    DisplayImageOptions imageOptions = null;

    public static void startActivity(Activity activity) {
        activity.startActivity(new Intent(activity, UserInfoActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageOptions = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(10))
                .build();
        String token = UserManager.getInstance().getCurrentToken();
        if(TextUtils.isEmpty(token)){
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            mProfile = UserServer.getInstance().getProfile(token);
        }
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case MSG_SHOW_AVATAR:
                        int avatarId = msg.arg1;
                        ImageLoader.getInstance().displayImage(UserServer.getInstance().getAvatarUrl(avatarId), mIvAvatar, imageOptions);
                        break;
                }
            }
        };
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
        avatarLay = (RelativeLayout) findViewById(R.id.avatarLay);
        mIvAvatar = (ImageView) findViewById(R.id.imageView_avatar);
        if(mProfile.avatar!=-1){
//            Picasso.with(this).load(UserServer.getInstance().getAvatarUrl(mProfile.avatar));
            ImageLoader.getInstance().displayImage(UserServer.getInstance().getAvatarUrl(mProfile.avatar), mIvAvatar, imageOptions);
        }
        avatarLay.setOnClickListener(this);
    }

    private void initView() {
        phoneLay = (RelativeLayout) this.findViewById(R.id.layout_phone);
        emailLay = (RelativeLayout) this.findViewById(R.id.layout_email);
        pseudonymLay = (RelativeLayout) this.findViewById(R.id.pseudonymLay);
        introLay = (RelativeLayout) this.findViewById(R.id.introLay);
        hobbyLay = (RelativeLayout) this.findViewById(R.id.hobbyLay);
        forteLay = (RelativeLayout) this.findViewById(R.id.forteLay);

        phoneLay.setOnClickListener(this);
        emailLay.setOnClickListener(this);
        pseudonymLay.setOnClickListener(this);
        introLay.setOnClickListener(this);
        hobbyLay.setOnClickListener(this);
        forteLay.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avatarLay:
                if (userInfoPhotoChoose == null) {
                    userInfoPhotoChoose = new PhotoChoose();
                    mUri = Uri.fromFile(new File(Utility.getTumccaImgPath(this) + "/" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                    userInfoPhotoChoose.setUri(mUri);
                }
                userInfoPhotoChoose.show(getSupportFragmentManager(), "UserInfoPhotoChoose");
                break;
            case R.id.layout_phone:
                if (userInfoPhone == null) {
                    userInfoPhone = new UserInfoPhone();
                }
                userInfoPhone.show(getSupportFragmentManager(), "UserInfoPhone");
                break;
            case R.id.layout_email:
                if (userInfoEmail == null) {
                    userInfoEmail = new UserInfoEmail();
                }
                userInfoEmail.show(getSupportFragmentManager(), "UserInfoEmail");
                break;
            case R.id.pseudonymLay:
                if (userInfoPseudonym == null) {
                    userInfoPseudonym = new UserInfoPseudonym();
                }
                userInfoPseudonym.show(getSupportFragmentManager(), "UserInfoPseudonym");
                break;
            case R.id.introLay:
                if (userInfoIntro == null) {
                    userInfoIntro = new UserInfoIntro();
                }
                userInfoIntro.show(getSupportFragmentManager(), "UserInfoIntro");
                break;
            case R.id.hobbyLay:
                if (userInfoHobby == null) {
                    userInfoHobby = new UserInfoHobby();
                }
                userInfoHobby.show(getSupportFragmentManager(), "UserInfoHobby");
                break;
            case R.id.forteLay:
                if (userInfoForte == null) {
                    userInfoForte = new UserInfoForte();
                }
                userInfoForte.show(getSupportFragmentManager(), "UserInfoForte");
                break;
        }
    }


    public void addGenderView() {
        mTvGender = (TextView) findViewById(R.id.textView_gender);
        if(mProfile.gender == 1){
            mTvGender.setText(getString(R.string.male));
        }
        else{
            mTvGender.setText(getString(R.string.female));
        }
        mGenderLay = (RelativeLayout) findViewById(R.id.layout_gender);
        mGenderLay.setOnClickListener(this);
    }

    @Override
    public void addPseudonymView() {
        mTvPseudonym = (TextView) findViewById(R.id.textView_pseudonym);
        mTvPseudonym.setText(mProfile.pseudonym);
    }

    @Override
    public void addTitleView() {
        mTvTitle = (TextView) findViewById(R.id.textView_title);
    }

    @Override
    public void addIntroView() {
        mTvIntro = (TextView) findViewById(R.id.textView_intro);
        mTvIntro.setText(mProfile.introduction);
    }

    @Override
    public void addHobbyView() {
        mTvHobby = (TextView) findViewById(R.id.textView_hobbies);
        mTvHobby.setText(mProfile.hobbies);
    }

    @Override
    public void addForteView() {
        mTvForte = (TextView) findViewById(R.id.textView_forte);
        mTvForte.setText(mProfile.forte);
    }

    @Override
    public void addRegionView() {
        mTvCountry = (TextView) findViewById(R.id.textView_country);
        mTvCountry.setText(mProfile.country);
        mTvProvince = (TextView) findViewById(R.id.textView_province);
        mTvProvince.setText(mProfile.province);
        mTvCity = (TextView) findViewById(R.id.textView_city);
        mTvCity.setText(mProfile.city);
    }

    @Override
    public void setAvatar(int avatarId) {
        //Picasso.with(this).load(imgUrl).into(mIvAvatar);
        mProfile.avatar = avatarId;
        updateUser(mProfile);
    }

    @Override
    public void setGender(int genderIndex) {
        if (genderIndex == 1) {
            mTvGender.setText(getString(R.string.male));
            mProfile.gender = 1;
        } else if (genderIndex == 0) {
            mTvGender.setText(getString(R.string.female));
            mProfile.gender = 0;
        } else {
            mTvGender.setText(getString(R.string.male));
            mProfile.gender = 1;
        }
        updateUser(mProfile);
    }

    @Override
    public void setPseudonym(String pseudonym) {
        mTvPseudonym.setText(pseudonym);
        mProfile.pseudonym = pseudonym;
        updateUser(mProfile);
    }

    @Override
    public void setTitle(String title) {
        mTvTitle.setText(title);
        mProfile.title = title;
        updateUser(mProfile);
    }

    @Override
    public void setIntro(String intro) {
        mTvIntro.setText(intro);
        mProfile.introduction = intro;
        updateUser(mProfile);

    }

    @Override
    public void setHobbies(String[] hobbies) {
        if (hobbies.length > 0) {
            String str = hobbies[0];
            for (int i = 1; i < hobbies.length; i++) {
                str += "," + hobbies[i];
            }
            mTvHobby.setText(str);
            mProfile.hobbies = str;
            updateUser(mProfile);
        }

    }

    @Override
    public void setForte(String[] forte) {
        if (forte.length > 0) {
            String str = forte[0];
            for (int i = 1; i < forte.length; i++) {
                str += "," + forte[i];
            }
            mTvForte.setText(str);
            mProfile.forte = str;
            updateUser(mProfile);
        }

    }

    @Override
    public void setRegion(String country, String province, String city) {
        if (!TextUtils.isEmpty(country)) {
            mTvCountry.setText(country);
            mProfile.country = country;
        }
        if (!TextUtils.isEmpty(province)) {
            mTvProvince.setText(province);
            mProfile.province = province;
        }
        if (!TextUtils.isEmpty(city)) {
            mTvCity.setText(city);
            mProfile.city = city;
        }
        updateUser(mProfile);
    }

    @Override
    public void setUser(Profile profile) {
        mProfile = profile;
    }

    @Override
    public void updateUser(Profile profile) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PhotoChoose.FROMCAMERA:
                    startPhotoZoom(mUri);
                    break;
                case CROP_REQUEST_CODE:
                    File file = null;
                    try {
                        file = new File(new URI(mCropUri.toString()));
                    }
                    catch (URISyntaxException e)
                    {}
                    final File temp = file;
                    Thread t = new Thread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    String token = UserManager.getInstance().getCurrentToken();
                                    int pictureId = -1;
                                    pictureId = UserServer.getInstance().uploadAvatar(temp);
                                    mProfile.avatar = pictureId;
                                    String result = UserServer.getInstance().updateUser(mProfile, token);
                                    if (!"fail".equals(result))
                                    {
                                        Message msg = mHandler.obtainMessage(MSG_SHOW_AVATAR);
                                        msg.arg1 = pictureId;
                                        mHandler.sendMessage(msg);
                                    }
                                }
                            }
                    );
                    t.start();
                    break;
            }
        }
    }

    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // ���òü�
        intent.putExtra("crop", "true");
        // aspectX aspectY �ǿ�ߵı���
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY �ǲü�ͼƬ���
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("scale", true);// �ڱ�
        intent.putExtra("scaleUpIfNeeded", true);// �ڱ�
        intent.putExtra("return-data", true);
        File capturePath = new File(Utility.getTumccaImgPath(this) + "/" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        mCropUri = Uri.fromFile(capturePath);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropUri);
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }
}