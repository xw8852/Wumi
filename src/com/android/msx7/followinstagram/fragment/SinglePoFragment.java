package com.android.msx7.followinstagram.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.android.msx7.followinstagram.fragment.TabHomeFragment.HomeItem;
import com.android.msx7.followinstagram.fragment.TabHomeFragment.ZanItem;
import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.CommentActivity;
import com.android.msx7.followinstagram.activity.MainTabActivity;
import com.android.msx7.followinstagram.bean.UserInfo;
import com.android.msx7.followinstagram.bean.dbBean.Good;
import com.android.msx7.followinstagram.common.BaseFragment;
import com.android.msx7.followinstagram.common.BaseResponse;
import com.android.msx7.followinstagram.common.ErrorCode;
import com.android.msx7.followinstagram.net.PoRequest;
import com.android.msx7.followinstagram.net.ZanRequest;
import com.android.msx7.followinstagram.ui.span.AdressSpan;
import com.android.msx7.followinstagram.ui.span.EventSpan;
import com.android.msx7.followinstagram.ui.span.NameSpan;
import com.android.msx7.followinstagram.ui.span.TopicSpan;
import com.android.msx7.followinstagram.ui.text.TextViewFixTouchConsume;
import com.android.msx7.followinstagram.util.DateUtils;
import com.android.msx7.followinstagram.util.L;
import com.android.msx7.followinstagram.util.StringsUtils;
import com.android.msx7.followinstagram.util.ToastUtil;
import com.android.msx7.followinstagram.util.VolleyErrorUtils;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Josn on 2015/9/13.
 */
public class SinglePoFragment extends BaseFragment {
    TabHomeFragment.Holder holder;
    long poId;
    public static final String PARAM_ID = "param_id";

    public static final SinglePoFragment getFragment(long poId) {
        SinglePoFragment fragment = new SinglePoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(PARAM_ID, poId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static final SinglePoFragment getFragment(HomeItem data) {
        SinglePoFragment fragment = new SinglePoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("data", new Gson().toJson(data));
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_single_po, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addBack();
        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder = new TabHomeFragment.Holder(getView().findViewById(R.id.layout_pinned_item));
        getTitleBar().setTitle("照片", null);
        getTitleBar().setRightImg(R.drawable.nav_refresh, freshListener);
        if (getArguments().containsKey("data")) {
            showData(new Gson().fromJson(getArguments().getString("data"), HomeItem.class));
            poId = mItem.id;
        } else {
            poId = getArguments().getLong(PARAM_ID);
            freshListener.onClick(null);
        }


    }

    View.OnClickListener freshListener = new View.OnClickListener() {
        @Override
        public void onClick(View v1) {
            showLoadingDialog(-1);
            final HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("type", "info");
            map.put("i_id", poId);
            IMApplication.getApplication().runVolleyRequest(new PoRequest(Request.Method.GET, new Gson().toJson(map),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            L.d("response---" + response);
                            dismissLoadingDialog();
                            BaseResponse<HomeItem> _respone = new Gson().fromJson(response, new TypeToken<BaseResponse<HomeItem>>() {
                            }.getType());
                            if (_respone.retcode != 0) {
                                ToastUtil.show(_respone.showmsg);
                            } else showData(_respone.retbody);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    dismissLoadingDialog();
                    VolleyErrorUtils.showError(error);
                }
            }));
        }
    };
    HomeItem mItem;

