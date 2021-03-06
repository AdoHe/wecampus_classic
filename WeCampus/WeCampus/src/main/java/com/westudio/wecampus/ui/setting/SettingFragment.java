package com.westudio.wecampus.ui.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.umeng.analytics.MobclickAgent;
import com.westudio.wecampus.R;
import com.westudio.wecampus.ui.base.BaseApplication;
import com.westudio.wecampus.ui.base.ShareMenuActivity;
import com.westudio.wecampus.ui.devtool.DevelopInfoPage;
import com.westudio.wecampus.util.CacheUtil;
import com.westudio.wecampus.util.Config;

/**
 * Created by nankonami on 13-12-2.
 */
public class SettingFragment extends SherlockFragment {

    private Activity mActivity;

    //Widgets
    private RelativeLayout rlChangePwd;
    private RelativeLayout rlClearCache;
    private RelativeLayout rlShare;
    private RelativeLayout rlFeedback;
    private RelativeLayout rlAbout;
    private ProgressDialog progressDialog;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        initWidget(view);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SettingFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initWidget(final View view) {
        rlChangePwd = (RelativeLayout)view.findViewById(R.id.ly_setting_change_pwd);
        if(!BaseApplication.getInstance().hasAccount) {
            view.findViewById(R.id.change_pwd_container).setVisibility(View.GONE);
        }
        rlChangePwd.setOnClickListener(clickListener);
        rlClearCache = (RelativeLayout)view.findViewById(R.id.ly_setting_clear_cache);
        rlClearCache.setOnClickListener(clickListener);
        rlShare = (RelativeLayout)view.findViewById(R.id.ly_setting_share_wecampus);
        rlShare.setOnClickListener(clickListener);
        rlFeedback = (RelativeLayout)view.findViewById(R.id.ly_setting_feedback);
        rlFeedback.setOnClickListener(clickListener);
        rlAbout = (RelativeLayout)view.findViewById(R.id.ly_setting_about_us);
        rlAbout.setOnClickListener(clickListener);


        // 开发模式下长按
        if (Config.IS_TEST) {
            rlAbout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    startActivity(new Intent(getActivity(), DevelopInfoPage.class));
                    return true;
                }
            });
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ly_setting_change_pwd: {
                    if(BaseApplication.getInstance().hasAccount) {
                        Intent intent = new Intent(mActivity, ChangePwdActivity.class);
                        startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                    } else {
                        Toast.makeText(mActivity, R.string.please_login, Toast.LENGTH_SHORT).show();
                    }
                    MobclickAgent.onEvent(getActivity(), "settings_changepsd");
                    break;
                }
                case R.id.ly_setting_about_us: {
                    Intent intent = new Intent(mActivity, AboutUsActivity.class);
                    startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                    MobclickAgent.onEvent(getActivity(), "settings_about");
                    break;
                }
                case R.id.ly_setting_clear_cache:
                    clearImageCache();
                    MobclickAgent.onEvent(getActivity(), "settings_cache");
                    break;
                case R.id.ly_setting_feedback: {
                    Uri emailUri = Uri.parse("mailto:feedback@wecampus.net");
                    Intent intent = new Intent(Intent.ACTION_SENDTO, emailUri);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "WeCampus Android Feedback");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                    MobclickAgent.onEvent(getActivity(), "settings_feedback");
                    break;
                }
                case R.id.ly_setting_share_wecampus: {
                    Intent intent = new Intent(mActivity, ShareMenuActivity.class);
                    intent.putExtra(ShareMenuActivity.IS_SHARE_APP, true);
                    startActivity(intent);
                    MobclickAgent.onEvent(getActivity(), "settings_share");
                    break;
                }
            }
        }
    };

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 333) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), R.string.msg_clear_img_cache_complete, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void clearImageCache() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
        }
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CacheUtil.clearCache(getActivity());
                } catch (Exception e) {
                } finally {
                    handler.sendEmptyMessage(333);
                }
            }
        }).start();
    }

}
