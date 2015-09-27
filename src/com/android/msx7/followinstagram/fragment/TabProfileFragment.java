package com.android.msx7.followinstagram.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.msx7.followinstagram.fragment.TabHomeFragment.HomeItem;
import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.EditUserActivity;
import com.android.msx7.followinstagram.activity.MainTabActivity;
import com.android.msx7.followinstagram.activity.SettinActivity;
import com.android.msx7.followinstagram.bean.UserInfo;
import com.android.msx7.followinstagram.common.BaseAdapter;
import com.android.msx7.followinstagram.common.BaseFragment;
import com.android.msx7.followinstagram.common.BaseResponse;
import com.android.msx7.followinstagram.common.YohoField;
import com.android.msx7.followinstagram.net.BaseRequest;
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
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Josn on 2015/9/9.
 */
public class TabProfileFragment extends BaseFragment {
    public static final String PARAM_USER_ID = "param_user_id";
    public static final String PARAM_USER_NAME = "param_user_name";
    ImageView profileImageView;
    long userId = -1;
    String userName;
    PushHeader header;
    GridView gridView;
    ViewGroup head;
    FeedAdapter mAdapter;

    TextView mPoView;
    TextView mFansView;
    TextView mFollowView;
    View footerBar;
    View edit;
    TextView follow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_profile_fragment, null);
    }

    int imgWidth;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileImageView = (ImageView) getView().findViewById(R.id.img);
        footerBar = getView().findViewById(R.id.footerBar);
        if (getArguments() != null) {
            userId = getArguments().getLong(PARAM_USER_ID);
            userName = getArguments().getString(PARAM_USER_NAME);
            addBack();
        } else {
            userId = IMApplication.getApplication().getUserInfo().userId;
            userName = IMApplication.getApplication().getUserInfo().userName;
            UserInfo userInfo = IMApplication.getApplication().getUserInfo();
            IMApplication.getApplication().displayImage(userInfo.userImg, profileImageView);
            getTitleBar().setRightImg(R.drawable.ic_set, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(v.getContext(), SettinActivity.class));
                }
            });
        }
        gridView = (GridView) getView().findViewById(R.id.gridview);
        header = new PushHeader(gridView);
        head = (ViewGroup) getView().findViewById(R.id.header);
        head.addView(header.getHeader());
        mAdapter = new FeedAdapter(getView().getContext(), new ArrayList<HomeItem>());
        gridView.setAdapter(mAdapter);
        header.setOnRefreshListener(onRefreshListener);
        mPoView = (TextView) getView().findViewById(R.id.poCount);
        mFansView = (TextView) getView().findViewById(R.id.fansCount);
        mFollowView = (TextView) getView().findViewById(R.id.followCount);
        mPoView.setText(getString(R.string.profile_po, 0));
        mFansView.setText(getString(R.string.profile_fans, 0));
        mFollowView.setText(getString(R.string.profile_follow, 0));
        imgWidth = (getResources().getDisplayMetrics().widthPixels - 2 * getResources().getDimensionPixelSize(R.dimen.row_text_button_padding)) / 3;
        getView().findViewById(R.id.grid).setSelected(true);
        getView().findViewById(R.id.tag).setOnClickListener(tagListener);
        mFansView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FansListFragment fragment = new FansListFragment();
                Bundle bundle = new Bundle();
                bundle.putLong(FansListFragment.PARAM_USER_ID, userId);
                fragment.setArguments(bundle);
                MainTabActivity.addFragmentToBackStack(fragment, v.getContext());
            }
        });
        mFollowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowListFragment fragment = new FollowListFragment();
                Bundle bundle = new Bundle();
                bundle.putLong(FollowListFragment.PARAM_USER_ID, userId);
                fragment.setArguments(bundle);
                MainTabActivity.addFragmentToBackStack(fragment, v.getContext());
            }
        });

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
        edit = getView().findViewById(R.id.edit);
        follow = (Button) getView().findViewById(R.id.btn_follow);
        boolean isme = (userId == IMApplication.getApplication().getUserInfo().userId) || (IMApplication.getApplication().getUserInfo().userName.equals(userName));
        if (!isme) {
            edit.setVisibility(View.GONE);
            follow.setVisibility(View.VISIBLE);
        } else userName = IMApplication.getApplication().getUserInfo().userName;
        getTitleBar().setTitle(userName, null);
        follow.setOnClickListener(new AddFollow());
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(v.getContext(), EditUserActivity.class), 100);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getTitleBar().setTitle(IMApplication.getApplication().getUserInfo().userName, null);
    }

    class AddFollow implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            if (userId < 0) return;
            showLoadingDialog(-1);
            final HashMap<String, Object> maps = new HashMap<String, Object>();
            maps.put("type", "insert");
            maps.put("i_follow_uid", userId);
            maps.put("chkcode", IMApplication.getApplication().getchkcode());
            IMApplication.getApplication().runVolleyRequest(new BaseRequest(Request.Method.POST, YohoField.URL_FOLLOW, new Gson().toJson(maps),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dismissLoadingDialog();
                            L.d(response);
                            BaseResponse rs = new Gson().fromJson(response, BaseResponse.class);
                            if (rs.retcode == 0 || rs.retcode == 7) {
                                //TODO;
                                follow.setOnClickListener(new DeleteFollow());
                                follow.setSelected(true);
                                follow.setText(R.string.byfollow);
                            } else ToastUtil.show(rs.showmsg);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissLoadingDialog();
                }
            }));
        }
    }

    class DeleteFollow implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (userId < 0) return;
            showLoadingDialog(-1);
            final HashMap<String, Object> maps = new HashMap<String, Object>();
            maps.put("type", "delete");
            maps.put("i_follow_uid", userId);
            maps.put("chkcode", IMApplication.getApplication().getchkcode());
            IMApplication.getApplication().runVolleyRequest(new BaseRequest(Request.Method.POST, YohoField.URL_FOLLOW, new Gson().toJson(maps),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dismissLoadingDialog();
                            L.d(response);
                            BaseResponse rs = new Gson().fromJson(response, BaseResponse.class);
                            if (rs.retcode == 0 || rs.retcode == 7) {
                                //TODO;
                                follow.setOnClickListener(new AddFollow());
                                follow.setSelected(false);
                                follow.setText(R.string.gofollow);
                            } else ToastUtil.show(rs.showmsg);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissLoadingDialog();
                }
            }));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        header.onRefresh();
    }

    View.OnClickListener tagListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GridPoFragment fragment = new GridPoFragment();
            Bundle bundle = new Bundle();
            bundle.putLong(GridPoFragment.PARAM_USER_ID, userId);
            if (userId != IMApplication.getApplication().getUserInfo().userId)
                bundle.putString(GridPoFragment.PARAM_USER_NAME, userName);
            fragment.setArguments(bundle);
            MainTabActivity.addFragmentToBackStack(fragment, v.getContext());
        }
    };
    PushHeader.OnRefreshListener onRefreshListener = new PushHeader.OnRefreshListener() {
        @Override
        public void onRefresh() {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("type", "info");
            if (userId > 0)
                map.put("i_user_id", userId);
            else map.put("s_user_name", userName);
            map.put("need_counter", 1);
            map.put("need_relation", 1);
            map.put("chkcode",IMApplication.getApplication().getchkcode());
            IMApplication application = IMApplication.getApplication();
            application.runVolleyRequest(new UserRequest(Request.Method.GET, new Gson().toJson(map), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    L.d(response);
                    if (userId <= 0)
                        header.onRefreshComplete();
                    BaseResponse<ProfileInfo> result = new Gson().fromJson(response, new TypeToken<BaseResponse<ProfileInfo>>() {
                    }.getType());
                    if (result.retcode != 0)
                        ToastUtil.show(result.showmsg);
                    else {
                        IMApplication.getApplication().displayImage(result.retbody.img, profileImageView);
                        mPoView.setText(getString(R.string.profile_po, result.retbody.poCount));
                        mFansView.setText(getString(R.string.profile_fans, result.retbody.fanCount));
                        mFollowView.setText(getString(R.string.profile_follow, result.retbody.followCunt));
                        if (userId <= 0) {
                            userId = result.retbody.uid;
                            //没有userid不能取图片
                            header.onRefresh();
                        }
                        userId = result.retbody.uid;
                        userName = result.retbody.name;
                        getTitleBar().setTitle(userName, null);
                        UserInfo userInfo = IMApplication.getApplication().getUserInfo();
                        if (userInfo.userId == result.retbody.uid) {
                            userInfo.s_introduce = result.retbody.s_introduce;
                            userInfo.sex = result.retbody.sex;
                            IMApplication.getApplication().saveUserInfo(userInfo);
                        }
                        if (follow.getVisibility() == View.VISIBLE) {
                            if (result.retbody.i_relation > 0) {
                                follow.setText(R.string.byfollow);
                                follow.setOnClickListener(null);
                                follow.setSelected(true);
                            }
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    if (userId <= 0)
                        header.onRefreshComplete();
                    VolleyErrorUtils.showError(error);
                }
            }));
            getMoreInfo();
        }
    };

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
                BaseResponse<List<HomeItem>> result = new Gson().fromJson(response, new TypeToken<BaseResponse<List<HomeItem>>>() {
                }.getType());
                page = 0;
                if (result.retcode != 0)
                    ToastUtil.show(result.showmsg);
                else {
                    mAdapter.addMore(result.retbody);
                }
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

    public void getMoreInfo() {
        if (userId < 0) {
            header.onRefreshComplete();
            return;
        }
        page = -1;
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("type", "pobyta");
        map.put("i_user_id", userId);
        map.put("chkcode", IMApplication.getApplication().getchkcode());
        IMApplication application = IMApplication.getApplication();
        application.runVolleyRequest(new PoRequest(Request.Method.GET, new Gson().toJson(map), new Response.Listener<String>() {
            @Override
            public synchronized void onResponse(String response) {
                L.d(response);
                header.onRefreshComplete();
                BaseResponse<List<HomeItem>> result = new Gson().fromJson(response, new TypeToken<BaseResponse<List<HomeItem>>>() {
                }.getType());
                page = 0;
                if (result.retcode != 0)
                    ToastUtil.show(result.showmsg);
                else {
                    mAdapter.clear();
                    mAdapter.changeData(result.retbody);
                }
                handler.sendEmptyMessageDelayed(0, 1000);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                page = 0;
                error.printStackTrace();
                VolleyErrorUtils.showError(error);
                header.onRefreshComplete();
            }
        }));
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (gridView.getLastVisiblePosition() == gridView.getAdapter().getCount()) {
                loadMore();
            }
        }
    };

    public class ProfileInfo {
        @SerializedName("i_status")
        public int statue;
        @SerializedName("s_user_name")
        public String name;
        @SerializedName("s_user_image")
        public String img;
        @SerializedName("s_address")
        public String address;
        //粉丝数目
        @SerializedName("i_fan_count")
        public int fanCount;
        //发布数目
        @SerializedName("i_po_count")
        public int poCount;
        //点赞总数
        @SerializedName("i_zan_count")
        public int likeCount;
        //关注人数
        @SerializedName("i_follow_count")
        public int followCunt;
        //UID
        @SerializedName("i_user_id")
        public long uid;
        @SerializedName("s_introduce")
        public String s_introduce;
        @SerializedName("i_sex")
        public int sex;
        @SerializedName("i_relation")
        public int i_relation = -1;
    }


    class FeedAdapter extends BaseAdapter<HomeItem> {
        public FeedAdapter(Context ctx, List<HomeItem> data) {
            super(ctx, data);
        }

        public FeedAdapter(Context ctx, HomeItem... data) {
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
