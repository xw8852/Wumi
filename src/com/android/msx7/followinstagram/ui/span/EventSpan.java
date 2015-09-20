package com.android.msx7.followinstagram.ui.span;

import android.os.Bundle;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.MainTabActivity;
import com.android.msx7.followinstagram.fragment.GridActionFragment;
import com.android.msx7.followinstagram.fragment.TabHomeFragment;
import com.android.msx7.followinstagram.fragment.TabHomeFragment.SimpleEvent;
import com.android.msx7.followinstagram.util.L;

/**
 * Created by Josn on 2015/9/12.
 */
public class EventSpan extends URLSpan {
    SimpleEvent event;

    public EventSpan(SimpleEvent event) {
        super(event.name);
       this.event=event;
    }

    boolean click;



    @Override
    public void onClick(View widget) {
        if (click) return;
//        if (widget.getContext() instanceof MainTabActivity) {
//            MainTabActivity activity = (MainTabActivity) widget.getContext();
            GridActionFragment fragment = new GridActionFragment();
//            TabHomeFragment fragment = new TabHomeFragment();
            Bundle bundle = new Bundle();
            bundle.putLong(GridActionFragment.PARAM_EVENT_ID, event.id);
            bundle.putString(GridActionFragment.PARAM_EVENT_NAME, event.name);
            fragment.setArguments(bundle);
            MainTabActivity.addFragmentToBackStack(fragment,widget.getContext());
//        } else
//            L.d("-----widget--" + event.name);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(IMApplication.getApplication().getResources().getColor(R.color.accent_blue_medium));
        ds.setUnderlineText(false);
    }
}
