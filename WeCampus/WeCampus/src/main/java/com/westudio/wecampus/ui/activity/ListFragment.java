package com.westudio.wecampus.ui.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;

import com.westudio.wecampus.data.ActivityDataHelper;
import com.westudio.wecampus.data.model.Activity;
import com.westudio.wecampus.ui.base.BasePageListFragment;

/**
 * Created by nankonami on 13-11-29.
 */
public class ListFragment extends BasePageListFragment<Activity.ActivityRequestData> implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private String mCategory;

    private ActivityDataHelper mDataHelper;

    public static ListFragment newInstance(Bundle bundle) {
        ListFragment fragment = new ListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void parseArgument() {
        Bundle bundle = getArguments();
        mCategory = bundle.getString(ActivityListActivity.EXTRA_CATEGORY);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = super.onCreateView(inflater, container, savedInstanceState);
        parseArgument();
        mDataHelper = new ActivityDataHelper(mActivity);

        getLoaderManager().initLoader(0, null, this);

        return contentView;
    }

    @Override
    protected String getRequestUrl() {
        return null;
    }

    @Override
    protected int getContentViewResId() {
        return 0;
    }

    @Override
    protected int getListViewId() {
        return 0;
    }

    @Override
    protected CursorAdapter getAdapter() {
        return (CursorAdapter)super.getAdapter();
    }

    @Override
    protected BaseAdapter newAdapter() {
        return null;
    }

    @Override
    protected Class getResponseDataClass() {
        return null;
    }

    @Override
    protected void processResponseData() {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return mDataHelper.getCursorLoader(mCategory);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        getAdapter().changeCursor(cursor);
        if(cursor != null && cursor.getCount() == 0) {
            loadFirstPage();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
