package com.android.msx7.followinstagram.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.MainTabActivity;
import com.android.msx7.followinstagram.bean.EventBean;
import com.android.msx7.followinstagram.common.BaseAdapter;
import com.android.msx7.followinstagram.common.BaseFragment;
import com.android.msx7.followinstagram.common.BaseResponse;
import com.android.msx7.followinstagram.common.YohoField;
import com.android.msx7.followinstagram.net.BaseRequest;
import com.android.msx7.followinstagram.ui.push.PageFooter;
import com.android.msx7.followinstagram.ui.push.PushHeader;
import com.android.msx7.followinstagram.ui.span.NameSpan;
import com.android.msx7.followinstagram.ui.text.TextViewFixTouchConsume;
import com.android.msx7.followinstagram.util.L;
import com.android.msx7.followinstagram.util.ToastUtil;
import com.android.msx7.followinstagram.util.VolleyErrorUtils;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Josn on 2015/9/25.
 */
public class TabSearchFragment extends BaseFragment {

    ListView listView;
    PushHeader header;
    EventAdpater mAdapter;
    TextView mEmptyView;
    PageFooter footer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) getView().findViewById(R.id.list);
        mEmptyView = (TextView) getView().findViewById(R.id.empty);
        footer = new PageFooter(listView, mAdapter);
        footer.setLoadMoreListener(moreListener);
        footer.updateStatus(0, 0);
        mAdapter = new EventAdpater(view.getContext(), new ArrayList<EventBean>());
        header = new PushHeader(listView, new PushHeader.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendRequest();
            }
        });
        getTitleBar().setTitle("发现", null);
        addBack();
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id < 0) return;
                position = (int) id;
                EventBean bean = mAdapter.getItem(position);
                GridActionFragment fragment = new GridActionFragment();
                Bundle bundle = new Bundle();
                bundle.putLong(GridActionFragment.PARAM_EVENT_ID, bean.eventId);
                bundle.putString(GridActionFragment.PARAM_EVENT_NAME, bean.name);
                fragment.setArguments(bundle);
                MainTabActivity.addFragmentToBackStack(fragment, view.getContext());
            }
        });
        header.onRefresh();
    }


    String type = "activity";

    void sendRequest() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("type", type);
        map.put("chkcode", IMApplication.getApplication().getchkcode());
        map.put("pageno", 0);
        Request request = new BaseRequest(Request.Method.GET, YohoField.URL_DISCOVERY,
                new Gson().toJson(map),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        L.d(response);
                        header.onRefreshComplete();
                        dismissLoadingDialog();
                        BaseResponse<List<EventBean>> re = new Gson().fromJson(response, new TypeToken<BaseResponse<List<EventBean>>>() {
                        }.getType());
                        if (re.retcode != 0) {
                            ToastUtil.show(re.showmsg);
                            return;
                        } else {
                            mAdapter.changeData(re.retbody);
                        }
                        if (mAdapter.getCount() < 10) footer.updateStatus(0, 0);
                        else footer.updateStatus(0, 1);
                        if (mAdapter.getCount() == 0) {
                            mEmptyView.setText("暂无活动");
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dismissLoadingDialog();
                header.onRefreshComplete();
            }
        });
        request.setShouldCache(true);
        IMApplication.getApplication().runVolleyRequest(request);
    }


    PageFooter.ILoadMoreListener moreListener = new PageFooter.ILoadMoreListener() {
        @Override
        public void loadMore(int nextPage) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("type", "list");
            map.put("chkcode", IMApplication.getApplication().getchkcode());
            map.put("pageno", mAdapter.getCount() / 10);
            IMApplication.getApplication().runVolleyRequest(
                    new BaseRequest(Request.Method.POST, YohoField.URL_ACTIVITY, new Gson().toJson(map),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    L.d(response);
                                    header.onRefreshComplete();
                                    BaseResponse<List<EventBean>> re = new Gson().fromJson(response, new TypeToken<BaseResponse<List<EventBean>>>() {
                                    }.getType());
                                    if (re.retcode != 0) {
                                        ToastUtil.show(re.showmsg);
                                        return;
                                    } else {
                                        mAdapter.addMore(re.retbody);
                                    }
                                    int page = mAdapter.getCount() / 10 - 1;
                                    page = Math.max(0, page);
                                    if (mAdapter.getCount() < 10 * page + 10)
                                        footer.updateStatus(page, page);
                                    else footer.updateStatus(page, page + 1);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            header.onRefreshComplete();
                            VolleyErrorUtils.showError(error);
                            int page = mAdapter.getCount() / 10 - 1;
                            page = Math.max(0, page);
                            footer.updateStatus(page, page + 1);
                        }
                    }
                    )
            );
        }
    };


    class EventAdpater extends BaseAdapter<EventBean> {
        public EventAdpater(Context ctx, List<EventBean> data) {
            super(ctx, data);
        }

        public EventAdpater(Context ctx, EventBean... data) {
            super(ctx, data);
        }

        @Override
        public View getView(int position, View convertView, LayoutInflater inflater) {
            Holder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_item_event, null);
                holder = new Holder();
                holder.name = (TextView) convertView.findViewById(R.id.eventName);
                holder.type = (TextView) convertView.findViewById(R.id.type);
                holder.desc = (TextView) convertView.findViewById(R.id.eventDesc);
                holder.creatName = (TextViewFixTouchConsume) convertView.findViewById(R.id.userInfo);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.img.setVisibility(View.VISIBLE);
                convertView.setTag(holder);
            }
            holder = (Holder) convertView.getTag();
            EventBean bean = getItem(position);
            holder.name.setText(bean.name);
            holder.desc.setText(bean.desc);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(bean.s_creat_uname);
            builder.setSpan(new NameSpan(bean.s_creat_uname, bean.uid), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append("\t" + new SimpleDateFormat("yyyy-MM-dd").format(bean.creatTime));
            holder.creatName.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
            holder.creatName.setText(builder);
            IMApplication.getApplication().displayImage(bean.s_activity_image, holder.img);
            if (bean.status == 0) {
                holder.type.setText("公开");
            } else holder.type.setText("私密");
            return convertView;
        }
    }

    class Holder {
        TextView name;
        TextView type;
        TextView desc;
        ImageView img;
        TextViewFixTouchConsume creatName;
    }
}
