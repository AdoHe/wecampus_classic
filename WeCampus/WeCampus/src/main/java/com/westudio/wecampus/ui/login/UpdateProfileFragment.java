package com.westudio.wecampus.ui.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;
import com.westudio.wecampus.R;
import com.westudio.wecampus.data.model.User;
import com.westudio.wecampus.net.WeCampusApi;
import com.westudio.wecampus.ui.base.BaseApplication;
import com.westudio.wecampus.ui.base.BaseFragment;
import com.westudio.wecampus.ui.main.MainActivity;
import com.westudio.wecampus.ui.view.CircleImageView;

/**
 * Created by nankonami on 13-9-20.
 */
public class UpdateProfileFragment extends BaseFragment implements View.OnClickListener{

    private Activity activity;

    private TextView tvSkip;
    private CircleImageView ivAvatar;
    private ProgressDialog progressDialog;
    private String mStrImgLocalPath;
    private Button btnSubmit;
    private ScrollView baseFrame;
    private EditText etTrueName;

    private AvatarUpdateHandler avatarHandler = new AvatarUpdateHandler();
    private ProfileUpdateHandler profileUpdateHandler;

    public static UpdateProfileFragment newInstance(Bundle bundle) {
        UpdateProfileFragment fragment = new UpdateProfileFragment();

        if(bundle != null) {
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    public UpdateProfileFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateActionBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_two, container, false);

        tvSkip = (TextView)view.findViewById(R.id.rege_step_two_skip);
        tvSkip.setOnClickListener(this);
        ivAvatar = (CircleImageView) view.findViewById(R.id.rege_step_two_avatar);
        ivAvatar.setOnClickListener(this);

        btnSubmit = (Button) view.findViewById(R.id.rege_step_two_submit);
        btnSubmit.setOnClickListener(this);
        baseFrame = (ScrollView) view.findViewById(R.id.base_frame);
        etTrueName = (EditText) view.findViewById(R.id.rege_step_two_true_name);
        etTrueName.setOnFocusChangeListener(onFocusChangeListener);
        view.findViewById(R.id.rege_step_two_description).setOnFocusChangeListener(onFocusChangeListener);

        profileUpdateHandler = new ProfileUpdateHandler(getActivity(), view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("UpdateProfileFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("UpdateProfileFragment");
    }

    /**
     * Update the Action Bar Title
     */
    private void updateActionBar() {
        ActionBar actionBar = ((AuthActivity)activity).getSupportActionBar();
        actionBar.setTitle(R.string.rege_actionbar_title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rege_step_two_skip: {
                startActivity(new Intent(activity, MainActivity.class));
                activity.finish();
                MobclickAgent.onEvent(getActivity(), "register_skip");
                break;
            }
            case R.id.rege_step_two_avatar: {
                ((AuthActivity)getActivity()).doPickPhotoAction();
                MobclickAgent.onEvent(getActivity(), "register_avatar_upload");
                break;
            }
            case R.id.rege_step_two_submit: {
                profileUpdateHandler.uploadProfile();
                MobclickAgent.onEvent(getActivity(), "register_submit2_btn");
            }
        }
    }

    public void setCropedAvatarImage(Bundle bundle) {
        Bitmap bmAvatar = bundle.getParcelable("cropedImage");
        mStrImgLocalPath = bundle.getString("imagePath");
        ivAvatar.setImageBitmap(bmAvatar);

        // fire an upload image request
        WeCampusApi.postUpdateAvatar(getActivity(), mStrImgLocalPath, avatarHandler, avatarHandler);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
    }

    private class AvatarUpdateHandler implements Response.Listener<User>, Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), R.string.upload_fail, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(User user) {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), R.string.upload_success, Toast.LENGTH_SHORT).show();
        }
    }

    private class ProfileUpdateHandler implements Response.Listener<User>, Response.ErrorListener {
        private EditText etRealName;
        private EditText etWords;
        private Activity _activity;

        public ProfileUpdateHandler(Activity activity, View v) {
            etRealName = (EditText) v.findViewById(R.id.rege_step_two_true_name);
            etWords = (EditText) v.findViewById(R.id.rege_step_two_description);
            this._activity = activity;
        }

        public void uploadProfile() {
            String realName = etRealName.getText().toString();
            String words = etWords.getText().toString();
            if (realName.length() == 0 && words.length() == 0) {
                Toast.makeText(getActivity(), R.string.msg_please_input_info,
                        Toast.LENGTH_SHORT).show();
            } else {
                User user = new User();
                user.id = ((BaseApplication) _activity.getApplication()).getAccountMgr().getUserId();
                user.name = realName;
                user.words = words;
                progressDialog = new ProgressDialog(_activity);
                progressDialog.show();
                WeCampusApi.postUpdateProfile(_activity, user, this, this);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), R.string.upload_fail, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(User user) {
            progressDialog.dismiss();
            startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        }
    }

    private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            final int index = view.getId() == R.id.rege_step_two_true_name ?
                    0 : baseFrame.getChildCount() - 1;
            if (b) {
                if (view.getId() != R.id.rege_step_two_true_name) {
                    scrollToBottom(baseFrame);
                }
            }
        }
    };

    private void scrollToBottom(final View scroll) {

        Handler mHandler = new Handler();

        mHandler.post(new Runnable() {
            public void run() {
                if (scroll == null) {
                    return;
                }

                scroll.scrollTo(0, scroll.getBottom());
            }
        });
    }

}
