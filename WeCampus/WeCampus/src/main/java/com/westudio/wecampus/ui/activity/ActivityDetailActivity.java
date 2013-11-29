package com.westudio.wecampus.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.westudio.wecampus.R;
import com.westudio.wecampus.data.ActivityDataHelper;
import com.westudio.wecampus.data.OrgDataHelper;
import com.westudio.wecampus.data.model.Activity;
import com.westudio.wecampus.data.model.Participants;
import com.westudio.wecampus.net.WeCampusApi;
import com.westudio.wecampus.ui.base.BaseApplication;
import com.westudio.wecampus.ui.base.ImageDetailActivity;
import com.westudio.wecampus.ui.base.ShareMenuActivity;
import com.westudio.wecampus.ui.organiztion.OrganizationHomepageActivity;
import com.westudio.wecampus.util.DateUtil;
import com.westudio.wecampus.util.ImageUtil;
import com.westudio.wecampus.util.Utility;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

/**
 * Created by nankonami on 13-10-4.
 * Activity that display the detail of activity
 */
public class ActivityDetailActivity extends SherlockFragmentActivity implements OnRefreshListener {

    //Widgets
    private TextView tvOrg;
    private ImageView ivOrgAvatar;
    private TextView tvTitle;
    private TextView tvTime;
    private TextView tvLocation;
    private TextView tvTag;
    private ImageView ivPoster;
    private TextView tvTicket;
    private TextView tvCompany;
    private TextView tvContent;
    private ImageView ivCat;

    private ProgressBar progressBar;
    private ProgressBar pbContent;
    private LinearLayout lyContent;
    private LinearLayout noContentContainer;
    private FrameLayout contentContainer;
    private RelativeLayout rlOrganization;
    private RelativeLayout rlParticipants;
    private RelativeLayout rlCompany;

    ActivityDataHelper acDataHelper;
    private OrgDataHelper orgDataHelper;

    private int activityId = -1;
    private Activity activity;
    private JoinHandler joinHandler;
    private LikeHandler likeHandler;
    private ParticipateHandler participateHandler;
    private ActivityDetailUpdater updater;

    //Pull to refresh widget
    private PullToRefreshAttacher mPullToRefreshAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        activityId = getIntent().getIntExtra(ActivityListFragment.ACTIVITY_ID, -1);

        acDataHelper = new ActivityDataHelper(this);
        orgDataHelper = new OrgDataHelper(this);
        //refreshActivityFromDb();
        initWidget();
        updateActionBar();

