package com.westudio.wecampus.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.westudio.wecampus.R;
import com.westudio.wecampus.ui.login.AuthActivity;
import com.westudio.wecampus.util.Constants;

/**
 * Created by nankonami on 13-12-15.
 */
public class PickStageActivity extends SherlockFragmentActivity implements View.OnClickListener {

    private String stage;
    private int lastClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_stage);

        stage = getIntent().getStringExtra(MyProfileActivity.PICK_STAGE);
        updateActionBar();
        initWidget();
    }

    @Override
    public void onClick(View v) {
        if(lastClick != 0) {
            findViewById(lastClick).findViewById(R.id.imageView).setVisibility(View.GONE);
        }
        lastClick = v.getId();
        v.findViewById(R.id.imageView).setVisibility(View.VISIBLE);

        int stage = 0;
        switch (v.getId()) {
            case R.id.item_small_sister:
                stage = 1;
                break;
            case R.id.item_small_brother:
                stage = 2;
                break;
            case R.id.item_big_brother:
                stage = 3;
                break;
            case R.id.item_big_sister:
                stage = 4;
                break;
        }

        Handler handler = new Handler();
        final int finalType = stage;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra(MyProfileActivity.PICK_STAGE, finalType);
                setResult(MyProfileActivity.PICK_STAGE_RESULT, intent);
                finish();
            }
        };

        handler.postDelayed(runnable, 400);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.role);
    }

    private void initWidget() {
        View smallSister = findViewById(R.id.item_small_sister);
        smallSister.setOnClickListener(this);
        View smallBrother = findViewById(R.id.item_small_brother);
        smallBrother.setOnClickListener(this);
        View bigBrother = findViewById(R.id.item_big_brother);
        bigBrother.setOnClickListener(this);
        View bigSister = findViewById(R.id.item_big_sister);
        bigSister.setOnClickListener(this);
        if(Constants.SMALL_SISTER.equals(stage)) {
            smallSister.findViewById(R.id.imageView).setVisibility(View.VISIBLE);
            lastClick = R.id.item_small_sister;
        } else if(Constants.SMALL_BROTHER.equals(stage)) {
            smallBrother.findViewById(R.id.imageView).setVisibility(View.VISIBLE);
            lastClick = R.id.item_small_brother;
        } else if(Constants.BIG_BROTHER.equals(stage)) {
            bigBrother.findViewById(R.id.imageView).setVisibility(View.VISIBLE);
            lastClick = R.id.item_big_brother;
        } else if(Constants.BIG_SISTER.equals(stage)) {
            bigSister.findViewById(R.id.imageView).setVisibility(View.VISIBLE);
            lastClick = R.id.item_big_sister;
        } else {
            lastClick = 0;
        }
    }
}
