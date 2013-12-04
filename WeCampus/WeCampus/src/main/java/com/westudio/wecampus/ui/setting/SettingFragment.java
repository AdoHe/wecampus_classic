package com.westudio.wecampus.ui.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.westudio.wecampus.R;
import com.westudio.wecampus.ui.base.BaseApplication;
import com.westudio.wecampus.ui.main.MainActivity;

/**
 * Created by nankonami on 13-12-2.
 */
public class SettingFragment extends SherlockFragment {

    private Activity mActivity;

    //Widgets
    private RelativeLayout rlLogin;
    private RelativeLayout rlChangePwd;
    private RelativeLayout rlClearCache;
    private RelativeLayout rlShare;
    private RelativeLayout rlFeedback;
    private RelativeLayout rlAbout;

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

        updateActionBar();
        initWidget(view);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void updateActionBar() {
        ActionBar actionBar = ((MainActivity) mActivity).getSupportActionBar();
        actionBar.setTitle(R.string.menu_settings);
    }

    private void initWidget(final View view) {
        rlLogin = (RelativeLayout)view.findViewById(R.id.ly_setting_login);
        if(BaseApplication.getInstance().hasAccount) {
            rlLogin.setVisibility(View.GONE);
        }
        rlLogin.setOnClickListener(clickListener);
        rlChangePwd = (RelativeLayout)view.findViewById(R.id.ly_setting_change_pwd);
        rlChangePwd.setOnClickListener(clickListener);
        rlClearCache = (RelativeLayout)view.findViewById(R.id.ly_setting_clear_cache);
        rlClearCache.setOnClickListener(clickListener);
        rlShare = (RelativeLayout)view.findViewById(R.id.ly_setting_share_wecampus);
        rlShare.setOnClickListener(clickListener);
        rlFeedback = (RelativeLayout)view.findViewById(R.id.ly_setting_feedback);
        rlFeedback.setOnClickListener(clickListener);
        rlAbout = (RelativeLayout)view.findViewById(R.id.ly_setting_about_us);
        rlAbout.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
}