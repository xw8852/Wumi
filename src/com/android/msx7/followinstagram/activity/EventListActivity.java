package com.android.msx7.followinstagram.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.db.BaseTable;
import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.bean.EventBean;
import com.android.msx7.followinstagram.common.BaseActivity;
import com.android.msx7.followinstagram.common.BaseAdapter;
import com.android.msx7.followinstagram.common.BaseResponse;
import com.android.msx7.followinstagram.common.YohoField;
import com.android.msx7.followinstagram.net.BaseRequest;
import com.android.msx7.followinstagram.ui.push.PageFooter;
import com.android.msx7.followinstagram.ui.push.PushHeader;
import com.android.msx7.followinstagram.util.L;
import com.android.msx7.followinstagram.util.RequestGsonUtils;
import com.android.msx7.followinstagram.util.ToastUtil;
import com.android.msx7.followinstagram.util.VolleyErrorUtils;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Josn on 2015/9/19.
 */
public class EventListActivity extends BaseActivity {

    PushHeader mHeader;
    PageFooter footer;
    ListView mListView;
    TextView mEmptyView;
    EventAdpater mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list);
        mListView = (ListView) findViewById(R.id.list);
        mEmptyView = (TextView) findViewById(R.id.empty);
        mAdapter = new EventAdpater(this, new ArrayList<EventBean>());
        mHeader = new PushHeader(mListView, refreshListener);
        footer = new PageFooter(mListView, mAdapter);
        footer.setLoadMoreListener(moreListener);
        getTitleBar().setTitle("活动", null);
        getTitleBar().setLeftBtn("新建", addEventListener);
        footer.updateStatus(0, 0);
//        mListView.setBackgroundResource(R.color.grey_1);
//        mListView.setDivider(new ColorDrawable(0xffedeeee));
        mListView.setSelector(new ColorDrawable(getResources().getColor(R.color.accent_blue_light)));
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setAdapter(mAdapter);
        getTitleBar().setRightBtn("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.d("MSG", mListView.getSelectedItemPosition() + "," + mListView.getSelectedItemId());
                Intent intent = new Intent();
                if (mAdapter.getCount() > 0 && mListView.getCheckedItemPosition() > 0) {
                    int postion = Math.max(0, mListView.getCheckedItemPosition() - mListView.getHeaderViewsCount());
                    EventBean eventBean = mAdapter.getItem(postion);
                    intent.putExtra("data", new Gson().toJson(eventBean));
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHeader.onRefresh();
    }

    View.OnClickListener addEventListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivityForResult(new Intent(EventListActivity.this, AddEventActivity.class), 100);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    PushHeader.OnRefreshListener refreshListener = new PushHeader.OnRefreshListener() {
        @Override
        public void onRefresh() {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("type", "list");
            map.put("chkcode", IMApplication.getApplication().getchkcode());
            map.put("pageno", 0);
            IMApplication.getApplication().runVolleyRequest(
                    new BaseRequest(Request.Method.POST, YohoField.URL_ACTIVITY, new Gson().toJson(map),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    L.d(response);
                                    mHeader.onRefreshComplete();
                                    BaseResponse<List<EventBean>> re = new Gson().fromJson(response, new TypeToken<BaseResponse<List<EventBean>>>() {
                                    }.getType());
                                    if (re.retcode != 0) {
                                        ToastUtil.show(re.showmsg);
                                        return;
                                    } else {
                                        mAdapter.changeData(re.retbody);
                                        updateDb(re.retbody);
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
                            mHeader.onRefreshComplete();
                            VolleyErrorUtils.showError(error);
                        }
                    }
                    )
            );
        }
    };

    void updateDb(List<EventBean> list) {
        if (list == null || list.isEmpty()) return;
        BaseTable<EventBean> eventDb = new BaseTable<EventBean>(EventListActivity.this) {
        };
        for (EventBean bean : list) {
            eventDb.insertOrUpdate(bean);
        }
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
                                    mHeader.onRefreshComplete();
                                    BaseResponse<List<EventBean>> re = new Gson().fromJson(response, new TypeToken<BaseResponse<List<EventBean>>>() {
                                    }.getType());
                                    if (re.retcode != 0) {
                                        ToastUtil.show(re.showmsg);
                                        return;
                                    } else {
                                        updateDb(re.retbody);
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
                            mHeader.onRefreshComplete();
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
                convertView.setTag(holder);
            }
            holder = (Holder) convertView.getTag();
            EventBean bean = getItem(position);
            holder.name.setText(bean.name);
            holder.desc.setText(bean.desc);
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

    }


}
