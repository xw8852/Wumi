package com.android.msx7.followinstagram.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.common.BaseActivity;
import com.android.msx7.followinstagram.common.BaseAdapter;
import com.android.msx7.followinstagram.common.BaseResponse;
import com.android.msx7.followinstagram.common.YohoField;
import com.android.msx7.followinstagram.net.BaseRequest;
import com.android.msx7.followinstagram.ui.push.PageFooter;
import com.android.msx7.followinstagram.ui.push.PushHeader;
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
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Josn on 2015/9/12.
 */
public class CommentActivity extends BaseActivity {
    //    SwipeListView mListView;
    ListView mListView;
    public static final String PARAM_PO_ID = "param_po_id";
    public long poId;
    CommentAdapter mAdapter;

    EditText editText;
    TextView mShareBtn;
    PushHeader header;
    PageFooter footer;
    CommentItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);
        poId = getIntent().getLongExtra(PARAM_PO_ID, -1);
        addBack();
        mAdapter = new CommentAdapter(this, new ArrayList<CommentItem>());
        editText = (EditText) findViewById(R.id.comment);
        mShareBtn = (TextView) findViewById(R.id.direct_private_share_action_button);
        mShareBtn.setOnClickListener(mShareListener);
        mShareBtn.setEnabled(false);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    mShareBtn.setEnabled(false);
                else mShareBtn.setEnabled(true);
            }
        });


