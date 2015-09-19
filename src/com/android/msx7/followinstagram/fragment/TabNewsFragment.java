package com.android.msx7.followinstagram.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.db.BaseTable;
import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.MainTabActivity;
import com.android.msx7.followinstagram.bean.dbBean.ActionDB;
import com.android.msx7.followinstagram.common.BaseAdapter;
import com.android.msx7.followinstagram.common.BaseFragment;
import com.android.msx7.followinstagram.common.BaseResponse;
import com.android.msx7.followinstagram.common.YohoField;
import com.android.msx7.followinstagram.net.BaseRequest;
import com.android.msx7.followinstagram.ui.actionbar.ActionBar;
import com.android.msx7.followinstagram.ui.push.PageFooter;
import com.android.msx7.followinstagram.ui.push.PushHeader;
import com.android.msx7.followinstagram.ui.span.NameSpan;
import com.android.msx7.followinstagram.ui.text.TextViewFixTouchConsume;
import com.android.msx7.followinstagram.util.DialogUtils;
import com.android.msx7.followinstagram.util.L;
import com.android.msx7.followinstagram.util.ToastUtil;
import com.android.msx7.followinstagram.util.VolleyErrorUtils;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by Josn on 2015/9/10.
 */
public class TabNewsFragment extends BaseFragment {
    ListView mListView;
    PushHeader header;
    FollowAdapter mAdapter;
    PageFooter footer;
    TextView empty;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) getView().findViewById(R.id.list);
        header = new PushHeader(mListView, refreshListener);
        mAdapter = new FollowAdapter(getView().getContext(), new ArrayList<MessageBean>());
        mListView.setAdapter(mAdapter);
        footer = new PageFooter(mListView, mAdapter);
        footer.setLoadMoreListener(loadMoreListener);
        footer.updateStatus(0, 0);
        getTitleBar().setTitle("消息", null);
    }

    @Override
    public void onResume() {
        super.onResume();
        header.onRefresh();
    }

    PushHeader.OnRefreshListener refreshListener = new PushHeader.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refresh();
        }
    };

    void validEmpty() {
        if (empty == null) empty = (TextView) getView().findViewById(R.id.empty);
        if (mAdapter.getCount() > 0) {
            empty.setVisibility(View.GONE);
            return;
        }
        empty.setVisibility(View.VISIBLE);
        empty.setText("暂时没有消息");
    }

    public void refresh() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("type", "list");
        map.put("pageno", 0);
        map.put("chkcode", IMApplication.getApplication().getchkcode());
        IMApplication.getApplication().runVolleyRequest(new BaseRequest(Request.Method.POST, YohoField.URL_MESSAGE, new Gson().toJson(map), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.d(response);
                BaseResponse<List<MessageBean>> result = new Gson().fromJson(response, new TypeToken<BaseResponse<List<MessageBean>>>() {
                }.getType());
                header.onRefreshComplete();
                footer.updateStatus(0, 0);
                if (result.retbody != null && !result.retbody.isEmpty() && result.retbody.size() >= 10)
                    footer.updateStatus(0, 1);
                if (result.retcode != 0) {
                    ToastUtil.show(result.showmsg);
                } else {
                    sendClear();
                    mAdapter.changeData(result.retbody);
                    if (mListView.getLastVisiblePosition() - mListView.getHeaderViewsCount() > 10) {
                        footer.pushLoadMore();
                    }
                }
                validEmpty();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                header.onRefreshComplete();
                footer.updateStatus(0, 0);
                error.printStackTrace();
                VolleyErrorUtils.showError(error);
                validEmpty();
            }
        }));
    }

    PageFooter.ILoadMoreListener loadMoreListener = new PageFooter.ILoadMoreListener() {
        @Override
        public void loadMore(final int nextPage) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("type", "list");
            map.put("pageno", nextPage);
            map.put("chkcode", IMApplication.getApplication().getchkcode());
            IMApplication.getApplication().runVolleyRequest(new BaseRequest(Request.Method.POST, YohoField.URL_MESSAGE, new Gson().toJson(map), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    L.d(response);
                    BaseResponse<List<MessageBean>> result = new Gson().fromJson(response, new TypeToken<BaseResponse<List<MessageBean>>>() {
                    }.getType());
                    header.onRefreshComplete();
                    footer.updateStatus(nextPage, nextPage);
                    if (result.retbody != null && !result.retbody.isEmpty() && result.retbody.size() % 10 == 0)
                        footer.updateStatus(nextPage, nextPage + 1);
                    if (result.retcode != 0) {
                        ToastUtil.show(result.showmsg);
                    } else
                        mAdapter.addMore(result.retbody);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    header.onRefreshComplete();
                    int page = nextPage - 1;
                    footer.updateStatus(page, page + 1);
                    error.printStackTrace();
                    VolleyErrorUtils.showError(error);
                }
            }));
        }
    };

    class FollowAdapter extends BaseAdapter<MessageBean> {
        public FollowAdapter(Context ctx, List<MessageBean> data) {
            super(ctx, data);
        }

        public FollowAdapter(Context ctx, MessageBean... data) {
            super(ctx, data);
        }

        @Override
        public View getView(int position, View convertView, LayoutInflater inflater) {
            Holder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_follow_item, null);
                holder = new Holder();
                holder.profileImg = (ImageView) convertView.findViewById(R.id.profileImg);
                holder.comment = (TextViewFixTouchConsume) convertView.findViewById(R.id.comment);
                holder.btn = (TextView) convertView.findViewById(R.id.btn_follow);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                convertView.setTag(holder);
            } else holder = (Holder) convertView.getTag();
            final MessageBean bean = getItem(position);
            IMApplication.getApplication().displayImage(bean.j_detail.s_send_uimage, holder.profileImg);
            SpannableStringBuilder builder = new SpannableStringBuilder(bean.userName);
            builder.setSpan(new NameSpan(bean.userName, bean.i_send_uid), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (bean.j_detail.content.startsWith(bean.userName)) {
                bean.j_detail.content = bean.j_detail.content.replace(bean.userName, " ");
            }
            builder.append(" " + bean.j_detail.content);
            holder.comment.setText(builder);
            holder.comment.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
            holder.btn.setVisibility(View.GONE);
            holder.img.setVisibility(View.GONE);
            holder.profileImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainTabActivity activity = (MainTabActivity) v.getContext();
                    TabProfileFragment fragment = new TabProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putLong(TabProfileFragment.PARAM_USER_ID, bean.i_send_uid);
                    bundle.putString(TabProfileFragment.PARAM_USER_NAME, bean.userName);
                    fragment.setArguments(bundle);
                    activity.addFragmentToBackStack(fragment);
                }
            });
            if (bean.j_detail.i_po_id > 0) {
                holder.img.setVisibility(View.VISIBLE);
                IMApplication.getApplication().displayImage(bean.j_detail.imgs.get(0), holder.img);
                holder.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainTabActivity activity = (MainTabActivity) v.getContext();
                        activity.addFragmentToBackStack(SinglePoFragment.getFragment(bean.j_detail.i_po_id));
                    }
                });
            } else if ((bean.i_type == 112 || bean.i_type == 109) && bean.j_detail.i_activity_id > 0 &&
                    // 查询本地数据库是否对进行过此操作
                    !new ActionDB.ActionDatabase(inflater.getContext()).hasRead(bean.i_message_id)) {
                //type 112表示申请 type 111表示操作结果
                holder.btn.setVisibility(View.VISIBLE);
                holder.btn.setText("通过");
                holder.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtils.ShowDialog("活动申请", bean.j_detail.content, "通过", new PassListener(bean), "否决", new NoPassListener(bean), v.getContext());
                    }
                });
            }
            return convertView;
        }

        class Holder {
            ImageView profileImg;
            TextViewFixTouchConsume comment;
            TextView btn;
            ImageView img;

        }
    }

    class PassListener implements View.OnClickListener {
        MessageBean bean;

        public PassListener(MessageBean bean) {
            this.bean = bean;
        }

        @Override
        public void onClick(View v) {
            showLoadingDialog(-1);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("type", "process");
            map.put("i_msg_id", bean.i_message_id);
            map.put("in_or_not", 1);
            map.put("i_activity_id", bean.j_detail.i_activity_id);
            map.put("i_apply_uid", bean.i_send_uid);
            map.put("chkcode", IMApplication.getApplication().getchkcode());
            Response.ErrorListener listener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyErrorUtils.showError(error);
                    dismissLoadingDialog();
                }
            };
            Response.Listener<String> resultListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    L.d(response);
                    dismissLoadingDialog();
                    BaseResponse re = new Gson().fromJson(response, BaseResponse.class);
                    if (re.retcode != 0) {
                        ToastUtil.show(re.showmsg);
                    } else {
                        new BaseTable<ActionDB>(IMApplication.getApplication()) {
                        }.insertOrUpdate(new ActionDB(bean.i_message_id));
                        ToastUtil.show("成功通过申请");
                        onResume();
                    }
                }
            };
            IMApplication.getApplication().runVolleyRequest(new BaseRequest(Request.Method.POST, YohoField.URL_ACTIVITY, new Gson().toJson(map), resultListener, listener));
        }
    }

    class NoPassListener implements View.OnClickListener {
        MessageBean bean;

        public NoPassListener(MessageBean bean) {
            this.bean = bean;
        }

        @Override
        public void onClick(View v) {
            showLoadingDialog(-1);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("type", "process");
            map.put("i_msg_id", bean.i_message_id);
            map.put("in_or_not", 0);
            map.put("i_activity_id", bean.j_detail.i_activity_id);
            map.put("i_apply_uid", bean.i_send_uid);
            map.put("chkcode", IMApplication.getApplication().getchkcode());
            Response.ErrorListener listener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyErrorUtils.showError(error);
                    dismissLoadingDialog();
                }
            };
            Response.Listener<String> resultListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    L.d(response);
                    dismissLoadingDialog();
                    BaseResponse re = new Gson().fromJson(response, BaseResponse.class);
                    if (re.retcode != 0) {
                        ToastUtil.show(re.showmsg);
                        new BaseTable<ActionDB>(IMApplication.getApplication()) {
                        }.insertOrUpdate(new ActionDB(bean.i_message_id));
                    } else {
                        ToastUtil.show("成功拒绝申请");
                        onResume();
                    }
                }
            };
            IMApplication.getApplication().runVolleyRequest(new BaseRequest(Request.Method.POST, YohoField.URL_ACTIVITY, new Gson().toJson(map), resultListener, listener));
        }
    }

    void sendClear() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("type", "clear");
        map.put("chkcode", IMApplication.getApplication().getchkcode());
        IMApplication.getApplication().runVolleyRequest(new BaseRequest(Request.Method.POST, YohoField.URL_MESSAGE, new Gson().toJson(map), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));
    }

    /***
     * "i_recv_uid": 1,             # 接受者UID
     * #             "i_send_uid": 3,             # 发送者UID
     * #             "i_creat_time": 1441857804,  # 消息产生时间
     * #             "i_type": 110,               # 消息类型
     * #             "j_detail": {                # 消息体，消息体中，content字段及s_send_uimage字段是必须有的。
     * #                 "i_activity_id": 7,
     * #                 "content": "\u606d\u559c\u4f60\uff0c\u5927\u6c34\u725b1234 \u901a\u8fc7\u4e86\u4f60\u7684\u7533\u8bf7\uff0c\u5feb\u52a0\u5165\u6d3b\u52a8\"\u5927\u522b\u5c71\u79d8\u5bc6\u641c\u7d22\u961f\"\u53d1\u8868\u56fe\u7247\u5427\uff01",  # 消息内容
     * #                 "s_send_uimage": "http://pic.yooho.me/p/95_abs",  # 发送者头像
     * #                 "s_activity_name": "\u5927\u522b\u5c71\u79d8\u5bc6\u641c\u7d22\u961f"
     * #                 },
     * #             "s_send_uname": "\u5927\u6c34\u725b1234",  # 发送者用户名
     * #             "i_message_id": 176,                       # 消息ID
     * #             "i_has_read": 0                            #
     */
    public static class MessageBean {
        // 发送者用户名
        @SerializedName("s_send_uname")
        public String userName;
        @SerializedName("i_message_id")
//        消息ID
        public int i_message_id;
        //发送者UID
        @SerializedName("i_send_uid")
        public int i_send_uid;
        //接受者UID
        @SerializedName("i_recv_uid")
        public long i_recv_uid;
        //        消息产生时间
        @SerializedName("i_creat_time")
        public long i_creat_time;
        //        消息类型
        @SerializedName("i_type")
        public int i_type;
        //是否读过  如果用户有3个未读计数，那么把前三个消息贴上标记。0未读
        @SerializedName("i_has_read")
        public int i_has_read;
        @SerializedName("j_detail")
        public Messagedetail j_detail;

    }

    public class Messagedetail {
        //i_activity_id
        @SerializedName("i_activity_id")
        public long i_activity_id;
        //        发送者头像
        @SerializedName("s_send_uimage")
        public String s_send_uimage;
        @SerializedName("s_activity_name")
        public String s_activity_name;
        //消息内容
        @SerializedName("content")
        public String content;
        //回复内容
        @SerializedName("reply")
        public String reply;
        //产生消息的po_id
        @SerializedName("i_po_id")
        public long i_po_id;
        @SerializedName("l_img_url_list")
        public List<String> imgs;

    }

}
