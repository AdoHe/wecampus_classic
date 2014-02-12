package com.westudio.wecampus.ui.user;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.westudio.wecampus.R;
import com.westudio.wecampus.data.UserDataHelper;
import com.westudio.wecampus.data.model.User;
import com.westudio.wecampus.net.WeCampusApi;
import com.westudio.wecampus.ui.base.BaseApplication;
import com.westudio.wecampus.ui.base.BaseDetailActivity;
import com.westudio.wecampus.ui.base.PickPhotoActivity;
import com.westudio.wecampus.ui.login.AuthActivity;
import com.westudio.wecampus.ui.login.PickGenderActivity;
import com.westudio.wecampus.ui.login.PickSchoolActivity;
import com.westudio.wecampus.util.Constants;
import com.westudio.wecampus.util.Utility;

/**
 * Created by Martian on 13-10-20.
 */
public class MyProfileActivity extends PickPhotoActivity {

    public static final String NICK_NAME = "nick_name";
    public static final String REAL_NAME = "real_name";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String WORDS = "words";
    public static final String PICK_EMOTION = "emotion";
    public static final String PICK_STAGE = "stage";
    public static final String GENDER = "gender";
    public static final int UPDATE_NICK_REQUEST = 1;
    public static final int UPDATE_NICK_RESULT = 2;
    public static final int UPDATE_NAME_REQUEST = 3;
    public static final int UPDATE_NAME_RESULT = 4;
    public static final int UPDATE_PHONE_REQUEST = 5;
    public static final int UPDATE_PHONE_RESULT = 6;
    public static final int UPDATE_EMAIL_REQUEST = 7;
    public static final int UPDATE_EMAIL_RESULT = 8;
    public static final int UPDATE_WORDS_REQUEST = 9;
    public static final int UPDATE_WORDS_RESULT = 10;
    public static final int PICK_EMOTION_REQUEST = 11;
    public static final int PICK_EMOTION_RESULT = 12;
    public static final int PICK_STAGE_REQUEST = 13;
    public static final int PICK_STAGE_RESULT = 14;

    private TextView tvNickName;
    private TextView tvGender;
    private TextView tvSchool;
    private TextView tvWord;
    private TextView tvName;
    private TextView tvPhone;
    private TextView tvEmail;
    private TextView tvBirthday;
    private TextView tvLove;
    private TextView tvRole;
    private ImageView ivAvatar;
    private ProgressDialog progressDialog;

    private UserDataHelper mDataHelper;
    private User mUser;
    private int id;

