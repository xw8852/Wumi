package com.android.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.util.ViewUtils;


public class TitleView extends LinearLayout {
    ImageView mLeftImg;
    TextView mLeftText;
    ImageView mRightImg;
    TextView mRightText;
    TextView mTitleText;
    LinearLayout mMiddleSpace;
    LinearLayout mTitleSpace;
    LinearLayout mConatiner;

    public TitleView(Context context) {
        super(context);
        init();
    }

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public TitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_widget_title_bar, this);
        mLeftImg = (ImageView) findViewById(R.id.LeftImg);
        mRightImg = (ImageView) findViewById(R.id.RightImg);
        mLeftText = (TextView) findViewById(R.id.LeftText);
        mRightText = (TextView) findViewById(R.id.RightText);
        mTitleText = (TextView) findViewById(R.id.TitleText);
        mMiddleSpace = (LinearLayout) findViewById(R.id.MiddleSpace);
        mTitleSpace = (LinearLayout) findViewById(R.id.TitleSpace);
        mConatiner = (LinearLayout) findViewById(R.id.container);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
    }

    public LinearLayout getContainer() {
        return mConatiner;
    }

    public ImageView getLeftImg() {
        return mLeftImg;
    }

    public TextView getLeftText() {
        return mLeftText;
    }

    public ImageView getRightImg() {
        return mRightImg;
    }

    public TextView getRightText() {
        return mRightText;
    }

    /**
     * 设置标题栏的背景  仅为标题栏部分，不包括整个View（即自定义添加的部分)
     *
     * @param resId 背景资源
     */
    public void setTitleBackgroud(int resId) {
        mTitleSpace.setBackgroundResource(resId);
    }

    /**
     * 设置标题栏的背景  仅为标题栏部分，不包括整个View（即自定义添加的部分)
     *
     * @param drawable 背景资源
     */
    public void setTitleBackgroud(Drawable drawable) {
        mTitleSpace.setBackgroundDrawable(drawable);
    }

    /**
     * 设置标题栏的背景 仅为标题栏部分，不包括整个View（即自定义添加的部分)
     */
    public void setTitleBackgroud(Bitmap bitmap) {
        mTitleSpace.setBackgroundDrawable(new BitmapDrawable(bitmap));
    }


    /**
     * 标题栏标题部分，替换成自定义View
     *
     * @param layoutId 布局资源ID
     */
    public void replaceTitleContent(int layoutId) {
        mTitleText.setVisibility(View.GONE);
        mMiddleSpace.removeAllViews();
        LayoutInflater.from(getContext()).inflate(layoutId, mMiddleSpace);
    }

    /**
     * 设置 标题
     *
     * @param resId 资源id
     */
    public void setTitle(int resId, OnClickListener listener) {
        setTitle(getContext().getString(resId), listener);
    }

    /**
     * 设置 标题
     */
    public void setTitle(String title, OnClickListener listener) {
        mTitleText.setText(title);
        mTitleText.setOnClickListener(listener);
    }

    public TextView getTitleTextView() {
        return mTitleText;
    }

    public View getTitleSpace() {
        return mTitleSpace;
    }

    /**
     * 设置标题栏左侧按钮文字
     *
     * @param resId
     */
    public void setLeftBtn(int resId, OnClickListener listener) {
        setLeftBtn(getContext().getString(resId), listener);
    }

    /**
     * 设置标题栏左侧按钮文字
     */
    public void setLeftBtn(String leftText, OnClickListener listener) {
        mLeftText.setText(leftText);
        mLeftText.setOnClickListener(listener);
    }

    /**
     * 设置标题栏忧侧按钮文字
     */
    public void setRightBtn(int resId, OnClickListener listener) {
        setRightBtn(getContext().getString(resId), listener);
    }

    /**
     * 设置标题栏右侧按钮文字
     */
    public void setRightBtn(String rightText, OnClickListener listener) {
        mRightText.setText(rightText);
        mRightText.setOnClickListener(listener);
    }


    /**
     * 标题栏左侧图标
     */
    public void setLeftImg(int resId, OnClickListener listener) {
        mLeftImg.setImageResource(resId);
        mLeftImg.setOnClickListener(listener);
    }

    /**
     * 标题栏右侧图标
     */
    public void setRightImg(int resId, OnClickListener listener) {
        mRightImg.setImageResource(resId);
        mRightImg.setOnClickListener(listener);
    }

    public int getMeasureHeight() {
        ViewUtils.measureView(this);
        return getMeasuredHeight();
    }
}
