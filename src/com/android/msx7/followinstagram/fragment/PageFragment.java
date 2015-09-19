package com.android.msx7.followinstagram.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.MainTabActivity;
import com.android.msx7.followinstagram.common.BaseFragment;
import com.android.msx7.followinstagram.fragment.TabHomeFragment.HomeItem;
import com.android.msx7.followinstagram.ui.span.AdressSpan;
import com.android.msx7.followinstagram.util.L;
import com.android.msx7.followinstagram.util.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Josn on 2015/9/19.
 */
public class PageFragment extends BaseFragment {
    ViewPager pager;

    List<HomeItem> items;
    int index = 0;
    boolean[] hides = null;

    public static final PageFragment getFragment(List<HomeItem> items, int index) {
        PageFragment fragment = new PageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        bundle.putString("data", new Gson().toJson(items));
        fragment.setArguments(bundle);
        return fragment;
    }

    PageAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_viewpager, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pager = (ViewPager) getView().findViewById(R.id.viewPager);
        getTitleBar().setTitle("照片", null);
        items = new Gson().fromJson(getArguments().getString("data"), new TypeToken<List<HomeItem>>() {
        }.getType());
        index = getArguments().getInt("index", 0);
        adapter = new PageAdapter();
        pager.setAdapter(adapter);
        hides = new boolean[items.size()];
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i >= adapter.mViews.size()) return;
                Item item = (Item) adapter.mViews.get(i).getTag();
                item.mAttacher.update();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        pager.setCurrentItem(index);
    }


    class PageAdapter extends android.support.v4.view.PagerAdapter {
        List<View> mViews = new ArrayList<View>();

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews.get(position % 4));
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            while (position % 4 >= mViews.size()) {
                mViews.add(LayoutInflater.from(container.getContext()).inflate(R.layout.layout_item_pager, null));
            }
            View view = mViews.get(position % 4);
            view.setTag(new Item(view, items.get(position), position));
            container.addView(view);
            return view;
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    class Item {
        View root;
        PhotoView imageView;
        public HomeItem data;
        View bar;
        PhotoViewAttacher mAttacher;
        TextView goods;
        TextView comment;
        TextView Address;
        int position;

        public Item(View root, HomeItem item, final int position) {
            this.root = root;
            this.position = position;
            imageView = (PhotoView) root.findViewById(R.id.img);
            this.data = item;
            bar = root.findViewById(R.id.item);
            IMApplication.getApplication().displayImage(item.imgInfo.get(0).imgurl, imageView);
            mAttacher = new PhotoViewAttacher(imageView);
            mAttacher.update();

            comment = (TextView) root.findViewById(R.id.comments);
            goods = (TextView) root.findViewById(R.id.good);
            Address = (TextView) root.findViewById(R.id.address);
            comment.setText(item.commentCount + "");
            goods.setText(item.goodCount + "");
            if (item.j_loc_info != null) {
                SpannableStringBuilder builder = new SpannableStringBuilder(item.j_loc_info.addr);
                builder.setSpan(new AdressSpan(item.j_loc_info.loc_id, item.j_loc_info.addr).setEnable(false), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                Address.setText(builder);
            } else Address.setText("");
            bar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainTabActivity activity = (MainTabActivity) getView().getContext();
                    activity.addFragmentToBackStack(SinglePoFragment.getFragment(data));
                }
            });
            mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                    if (hides[position]) {
                        bar.setVisibility(View.GONE);
                        hides[position] = false;
                    } else {
                        bar.setVisibility(View.VISIBLE);
                        hides[position] = true;
                    }
                }
            });
        }


    }
}
