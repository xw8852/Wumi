package com.android.msx7.followinstagram.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.common.BaseAdapter;
import com.android.msx7.followinstagram.common.BaseFragment;
import com.android.msx7.followinstagram.common.BaseResponse;
import com.android.msx7.followinstagram.net.ZanRequest;
import com.android.msx7.followinstagram.ui.push.PushHeader;
import com.android.msx7.followinstagram.util.DateUtils;
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
 * Created by Josn on 2015/9/13.
 */
public class GoodListFragment extends BaseFragment {
    public static final String PARAM_PO_ID = "param_po_id";
    public long poId;

    ListView mListView;
    PushHeader header;
    LikeAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        poId = getArguments().getLong(PARAM_PO_ID);
        mListView = (ListView) getView().findViewById(R.id.list);
        header = new PushHeader(mListView, listener);
        mAdapter = new LikeAdapter(getView().getContext(), new ArrayList<LikeInfo>());
        mListView.setAdapter(mAdapter);
    }

    PushHeader.OnRefreshListener listener = new PushHeader.OnRefreshListener() {
        @Override
        public void onRefresh() {
            final HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("type", "list");
            map.put("i_po_id", poId);
            IMApplication.getApplication().runVolleyRequest(new ZanRequest(Request.Method.GET, new Gson().toJson(map), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    L.d(response);
                    BaseResponse<List<LikeInfo>> result = new Gson().fromJson(response, new TypeToken<BaseResponse<List<LikeInfo>>>() {
                    }.getType());
                    if (result.retcode != 0) {
                        ToastUtil.show(result.showmsg);
                    } else {
                        mAdapter.changeData(result.retbody);
                    }
                    header.onRefreshComplete();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    header.onRefreshComplete();
                    error.printStackTrace();
                    VolleyErrorUtils.showError(error);
                }
            }));
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        header.onRefresh();
    }

    class LikeAdapter extends BaseAdapter<LikeInfo> {
        public LikeAdapter(Context ctx, List<LikeInfo> data) {
            super(ctx, data);
        }

        public LikeAdapter(Context ctx, LikeInfo... data) {
            super(ctx, data);
        }


        @Override
        public View getView(int position, View convertView, LayoutInflater inflater) {
            Holder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_comment_ac_item, null);
                holder = new Holder();
                holder.profileImg = (ImageView) convertView.findViewById(R.id.profileImg);
                holder.comment = (TextView) convertView.findViewById(R.id.comment);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                convertView.setTag(holder);
            } else holder = (Holder) convertView.getTag();
            LikeInfo item = getItem(position);
            IMApplication.getApplication().displayImage(item.url, holder.profileImg);
            holder.time.setText(DateUtils.getActivityTime(item.time));
            holder.comment.setText(item.name);
            return convertView;
        }

        class Holder {
            ImageView profileImg;
            TextView comment;
            TextView time;

        }
    }

    public class LikeInfo {

        @SerializedName("i_creat_time")
        public long time;
        @SerializedName("i_po_id")
        public long poId;
        @SerializedName("s_user_name")
        public String name;
        @SerializedName("s_user_image")
        public String url;
        @SerializedName("i_user_id")
        public long userId;
    }
}
