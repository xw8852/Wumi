package com.android.msx7.followinstagram.common;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by XiaoWei on 2015/7/7.
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {
    protected Context mCtx;
    protected List<T> data;

    public List<T> getData(){
        return data;
    }
    public BaseAdapter(Context ctx, List<T> data) {
        super();
        this.mCtx = ctx;
        if (data == null) data = new ArrayList<T>();
        this.data = data;
    }

    public BaseAdapter(Context ctx, T... data) {
        super();
        this.mCtx = ctx;
        this.data = Arrays.asList(data);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, LayoutInflater.from(mCtx));
    }

    public void changeData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void changeData(T... data) {
        this.data = Arrays.asList(data);
        notifyDataSetChanged();
    }

    public void addMore(List<T> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void addMore(T... data) {
        this.data.addAll(Arrays.asList(data));
        notifyDataSetChanged();
    }

    public void clear() {
        this.data.clear();
        notifyDataSetChanged();
    }

    public void remove(T source) {
        this.data.remove(source);
        notifyDataSetChanged();
    }

    public void remove(List<T> source) {
        this.data.removeAll(source);
        notifyDataSetChanged();
    }

    public void remove(T... source) {
        this.data.removeAll(Arrays.asList(source));
        notifyDataSetChanged();
    }

    public abstract View getView(int position, View convertView, LayoutInflater inflater);
}
