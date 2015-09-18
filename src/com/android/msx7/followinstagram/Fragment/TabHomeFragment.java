package com.android.msx7.followinstagram.Fragment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.CommentActivity;
import com.android.msx7.followinstagram.activity.MainTabActivity;
import com.android.msx7.followinstagram.bean.ImgInfo;
import com.android.msx7.followinstagram.bean.UserInfo;
import com.android.msx7.followinstagram.bean.dbBean.Good;
import com.android.msx7.followinstagram.common.BaseFragment;
import com.android.msx7.followinstagram.common.BaseResponse;
import com.android.msx7.followinstagram.common.ErrorCode;
import com.android.msx7.followinstagram.common.YohoField;
import com.android.msx7.followinstagram.net.BaseRequest;
import com.android.msx7.followinstagram.net.FeedRequest;
import com.android.msx7.followinstagram.net.ZanRequest;
import com.android.msx7.followinstagram.ui.image.ContactImageView;
import com.android.msx7.followinstagram.ui.push.PageFooter;
import com.android.msx7.followinstagram.ui.push.PushHeader;
import com.android.msx7.followinstagram.ui.span.AdressSpan;
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
import com.android.widget.PinnedAdapter;
import com.android.widget.PinnedHeaderListView;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Josn on 2015/9/10.
 */
public class TabHomeFragment extends BaseFragment {
    public static final String PARAM_TAG = "param_tag";
    public static final String PARAM_ADRESS = "param_ADRESS";
    public static final String PARAM_ADRESS_NAME = "param_adress_name";
    PinnedHeaderListView listView;
    HomeAdapter mAdapter;
    PushHeader header;
    PageFooter footer;
    public static final int RESULT_COMMENT = 0x100;
    String tag;
    long addressId = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_home_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getTitleBar().setTitle("动态", null);
        if (getArguments() != null && getArguments().containsKey(PARAM_TAG)) {
            tag = getArguments().getString(PARAM_TAG);
            getTitleBar().setTitle(tag, null);
        }
        if (getArguments() != null && getArguments().containsKey(PARAM_ADRESS)) {
            addressId = getArguments().getLong(PARAM_ADRESS);
            getTitleBar().setTitle(getArguments().getString(PARAM_ADRESS_NAME), null);
        }
        listView = (PinnedHeaderListView) getView().findViewById(R.id.pinListView);
        View _view = getView().findViewById(R.id.pin_header);
        _view.setVisibility(View.INVISIBLE);
        listView.setPinHeader(_view);
        header = new PushHeader(listView, onRefreshListener);
        mAdapter = new HomeAdapter(getView().getContext(), new ArrayList<HomeItem>());
        footer = new PageFooter(listView, mAdapter);
        listView.setAdapter(mAdapter);
        footer.setLoadMoreListener(moreListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        header.onRefresh();
    }

