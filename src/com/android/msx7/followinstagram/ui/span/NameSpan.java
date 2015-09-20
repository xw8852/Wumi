package com.android.msx7.followinstagram.ui.span;

import android.os.Bundle;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import com.android.msx7.followinstagram.fragment.TabProfileFragment;
import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.MainTabActivity;
import com.android.msx7.followinstagram.util.L;

/**
 * Created by Josn on 2015/9/12.
 */
public class NameSpan extends URLSpan {
    String name;
    long id;

    public NameSpan(String name, long id) {
        super(name);
        this.name = name;
        this.id = id;
    }

    @Override
    public void onClick(View widget) {
//        if (widget.getContext() instanceof MainTabActivity) {
//            MainTabActivity activity = (MainTabActivity) widget.getContext();
            TabProfileFragment fragment = new TabProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putLong(TabProfileFragment.PARAM_USER_ID, id);
            bundle.putString(TabProfileFragment.PARAM_USER_NAME, name);
            fragment.setArguments(bundle);
            MainTabActivity.addFragmentToBackStack(fragment,widget.getContext());
//        } else
//            L.d("-----widget--" + name + "," + id);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(IMApplication.getApplication().getResources().getColor(R.color.accent_blue_medium));
        ds.setUnderlineText(false);
    }
}
