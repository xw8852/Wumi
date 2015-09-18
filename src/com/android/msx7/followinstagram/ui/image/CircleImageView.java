package com.android.msx7.followinstagram.ui.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.AnimatedRotateDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Josn on 2015/9/8.
 */
public class CircleImageView extends ImageView {


    public CircleImageView(Context context) {
        super(context);

    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    boolean isRoud = true;

    public void setRoud(boolean isRoud) {
        this.isRoud = isRoud;
        requestLayout();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

//        Path clipPath = new Path();
//        int w = this.getWidth();
//        int h = this.getHeight();
//        clipPath.addCircle(w / 2, h / 2, Math.min(w / 2, h / 2), Path.Direction.CW);
//        canvas.clipPath(clipPath);
//        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//        super.onDraw(canvas);
        if (!isRoud||!(getDrawable() instanceof  BitmapDrawable)) {
            super.onDraw(canvas);
            return;
        }
        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
        int w = getWidth(), h = getHeight();

        Bitmap roundBitmap = getCroppedBitmap(bitmap, Math.min(w, h));

        canvas.drawBitmap(roundBitmap, (w - Math.min(w, h)) / 2.0f, (h - Math.min(w, h)) / 2.0f, null);
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if (bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        float _radius = Math.min(sbmp.getWidth(), sbmp.getHeight());
        final Rect rect = new Rect(0, 0, (int) _radius, (int) _radius);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));

        canvas.drawCircle(_radius / 2 + 0.7f,
                _radius / 2 + 0.7f, _radius / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }


}