    void showData(final HomeItem item) {
        this.mItem = item;
        if (mItem.userInfo.uid == IMApplication.getApplication().getUserInfo().userId) {
            mItem.userInfo.userName = IMApplication.getApplication().getUserInfo().userName;
        }
        IMApplication application = IMApplication.getApplication();
        application.displayImage(item.userInfo.userImg, holder.userImg);
        holder.userName.setText(item.userInfo.userName);
        holder.userTime.setText(DateUtils.getActivityTime(item.creatTime));
        holder.img.setUrl(item.imgInfo.get(0).imgurl, item.imgInfo.get(0).guys);
        holder.address.setVisibility(View.GONE);
        holder.commentNum.setVisibility(View.GONE);
        holder.commentList.setVisibility(View.GONE);
        holder.goodNames.setVisibility(View.GONE);
        holder.goods.setVisibility(View.GONE);
        holder.desc.setVisibility(View.GONE);
        holder.goods.setOnClickListener(new LikeFragmentListener(item.id));
        holder.event.setVisibility(View.GONE);
        if (item.event != null) {
            SpannableStringBuilder builder = new SpannableStringBuilder(item.event.name);
            builder.setSpan(new EventSpan(item.event), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.event.setText(builder);
            holder.event.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
            holder.event.setVisibility(View.VISIBLE);
        }
        if (item.j_loc_info != null && !TextUtils.isEmpty(item.j_loc_info.addr)) {
            SpannableStringBuilder builder = new SpannableStringBuilder(item.j_loc_info.addr);
            builder.setSpan(new AdressSpan(item.j_loc_info.loc_id, item.j_loc_info.addr), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.address.setText(builder);
            holder.address.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
            holder.address.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(item.desc)) {
            holder.desc.setText(item.desc);
            holder.desc.setVisibility(View.VISIBLE);
            SpannableStringBuilder builder = new SpannableStringBuilder(item.desc);
            L.d("---- " + Arrays.toString(StringsUtils.findString(item.desc)));
            String[] arr = StringsUtils.findString(item.desc);
            if (arr != null) {
                int start = 0;
                int lastEnd = 0;
                for (String _arr : arr) {
                    start = item.desc.indexOf(_arr, start);
                    int end = _arr.length();
                    if (end == 1) continue;
                    if (start < 0) start = lastEnd;
                    if (_arr.startsWith("@")) {
                        builder.setSpan(new NameSpan(_arr.substring(1).trim(), -1), start, start + end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (_arr.startsWith("#")) {
                        String topic = _arr.substring(1).trim();
                        builder.setSpan(new TopicSpan(topic), start, start + end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    start = end;
                    lastEnd = end;
                }
            }
            holder.desc.setText(builder);
            holder.desc.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
        }
//            holder.goodNames.setOnClickListener(new LikeFragmentListener(item.id));
        holder.header.setOnClickListener(new NameListener(item.userInfo.userName, item.userInfo.userId));
        if (!item.isGood) {
            Good good = new Good.GoodDB(LayoutInflater.from(getView().getContext()).getContext()).isGood(String.valueOf(item.id));
            if (good != null) item.isGood = good.good;
        }
        if (item.isGood) {
            holder.good.setImageResource(R.drawable.feed_button_like_active);
            holder.good.setOnClickListener(null);
        } else {
            holder.good.setImageResource(R.drawable.feed_button_like);
            holder.good.setOnClickListener(new GoodListener(item.id));
        }
        if (item.goodCount > 0) {
            holder.goods.setText(item.goodCount + " 个赞");
            holder.goods.setVisibility(View.VISIBLE);
        }
        if (item.zans != null && !item.zans.isEmpty() && item.goodCount < 10) {
            holder.goodNames.setVisibility(View.VISIBLE);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            for (ZanItem zan : item.zans) {
                int start = builder.length();
                builder.append(zan.name);
                builder.setSpan(new NameSpan(item.userInfo.userName, item.userInfo.uid), start, start + zan.name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            Paint paint = new Paint();
            paint.setTextSize(holder.goodNames.getTextSize());
            float length = paint.measureText(builder, 0, builder.length());
            //计算textview长度

            float textviewLenth = length + holder.goodNames.getPaddingLeft() + holder.goodNames.getPaddingRight() + holder.goodNames.getCompoundDrawablePadding() + holder.goodNames.getCompoundDrawables()[0].getIntrinsicWidth();
            DisplayMetrics dm = LayoutInflater.from(getView().getContext()).getContext().getResources().getDisplayMetrics();
            L.d("textviewLenth :" + textviewLenth + "," + dm.widthPixels);
            if (textviewLenth > dm.widthPixels) {
                holder.goodNames.setVisibility(View.GONE);
            }
            holder.goodNames.setText(builder);
            holder.goodNames.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CommentActivity.class);
                intent.putExtra(CommentActivity.PARAM_PO_ID, item.id);
                startActivityForResult(intent, TabHomeFragment.RESULT_COMMENT);
            }
        };
        if (item.commentCount > 0) {
            holder.commentNum.setText(getString(R.string.comments, item.commentCount));
            holder.commentNum.setVisibility(View.VISIBLE);
            holder.commentNum.setOnClickListener(listener);
        }
        holder.comment.setOnClickListener(listener);
    }

    class LikeFragmentListener implements View.OnClickListener {
        long poid;

        public LikeFragmentListener(long poid) {
            this.poid = poid;
        }

        @Override
        public void onClick(View v) {
            MainTabActivity activity = (MainTabActivity) v.getContext();
            GoodListFragment fragment = new GoodListFragment();
            Bundle bundle = new Bundle();
            bundle.putLong(GoodListFragment.PARAM_PO_ID, poid);
            fragment.setArguments(bundle);
            activity.addFragmentToBackStack(fragment);

        }
    }

    public class NameListener implements View.OnClickListener {
        String name;
        long id;

        public NameListener(String name, long id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public void onClick(View view) {
            if (view.getContext() instanceof MainTabActivity) {
                MainTabActivity activity = (MainTabActivity) view.getContext();
                TabProfileFragment fragment = new TabProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putLong(TabProfileFragment.PARAM_USER_ID, id);
                bundle.putString(TabProfileFragment.PARAM_USER_NAME, name);
                fragment.setArguments(bundle);
                activity.addFragmentToBackStack(fragment);
            } else
                L.d("-----widget--" + name + "," + id);
        }
    }

    public class GoodListener implements View.OnClickListener {
        long poId;

        public GoodListener(long poId) {
            this.poId = poId;
        }

        @Override
        public void onClick(final View v) {
            v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.fade_out));
            final HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("i_po_id", poId);
            map.put("type", "zan");
            map.put("chkcode", IMApplication.getApplication().getchkcode());
            showLoadingDialog(-1);
            IMApplication.getApplication().runVolleyRequest(new ZanRequest(new Gson().toJson(map), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    L.d(response);
                    dismissLoadingDialog();
                    BaseResponse _re = new Gson().fromJson(response, BaseResponse.class);
                    if (_re.retcode == ErrorCode.E_DUP_OPERATION || _re.retcode == 0) {
                        HomeItem item = mItem;
                        if (_re.retcode == 0) {
                            item.isGood = true;
                            item.goodCount++;
                            ZanItem zanItem = new ZanItem();
                            UserInfo info = IMApplication.getApplication().getUserInfo();
                            zanItem.usePic = info.userImg;
                            zanItem.name = info.userName;
                            zanItem.userId = info.userId;
                            if (item.zans == null) item.zans = new ArrayList<ZanItem>();
                            item.zans.add(zanItem);
                        }
                        new Good.GoodDB(v.getContext()).insertOrUpdate(new Good(String.valueOf(poId), true));
                        showData(item);
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
    }
}
