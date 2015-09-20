package com.android.msx7.followinstagram.ui.viewpage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Josn on 2015/9/20.
 */
public class ViewPager extends android.support.v4.view.ViewPager {
    public ViewPager(Context context) {
        super(context);
    }

    public ViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean flag = false;
        try {
            flag = super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException  e) {
            e.printStackTrace();
        } finally {
        }
        return flag;
    }
}
