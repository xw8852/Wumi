package com.android.msx7.followinstagram.ui.span;

import android.os.Bundle;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import com.android.msx7.followinstagram.fragment.GridActionFragment;
import com.android.msx7.followinstagram.fragment.GridPoFragment;
import com.android.msx7.followinstagram.fragment.TabHomeFragment;
import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.MainTabActivity;
import com.android.msx7.followinstagram.util.L;

/**
 * Created by Josn on 2015/9/12.
 */
public class AdressSpan extends URLSpan {
    long addressId;
    String add;

    public AdressSpan(long name, String add) {
        super(add);
        this.addressId = name;
        this.add = add;
    }

    boolean click;

    public AdressSpan setEnable(boolean click) {
        this.click = click;
        return this;
    }

    @Override
    public void onClick(View widget) {
        if (click) return;
        if (widget.getContext() instanceof MainTabActivity) {
            MainTabActivity activity = (MainTabActivity) widget.getContext();
            GridActionFragment fragment = new GridActionFragment();
//            TabHomeFragment fragment = new TabHomeFragment();
            Bundle bundle = new Bundle();
            bundle.putLong(GridActionFragment.PARAM_ADRESS, addressId);
            bundle.putString(GridActionFragment.PARAM_ADRESS_NAME, add);
            fragment.setArguments(bundle);
            activity.addFragmentToBackStack(fragment);
        } else
            L.d("-----widget--" + addressId);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(IMApplication.getApplication().getResources().getColor(R.color.accent_blue_medium));
        ds.setUnderlineText(false);
    }
}
