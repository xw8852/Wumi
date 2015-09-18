package com.android.msx7.followinstagram.ui.login;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.util.L;

import java.util.Arrays;


/**
 * Created by xiaowei on 2015/9/8.
 */
public class BackActionButtonDrawable extends Drawable {
    Paint paint = new Paint();
    Resources resources;
    boolean isPressed;
    boolean showDivider=true;

    public BackActionButtonDrawable(Resources resources) {
        this.resources = resources;
    }

    public BackActionButtonDrawable(Resources resources, boolean showDivider) {
        this.resources = resources;
        this.showDivider = showDivider;
    }

    @Override
    public void draw(Canvas canvas) {
        if (isPressed) {
//            paint.setColor(resources.getColor(R.color.grey_light));
            paint.setColor(0x40800000);
        } else paint.setColor(resources.getColor(R.color.transparent));
        canvas.drawRect(getBounds(), paint);
        paint.setColor(resources.getColor(R.color.white));
        if (showDivider)
            canvas.drawRect(getBounds().width(), getBounds().height() / 4.0f, getBounds().width(), 3.0f * getBounds().height() / 4.0f, paint);

    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    protected boolean onStateChange(int[] state) {
        super.onStateChange(state);
        isPressed = false;
        if (state != null && state.length > 0) {
            for (int _state : state) {
                if (_state == android.R.attr.state_pressed) {
                    isPressed = true;
                    return true;
                }
            }
        }
        invalidateSelf();
        return true;
    }

    @Override
    public boolean isStateful() {
        return true;
    }
}
