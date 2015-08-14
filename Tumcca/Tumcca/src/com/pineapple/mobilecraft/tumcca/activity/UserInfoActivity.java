package com.pineapple.mobilecraft.tumcca.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.Constants;
import com.pineapple.mobilecraft.tumcca.manager.PictureManager;
import com.pineapple.mobilecraft.tumcca.utility.Utility;
import com.pineapple.mobilecraft.tumcca.data.Profile;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.manager.WorksManager;
import com.pineapple.mobilecraft.tumcca.mediator.IUserInfo;
import com.pineapple.mobilecraft.tumcca.server.UserServer;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by yihao on 15/6/8.
 */
public class UserInfoActivity extends FragmentActivity implements IUserInfo, View.OnClickListener {


    public static final int CROP_REQUEST_CODE = 2;

    private static final int MSG_SHOW_AVATAR = 0;
    public static final int MSG_CHANGE_PHONE = 1;
    public static final int MSG_CHANGE_EMAIL = 2;
    public static final int MSG_CHANGE_PSEUDONYM = 3;
    public static final int MSG_CHANGE_INTRO = 4;
    public static final int MSG_CHANGE_HOBBY = 5;
    public static final int MSG_CHANGE_REGION = 6;
    public static final int MSG_CHANGE_FORTE = 7;
    public static final int MSG_CHANGE_SEX = 8;
    public static final int MSG_CHANGE_TITLE = 9;

    private RelativeLayout avatarLay;
    private RelativeLayout phoneLay;
    private RelativeLayout mGenderLay;
    private RelativeLayout emailLay;
    private RelativeLayout pseudonymLay;
    private RelativeLayout titleLay;
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
    private AvatarChoose userInfoAvatarChoose;
    private UserInfoPhone userInfoPhone;
    private UserInfoEmail userInfoEmail;
    private UserInfoPseudonym userInfoPseudonym;
    private UserInfoTitle userInfoTitle;
    private UserInfoIntro userInfoIntro;
    private UserInfoHobby userInfoHobby;
    private UserInfoForte userInfoForte;
    private UserCity userCity;
    private TextView mTvCountry;
    private TextView mTvProvince;
    private TextView mTvCity;
    private TextView mTvLogout;

    private long mAuthorId;
    private Profile mProfile;

    public Uri mUri;
    public Uri mCropUri;
    public Handler mHandler;
    public static final int RESULT_LOGOUT = 1;

