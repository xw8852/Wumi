package com.android.widget;

import android.content.Context;

import com.android.msx7.followinstagram.common.BaseAdapter;

import java.util.List;

/**
 * Created by Josn on 2015/9/12.
 */
public abstract  class PinnedAdapter<T> extends BaseAdapter<T> implements PinnedHeaderListView.PinnedSectionedHeaderAdapter {
    public PinnedAdapter(Context ctx, List<T> data) {
        super(ctx, data);
    }

    public PinnedAdapter(Context ctx, T... data) {
        super(ctx, data);
    }
}
