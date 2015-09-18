package com.android.msx7.followinstagram.ui.TriangleShape;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.android.msx7.followinstagram.R;

/**
 * Created by xiaowei on 2015/9/1.
 */
public class TriangleShape extends View {
    public int fillColor;
    private Paint paint;
    private Path path;
    private int[] location;
    View notchCenterXOn;
    Direction direction = Direction.NORTH;

    public TriangleShape(Context context) {
        super(context);
        init(null);
    }

    public TriangleShape(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TriangleShape(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public TriangleShape(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet paramAttributeSet) {
        TypedArray localTypedArray = getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.TriangleShape);
        this.fillColor = localTypedArray.getColor(R.styleable.TriangleShape_fillColor, -1);
        if (!TextUtils.isEmpty(localTypedArray.getString(R.styleable.TriangleShape_direction)))
            this.direction = Direction.valueOf(String.valueOf(localTypedArray.getInt(R.styleable.TriangleShape_direction, 0)));
        localTypedArray.recycle();
        this.location = new int[2];
        this.paint = new Paint(1);
        this.paint.setColor(this.fillColor);
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.path = new Path();
        this.path.setFillType(Path.FillType.EVEN_ODD);

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int height = getHeight();
        getLocationInWindow(location);
        int centerX = 0;
        if (notchCenterXOn != null) {
            notchCenterXOn.getLocationInWindow(this.location);
            int notchCenter = location[0] + (int) (notchCenterXOn.getWidth() * notchCenterXOn.getScaleX() / 2.0F);
            getLocationInWindow(location);
            centerX = notchCenter - location[0];
        } else {
            getLocationInWindow(location);
            centerX = location[0];
        }


        this.path.reset();
        if (direction == Direction.SOUTH) {
            this.path.moveTo(centerX - height, 0.0F);
            this.path.lineTo(centerX + height, 0.0F);
            this.path.lineTo(centerX, height);
        } else {
            this.path.moveTo(centerX - height, height);
            this.path.lineTo(centerX + height, height);
            this.path.lineTo(centerX, 0.0F);
        }
        this.path.close();
        canvas.drawPath(path, paint);
    }

    public void setDirection(Direction parama) {
        this.direction = parama;
    }

    public void setNotchCenterXOn(View paramView) {
        this.notchCenterXOn = paramView;
    }

    public enum Direction {

        NORTH("0"), SOUTH("1");

        private String desc;

        Direction(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return this.desc;
        }

    }
}
