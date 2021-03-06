package com.westudio.wecampus.ui.user;

import android.os.Bundle;
import android.view.KeyEvent;

import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;
import com.westudio.wecampus.R;
import com.westudio.wecampus.ui.base.BaseDetailActivity;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshAttacher;

/**
 * Created by nankonami on 13-10-6.
 * User Profile Activity
 */
public class UserHomepageActivity extends BaseDetailActivity {

    public static String USER_ID = "user_id";
    private static String FRAGMENT = "USER_HOME_PAGE";
    public static String USER = "user";
    public static String USER_LIST = "user_list";

    private PullToRefreshAttacher mPullToRefreshAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        mPullToRefreshAttacher = PullToRefreshAttacher.get(this);

        updateActionBar();
        getSupportFragmentManager().beginTransaction().replace(R.id.user_home_page,
                UserHomepageFragment.newInstance(getIntent().getExtras()), FRAGMENT).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("UserHomepageActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("UserHomepageActivity");
        MobclickAgent.onPause(this);
    }

    public PullToRefreshAttacher getPullToRefreshAttacher() {
        return mPullToRefreshAttacher;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void updateActionBar() {
        setTitle(R.string.fragment_title_homepage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
