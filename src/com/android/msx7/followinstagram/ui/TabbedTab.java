package com.android.msx7.followinstagram.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.ui.TriangleShape.TriangleShape;

/**
 * Created by xiaowei on 2015/9/1.
 */
public class TabbedTab extends FrameLayout {
    TextView mTextView;
    TriangleShape mTriangleShape;

    public TabbedTab(Context context) {
        super(context);
        init(null);
    }

    public TabbedTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TabbedTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    void init(AttributeSet attributeSet) {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_tabbled_tab, this);
        mTextView = (TextView) findViewById(R.id.tabbed_landing_tab_text);
        mTriangleShape = (TriangleShape) findViewById(R.id.tabbed_landing_tab_triangle);
        mTriangleShape.setNotchCenterXOn(mTextView);
        TypedArray localTypedArray = getContext().obtainStyledAttributes(attributeSet, com.android.internal.R.styleable.TextView);
        mTextView.setText(localTypedArray.getString(com.android.internal.R.styleable.TextView_text));
    }

    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            mTextView.setTextColor(getResources().getColor(R.color.white));
            mTriangleShape.setVisibility(View.VISIBLE);
        } else {
            mTriangleShape.setVisibility(View.GONE);
            mTextView.setTextColor(getResources().getColor(R.color.grey_light));
        }
    }


}
