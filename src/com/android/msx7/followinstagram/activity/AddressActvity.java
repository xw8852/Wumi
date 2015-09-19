package com.android.msx7.followinstagram.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.common.BaseActivity;
import com.android.msx7.followinstagram.common.BaseAdapter;
import com.android.msx7.followinstagram.common.BaseResponse;
import com.android.msx7.followinstagram.common.YohoField;
import com.android.msx7.followinstagram.net.BaseRequest;
import com.android.msx7.followinstagram.ui.actionbar.ActionBar;
import com.android.msx7.followinstagram.ui.push.PushHeader;
import com.android.msx7.followinstagram.util.L;
import com.android.msx7.followinstagram.util.RequestGsonUtils;
import com.android.msx7.followinstagram.util.ToastUtil;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josn on 2015/9/16.
 */
public class AddressActvity extends BaseActivity {
    public static final String PARAM_LAT = "param_lat";
    public static final String PARAM_LNG = "param_lng";

    double lat, lng;
    ListView listView;
    PushHeader header;
    SimpleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list);
        lat = getIntent().getDoubleExtra(PARAM_LAT, 0);
        lng = getIntent().getDoubleExtra(PARAM_LNG, 0);
        listView = (ListView) findViewById(R.id.list);
        mAdapter = new SimpleAdapter(this, new ArrayList<AddressLocation>());
        header = new PushHeader(listView, new PushHeader.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendRequest();
            }
        });
        listView.setSelector(new ColorDrawable(getResources().getColor(R.color.accent_blue_light)));
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getTitleBar().setTitle("地址", null);
        getTitleBar().setRightBtn("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (mAdapter.getCount() > 0 && listView.getCheckedItemPosition() > 0) {
                    int postion = Math.max(0, listView.getCheckedItemPosition() - listView.getHeaderViewsCount());
                    AddressLocation location = mAdapter.getItem(postion);
                    intent.putExtra("data", new Gson().toJson(location));
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        listView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        header.onRefresh();
    }

    void sendRequest() {

        Request request = new BaseRequest(Request.Method.GET, YohoField.URL_LOCATION,
                RequestGsonUtils.getGson(new Pair<String, String>("type", "nearby"),
                        new Pair<String, String>("f_lat", "" + lat),
                        new Pair<String, String>("f_lng", "" + lng)),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        L.d(response);
                        header.onRefreshComplete();
                        BaseResponse<List<AddressLocation>> rs = new Gson().fromJson(response, new TypeToken<BaseResponse<List<AddressLocation>>>() {
                        }.getType());
                        if (rs.retcode != 0) {
                            ToastUtil.show(rs.showmsg);
                        } else mAdapter.changeData(rs.retbody);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                header.onRefreshComplete();
            }
        });
        IMApplication.getApplication().runVolleyRequest(request);
    }

    class SimpleAdapter extends BaseAdapter<AddressLocation> {
        public SimpleAdapter(Context ctx, List<AddressLocation> data) {
            super(ctx, data);
        }


        @Override
        public View getView(int position, View convertView, LayoutInflater inflater) {
            TextView textView;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.action_bar_button_text, null);
                convertView.setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.action_bar_height));
            }
            textView = (TextView) convertView;
            textView.setTextColor(getResources().getColor(R.color.grey_medium));
            textView.setText(getItem(position).s_addr + "(" + getItem(position).s_city + ")");
            return convertView;
        }
    }

    public static class AddressLocation {
        @SerializedName("i_po_count")
        public int poCount;
        @SerializedName("_id")
        public long _id;
        @SerializedName("s_country")
        public String s_country;
        @SerializedName("s_addr")
        public String s_addr;
        @SerializedName("s_type")
        public String s_type;
        @SerializedName("s_city")
        public String s_city;
        @SerializedName("s_name")
        public String s_name;
    }
}
