package com.westudio.wecampus.ui.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.westudio.wecampus.R;
import com.westudio.wecampus.data.ActivityDataHelper;
import com.westudio.wecampus.data.UserDataHelper;
import com.westudio.wecampus.data.model.User;
import com.westudio.wecampus.net.WeCampusApi;
import com.westudio.wecampus.ui.base.BaseApplication;
import com.westudio.wecampus.ui.login.AuthActivity;
import com.westudio.wecampus.ui.user.MyProfileActivity;
import com.westudio.wecampus.ui.view.CircleImageView;
import com.westudio.wecampus.util.Constants;
import com.westudio.wecampus.util.Utility;

/**
 * Created by martian on 13-9-15.
 * This Fragment is for main menu in the app drawer
 */
public class LeftMenuFragment extends Fragment implements View.OnClickListener,
        Response.Listener<User>, Response.ErrorListener {

    private RelativeLayout mActivitySection, mUserSection, mSquareSection, mSettingsSection;
    private ImageButton mBtnEdit;
    private Button mBtnSignOut;
    private Button mBtnSignIn;
    private CircleImageView ivAvatar;
    private TextView tvName;
    private TextView tvWord;
    private LogoutHandler mLogoutHandler;
    private UserDataHelper mDataHelper;

    private Activity mActivity;
    private User mUser;
    private QueryUserTask mTask;
    private int id;

    public static LeftMenuFragment newInstance() {
        LeftMenuFragment f = new LeftMenuFragment();
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leftmenu, container, false);

        //avatar
        ivAvatar = (CircleImageView) view.findViewById(R.id.img_avatar);
        ivAvatar.setOnClickListener(this);
        ivAvatar.setBorderWidth(Utility.dip2px(getActivity(), 2f));
        mBtnEdit = (ImageButton) view.findViewById(R.id.edit_button);
        mBtnEdit.setOnClickListener(this);
        mBtnSignOut = (Button) view.findViewById(R.id.btn_sign_out);
        mBtnSignOut.setOnClickListener(this);
        mBtnSignIn = (Button) view.findViewById(R.id.btn_sign_in);
        mBtnSignIn.setOnClickListener(this);
        tvName = (TextView)view.findViewById(R.id.text_user_name);
        tvWord = (TextView)view.findViewById(R.id.text_user_words);

        //list
        mActivitySection = (RelativeLayout) view.findViewById(R.id.activity_section);
        mUserSection = (RelativeLayout) view.findViewById(R.id.user_section);
        mSquareSection = (RelativeLayout) view.findViewById(R.id.square_section);
        mSettingsSection = (RelativeLayout) view.findViewById(R.id.settings_section);
        mActivitySection.setOnClickListener(this);
        mUserSection.setOnClickListener(this);
        mSquareSection.setOnClickListener(this);
        mSettingsSection.setOnClickListener(this);

        if (BaseApplication.getInstance().hasAccount) {
            mTask = new QueryUserTask();
            Utility.executeAsyncTask(mTask);
            mBtnSignIn.setVisibility(View.GONE);
            mBtnSignOut.setVisibility(View.VISIBLE);
        } else {
            mBtnSignIn.setVisibility(View.VISIBLE);
            mBtnSignOut.setVisibility(View.GONE);
            mBtnEdit.setVisibility(View.GONE);
            mUserSection.setVisibility(View.GONE);
            tvName.setText(R.string.menu_no_login);
            tvWord.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = BaseApplication.getInstance().getAccountMgr().getUserId();
        mLogoutHandler = new LogoutHandler();
        mDataHelper = new UserDataHelper(mActivity);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("LeftMentFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("LeftMentFragment");

        if(Utility.isConnect(mActivity) && BaseApplication.getInstance().hasAccount) {
            WeCampusApi.getProfile(mActivity, this, this);
        }
    }

    private void updateUI() {
        tvName.setText(mUser.nickname);
        tvWord.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(mUser.words)) {
            tvWord.setText(R.string.come_to_add_words);
        } else {
            tvWord.setText(mUser.words);
        }
        mUserSection.setVisibility(View.VISIBLE);
        mBtnSignIn.setVisibility(View.GONE);
        mBtnSignOut.setVisibility(View.VISIBLE);
        mBtnEdit.setVisibility(View.VISIBLE);
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
                    if(data != null) {
                        ivAvatar.setImageBitmap(data);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
        }
    }

    private void refreshUI() {
        tvName.setText(getResources().getString(R.string.menu_no_login));
        tvWord.setVisibility(View.GONE);
        mUserSection.setVisibility(View.GONE);
        ivAvatar.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_default_avatar));
    }

    @Override
    public void onClick(View view) {
        MainActivity mainActivity = (MainActivity) getActivity();
        switch (view.getId()) {
            case R.id.activity_section: {
                mainActivity.changeContent(MainActivity.ContentType.ACTIVITY);
                MobclickAgent.onEvent(getActivity(), "menu_activity");
                break;
            }
            case R.id.user_section: {
                mainActivity.changeContent(MainActivity.ContentType.USERS);
                MobclickAgent.onEvent(getActivity(), "menu_friends");
                break;
            }
            case R.id.square_section: {
                mainActivity.changeContent(MainActivity.ContentType.SQUARE);
                MobclickAgent.onEvent(getActivity(), "menu_explore");
                break;
            }
            case R.id.settings_section: {
                mainActivity.changeContent(MainActivity.ContentType.SETTINGS);
                MobclickAgent.onEvent(getActivity(), "menu_settings");
                break;
            }
            case R.id.img_avatar: {
                if(BaseApplication.getInstance().hasAccount) {
                    mainActivity.changeContent(MainActivity.ContentType.HOMEPAGE);
                    MobclickAgent.onEvent(getActivity(), "menu_avatar");
                }
                break;
            }
            case R.id.edit_button: {
                if(BaseApplication.getInstance().hasAccount) {
                    startActivity(new Intent(getActivity(), MyProfileActivity.class));
                    MobclickAgent.onEvent(getActivity(), "menu_edit");
                }
                break;
            }
            case R.id.btn_sign_in: {
                startActivity(new Intent(getActivity(), AuthActivity.class));
                MobclickAgent.onEvent(getActivity(), "menu_login");
                break;
            }
            case R.id.btn_sign_out: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.ensure_logout);
                builder.setCancelable(true);
                builder.setNegativeButton(R.string.text_cancel, null);
                builder.setPositiveButton(R.string.text_ensure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mLogoutHandler.logout();
                        MobclickAgent.onEvent(getActivity(), "menu_logout");
                    }
                });
                builder.show();
                break;
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Toast.makeText(mActivity, R.string.network_problem, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(User user) {
        mUser = user;
        if(mUser != null) {
            updateUI();
            updateUserToDb();
        }
    }

    private void updateUserToDb() {
        Utility.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                if(mUser != null) {
                    int row = mDataHelper.update(mUser);
                    if(row == 0) {
                        mDataHelper.insert(mUser);
                    }
                }
                return null;
            }
        });
    }

    private class QueryUserTask extends AsyncTask<Object, Object, Object> {

        @Override
        protected Object doInBackground(Object... params) {
            mUser = mDataHelper.query(id);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if(mUser != null) {
                updateUI();
            }
        }
    }

    private class LogoutHandler implements Response.Listener, Response.ErrorListener {
        ProgressDialog progressDialog;
        ActivityDataHelper activityDataHelper = new ActivityDataHelper(getActivity());

        public void logout() {
            BaseApplication app = BaseApplication.getInstance();
            int id = app.getAccountMgr().getUserId();
            app.hasAccount = false;
            app.getAccountMgr().clearAccountInfo();
            WeCampusApi.logout(LeftMenuFragment.this, id, this, this);

            if (progressDialog == null) {
                progressDialog = new ProgressDialog(getActivity());
            }
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.show();
        }
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            setUiAsLogout();
        }

        @Override
        public void onResponse(Object o) {
            setUiAsLogout();
        }

        private void setUiAsLogout() {
            refreshUI();
            mBtnEdit.setVisibility(View.GONE);
            mBtnSignOut.setVisibility(View.GONE);
            mBtnSignIn.setVisibility(View.VISIBLE);
            Utility.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
                @Override
                protected Object doInBackground(Object... objects) {
                    mDataHelper.deleteAll();
                    activityDataHelper.deleteAll();
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    progressDialog.dismiss();
                }
            });
        }
    }
}
