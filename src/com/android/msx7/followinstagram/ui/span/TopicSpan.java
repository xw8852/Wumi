package com.android.msx7.followinstagram.ui.span;

import android.os.Bundle;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;

import com.android.msx7.followinstagram.fragment.TabHomeFragment;
import com.android.msx7.followinstagram.fragment.TabProfileFragment;
import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.MainTabActivity;
import com.android.msx7.followinstagram.util.L;

/**
 * Created by Josn on 2015/9/12.
 */
public class TopicSpan extends URLSpan {
    String name;

    public TopicSpan(String name) {
        super(name);
        this.name = name;
    }

    boolean click;

    public TopicSpan setEnable(boolean click) {
        this.click = click;
        return this;
    }

    @Override
    public void onClick(View widget) {
        if (click) return;
        if (widget.getContext() instanceof MainTabActivity) {
            MainTabActivity activity = (MainTabActivity) widget.getContext();
            TabHomeFragment fragment = new TabHomeFragment();
            Bundle bundle = new Bundle();
            bundle.putString(TabHomeFragment.PARAM_TAG, name);
            fragment.setArguments(bundle);
            activity.addFragmentToBackStack(fragment);
        } else
            L.d("-----widget--" + name);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(IMApplication.getApplication().getResources().getColor(R.color.accent_blue_medium));
        ds.setUnderlineText(false);
    }
}