        updater = new ActivityDetailUpdater();
        updater.fetchActivityDetail();
    }

    private void refreshActivityFromDb() {
        if (activityId != -1) {
            Utility.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
                @Override
                protected Object doInBackground(Object... objects) {
                    activity = acDataHelper.query(activityId);
                    activity.organization = orgDataHelper.query(activity.organization_id);
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    initWidget();
                }
            });
        }
    }

    private void initWidget() {

        tvOrg = (TextView)findViewById(R.id.detail_tv_organization);
        ivOrgAvatar = (ImageView) findViewById(R.id.detail_org_avatar);
        tvTitle = (TextView)findViewById(R.id.detail_tv_title);
        tvTime = (TextView)findViewById(R.id.detail_tv_time);
        tvLocation = (TextView)findViewById(R.id.detail_tv_location);
        tvTag = (TextView)findViewById(R.id.detail_tv_tag);
        tvTicket = (TextView)findViewById(R.id.detail_tv_ticket);
        tvCompany = (TextView)findViewById(R.id.detail_tv_company);
        tvContent = (TextView)findViewById(R.id.detail_tv_content);
        ivPoster = (ImageView)findViewById(R.id.detail_img_poster);
        ivPoster.setOnClickListener(clickListener);
        ivCat = (ImageView)findViewById(R.id.detail_no_content_img);
        ivCat.setOnClickListener(clickListener);
        noContentContainer = (LinearLayout)findViewById(R.id.detail_no_content);
        progressBar = (ProgressBar)findViewById(R.id.detail_no_content_pb);
        //pbContent = (ProgressBar)findViewById(R.id.detail_content_pb);
        lyContent = (LinearLayout)findViewById(R.id.detail_content);
        contentContainer = (FrameLayout)findViewById(R.id.detail_content_container);
        rlOrganization = (RelativeLayout)findViewById(R.id.detail_part_three);
        rlOrganization.setOnClickListener(clickListener);
        rlParticipants = (RelativeLayout)findViewById(R.id.detail_part_four);
        rlParticipants.setOnClickListener(clickListener);
        rlCompany = (RelativeLayout)findViewById(R.id.detail_rl_sponsor);
        rlCompany.setOnClickListener(clickListener);
        //showBottomActionBar();

        /*if (activity == null) {
            return;
        }

        tvTitle.setText(activity.title);
        tvLocation.setText(activity.location);
        tvTag.setText(activity.category);

        Drawable defaultDrawable = new ColorDrawable(Color.rgb(229, 255, 255));
        WeCampusApi.requestImage(activity.image, WeCampusApi.getImageListener(ivPoster,
                defaultDrawable, defaultDrawable));
        ivPoster.setOnClickListener(clickListener);

        updater = new ActivityDetailUpdater();
        //TODO:IF NO NETWORK CONNECTED
        updater.fetchActivityDetail();
        participateHandler = new ParticipateHandler(this);*/
        /*joinHandler = new JoinHandler(this);
        joinHandler.refreshUi(activity.can_join);
        likeHandler = new LikeHandler(this);*/
        participateHandler = new ParticipateHandler(this);
        joinHandler = new JoinHandler(this);
        likeHandler = new LikeHandler(this);

        mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
        PullToRefreshLayout pullToRefreshLayout = (PullToRefreshLayout)findViewById(R.id.ptr_detail_layout);
        pullToRefreshLayout.setPullToRefreshAttacher(mPullToRefreshAttacher, this);
    }

    private void updateActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    private void updateUI() {

        tvTitle.setText(activity.title);
        tvTime.setText(DateUtil.getActivityTime(this, activity.begin, activity.end));
        tvLocation.setText(activity.location);
        tvTag.setText(activity.category);

        Drawable defaultDrawable = new ColorDrawable(Color.rgb(229, 255, 255));
        WeCampusApi.requestImage(activity.image, WeCampusApi.getImageListener(ivPoster,
                defaultDrawable, defaultDrawable));
        ivPoster.setOnClickListener(clickListener);

        //If the organization is not null
        if (activity.organization != null) {
            tvOrg.setText(activity.organization.getName());
            final Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.detail_organization);
            WeCampusApi.requestImage(activity.organization.avatar, new ImageLoader.ImageListener() {

                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    Bitmap data = imageContainer.getBitmap();
                    if (data != null) {
                        ivOrgAvatar.setImageBitmap(ImageUtil.getRoundedCornerBitmap(data));
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    ivOrgAvatar.setImageBitmap(ImageUtil.getRoundedCornerBitmap(defaultBitmap));
                }
            });
        }

        if (activity.have_ticket) {
            findViewById(R.id.detail_tv_ticket).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.detail_tv_ticket)).setText(activity.ticket_service);
        } else {
            findViewById(R.id.detail_tv_ticket).setVisibility(View.GONE);
        }

        if (activity.have_sponsor) {
            findViewById(R.id.detail_tv_ticket).setVisibility(View.VISIBLE);
            findViewById(R.id.detail_rl_sponsor).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.detail_tv_company)).setText(activity.sponsor_name);
        } else {
            findViewById(R.id.detail_rl_sponsor).setVisibility(View.GONE);
        }


        tvContent.setText(activity.description);
        participateHandler.refreshUI();
        if(BaseApplication.getInstance().hasAccount) {
            joinHandler.refreshUi(activity.can_join);
            likeHandler.refreshUi(activity.can_like);
        }
    }

    private void updateExtraUi() {

        //If the organization is not null
        if (activity.organization != null) {
            tvOrg.setText(activity.organization.getName());
            final Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.detail_organization);
            WeCampusApi.requestImage(activity.organization.avatar, new ImageLoader.ImageListener() {

                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    Bitmap data = imageContainer.getBitmap();
                    if (data != null) {
                        ivOrgAvatar.setImageBitmap(ImageUtil.getRoundedCornerBitmap(data));
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    ivOrgAvatar.setImageBitmap(ImageUtil.getRoundedCornerBitmap(defaultBitmap));
                }
            });
        }

        if (activity.have_ticket) {
            findViewById(R.id.detail_tv_ticket).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.detail_tv_ticket)).setText(activity.ticket_service);
        } else {
            findViewById(R.id.detail_tv_ticket).setVisibility(View.GONE);
        }

        if (activity.have_sponsor) {
            findViewById(R.id.detail_tv_ticket).setVisibility(View.VISIBLE);
            findViewById(R.id.detail_rl_sponsor).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.detail_tv_company)).setText(activity.sponsor_name);
        } else {
            findViewById(R.id.detail_rl_sponsor).setVisibility(View.GONE);
        }


        tvContent.setText(activity.description);
        participateHandler.refreshUI();
        //joinHandler.refreshUi(activity.can_join);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.detail_menu_share: {
                Intent i = new Intent(this, ShareMenuActivity.class);
                i.putExtra(ShareMenuActivity.ACTIVITY_ID, activityId);
                startActivity(i);
                return true;
            }
            case android.R.id.home: {
                finish();
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                return true;
            }
             default: {
                 return super.onOptionsItemSelected(item);
             }
        }
    }

    private View.OnClickListener clickListener =  new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.detail_img_poster: {
                    Intent intent = new Intent(ActivityDetailActivity.this, ImageDetailActivity.class);
                    intent.putExtra(ImageDetailActivity.KEY_IMAGE_URL, activity.image);
                    startActivity(intent);
                    break;
                }
                case R.id.detail_part_three: {
                    Intent intent = new Intent(ActivityDetailActivity.this, OrganizationHomepageActivity.class);
                    intent.putExtra(OrganizationHomepageActivity.ORG_ID, activity.organization_id);
                    startActivity(intent);
                    break;
                }
                case R.id.detail_no_content_img: {
                    if(Utility.isConnect(ActivityDetailActivity.this)) {
                        progressBar.setVisibility(View.VISIBLE);
                        ivCat.setVisibility(View.GONE);
                        updater.fetchActivityDetail();
                    } else {
                        Toast.makeText(ActivityDetailActivity.this, getResources().getString(R.string.network_problem),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case R.id.detail_rl_sponsor: {
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setData(Uri.parse(activity.sponsor_url));
                    startActivity(intent);
                }
            }
        }
    };

    @Override
    public void onRefreshStarted(View view) {
        if(Utility.isConnect(this)) {
            updater.fetchActivityDetail();
        } else {
            if(mPullToRefreshAttacher.isRefreshing()) {
                mPullToRefreshAttacher.setRefreshComplete();
            }
            Toast.makeText(ActivityDetailActivity.this, getResources().getString(R.string.network_problem),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private class JoinHandler implements Response.Listener<Activity>, Response.ErrorListener {
        private  android.app.Activity ac;
        ProgressBar progressBar;
        ImageView icon;
        LinearLayout container;
        View divider;

        public JoinHandler(android.app.Activity activity) {
            this.ac = activity;
            progressBar = (ProgressBar) ac.findViewById(R.id.progress_join);
            icon = (ImageView) ac.findViewById(R.id.ic_activity_join);
            container = (LinearLayout) ac.findViewById(R.id.bottom_bar_attend);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Check the session
                    if(BaseApplication.getInstance().hasAccount) {
                        if(Utility.isConnect(ActivityDetailActivity.this)) {
                            join();
                        } else {
                            Toast.makeText(ActivityDetailActivity.this, getResources().getString(R.string.network_problem),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ActivityDetailActivity.this, getResources().getString(R.string.please_login),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
            divider = ac.findViewById(R.id.divider);
        }

        public void join() {
            progressBar.setVisibility(View.VISIBLE);
            icon.setVisibility(View.GONE);
            WeCampusApi.joinActivityWithId(ActivityDetailActivity.this, activity.id, this, this);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            progressBar.setVisibility(View.GONE);
            icon.setVisibility(View.VISIBLE);
        }

        @Override
        public void onResponse(Activity data) {
            activity = data;
            refreshUi(activity.can_join);
            Toast.makeText(ActivityDetailActivity.this, getResources().getString(R.string.attend_success),
                    Toast.LENGTH_SHORT).show();
        }

        public void refreshUi(boolean canJoin) {
            if (canJoin) {
                container.setVisibility(View.VISIBLE);
                divider.setVisibility(View.VISIBLE);
            } else {
                container.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
            }
        }
    }

    private class LikeHandler implements Response.Listener<Activity>, Response.ErrorListener {
        private  android.app.Activity ac;
        boolean like;
        ProgressBar progressBar;
        ImageView icon;
        LinearLayout container;

        public LikeHandler(android.app.Activity activity) {
            this.ac = activity;
            progressBar = (ProgressBar) ac.findViewById(R.id.progress_like);
            icon = (ImageView) ac.findViewById(R.id.ic_activity_like);
            container = (LinearLayout) ac.findViewById(R.id.bottom_bar_like);

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(BaseApplication.getInstance().hasAccount) {
                        if(Utility.isConnect(ActivityDetailActivity.this)) {
                            changeLikeState();
                        } else {
                            Toast.makeText(ActivityDetailActivity.this, getResources().getString(R.string.network_problem),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ActivityDetailActivity.this, getResources().getString(R.string.please_login),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void changeLikeState() {
            progressBar.setVisibility(View.VISIBLE);
            icon.setVisibility(View.GONE);
            if(like) {
                WeCampusApi.likeActivityWithId(ActivityDetailActivity.this, activity.id, this, this);
            } else {
                WeCampusApi.disLikeActivityWithId(ActivityDetailActivity.this, activity.id, this, this);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            progressBar.setVisibility(View.GONE);
            icon.setVisibility(View.VISIBLE);
        }

        @Override
        public void onResponse(Activity data) {
            activity = data;
            progressBar.setVisibility(View.GONE);
            icon.setVisibility(View.VISIBLE);
            icon.setImageDrawable(getResources().getDrawable(activity.can_like ?
                    R.drawable.ic_activity_like_un : R.drawable.ic_activity_like_sl));
            Toast.makeText(ActivityDetailActivity.this, getResources().getString(R.string.like_success),
                    Toast.LENGTH_SHORT).show();
        }

        public void refreshUi(boolean canLike) {
            like = canLike;
            if(canLike) {

            } else {
                icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_activity_like_sl));
            }
        }
    }

    private class ParticipateHandler implements Response.Listener<Participants.ParticipantsRequestData>, Response.ErrorListener {

        private android.app.Activity ac;
        TextView tvNoAttend;
        TextView tvAttend;
        LinearLayout container;

        public ParticipateHandler(android.app.Activity activity) {
            this.ac = activity;
            tvAttend = (TextView)activity.findViewById(R.id.detail_tv_attend_num);
            tvNoAttend = (TextView) activity.findViewById(R.id.detail_tv_no_people_attend);
            container = (LinearLayout)activity.findViewById(R.id.detail_participants_container);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {

        }

        @Override
        public void onResponse(Participants.ParticipantsRequestData participantsRequestData) {
            container.removeAllViews();
            for(int i = 0; i < (activity.count_of_participants > 5 ? 5 : activity.count_of_participants); i++) {
                Participants participants = participantsRequestData.getObjects().get(i);
                final ImageView imageView = new ImageView(ac);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Utility.dip2px(ac, 43), Utility.dip2px(ac, 43));
                layoutParams.setMargins(0, 0, 10, 0);
                imageView.setLayoutParams(layoutParams);
                Utility.log("avatar", participants.avatar);
                WeCampusApi.requestImage(participants.avatar, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        Bitmap data = imageContainer.getBitmap();
                        if(data != null) {
                            imageView.setImageBitmap(data);
                            container.addView(imageView);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
            }
        }

        public void refreshUI() {
            String attend = getResources().getString(R.string.detail_attend);
            tvAttend.setText(String.format(attend, activity.count_of_participants));

            if(activity.count_of_participants == 0) {
                tvNoAttend.setVisibility(View.VISIBLE);
            } else {
                WeCampusApi.getActivityParticipantsWithId(ActivityDetailActivity.this, activity.id, this, this);
            }
        }
    }

    private class ActivityDetailUpdater implements Response.Listener<Activity>, Response.ErrorListener {

        public void fetchActivityDetail() {
            mPullToRefreshAttacher.setRefreshing(true);
            WeCampusApi.getActivityById(ActivityDetailActivity.this, activityId, this, this);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            //TODO fetch activity failed
            if(mPullToRefreshAttacher.isRefreshing()) {
                mPullToRefreshAttacher.setRefreshComplete();
            }

            noContentContainer.setVisibility(View.VISIBLE);
            contentContainer.setVisibility(View.GONE);
            Toast.makeText(ActivityDetailActivity.this, getResources().getString(R.string.network_problem),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(final Activity data) {
            if(mPullToRefreshAttacher.isRefreshing()) {
                mPullToRefreshAttacher.setRefreshComplete();
            }
            //pbContent.setVisibility(View.GONE);
            lyContent.setVisibility(View.VISIBLE);
            activity = data;
            noContentContainer.setVisibility(View.GONE);
            contentContainer.setVisibility(View.VISIBLE);
            //updateExtraUi();
            //updateActivityToDb();
            updateUI();
            updateActivityToDb();
        }
    }

    private void updateActivityToDb() {
        Utility.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... objects) {
                if (activity != null) {
                    acDataHelper.update(activity);
                }
                return null;
            }
        });
        Utility.executeAsyncTask(new AsyncTask<Object, Object, Object>() {

            @Override
            protected Object doInBackground(Object... objects) {
                if (activity != null) {
                    int updatedRows = orgDataHelper.update(activity.organization);
                    if (updatedRows == 0) {
                        orgDataHelper.insert(activity.organization);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                View v = findViewById(R.id.detail_part_three);
                if (v != null) {
                    v.setOnClickListener(clickListener);
                }
            }
        });
    }
}
