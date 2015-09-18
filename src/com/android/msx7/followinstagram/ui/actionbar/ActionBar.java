package com.android.msx7.followinstagram.ui.actionbar;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.ui.login.BackActionButtonDrawable;

import org.w3c.dom.Text;

/**
 * Created by Josn on 2015/9/13.
 */
public class ActionBar {
    View root;
    ImageView mback;
    TextView middleText;
    ViewSwitcher switcher;

    public ActionBar(View view) {
        if (view == null)
            new IllegalAccessError("the param of view must be not null!");
        if (view.getId() != R.id.action_bar) {
            new IllegalAccessError("the view must be  R.layout.action_bar!");
        }
        root = view;
        mback = (ImageView) root.findViewById(R.id.action_bar_button_back);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) v.getContext();
                activity.onBackPressed();
            }
        });
        middleText = (TextView) view.findViewById(R.id.action_bar_textview_title);
        mback.setBackgroundDrawable(new BackActionButtonDrawable(view.getResources()));
        switcher = (ViewSwitcher) view.findViewById(R.id.action_bar_button_action);
    }

    public void setMiddleText(String text) {
        middleText.setText(text);
    }
    public void setMiddleText(int visible) {
        middleText.setVisibility(visible);
    }

    public void setMiddleTextCenter() {
        middleText.setGravity(Gravity.CENTER);
    }

    public void setBack(int visible) {
        mback.setVisibility(visible);
    }

    public ImageView addRightImg(int resId, View.OnClickListener listener) {
        ImageView imageView = new ImageView(root.getContext());
        imageView.setImageResource(resId);
        imageView.setOnClickListener(listener);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        switcher.addView(imageView);
        switcher.showNext();
        return imageView;
    }

}
