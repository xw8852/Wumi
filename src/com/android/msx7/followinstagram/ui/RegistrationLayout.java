package com.android.msx7.followinstagram.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.msx7.followinstagram.R;

/**
 * Created by xiaowei on 2015/9/2.
 */
public class RegistrationLayout extends FrameLayout {
    View logoAndIconContainer;
    View logoContainer;
    View iconContainer;
    View tabHeader;

    public RegistrationLayout(Context context) {
        super(context);
    }

    public RegistrationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RegistrationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RegistrationLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    void init() {
        if (logoAndIconContainer != null) return;
        logoAndIconContainer = findViewById(R.id.tabbed_landing_logo_and_icon_container);
        logoContainer = findViewById(R.id.tabbed_landing_logo_container);
        iconContainer = findViewById(R.id.tabbed_landing_icon);
        tabHeader = findViewById(R.id.tabbed_landing_tab_header);
        int topPadding = tabHeader.getHeight();
        int bottomPadding = topPadding / 2;
        ViewGroup.LayoutParams params = iconContainer.getLayoutParams();
        params.height = logoContainer.getHeight();
        iconContainer.setLayoutParams(params);
        logoAndIconContainer.setPadding(0, topPadding, 0, bottomPadding);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        init();


    }
}
