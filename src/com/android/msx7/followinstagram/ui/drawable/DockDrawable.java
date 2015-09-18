package com.android.msx7.followinstagram.ui.drawable;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;

/**
 * Created by Josn on 2015/9/9.
 */
public class DockDrawable extends StateListDrawable {

    public DockDrawable(int backgroudColor, int backgroudLightColor) {
        super();
        addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(backgroudLightColor));
        addState(new int[]{android.R.attr.state_checked}, new ColorDrawable(backgroudLightColor));
        addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(backgroudLightColor));
        addState(new int[]{}, new ColorDrawable(backgroudColor));
    }
}
