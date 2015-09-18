package com.android.msx7.followinstagram.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.msx7.followinstagram.Fragment.FollowListFragment.FollowBean;
import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.MainTabActivity;
import com.android.msx7.followinstagram.common.BaseAdapter;
import com.android.msx7.followinstagram.common.BaseFragment;
import com.android.msx7.followinstagram.common.BaseResponse;
import com.android.msx7.followinstagram.common.YohoField;
import com.android.msx7.followinstagram.net.BaseRequest;
import com.android.msx7.followinstagram.ui.push.PageFooter;
import com.android.msx7.followinstagram.ui.push.PushHeader;
import com.android.msx7.followinstagram.ui.text.TextViewFixTouchConsume;
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
 * Created by Josn on 2015/9/13.
 */
public class FansListFragment extends BaseFragment {
    public static final String PARAM_USER_ID = "param_user_id";

    ListView mListView;
    PushHeader header;
    FollowAdapter mAdapter;
    long userId;
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
        if (getArguments() != null) {
            userId = getArguments().getLong(PARAM_USER_ID);
        }
        mListView = (ListView) getView().findViewById(R.id.list);
        header = new PushHeader(mListView, refreshListener);
        mAdapter = new FollowAdapter(getView().getContext(), new ArrayList<FollowBean>());
        mListView.setAdapter(mAdapter);
        footer = new PageFooter(mListView, mAdapter);
        footer.setLoadMoreListener(loadMoreListener);
        footer.updateStatus(0, 0);
        getTitleBar().setTitle("粉丝",null);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id >= 0 ) {
                    FollowBean bean = mAdapter.getItem((int) id);
                    MainTabActivity activity = (MainTabActivity) view.getContext();
                    TabProfileFragment fragment = new TabProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putLong(TabProfileFragment.PARAM_USER_ID, bean.fanUid);
                    bundle.putString(TabProfileFragment.PARAM_USER_NAME, bean.userName);
                    fragment.setArguments(bundle);
                    activity.addFragmentToBackStack(fragment);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        header.onRefresh();
    }

    void validEmpty() {
        if (empty == null) empty = (TextView) getView().findViewById(R.id.empty);
        if (mAdapter.getCount() > 0) {
            empty.setVisibility(View.GONE);
            return;
        }
        empty.setVisibility(View.VISIBLE);
        empty.setText("暂时没有粉丝");
    }

    PushHeader.OnRefreshListener refreshListener = new PushHeader.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refresh();
        }
    };

    public void refresh() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("type", "fans");
        map.put("pageno", 0);
        map.put("i_user_id", userId);
        map.put("chkcode", IMApplication.getApplication().getchkcode());
        IMApplication.getApplication().runVolleyRequest(new BaseRequest(Request.Method.GET, YohoField.URL_FOLLOW, new Gson().toJson(map), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.d(response);
                BaseResponse<List<FollowBean>> result = new Gson().fromJson(response, new TypeToken<BaseResponse<List<FollowBean>>>() {
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
            map.put("pageno", nextPage);
            map.put("type", "fans");
            map.put("i_user_id", userId);
            map.put("chkcode", IMApplication.getApplication().getchkcode());
            IMApplication.getApplication().runVolleyRequest(new BaseRequest(Request.Method.GET, YohoField.URL_COMMET, new Gson().toJson(map), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    L.d(response);
                    BaseResponse<List<FollowBean>> result = new Gson().fromJson(response, new TypeToken<BaseResponse<List<FollowBean>>>() {
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

    class FollowAdapter extends BaseAdapter<FollowBean> {
        public FollowAdapter(Context ctx, List<FollowBean> data) {
            super(ctx, data);
        }

        public FollowAdapter(Context ctx, FollowBean... data) {
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
                convertView.setTag(holder);
            } else holder = (Holder) convertView.getTag();
            FollowBean bean = getItem(position);
            IMApplication.getApplication().displayImage(bean.userImg, holder.profileImg);
            holder.comment.setText(bean.userName);
            holder.btn.setVisibility(View.VISIBLE);
            if (bean.relation == 0) {
                holder.btn.setText(R.string.gofollow);
                holder.btn.setSelected(false);
                holder.btn.setOnClickListener(new AddFollow(position));
            } else {
                holder.btn.setText(R.string.byfollow);
                holder.btn.setSelected(true);
                holder.btn.setOnClickListener(new DeleteFollow(position));
            }
            return convertView;
        }

        class AddFollow implements View.OnClickListener {
            int position;

            public AddFollow(int position) {
                this.position = position;
            }

            @Override
            public void onClick(View v) {
                showLoadingDialog(-1);
                final HashMap<String, Object> maps = new HashMap<String, Object>();
                maps.put("type", "insert");
                maps.put("i_follow_uid", mAdapter.getItem(position).fanUid);
                maps.put("chkcode", IMApplication.getApplication().getchkcode());
                IMApplication.getApplication().runVolleyRequest(new BaseRequest(Request.Method.POST, YohoField.URL_FOLLOW, new Gson().toJson(maps),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                dismissLoadingDialog();
                                L.d(response);
                                BaseResponse rs = new Gson().fromJson(response, BaseResponse.class);
                                if (rs.retcode == 0 || rs.retcode == 7) {
                                    FollowBean bean = mAdapter.getItem(position);
                                    bean.relation = 2;
                                    mAdapter.remove(bean);
                                    mAdapter.data.add(position, bean);
                                    mAdapter.notifyDataSetChanged();
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
            int position;

            public DeleteFollow(int position) {
                this.position = position;
            }

            @Override
            public void onClick(View v) {
                showLoadingDialog(-1);
                final HashMap<String, Object> maps = new HashMap<String, Object>();
                maps.put("type", "delete");
                maps.put("i_follow_uid", mAdapter.getItem(position).fanUid);
                maps.put("chkcode", IMApplication.getApplication().getchkcode());
                IMApplication.getApplication().runVolleyRequest(new BaseRequest(Request.Method.POST, YohoField.URL_FOLLOW, new Gson().toJson(maps),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                dismissLoadingDialog();
                                L.d(response);
                                BaseResponse rs = new Gson().fromJson(response, BaseResponse.class);
                                if (rs.retcode == 0 || rs.retcode == 7) {
                                    FollowBean bean = mAdapter.getItem(position);
                                    bean.relation = 0;
                                    mAdapter.remove(bean);
                                    mAdapter.data.add(position, bean);
                                    mAdapter.notifyDataSetChanged();
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

        class Holder {
            ImageView profileImg;
            TextViewFixTouchConsume comment;
            TextView btn;

        }
    }
}
