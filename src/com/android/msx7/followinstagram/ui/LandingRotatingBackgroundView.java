package com.android.msx7.followinstagram.ui;

/**
 * Created by xiaowei on 2015/9/1.
 */

import android.view.View;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.os.Bundle;

import com.android.msx7.followinstagram.R;

public class LandingRotatingBackgroundView extends View {
    private final int[] a = new int[2];
    private final Matrix b = new Matrix();
    private final Paint c = new Paint(2);
    private long d = SystemClock.elapsedRealtime() % 30000L;
    private View e;
    private Bitmap f;

    public LandingRotatingBackgroundView(Context paramContext) {
        super(paramContext);
    }

    public LandingRotatingBackgroundView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public LandingRotatingBackgroundView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    public void onDraw(Canvas paramCanvas) {
        super.onDraw(paramCanvas);
        if (this.f == null)
            this.f = BitmapFactory.decodeResource(getResources(), R.drawable.landing_rainbow);
        getLocationInWindow(this.a);
        int i = this.a[1];
        if(e==null)return;
        this.e.getLocationInWindow(this.a);
        int j = this.a[1] + this.e.getHeight() - i;
        if (j <= 0) return;

        int k = getWidth() / 2;
        int l = 3 * (j / 2);
        float f1 = (float) Math.sqrt(l * l + k * k);
        float f2 = 2.0F * f1 / this.f.getWidth();
        float f3 = k - f1;
        float f4 = l - f1;
        long l1 = 360L * ((SystemClock.elapsedRealtime() - this.d) % 30000L) / 30000L;
        float f5 = this.f.getWidth() / 2;
        this.b.reset();
        this.b.preRotate((int) l1, f5, f5);
        this.b.postScale(f2, f2);
        this.b.postTranslate(f3, f4);
        paramCanvas.drawBitmap(this.f, this.b, this.c);
        invalidate();

    }

    public void onRestoreInstanceState(Parcelable paramParcelable) {
        Bundle localBundle = (Bundle) paramParcelable;
        super.onRestoreInstanceState(localBundle.getParcelable("parent-state"));
        this.d = (SystemClock.elapsedRealtime() + localBundle.getLong("offset"));
    }

    public Parcelable onSaveInstanceState() {
        Bundle localBundle = new Bundle();
        localBundle.putParcelable("parent-state", super.onSaveInstanceState());
        localBundle.putLong("offset", this.d - SystemClock.elapsedRealtime());
        return localBundle;
    }

    public void setAlignBottomView(View paramView) {
        this.e = paramView;
    }
}