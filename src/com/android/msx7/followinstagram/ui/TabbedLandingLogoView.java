package com.android.msx7.followinstagram.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.android.msx7.followinstagram.R;

/**
 * Created by xiaowei on 2015/9/1.
 */
public class TabbedLandingLogoView extends View {
    PaintLogo paintLogo;

    public TabbedLandingLogoView(Context context) {
        super(context);
    }

    public TabbedLandingLogoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabbedLandingLogoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private int getLogoSuggestWidth(int paramInt) {
        WindowManager localWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        localWindowManager.getDefaultDisplay().getMetrics(localDisplayMetrics);
        return Math.min(paramInt, 3 * localDisplayMetrics.heightPixels / 5);
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.paintLogo == null)
            return;
        this.paintLogo.logo.recycle();
    }

    public void onDraw(Canvas paramCanvas) {
        super.onDraw(paramCanvas);
        if (this.paintLogo == null)
            return;
        this.paintLogo.logo.init(getResources());
        this.paintLogo.draw(paramCanvas);
    }

    public void onMeasure(int paramInt1, int paramInt2) {
        super.onMeasure(paramInt1, paramInt2);
        int width = getMeasuredWidth();
//                getLogoSuggestWidth(getMeasuredWidth());
        LogoBitmap localah = getLogoBitmap(logoBitmaps, width);
        PaintLogo localag = localah.getPaintLogo(Math.min(localah.width, width));
        float f = (width - localag.logo.width) / 2.0f;

        localag.matrix.postTranslate(f, 0.0f);
        if (paintLogo != null)
            paintLogo.logo.recycle();
        paintLogo = localag;
        setMeasuredDimension(width, localag.logo.height);
    }


    public final LogoBitmap small = new LogoBitmap(R.drawable.nux_dayone_landing_logo_small, 300, 100);
    public final LogoBitmap medium = new LogoBitmap(R.drawable.nux_dayone_landing_logo_medium, 500, 150);
    public final LogoBitmap large = new LogoBitmap(R.drawable.nux_dayone_landing_logo_large, 660, 200);
    public LogoBitmap[] logoBitmaps = new LogoBitmap[]{small, medium, large};

    LogoBitmap getLogoBitmap(LogoBitmap[] arr, int width) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].width >= width) {
                return arr[i];
            }
        }
        return arr[arr.length - 1];
    }


    public class LogoBitmap {
        public int drawableId;
        public int height;
        public int width;
        public Bitmap bitmap;

        public LogoBitmap( int drawableId,int width, int height) {
            this.width = width;
            this.height = height;
            this.drawableId = drawableId;
        }

        public final PaintLogo getPaintLogo(int paramInt) {
            Matrix localMatrix = new Matrix();
            float f = (1.0f*paramInt) /width;
            localMatrix.postScale(f, f);
            LogoBitmap logoBitmap=new LogoBitmap(drawableId,paramInt, (int) (f * height));
            return new PaintLogo(logoBitmap, localMatrix);
        }

        public final void recycle() {
            if (bitmap == null)
                return;
           bitmap.recycle();
            bitmap = null;
        }

        public final void init(Resources paramResources) {
            if (bitmap != null)
                return;
          bitmap = BitmapFactory.decodeResource(paramResources, drawableId);
        }

        public final Bitmap getBitmap() {
            return bitmap;
        }
    }


    final class PaintLogo {
        private Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        public final LogoBitmap logo;
        public final Matrix matrix;

        public PaintLogo(LogoBitmap paramah, Matrix paramMatrix) {
            this.logo = paramah;
            this.matrix = paramMatrix;
        }

        public final void draw(Canvas paramCanvas) {
            if (logo.getBitmap() == null)
                return;
            paramCanvas.drawBitmap(logo.getBitmap(), matrix, paint);
        }
    }

}
