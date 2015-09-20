package com.android.msx7.followinstagram.fragment;

import android.content.Intent;
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
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.CommentActivity;
import com.android.msx7.followinstagram.activity.MainTabActivity;
import com.android.msx7.followinstagram.bean.UserInfo;
import com.android.msx7.followinstagram.bean.dbBean.Good;
import com.android.msx7.followinstagram.common.BaseFragment;
import com.android.msx7.followinstagram.common.BaseResponse;
import com.android.msx7.followinstagram.common.ErrorCode;
import com.android.msx7.followinstagram.fragment.TabHomeFragment.HomeItem;
import com.android.msx7.followinstagram.net.ZanRequest;
import com.android.msx7.followinstagram.ui.image.PhotoImageView;
import com.android.msx7.followinstagram.ui.span.AdressSpan;
import com.android.msx7.followinstagram.util.L;
import com.android.msx7.followinstagram.util.ToastUtil;
import com.android.msx7.followinstagram.util.VolleyErrorUtils;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
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
        addBack();
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
                getTitleBar().setTitle("照片(" + (i + 1) + "/" + adapter.getCount() + ")", null);
//                if (i >= adapter.mViews.size()) return;
//                Item item = (Item) adapter.mViews.get(i).getTag();
//                PhotoViewAttacher.OnPhotoTapListener listener = item.mAttacher.getOnPhotoTapListener();
//                item.mAttacher.update();
//                item.mAttacher.setOnPhotoTapListener(listener);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        pager.setCurrentItem(index);
        getTitleBar().setTitle("照片(" + (index + 1) + "/" + adapter.getCount() + ")", null);
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
            if (view.getTag() != null) {
                Item item = (Item) view.getTag();
                item.updateItem(items.get(position), position);
            } else
                view.setTag(new Item(view, items.get(position), position));
//            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_item_pager, null);
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
        PhotoImageView imageView;
        public HomeItem data;
        View bar;
        TextView goods;
        TextView comment;
        TextView Address;
        int position;

        public Item(View root, HomeItem item, final int position) {
            this.root = root;
            imageView = (PhotoImageView) root.findViewById(R.id.img);
            comment = (TextView) root.findViewById(R.id.comments);
            goods = (TextView) root.findViewById(R.id.good);
            Address = (TextView) root.findViewById(R.id.address);

            bar = root.findViewById(R.id.item);
            this.position = position;
            this.data = item;
            updateItem(data, position);
        }

        public void update() {
            updateItem(data, position);
        }

        public void updateItem(HomeItem item, final int position) {
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.show("onClick");
                }
            });
            this.position = position;
            this.data = item;
            if (hides[position]) Address.setVisibility(View.VISIBLE);
            else Address.setVisibility(View.GONE);
            imageView.setUrl(item.imgInfo.get(0).imgurl,item.imgInfo.get(0).guys);
            comment.setText(item.commentCount + "");
            goods.setText(item.goodCount + "");
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), CommentActivity.class);
                    intent.putExtra(CommentActivity.PARAM_PO_ID, data.id);
                    startActivityForResult(intent, TabHomeFragment.RESULT_COMMENT);
                }
            };
            comment.setOnClickListener(listener);
            if (item.j_loc_info != null) {
                SpannableStringBuilder builder = new SpannableStringBuilder(item.j_loc_info.addr);
                builder.setSpan(new AdressSpan(item.j_loc_info.loc_id, item.j_loc_info.addr).setEnable(false), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                Address.setText(builder);
                Address.setVisibility(View.VISIBLE);
            } else Address.setText("");
            bar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainTabActivity.addFragmentToBackStack(SinglePoFragment.getFragment(data), v.getContext());
                }
            });
            goods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.fade_out));
                    final HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("i_po_id", data.id);
                    map.put("type", "zan");
                    map.put("chkcode", IMApplication.getApplication().getchkcode());
                    showLoadingDialog(-1);
                    IMApplication.getApplication().runVolleyRequest(new ZanRequest(new Gson().toJson(map), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            L.d(response);

                            BaseResponse _re = new Gson().fromJson(response, BaseResponse.class);
                            if (_re.retcode == ErrorCode.E_DUP_OPERATION || _re.retcode == 0) {
                                if (_re.retcode == 0) {
                                    data.isGood = true;
                                    data.goodCount++;
                                    TabHomeFragment.ZanItem zanItem = new TabHomeFragment.ZanItem();
                                    UserInfo info = IMApplication.getApplication().getUserInfo();
                                    zanItem.usePic = info.userImg;
                                    zanItem.name = info.userName;
                                    zanItem.userId = info.userId;
                                    if (data.zans == null)
                                        data.zans = new ArrayList<TabHomeFragment.ZanItem>();
                                    data.zans.add(zanItem);
                                }
                                new Good.GoodDB(v.getContext()).insertOrUpdate(new Good(String.valueOf(data.id), true));
                                goods.setText(data.goodCount + "");
                                dismissLoadingDialog();
                            } else if (_re.retcode != 0) {
                                ToastUtil.show(_re.showmsg);
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dismissLoadingDialog();
                            error.printStackTrace();
                            VolleyErrorUtils.showError(error);
                        }
                    }));
                }
            });
        }

        public class GoodListener implements View.OnClickListener {
            long poId;

            public GoodListener(long poId) {
                this.poId = poId;
            }

            @Override
            public void onClick(final View v) {

            }
        }

    }


}
