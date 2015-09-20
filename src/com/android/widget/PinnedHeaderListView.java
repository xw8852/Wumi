package com.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;

public class PinnedHeaderListView extends ListView implements OnScrollListener {


    public static interface PinnedSectionedHeaderAdapter {
        public void configHeaderView(int position, View header);

        public int getCount();

    }

    private PinnedAdapter mAdapter;
    private int mHeaderOffset;
    private boolean mShouldPin = true;
    private int mWidthMode;
    private int mHeightMode;

    public PinnedHeaderListView(Context context) {
        super(context);
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void setAdapter(ListAdapter adapter) {
        mAdapter = (PinnedAdapter) adapter;
        super.setAdapter(adapter);
    }

    View header;

    public void setPinHeader(View header) {
        this.header = header;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mAdapter.configHeaderView(Math.max(0, getFirstVisiblePosition() - getHeaderViewsCount()), header);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mAdapter.configHeaderView(Math.max(0, getFirstVisiblePosition() - getHeaderViewsCount()), header);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (header != null && mAdapter != null && mAdapter.getCount() > 0) {
            int curPostion=Math.max(0, getFirstVisiblePosition() - getHeaderViewsCount());
            mAdapter.configHeaderView(Math.max(0, getFirstVisiblePosition() - getHeaderViewsCount()),header);
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (header != null && mAdapter != null && mAdapter.getCount() > 0) {
            header.setVisibility(View.VISIBLE);
            int firsetPostion, headerCount;
            int height = header.getMeasuredHeight();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    firsetPostion = getFirstVisiblePosition();
                    headerCount = getHeaderViewsCount();
//                    mAdapter.configHeaderView( Math.max(0,getFirstVisiblePosition()-getHeaderViewsCount()), header);
                    if (headerCount != 0 && firsetPostion < headerCount) {
                        mHeaderOffset = getChildAt(0).getBottom();
                    } else if (getChildAt(0).getBottom() < height) {
                        mHeaderOffset = getChildAt(0).getBottom() - header.getHeight();
                    } else mHeaderOffset = 0;
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height);
                    params.topMargin = mHeaderOffset;
                    header.setLayoutParams(params);
                    header.requestLayout();
                    break;
            }
        } else if (header != null) {
            header.setVisibility(View.INVISIBLE);
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (header != null && (mAdapter == null || mAdapter.getCount() == 0)) {
            header.setVisibility(View.INVISIBLE);
            return;
        }
        if (header == null) return;
        if (mAdapter == null || mAdapter.getCount() == 0) return;
        int firsetPostion, headerCount;
        int height = header.getMeasuredHeight();
        firsetPostion = getFirstVisiblePosition();
        headerCount = getHeaderViewsCount();
//        mAdapter.configHeaderView( Math.max(0,getFirstVisiblePosition()-getHeaderViewsCount()), header);
        if (headerCount != 0 && firsetPostion < headerCount) {
            mHeaderOffset = getChildAt(0).getBottom();
        } else if (getChildAt(0).getBottom() < height) {
            mHeaderOffset = getChildAt(0).getBottom() - header.getHeight();
        } else mHeaderOffset = 0;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height);
        params.topMargin = mHeaderOffset;
        header.setLayoutParams(params);
        header.requestLayout();
        requestLayout();

    }


    private void ensurePinnedHeaderLayout(View header) {
        if (header.isLayoutRequested()) {
            int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), mWidthMode);

            int heightSpec;
            ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
            if (layoutParams != null && layoutParams.height > 0) {
                heightSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
            } else {
                heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }
            header.measure(widthSpec, heightSpec);
            header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());
        }
    }




    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        mHeightMode = MeasureSpec.getMode(heightMeasureSpec);
    }


}