//        mListView = (SwipeListView) findViewById(R.id.swipeListView);
//        mListView.setSwipeListViewListener(listViewListener);
//        View view = getLayoutInflater().inflate(R.layout.layout_comment_ac_item, null).findViewById(R.id.back);
//        ViewUtils.measureView(view);
//        int offset = view.getMeasuredWidth();
//        offset = getResources().getDisplayMetrics().widthPixels - offset;
//        mListView.setOffsetLeft(offset);
        mListView = (ListView) findViewById(R.id.list);
        header = new PushHeader(mListView, refreshListener);
        footer = new PageFooter(mListView, mAdapter);
        footer.setLoadMoreListener(loadMoreListener);
        footer.updateStatus(0, 0);
        mListView.setAdapter(mAdapter);
        header.onRefresh();
        mListView.setOnItemClickListener(itemClickListener);
        getTitleBar().setTitle("评论", null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                L.d(" MotionEvent.ACTION_UP ");
                item = null;
                editText.setHint("评论");
                break;
        }
        return super.onTouchEvent(ev);
    }


    ListView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (id < 0) return;
            position = (int) id;
            mListView.clearChoices();
            item = mAdapter.getItem(position);
            editText.setHint("回复 " + item.cmtName);
        }
    };

    PushHeader.OnRefreshListener refreshListener = new PushHeader.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refresh();
        }
    };
    PageFooter.ILoadMoreListener loadMoreListener = new PageFooter.ILoadMoreListener() {
        @Override
        public void loadMore(final int nextPage) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("pageno", nextPage);
            map.put("type", "list");
            map.put("i_po_id", poId);
            IMApplication.getApplication().runVolleyRequest(new BaseRequest(Request.Method.GET, YohoField.URL_COMMET, new Gson().toJson(map), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    L.d(response);
                    BaseResponse<List<CommentItem>> result = new Gson().fromJson(response, new TypeToken<BaseResponse<List<CommentItem>>>() {
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


    public void refresh() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("pageno", 0);
        map.put("type", "list");
        map.put("i_po_id", poId);
        IMApplication.getApplication().runVolleyRequest(new BaseRequest(Request.Method.GET, YohoField.URL_COMMET, new Gson().toJson(map), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.d(response);
                BaseResponse<List<CommentItem>> result = new Gson().fromJson(response, new TypeToken<BaseResponse<List<CommentItem>>>() {
                }.getType());
                header.onRefreshComplete();
                footer.updateStatus(0, 0);
                if (result.retbody != null && !result.retbody.isEmpty() && result.retbody.size() >= 10)
                    footer.updateStatus(0, 1);
                if (result.retcode != 0) {
                    ToastUtil.show(result.showmsg);
                } else {
                    mAdapter.changeData(result.retbody);
                    if (mListView.getLastVisiblePosition() - mListView.getHeaderViewsCount() > 10) {
                        footer.pushLoadMore();
                    }
                }
                if (mAdapter.getCount() == 0) {
                    findViewById(R.id.empty).setVisibility(View.VISIBLE);
                } else findViewById(R.id.empty).setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                header.onRefreshComplete();
                footer.updateStatus(0, 0);
                error.printStackTrace();
                VolleyErrorUtils.showError(error);
            }
        }));
    }


    View.OnClickListener mShareListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("type", "insert");
            map.put("chkcode", IMApplication.getApplication().getchkcode());
            map.put("i_po_id", poId);
            map.put("reply", editText.getText().toString());
            if (item != null) {
                map.put("cmt_uname", item.cmtName);
                map.put("cmt_id", item.id);
                map.put("cmt_uid", item.cmtUid);
                map.put("cmt", item.detailList.reply);
            }
            showLoadingDialog(-1);
            IMApplication.getApplication().runVolleyRequest(new BaseRequest(new Gson().toJson(map), YohoField.URL_COMMET, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    L.d(response);
                    dismissLoadingDialog();
                    BaseResponse _respone = new Gson().fromJson(response, BaseResponse.class);
                    if (_respone.retcode != 0) {
                        ToastUtil.show(_respone.showmsg);
                    } else {
                        header.onRefresh();
                        editText.setText("");
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
    };


    class CommentAdapter extends BaseAdapter<CommentItem> {
        public CommentAdapter(Context ctx, List<CommentItem> data) {
            super(ctx, data);
        }

        public CommentAdapter(Context ctx, CommentItem... data) {
            super(ctx, data);
        }

        @Override
        public View getView(final int position, View convertView, LayoutInflater inflater) {
            Holder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_comment_ac_item, null);
                holder = new Holder();
                holder.profileImg = (ImageView) convertView.findViewById(R.id.profileImg);
                holder.comment = (TextViewFixTouchConsume) convertView.findViewById(R.id.comment);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.up = (TextView) convertView.findViewById(R.id.like);
                convertView.setTag(holder);
            } else holder = (Holder) convertView.getTag();
            final CommentItem item = getItem(position);
            IMApplication.getApplication().displayImage(item.cmtImage, holder.profileImg);
            holder.time.setText(DateUtils.getActivityTime(item.time));
            holder.comment.setText(item.detailList.reply);
            holder.up.setText("" + item.upCount);
            SpannableStringBuilder builder = new SpannableStringBuilder("");
            if (item.detailList.uid > 0) {
                builder = new SpannableStringBuilder("回复 ");
                int start = builder.length();
                builder.append(item.cmtName);
                builder.setSpan(new NameSpan(item.detailList.name, item.detailList.uid), start, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append(": ");
            }
            holder.up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("type", "ding");
                    map.put("i_cmt_id", item.id);
                    map.put("i_po_id", item.poId);
                    map.put("chkcode", IMApplication.getApplication().getchkcode());
                    IMApplication.getApplication().runVolleyRequest(new BaseRequest(Request.Method.POST, YohoField.URL_COMMET, new Gson().toJson(map),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    L.d(response);
                                    dismissLoadingDialog();
                                    BaseResponse re = new Gson().fromJson(response, BaseResponse.class);
                                    if (re.retcode == 0) {
                                        //TODO：攒点成功
                                        ToastUtil.show("点赞成功");
                                        item.upCount++;
                                        data.remove(position);
                                        data.add(position, item);
                                        notifyDataSetChanged();
                                    } else {
                                        ToastUtil.show(re.showmsg);
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dismissLoadingDialog();
                            VolleyErrorUtils.showError(error);
                        }
                    }));
                }
            });
//            String desc = item.detailList.reply;
//
//            String[] arr = StringsUtils.findString(desc);
//            if (arr != null) {
//                int len = builder.length();
//                int start = len;
//                builder.append(desc);
//                for (String _arr : arr) {
//                    start = desc.indexOf(_arr, start);
//                    int end = _arr.length();
//                    if (end == 1) continue;
//                    if (start < 0) start = len;
//                    if (_arr.startsWith("@")) {
//                        builder.setSpan(new NameSpan(_arr.substring(1).trim(), -1), start, start + end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    } else if (_arr.startsWith("#")) {
//                        String topic = _arr.substring(1).trim();
//                        builder.setSpan(new TopicSpan(topic), start, start + end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    }
//                    start = end;
//                }
//            }

            builder.append(item.detailList.reply);
            holder.comment.setText(builder);
            holder.comment.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
            return convertView;
        }

        class Holder {
            ImageView profileImg;
            TextViewFixTouchConsume comment;
            TextView time;
            TextView up;

        }
    }

    public class CommentDetail {
        @SerializedName("reply")
        public String reply;
        @SerializedName("cmt_uid")
        public long uid = -1;
        @SerializedName("cmt_uname")
        public String name;
    }

    public static class CommentItem {
        @SerializedName("i_po_id")
        public long poId;
        @SerializedName("i_creat_time")
        public long time;
        @SerializedName("i_ding_count")
        public long upCount;
        @SerializedName("s_cmt_uname")
        public String cmtName;
        @SerializedName("i_cmt_uid")
        public String cmtUid;
        @SerializedName("i_cmt_id")
        public String id;
        @SerializedName("s_user_image")
        public String cmtImage;
        @SerializedName("j_detail")
        public CommentDetail detailList;


    }

}