    public static void startActivity(Activity activity, long authorId, int requestCode) {

        Intent intent = new Intent(activity, UserInfoActivity.class);
        intent.putExtra("authorId", authorId);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        final ActionBar actionBar = getActionBar();
        if(null!=actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mAuthorId = getIntent().getLongExtra("authorId", Constants.INVALID_AUTHORD_ID);
        if(mAuthorId==Constants.INVALID_AUTHORD_ID){
            Toast.makeText(this, getString(R.string.please_login), Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            mProfile = UserServer.getInstance().getUserProfile(mAuthorId);
        }
        initHandler();
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
        addLogoutView();
        refreshData();


    }

    private void addLogoutView(){

        mTvLogout = (TextView)findViewById(R.id.btn_logout);
        mTvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(UserInfoActivity.this);
                pd.setCanceledOnTouchOutside(false);
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                    }
                });
                pd.setMessage(getString(R.string.logouting));
                pd.show();
                Thread thread =  new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.currentThread().sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        UserManager.getInstance().logout();
                        WorksManager.getInstance().clearCache();
                        setResult(RESULT_LOGOUT);
                        pd.dismiss();
                        finish();
                    }
                });
                thread.start();

            }
        });
        if(mAuthorId!=UserManager.getInstance().getCurrentUserId()){
            mTvLogout.setVisibility(View.GONE);
        }
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = null;
                switch (msg.what)
                {
                    case MSG_SHOW_AVATAR:
                        int avatarId = msg.arg1;
                        mProfile.avatar = avatarId;
                        PictureManager.getInstance().displayAvatar(mIvAvatar, mProfile.avatar, 24);
                        updateUser(mProfile);
                        //ImageLoader.getInstance().displayImage(UserServer.getInstance().getAvatarUrl(avatarId), mIvAvatar, imageOptions);
                        break;
                    case MSG_CHANGE_PSEUDONYM:
                        bundle = msg.getData();
                        String strPseudonym = bundle.getString("pseudonym");
                        mProfile.pseudonym = strPseudonym;
                        updateUser(mProfile);
                        break;
                    case MSG_CHANGE_INTRO:
                        bundle = msg.getData();
                        String intro = bundle.getString("intro");
                        mProfile.introduction = intro;
                        updateUser(mProfile);
                        break;
                    case MSG_CHANGE_HOBBY:
                        bundle = msg.getData();
                        String hobby = bundle.getString("hobby");
                        mProfile.hobbies = hobby;
                        updateUser(mProfile);
                        break;
                    case MSG_CHANGE_FORTE:
                        bundle = msg.getData();
                        String forte = bundle.getString("forte");
                        mProfile.forte = forte;
                        updateUser(mProfile);
                        break;
                    case MSG_CHANGE_SEX:
                        bundle = msg.getData();
                        int gender = bundle.getInt("gender");
                        mProfile.gender = gender;
                        updateUser(mProfile);
                        break;
                    case MSG_CHANGE_REGION:
                        bundle = msg.getData();
                        String provincename = bundle.getString("province");
                        String cityname = bundle.getString("city");
                        mProfile.province = provincename;
                        mProfile.city = cityname;
                        updateUser(mProfile);
                        break;
                    case MSG_CHANGE_TITLE:
                        bundle = msg.getData();
                        String title = bundle.getString("title");
                        mProfile.title = title;
                        updateUser(mProfile);
                        break;
                }
            }
        };
    }


    @Override
    public void addAvatarView() {
        avatarLay = (RelativeLayout) findViewById(R.id.avatarLay);
        mIvAvatar = (ImageView) findViewById(R.id.imageView_avatar);
        PictureManager.getInstance().displayAvatar(mIvAvatar, mProfile.avatar, 24);
        avatarLay.setOnClickListener(this);
    }

    private void initView() {
//        phoneLay = (RelativeLayout) this.findViewById(R.id.layout_phone);
//        emailLay = (RelativeLayout) this.findViewById(R.id.layout_email);
        pseudonymLay = (RelativeLayout) this.findViewById(R.id.nicknameLay);
        titleLay = (RelativeLayout) this.findViewById(R.id.titleLay);
        introLay = (RelativeLayout) this.findViewById(R.id.introLay);
        hobbyLay = (RelativeLayout) this.findViewById(R.id.hobbyLay);
        forteLay = (RelativeLayout) this.findViewById(R.id.forteLay);
        mGenderLay = (RelativeLayout)this.findViewById(R.id.layout_gender);
        regionLay = (RelativeLayout)this.findViewById(R.id.regionLay);

        mGenderLay.setOnClickListener(this);
        pseudonymLay.setOnClickListener(this);
        titleLay.setOnClickListener(this);
        introLay.setOnClickListener(this);
        hobbyLay.setOnClickListener(this);
        forteLay.setOnClickListener(this);
        regionLay.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avatarLay:
                if (userInfoAvatarChoose == null) {
                    userInfoAvatarChoose = new AvatarChoose();
                    mUri = Uri.fromFile(new File(Utility.getTumccaImgPath(this) + "/" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                    userInfoAvatarChoose.setUri(mUri);
                }
                userInfoAvatarChoose.show(getSupportFragmentManager(), "UserInfoPhotoChoose");
                break;

            case R.id.nicknameLay:
                if (userInfoPseudonym == null) {
                    userInfoPseudonym = new UserInfoPseudonym();
                }
                userInfoPseudonym.setPseudonym(mProfile.pseudonym);
                userInfoPseudonym.show(getSupportFragmentManager(), "UserInfoPseudonym");
                break;
            case R.id.titleLay:
                if (userInfoTitle == null) {
                    userInfoTitle = new UserInfoTitle();
                }
                userInfoTitle.setTitle(mProfile.title);
                userInfoTitle.show(getSupportFragmentManager(), "UserInfoTitle");
                break;
            case R.id.introLay:
                if (userInfoIntro == null) {
                    userInfoIntro = new UserInfoIntro();
                }
                userInfoIntro.setIntro(mProfile.introduction);
                userInfoIntro.show(getSupportFragmentManager(), "UserInfoIntro");
                break;
            case R.id.hobbyLay:
                if (userInfoHobby == null) {
                    userInfoHobby = new UserInfoHobby();
                }
                userInfoHobby.setHobby(mProfile.hobbies);
                userInfoHobby.show(getSupportFragmentManager(), "UserInfoHobby");
                break;
            case R.id.forteLay:
                if (userInfoForte == null) {
                    userInfoForte = new UserInfoForte();
                }
                userInfoForte.setForte(mProfile.forte);
                userInfoForte.show(getSupportFragmentManager(), "UserInfoForte");
                break;
            case R.id.layout_gender:
                if (mUserInfoGender == null) {
                    mUserInfoGender = new UserInfoGender();
                }
                mUserInfoGender.show(getSupportFragmentManager(), "UserInfoGender");
                break;
            case R.id.regionLay:
                userCity = new UserCity();
                userCity.show(getSupportFragmentManager(), "UserCity");
                break;
        }
    }

    private void refreshData()
    {
        if(mProfile.gender == 1){
            mTvGender.setText(getString(R.string.male));
        }
        else{
            mTvGender.setText(getString(R.string.female));
        }
        mTvPseudonym.setText(mProfile.pseudonym);
        mTvTitle.setText(mProfile.title);
        mTvIntro.setText(mProfile.introduction);
        mTvHobby.setText(mProfile.hobbies);
        mTvForte.setText(mProfile.forte);
        mTvCountry.setText(mProfile.country);
        mTvProvince.setText(mProfile.province);
        mTvCity.setText(mProfile.city);
    }

    public void addGenderView() {
        mTvGender = (TextView) findViewById(R.id.textView_gender);
        mGenderLay = (RelativeLayout) findViewById(R.id.layout_gender);
        mGenderLay.setOnClickListener(this);
    }

    @Override
    public void addPseudonymView() {
        mTvPseudonym = (TextView) findViewById(R.id.textView_nickname);
        mTvPseudonym.setText(mProfile.pseudonym);
    }

    @Override
    public void addTitleView() {
        mTvTitle = (TextView) findViewById(R.id.textView_title);
    }

    @Override
    public void addIntroView() {
        mTvIntro = (TextView) findViewById(R.id.textView_intro);
    }

    @Override
    public void addHobbyView() {
        mTvHobby = (TextView) findViewById(R.id.textView_hobbies);
    }

    @Override
    public void addForteView() {
        mTvForte = (TextView) findViewById(R.id.textView_forte);
    }

    @Override
    public void addRegionView() {
        mTvCountry = (TextView) findViewById(R.id.textView_country);
        mTvProvince = (TextView) findViewById(R.id.textView_province);
        mTvCity = (TextView) findViewById(R.id.textView_city);
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
//        updateUser(mProfile);
    }

    @Override
    public void setPseudonym(String pseudonym) {
        mTvPseudonym.setText(pseudonym);
        mProfile.pseudonym = pseudonym;
//        updateUser(mProfile);
    }

    @Override
    public void setTitle(String title) {
        mTvTitle.setText(title);
        mProfile.title = title;
//        updateUser(mProfile);
    }

    @Override
    public void setIntro(String intro) {
        mTvIntro.setText(intro);
        mProfile.introduction = intro;
//        updateUser(mProfile);

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
//            updateUser(mProfile);
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
//            updateUser(mProfile);
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
//        updateUser(mProfile);
    }

    @Override
    public void setUser(Profile profile) {
        mProfile = profile;
    }

    @Override
    public void updateUser(Profile profile) {

        if (UserManager.getInstance().updateProfile((long) UserManager.getInstance().getCurrentUserId(), profile))
        {
            refreshData();
            Toast.makeText(this, getString(R.string.profile_update_success), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, getString(R.string.profile_update_fail), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AvatarChoose.FROMCAMERA:
                    startPhotoZoom(mUri);
                    break;
                case AvatarChoose.FROMGALLERY:
                    startPhotoZoom(data.getData());
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
                                    String token = UserManager.getInstance().getCurrentToken(null);
                                    int pictureId = -1;
                                    pictureId = UserServer.getInstance().uploadAvatar(temp);
                                    //mProfile.avatar = pictureId;
                                    //String result = UserServer.getInstance().updateUser(mProfile, token);

                                        Message msg = mHandler.obtainMessage(MSG_SHOW_AVATAR);
                                        msg.arg1 = pictureId;
                                        mHandler.sendMessage(msg);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}