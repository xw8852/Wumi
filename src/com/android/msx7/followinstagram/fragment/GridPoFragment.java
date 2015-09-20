package com.android.msx7.followinstagram.fragment;

import android.content.Context;
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

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.MainTabActivity;
import com.android.msx7.followinstagram.bean.UserInfo;
import com.android.msx7.followinstagram.common.BaseAdapter;
import com.android.msx7.followinstagram.common.BaseFragment;
import com.android.msx7.followinstagram.common.BaseResponse;
import com.android.msx7.followinstagram.net.PoRequest;
import com.android.msx7.followinstagram.net.UserRequest;
import com.android.msx7.followinstagram.ui.push.PushHeader;
import com.android.msx7.followinstagram.util.L;
import com.android.msx7.followinstagram.util.ToastUtil;
import com.android.msx7.followinstagram.util.VolleyErrorUtils;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Josn on 2015/9/13.
 */
public class GridPoFragment extends BaseFragment {
    PushHeader header;
    GridView gridView;
    long userId;
    public static final String PARAM_USER_ID = "param_user_id";
    public static final String PARAM_USER_NAME = "param_user_name";
    ViewGroup head;
    FeedAdapter mAdapter;
    View footerBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_grid, null);
    }

    int imgWidth;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addBack();
        if (getArguments() != null) {
            userId = getArguments().getLong(PARAM_USER_ID);
        } else {
            userId = IMApplication.getApplication().getUserInfo().userId;
            UserInfo userInfo = IMApplication.getApplication().getUserInfo();
        }
        gridView = (GridView) getView().findViewById(R.id.gridview);
        footerBar = getView().findViewById(R.id.footerBar);
        header = new PushHeader(gridView);
        head = (ViewGroup) getView().findViewById(R.id.header);
        head.addView(header.getHeader());
        mAdapter = new FeedAdapter(getView().getContext(), new ArrayList<TabHomeFragment.HomeItem>());
        gridView.setAdapter(mAdapter);
        header.setOnRefreshListener(onRefreshListener);
        imgWidth = (getResources().getDisplayMetrics().widthPixels - 2 * getResources().getDimensionPixelSize(R.dimen.row_text_button_padding)) / 3;
        getTitleBar().setTitle("有你的照片", null);
        if (getArguments() != null) {
            String title = getArguments().getString(PARAM_USER_NAME);
            if (!TextUtils.isEmpty(title)) {
                title = "有" + title + "的照片";
                getTitleBar().setTitle(title, null);
            }
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainTabActivity.addFragmentToBackStack(PageFragment.getFragment(mAdapter.getData(), position),view.getContext());
//                MainTabActivity.addFragmentToBackStack(SinglePoFragment.getFragment(mAdapter.getItem(position).id),view.getContext());
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

    int page = 0;

    void loadMore() {
        if (page == -1) return;
        int count = gridView.getAdapter().getCount();
        if (count == 0 || count % 18 != 0) return;
        if (footerBar.getVisibility() == View.VISIBLE) return;
        footerBar.setVisibility(View.VISIBLE);
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("type", "pobyta");
        map.put("i_user_id", userId);
        map.put("pageno", count / 18);
        map.put("chkcode", IMApplication.getApplication().getchkcode());
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

    PushHeader.OnRefreshListener onRefreshListener = new PushHeader.OnRefreshListener() {
        @Override
        public void onRefresh() {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("type", "aboutta");
            map.put("i_user_id", userId);
            page = -1;
            map.put("chkcode", IMApplication.getApplication().getchkcode());
            IMApplication.getApplication().runVolleyRequest(new PoRequest(Request.Method.GET, new Gson().toJson(map), new Response.Listener<String>() {
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
                    if(mAdapter.getCount()==0){
                        getView().findViewById(R.id.empty).setVisibility(View.VISIBLE);
                    }else getView().findViewById(R.id.empty).setVisibility(View.GONE);
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
