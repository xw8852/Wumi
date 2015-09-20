package com.android.msx7.followinstagram.ui.image;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.ImgFindUserActivity.SimpleContact;
import com.android.msx7.followinstagram.activity.MainTabActivity;
import com.android.msx7.followinstagram.fragment.TabProfileFragment;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Josn on 2015/9/16.
 */
public class PhotoImageView extends FrameLayout {
    FrameLayout frames;

    public PhotoImageView(Context context) {
        super(context);
        init();
    }

    public PhotoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhotoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PhotoImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    PhotoView mImageView;
    PhotoViewAttacher mAttacher;

    void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_contact_image2, this);
        mImageView = (PhotoView) findViewById(R.id.PhotoView);
    }

    public PhotoView getPhotoView() {
        return mImageView;
    }

    int bitmapWidth, bitmapHeight;
    List<SimpleContact> contactList = new ArrayList<SimpleContact>();

    public void setUrl(String url, List<SimpleContact> list) {
        mAttacher = new PhotoViewAttacher(mImageView);
        contactList.clear();
        listView2.clear();
        if (list == null) list = new ArrayList<SimpleContact>();
        contactList.addAll(list);
        for (View view : listViews) {
            view.setVisibility(View.GONE);
        }
        IMApplication.getApplication().displayImage(url, mImageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                mImageView.setImageBitmap(bitmap);
                mAttacher.update();
                bitmapWidth = bitmap.getWidth();
                bitmapHeight = bitmap.getHeight();
//                DisplayMetrics dm = getResources().getDisplayMetrics();
//                int _width = dm.widthPixels;
//                int _height = _width * bitmapHeight / bitmapWidth;
//                ViewGroup.LayoutParams params = mImageView.getLayoutParams();
//                params.width = _width;
//                params.height = _height;
//                bitmapHeight = params.height;
//                bitmapWidth = params.width;
//                mImageView.setLayoutParams(params);
//                addTag();
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (View view : listView2) {
                    if (view.getVisibility() == VISIBLE) {
                        view.setVisibility(GONE);
                    } else view.setVisibility(VISIBLE);
                }
            }
        });
    }


    List<View> listViews = new ArrayList<View>();
    List<View> listView2 = new ArrayList<View>();

    void addTag() {

        for (int i = 0; i < contactList.size(); i++) {
            final SimpleContact contact = contactList.get(i);
            if (TextUtils.isEmpty(contact.position)) continue;
            String[] postion = contact.position.split("ABCD");
            if (postion == null || postion.length != 2 || TextUtils.isEmpty(postion[0]) || TextUtils.isEmpty(postion[1]))
                continue;
            View view = null;
            if (i < listViews.size()) {
                view = listViews.get(i);
                view.setVisibility(View.VISIBLE);
            } else {
                view = LayoutInflater.from(getContext()).inflate(R.layout.layout_img_tag, null);
                addView(view);
                listViews.add(view);
            }
            listView2.add(view);
            view.findViewById(R.id.delete).setVisibility(View.GONE);
            TextView tv = ((TextView) view.findViewById(R.id.text));
            tv.setText(contact.name);
            if (contact.userId > 0) {
                Drawable drawable = getResources().getDrawable(R.drawable.pin);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                tv.setCompoundDrawables(drawable, null, null, null);
                tv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TabProfileFragment fragment = new TabProfileFragment();
                        Bundle bundle = new Bundle();
                        bundle.putLong(TabProfileFragment.PARAM_USER_ID, contact.userId);
                        fragment.setArguments(bundle);
                        MainTabActivity.addFragmentToBackStack(fragment, v.getContext());
                    }
                });
            } else {
                tv.setOnClickListener(null);
                tv.setCompoundDrawables(null, null, null, null);
            }
//            ViewHelper.setTranslationX(view, bitmapHeight / Float.valueOf(postion[1]));
//            ViewHelper.setTranslationY(view,  bitmapWidth / Float.valueOf(postion[0]));
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.leftMargin = (int) (bitmapWidth / Float.valueOf(postion[0]));
            params.topMargin = (int) (mImageView.getTop() + bitmapHeight / Float.valueOf(postion[1]));
            view.setLayoutParams(params);

        }
        ViewGroup.LayoutParams params = mImageView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mImageView.setLayoutParams(params);
    }


}