    PageFooter.ILoadMoreListener moreListener = new PageFooter.ILoadMoreListener() {
        @Override
        public void loadMore(final int nextPage) {
            map.remove("page");
            map.put("pageno", nextPage);
            String url = YohoField.URL_FEED;
            int method = Request.Method.POST;
            if (!TextUtils.isEmpty(tag)) {
                url = YohoField.URL_PO;
                method = Request.Method.GET;
                pageSize = 18;
            } else if (addressId != -1) {
                pageSize = 18;
                map.put("i_loc_id", addressId);
                url = YohoField.URL_PO;
                method = Request.Method.GET;
            }
            IMApplication.getApplication().runVolleyRequest(new BaseRequest(method, url, new Gson().toJson(map),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            header.onRefreshComplete();
                            L.d("response---" + response);
                            BaseResponse<List<HomeItem>> _respone = new Gson().fromJson(response, new TypeToken<BaseResponse<List<HomeItem>>>() {
                            }.getType());
                            L.d("response---" + _respone.list_num + "," + _respone.retbody.size());
                            footer.updateStatus(nextPage, nextPage);
                            if (_respone.retcode != 0) {
                                ToastUtil.show(_respone.showmsg);
                            } else {
                                mAdapter.addMore(_respone.retbody);
                                if (mAdapter.getCount() % pageSize == 0)
                                    footer.updateStatus(nextPage, nextPage + 1);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    header.onRefreshComplete();
                }
            }));
        }
    };

    int pageSize = 10;
    HashMap<String, Object> map = new HashMap<String, Object>();
    PushHeader.OnRefreshListener onRefreshListener = new PushHeader.OnRefreshListener() {
        @Override
        public void onRefresh() {
            map.clear();
            map.put("pageno", 0);
            String url = YohoField.URL_FEED;
            int method = Request.Method.POST;
            if (!TextUtils.isEmpty(tag)) {
                map.put("type", "tag");
                map.put("s_tag", tag);
                url = YohoField.URL_PO;
                method = Request.Method.GET;
                pageSize = 18;
            } else if (addressId != -1) {
                map.put("type", "location");
                pageSize = 18;
                map.put("i_loc_id", addressId);
                url = YohoField.URL_PO;
                method = Request.Method.GET;
            } else {
                map.put("type", "list");
                map.put("chkcode", IMApplication.getApplication().getchkcode());
            }
            IMApplication.getApplication().runVolleyRequest(new BaseRequest(method, url, new Gson().toJson(map),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            header.onRefreshComplete();
                            L.d("response---" + response);
                            BaseResponse<List<HomeItem>> _respone = new Gson().fromJson(response, new TypeToken<BaseResponse<List<HomeItem>>>() {
                            }.getType());
                            L.d("response---" + _respone.list_num + "," + _respone.retbody.size());
                            footer.updateStatus(0, 0);
                            if (_respone.retcode != 0) {
                                ToastUtil.show(_respone.showmsg);
                            } else {
                                mAdapter.changeData(_respone.retbody);
                                if (mAdapter.getCount() == pageSize)
                                    footer.updateStatus(0, 1);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    header.onRefreshComplete();
                }
            }));
        }
    };

    class HomeAdapter extends PinnedAdapter<HomeItem> {
        public HomeAdapter(Context ctx, List<HomeItem> data) {
            super(ctx, data);
        }

        public HomeAdapter(Context ctx, HomeItem... data) {
            super(ctx, data);
        }


        @Override
        public void configHeaderView(int position, View header) {
            HeaderHolder headerHolder;
            if (header.getTag() == null) {
                headerHolder = new HeaderHolder(header);
            } else
                headerHolder = (HeaderHolder) header.getTag();
            HomeItem item = getItem(position);
            IMApplication application = IMApplication.getApplication();
            application.displayImage(item.userInfo.userImg, headerHolder.userImg);
            headerHolder.userName.setText(item.userInfo.userName);
            headerHolder.userTime.setText(DateUtils.getActivityTime(item.creatTime));
            header.setOnClickListener(new NameListener(item.userInfo.userName, item.userInfo.userId));
        }

        public void update(HomeItem item, int position) {
            this.data.set(position, item);
            this.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, final LayoutInflater inflater) {
            Holder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_pinned_item, null);
                ((ViewGroup) convertView).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            }
            holder = (Holder) convertView.getTag();
            final HomeItem item = getItem(position);
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
                            builder.setSpan(new TopicSpan(topic).setEnable(topic.equals(tag)), start, start + end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        start = end;
                        lastEnd = end;
                    }
                }
                holder.desc.setText(builder);
                holder.desc.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
            }
            holder.header.setOnClickListener(new NameListener(item.userInfo.userName, item.userInfo.userId));
            if (!item.isGood) {
                Good good = new Good.GoodDB(inflater.getContext()).isGood(String.valueOf(item.id));
                if (good != null) item.isGood = good.good;
            }
            if (item.isGood) {
                holder.good.setImageResource(R.drawable.feed_button_like_active);
                holder.good.setOnClickListener(null);
            } else {
                holder.good.setImageResource(R.drawable.feed_button_like);
                holder.good.setOnClickListener(new GoodListener(position, item.id));
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
                    builder.setSpan(new NameSpan(item.userInfo.userName, item.userInfo.userId), start, start + zan.name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                Paint paint = new Paint();
                paint.setTextSize(holder.goodNames.getTextSize());
                float length = paint.measureText(builder, 0, builder.length());
                //计算textview长度

                float textviewLenth = length + holder.goodNames.getPaddingLeft() + holder.goodNames.getPaddingRight() + holder.goodNames.getCompoundDrawablePadding() + holder.goodNames.getCompoundDrawables()[0].getIntrinsicWidth();
                DisplayMetrics dm = inflater.getContext().getResources().getDisplayMetrics();
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
                    startActivityForResult(intent, RESULT_COMMENT);
                }
            };
            if (item.commentCount > 0) {
                holder.commentNum.setText(getString(R.string.comments, item.commentCount));
                holder.commentNum.setVisibility(View.VISIBLE);
                holder.commentNum.setOnClickListener(listener);
            }
            holder.comment.setOnClickListener(listener);
            return convertView;
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


        public class HeaderHolder {
            View header;
            ImageView userImg;
            TextView userName;
            TextView userTime;

            public HeaderHolder(View view) {
                header = view;
                userImg = (ImageView) header.findViewById(R.id.userImg);
                userName = (TextView) header.findViewById(R.id.username);
                userTime = (TextView) header.findViewById(R.id.time);

            }
        }


    }

    public static class Holder {
        View root;
        View header;
        ImageView userImg;
        TextView userName;
        TextView userTime;
        ContactImageView img;
        ImageView good;
        ImageView comment;
        //显示赞的总人数
        TextView goods;
        //显示赞的人
        TextViewFixTouchConsume goodNames;
        //显示地址，如果有的话
        TextViewFixTouchConsume address;
        //显示评论总数
        TextView commentNum;
        TextViewFixTouchConsume desc;
        //评论列表，只显示有限的几个
        LinearLayout commentList;

        public Holder(View view) {
            root = view;
            header = root.findViewById(R.id.pin_header);
            userImg = (ImageView) header.findViewById(R.id.userImg);
            img = (ContactImageView) root.findViewById(R.id.ContactImageView);
            good = (ImageView) root.findViewById(R.id.good);
            goodNames = (TextViewFixTouchConsume) root.findViewById(R.id.goodNames);
            comment = (ImageView) root.findViewById(R.id.comments);
            userName = (TextView) header.findViewById(R.id.username);
            userTime = (TextView) header.findViewById(R.id.time);
            goods = (TextView) root.findViewById(R.id.goods);
            desc = (TextViewFixTouchConsume) root.findViewById(R.id.desc);
            address = (TextViewFixTouchConsume) root.findViewById(R.id.address);
            commentNum = (TextView) root.findViewById(R.id.commentNum);
            commentList = (LinearLayout) root.findViewById(R.id.commentList);
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
        int posistion;
        long poId;

        public GoodListener(int posistion, long poId) {
            this.posistion = posistion;
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
                        HomeItem item = mAdapter.getItem(posistion);
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
                        mAdapter.update(item, posistion);
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


    public static class HomeItem {
        @SerializedName("l_img_info_list")
        public List<ImgInfo> imgInfo;
        @SerializedName("j_user_info")
        public UserInfo userInfo;
        @SerializedName("s_desc")
        public String desc;
        @SerializedName("i_creat_time")
        public long creatTime;
        @SerializedName("i_status")
        public int status;
        @SerializedName("i_cmt_count")
        public int commentCount;
        @SerializedName("i_zan_count")
        public int goodCount;
        @SerializedName("_id")
        public long id;
        @SerializedName("l_zan_user_list")
        public List<ZanItem> zans;
        public boolean isGood;
        @SerializedName("j_loc_info")
        public Location j_loc_info;
    }

    public static class Location {
        @SerializedName("loc_id")
        public long loc_id;
        @SerializedName("addr")
        public String addr;
    }

    public static class ZanItem {
        @SerializedName("s_user_name")
        public String name;
        @SerializedName("s_user_image")
        public String usePic;
        @SerializedName("i_user_id")
        public long userId;
    }

}
