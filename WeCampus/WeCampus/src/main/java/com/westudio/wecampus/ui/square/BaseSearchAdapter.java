package com.westudio.wecampus.ui.square;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.westudio.wecampus.data.model.ActivityList;

import java.util.ArrayList;

/**
 * Created by martian on 13-12-5.
 */
public abstract class BaseSearchAdapter<T> extends BaseAdapter {

    protected ArrayList<T> mList;
    protected Context mContext;
    protected LayoutInflater mLayoutInflater;

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addAll(ArrayList<T> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        ImageView imageView;
        TextView textName;
        ImageLoader.ImageContainer imageRequest;
    }


}