    private int schoolId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_profile);
        registerSwipeToCloseListener(findViewById(R.id.content_frame));

        updateActionBar();
        initWidget();
        mDataHelper = new UserDataHelper(this);
        id = BaseApplication.getInstance().getAccountMgr().getUserId();

        refreshUserFromDb();
    }

    @Override
    protected void onPause() {
        WeCampusApi.cancelRequest(this);
        super.onPause();
        MobclickAgent.onPageEnd("MyProfileActivityActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MyProfileActivityActivity");
        MobclickAgent.onResume(this);
    }

    private void updateActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.menu_save_profile);
        getSupportActionBar().getCustomView().findViewById(R.id.save_profile).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if(item.getItemId() == R.id.save_profile) {
            updateProfile();
            return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    private void updateProfile() {
        User user = new User();
        user.gender = tvGender.getText().toString();
        user.birthday = "";
        user.email = tvEmail.getText().toString();
        user.emotion = tvLove.getText().toString();
        user.name = tvName.getText().toString();
        user.nickname = tvNickName.getText().toString();
        user.phone = tvPhone.getText().toString();
        user.stage = tvRole.getText().toString();
        user.words = tvWord.getText().toString();
        WeCampusApi.postUpdateProfile(this, user, new Response.Listener() {
            @Override
            public void onResponse(Object o) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    private void initWidget() {
        tvNickName = (TextView)findViewById(R.id.nickname);
        findViewById(R.id.change_name).setOnClickListener(clickListener);
        tvGender = (TextView)findViewById(R.id.gender);
        findViewById(R.id.change_gender).setOnClickListener(clickListener);
        tvSchool = (TextView)findViewById(R.id.school);
        tvWord = (TextView)findViewById(R.id.words);
        findViewById(R.id.change_words).setOnClickListener(clickListener);
        tvName = (TextView)findViewById(R.id.real_name);
        findViewById(R.id.change_real_name).setOnClickListener(clickListener);
        tvPhone = (TextView)findViewById(R.id.telephone);
        findViewById(R.id.change_telephone).setOnClickListener(clickListener);
        tvEmail = (TextView)findViewById(R.id.email);
        findViewById(R.id.change_email).setOnClickListener(clickListener);
        tvBirthday = (TextView)findViewById(R.id.birthday);
        findViewById(R.id.change_birthday).setOnClickListener(clickListener);
        tvLove = (TextView)findViewById(R.id.relationship);
        findViewById(R.id.change_relationship).setOnClickListener(clickListener);
        tvRole = (TextView)findViewById(R.id.role);
        findViewById(R.id.change_role).setOnClickListener(clickListener);
        ivAvatar = (ImageView)findViewById(R.id.profile_avatar);
        findViewById(R.id.change_avatar).setOnClickListener(clickListener);
    }

    private void updateUI() {
        tvNickName.setText(mUser.nickname);
        tvGender.setText(mUser.gender);
        tvSchool.setText(mUser.school_name);
        tvWord.setText(mUser.words);
        tvName.setText(mUser.name);
        tvPhone.setText(mUser.phone);
        tvEmail.setText(mUser.email);
        tvLove.setText(mUser.emotion);
        tvRole.setText(mUser.stage);
        if(Constants.IMAGE_NOT_FOUND.equals(mUser.avatar)) {
            if(Constants.MALE.equals(mUser.gender)) {
                ivAvatar.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.ic_default_male
                ));
            } else {
                ivAvatar.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.ic_default_female
                ));
            }
        } else {
            WeCampusApi.requestImage(mUser.avatar, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    Bitmap data = imageContainer.getBitmap();
                    if (data != null) {
                        ivAvatar.setImageBitmap(data);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
        }
    }

    private void refreshUserFromDb() {
        if(id != 0) {
            Utility.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
                @Override
                protected void onPostExecute(Object o) {
                    if(mUser != null) {
                        updateUI();
                    }
                }

                @Override
                protected Object doInBackground(Object... params) {
                    mUser = mDataHelper.query(id);
                    return null;
                }
            });
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.change_name: {
                    Intent intent = new Intent(MyProfileActivity.this, UpdateNickActivity.class);
                    intent.putExtra(NICK_NAME, tvNickName.getText().toString());
                    startActivityForResult(intent, UPDATE_NICK_REQUEST);
                    break;
                }
                case R.id.change_real_name: {
                    Intent intent = new Intent(MyProfileActivity.this, UpdateNameActivity.class);
                    intent.putExtra(REAL_NAME, tvName.getText().toString());
                    startActivityForResult(intent, UPDATE_NAME_REQUEST);
                    break;
                }
                case R.id.change_telephone: {
                    Intent intent = new Intent(MyProfileActivity.this, UpdatePhoneActivity.class);
                    intent.putExtra(PHONE, tvPhone.getText().toString());
                    startActivityForResult(intent, UPDATE_PHONE_REQUEST);
                    break;
                }
                case R.id.change_email: {
                    Intent intent = new Intent(MyProfileActivity.this, UpdateEmailActivity.class);
                    intent.putExtra(EMAIL, tvEmail.getText().toString());
                    startActivityForResult(intent, UPDATE_EMAIL_REQUEST);
                    break;
                }
                case R.id.change_gender: {
                    Intent intent = new Intent(MyProfileActivity.this, PickGenderActivity.class);
                    intent.putExtra(GENDER, tvGender.getText().toString());
                    startActivityForResult(intent, AuthActivity.PICK_GENDER_REQUEST);
                    break;
                }
                case R.id.change_words: {
                    Intent intent = new Intent(MyProfileActivity.this, UpdateWordsActivity.class);
                    intent.putExtra(WORDS, tvWord.getText().toString());
                    startActivityForResult(intent, UPDATE_WORDS_REQUEST);
                    break;
                }
                case R.id.change_relationship: {
                    Intent intent = new Intent(MyProfileActivity.this, PickEmotionActivity.class);
                    intent.putExtra(PICK_EMOTION, tvLove.getText().toString());
                    startActivityForResult(intent, PICK_EMOTION_REQUEST);
                    break;
                }
                case R.id.change_role: {
                    Intent intent = new Intent(MyProfileActivity.this, PickStageActivity.class);
                    intent.putExtra(PICK_STAGE, tvRole.getText().toString());
                    startActivityForResult(intent, PICK_STAGE_REQUEST);
                    break;
                }
                case R.id.change_avatar: {
                    doPickPhotoAction();
                    break;
                }
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)  {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.give_up_modify));
            builder.setCancelable(true);
            builder.setNegativeButton(getResources().getString(R.string.go_on_modify), null);
            builder.setPositiveButton(R.string.give_up, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.give_up_modify));
            builder.setCancelable(true);
            builder.setNegativeButton(getResources().getString(R.string.go_on_modify), null);
            builder.setPositiveButton(R.string.give_up, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UPDATE_NICK_REQUEST && resultCode == UPDATE_NICK_RESULT) {
            tvNickName.setText(data.getStringExtra(NICK_NAME));
        } else if(resultCode == AuthActivity.PICK_GENDER_RESULT) {
            int type = data.getIntExtra(AuthActivity.PICK_GENDER, 2);
            String strGender = (type == 0 ? getString(R.string.gender_male) :
                    (type == 1 ? getString(R.string.gender_female) : getString(R.string.gender_secret)));
            tvGender.setText(strGender);
        } else if(resultCode == AuthActivity.PICK_SCHOOL_RESULT) {
            tvSchool.setText(data.getStringExtra(AuthActivity.PICK_SCHOOL_NAME));
            schoolId = data.getIntExtra(AuthActivity.PICK_SCHOOL_ID, -1);
        } else if(requestCode == UPDATE_WORDS_REQUEST && resultCode == UPDATE_WORDS_RESULT) {
            tvWord.setText(data.getStringExtra(WORDS));
        } else if(requestCode == UPDATE_NAME_REQUEST && resultCode == UPDATE_NAME_RESULT) {
            tvName.setText(data.getStringExtra(REAL_NAME));
        } else if(requestCode == UPDATE_PHONE_REQUEST && resultCode == UPDATE_PHONE_RESULT) {
            tvPhone.setText(data.getStringExtra(PHONE));
        } else if(requestCode == UPDATE_EMAIL_REQUEST && resultCode == UPDATE_EMAIL_RESULT) {
            tvEmail.setText(data.getStringExtra(EMAIL));
        } else if(requestCode == PICK_EMOTION_REQUEST && resultCode == PICK_EMOTION_RESULT) {
            int type = data.getIntExtra(PICK_EMOTION, -1);
            tvLove.setText(Utility.getEmotionByType(this, type));
        } else if(requestCode == PICK_STAGE_REQUEST && resultCode == PICK_STAGE_RESULT) {
            int type = data.getIntExtra(PICK_STAGE, -1);
            tvRole.setText(Utility.getStageByType(this, type));
        } else if (requestCode == PHOTO_PICKED_WITH_DATA) {
            Uri selectedImage = data.getData();
            doCropPhoto(selectedImage, mCropedTemp);
        } else if (requestCode == CAMERA_WITH_DATA) {
            doCropPhoto(mUriTemp, mCropedTemp);
        } else if (requestCode == PHOTO_CROPED_WITH_DATA) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.show();
            WeCampusApi.postUpdateAvatar(this, mCropedTemp.getPath(),
                    new Response.Listener() {
                        @Override
                        public void onResponse(Object o) {
                            ivAvatar.setImageBitmap(decodeUriAsBitmap(mCropedTemp));
                            progressDialog.dismiss();
                            Toast.makeText(MyProfileActivity.this, R.string.upload_success, Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            progressDialog.dismiss();
                            Toast.makeText(MyProfileActivity.this, R.string.upload_fail,
                                    Toast.LENGTH_SHORT).show();                        }
                    }
            );
        }
    }
}
