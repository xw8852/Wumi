package com.android.msx7.followinstagram.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.db.BaseTable;
import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.MainTabActivity;
import com.android.msx7.followinstagram.activity.ShareImageActivity;
import com.android.msx7.followinstagram.bean.EventBean;
import com.android.msx7.followinstagram.bean.UserInfo;
import com.android.msx7.followinstagram.common.BaseAdapter;
import com.android.msx7.followinstagram.common.BaseFragment;
import com.android.msx7.followinstagram.common.BaseResponse;
import com.android.msx7.followinstagram.common.YohoField;
import com.android.msx7.followinstagram.net.BaseRequest;
import com.android.msx7.followinstagram.net.PoRequest;
import com.android.msx7.followinstagram.ui.push.PushHeader;
import com.android.msx7.followinstagram.util.L;
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
public class GridActionFragment extends BaseFragment {
    public static final String PARAM_TAG = "param_tag";
    public static final String PARAM_ADRESS = "param_ADRESS";
    public static final String PARAM_ADRESS_NAME = "param_adress_name";
    public static final String PARAM_EVENT_ID = "param_event_id";
    public static final String PARAM_EVENT_NAME = "param_event_name";
    PushHeader header;
    GridView gridView;

    ViewGroup head;
    FeedAdapter mAdapter;
    View footerBar;
    String tag;
    long addressId = -1;
    long eventId = -1;
    TextView desc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_grid, null);
    }

    int imgWidth;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        desc = (TextView) getView().findViewById(R.id.desc);
        addBack();
        if (getArguments() != null && getArguments().containsKey(PARAM_TAG)) {
            tag = getArguments().getString(PARAM_TAG);
            getTitleBar().setTitle(tag, null);
            addBack();
        }
        if (getArguments() != null && getArguments().containsKey(PARAM_ADRESS)) {
            addressId = getArguments().getLong(PARAM_ADRESS);
            getTitleBar().setTitle(getArguments().getString(PARAM_ADRESS_NAME), null);
            addBack();
        }
        if (getArguments() != null && getArguments().containsKey(PARAM_EVENT_ID)) {
            eventId = getArguments().getLong(PARAM_EVENT_ID);
            getTitleBar().setTitle(getArguments().getString(PARAM_EVENT_NAME), null);
            addBack();
            getInfoAboutEvent();
        }

        gridView = (GridView) getView().findViewById(R.id.gridview);
        footerBar = getView().findViewById(R.id.footerBar);
        header = new PushHeader(gridView);
        head = (ViewGroup) getView().findViewById(R.id.header);
        head.addView(header.getHeader(), 0);
        mAdapter = new FeedAdapter(getView().getContext(), new ArrayList<TabHomeFragment.HomeItem>());
        gridView.setAdapter(mAdapter);
        header.setOnRefreshListener(onRefreshListener);
        imgWidth = (getResources().getDisplayMetrics().widthPixels - 2 * getResources().getDimensionPixelSize(R.dimen.row_text_button_padding)) / 3;


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MainTabActivity.addFragmentToBackStack(PageFragment.getFragment(mAdapter.getData(), position), view.getContext());
            }
        });
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                L.d("firstVisibleItem  " + firstVisibleItem + ",visibleItemCount   " + visibleItemCount);
                if (visibleItemCount + firstVisibleItem == gridView.getAdapter().getCount()) {
                    loadMore();
                }
            }
        });
    }

    EventBean eventBean;

    //获取活动的相关信息
    void getInfoAboutEvent() {
        BaseTable<EventBean> eventDb = new BaseTable<EventBean>(getView().getContext()) {
        };
        List<EventBean> list = eventDb.getDataFromWhere(" i_activity_id = " + eventId);
        if (list != null && !list.isEmpty()) {
            eventBean = list.get(0);
            if (eventBean != null) {
                if (!TextUtils.isEmpty(eventBean.desc)) {
                    desc.setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.descInfo).setVisibility(View.VISIBLE);
                    desc.setText(eventBean.desc);
                    setSendPic();
                }
            }
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("i_activity_id", eventId);
        map.put("type", "info");
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.d("活动："+response);
                BaseResponse<EventBean> re = new Gson().fromJson(response, new TypeToken<BaseResponse<EventBean>>() {
                }.getType());
                if (re.retcode != 0) {
                    ToastUtil.show(re.showmsg);
                } else {
                    eventBean = re.retbody;
                    getTitleBar().setTitle(eventBean.name + "(" + eventBean.poCount + ")", null);
                    if (!TextUtils.isEmpty(eventBean.desc)) {
                        desc.setVisibility(View.VISIBLE);
                        desc.setText(eventBean.desc);
                        getView().findViewById(R.id.descInfo).setVisibility(View.VISIBLE);
                    }
                    //如果活动是公开的，默认谁都可以发图片，否则回去检查是否有参加活动的资格
                    if (eventBean.status == 0) {
                        setSendPic();
                    } else {
                        checkEvent();
                    }
                }
            }
        };
        IMApplication.getApplication().runVolleyRequest(new BaseRequest(Request.Method.GET, YohoField.URL_ACTIVITY, new Gson().toJson(map),
                listener, errorListener));
    }

    //私密活动检查用户是否已经参加了活动
    void checkEvent() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("i_activity_id", eventId);
        map.put("type", "check");
        map.put("chkcode", IMApplication.getApplication().getchkcode());
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.d(response);
                dismissLoadingDialog();
                BaseResponse<EventBean> re = new Gson().fromJson(response, new TypeToken<BaseResponse<EventBean>>() {
                }.getType());
                if (re.retcode == 0) {
                    setSendPic();
                    new BaseTable<EventBean>(getView().getContext()) {
                    }.insertOrUpdate(eventBean);
                } else if (re.retcode == 502) {
                    setApply();
                    ToastUtil.show(re.showmsg);
                } else if (re.retcode == 503) {
                    setApply();
                    ToastUtil.show(re.showmsg);
                }else{
                    ToastUtil.show(re.showmsg);
                }
            }

        };
        IMApplication.getApplication().runVolleyRequest(
                new BaseRequest(Request.Method.POST, YohoField.URL_ACTIVITY, new Gson().toJson(map), listener, errorListener)
        );
    }

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            dismissLoadingDialog();
        }
    };

    //设置右上角为申请
    void setApply() {
        getTitleBar().setRightBtn("申请", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog(-1);
                //发送申请
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("i_activity_id", eventId);
                map.put("type", "apply");
                map.put("chkcode", IMApplication.getApplication().getchkcode());
                IMApplication.getApplication().runVolleyRequest(new BaseRequest(Request.Method.POST, YohoField.URL_ACTIVITY, new Gson().toJson(map), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        L.d(response);
                        dismissLoadingDialog();
                        BaseResponse<EventBean> re = new Gson().fromJson(response, new TypeToken<BaseResponse<EventBean>>() {
                        }.getType());
                        if (re.retcode == 0) {
                            ToastUtil.show("发送申请成功");
                        }else{
                            ToastUtil.show(re.showmsg);
                        }
                    }
                }, errorListener));
            }
        });

    }

    //设置右上角为发图
    void setSendPic() {
        getTitleBar().setRightBtn("发图", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ShareImageActivity.class);
                intent.putExtra("EVENT", new Gson().toJson(eventBean));
                startActivity(intent);
            }
        });

    }

    int page = 0;

    void loadMore() {
        if (page == -1) return;
        int count = gridView.getAdapter().getCount();
        if (count == 0 || count % 18 != 0) return;
        if (footerBar.getVisibility() == View.VISIBLE) return;
        footerBar.setVisibility(View.VISIBLE);
        map.remove("pageno");
        map.put("pageno", count / 18);
        IMApplication application = IMApplication.getApplication();
        application.runVolleyRequest(new PoRequest(Request.Method.GET, new Gson().toJson(map), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.d(response);
                BaseResponse<List<TabHomeFragment.HomeItem>> result = new Gson().fromJson(response, new TypeToken<BaseResponse<List<TabHomeFragment.HomeItem>>>() {
                }.getType());
                page = 0;
                if (result.retcode != 0)
                    ToastUtil.show(result.showmsg);
                else mAdapter.addMore(result.retbody);
                footerBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                page = 0;
                error.printStackTrace();
                footerBar.setVisibility(View.GONE);
                VolleyErrorUtils.showError(error);
            }
        }));
    }

    @Override
    public void onResume() {
        super.onResume();
        header.onRefresh();
    }

    HashMap<String, Object> map = new HashMap<String, Object>();
    int pageSize = 18;
    PushHeader.OnRefreshListener onRefreshListener = new PushHeader.OnRefreshListener() {
        @Override
        public void onRefresh() {
            map.remove("pageno");
            map.put("pageno", 0);
            String url = YohoField.URL_FEED;
            int method = Request.Method.POST;
            if (!TextUtils.isEmpty(tag)) {
                url = YohoField.URL_PO;
                method = Request.Method.GET;
                map.put("type", "tag");
                map.put("s_tag", tag);
            } else if (addressId != -1) {
                map.put("i_loc_id", addressId);
                url = YohoField.URL_PO;
                method = Request.Method.GET;
                map.put("i_loc_id", addressId);
                map.put("type", "location");
            } else if (eventId != -1) {
                url = YohoField.URL_PO;
                method = Request.Method.GET;
                map.put("type", "activity");
                map.put("i_activity_id", eventId);
            }
            page = -1;
            map.put("chkcode", IMApplication.getApplication().getchkcode());
            IMApplication.getApplication().runVolleyRequest(new BaseRequest(method, url, new Gson().toJson(map), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    L.d(response);
                    header.onRefreshComplete();
                    BaseResponse<List<TabHomeFragment.HomeItem>> result = new Gson().fromJson(response, new TypeToken<BaseResponse<List<TabHomeFragment.HomeItem>>>() {
                    }.getType());
                    page = 0;
                    if (result.retcode != 0)
                        ToastUtil.show(result.showmsg);
                    else mAdapter.changeData(result.retbody);
                    if (mAdapter.getCount() == 0) {
                        getView().findViewById(R.id.empty).setVisibility(View.VISIBLE);
                    } else getView().findViewById(R.id.empty).setVisibility(View.GONE);
                    handler.sendEmptyMessageDelayed(0, 1000);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    VolleyErrorUtils.showError(error);
                    header.onRefreshComplete();
                    page = 0;
                }
            }));
        }

    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (gridView.getLastVisiblePosition() == gridView.getAdapter().getCount()) {
                loadMore();
            }
        }
    };

    class FeedAdapter extends BaseAdapter<TabHomeFragment.HomeItem> {
        public FeedAdapter(Context ctx, List<TabHomeFragment.HomeItem> data) {
            super(ctx, data);
        }

        public FeedAdapter(Context ctx, TabHomeFragment.HomeItem... data) {
            super(ctx, data);
        }

        @Override
        public View getView(int position, View convertView, LayoutInflater inflater) {

            ImageView imageView = null;
            if (convertView == null) {
                imageView = new ImageView(inflater.getContext());
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(imgWidth, imgWidth);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else imageView = (ImageView) convertView;
            IMApplication.getApplication().displayImage(getItem(position).imgInfo.get(0).imgurl, imageView);
            return imageView;
        }
    }
}
