package com.android.msx7.followinstagram.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.msx7.followinstagram.util.InputKeyBoardUtils;
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
import java.util.HashMap;
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
    EditText editText;
    TextView btn;
    View clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list);
        lat = getIntent().getDoubleExtra(PARAM_LAT, 0);
        lng = getIntent().getDoubleExtra(PARAM_LNG, 0);
        listView = (ListView) findViewById(R.id.list);
        View view = getLayoutInflater().inflate(R.layout.layout_searchbar, null);
        ((ViewGroup) listView.getParent().getParent()).addView(view, 1);
        btn = (TextView) view.findViewById(R.id.clear);
        clear = view.findViewById(R.id.delete);
        btn.setText("搜索");
        editText = (EditText) view.findViewById(R.id.action_bar_search_edit_text);
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
        addBack();
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id < 0) return;
                position = (int) id;
                Intent intent = new Intent();
                AddressLocation location = mAdapter.getItem(position);
                intent.putExtra("data", new Gson().toJson(location));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText.getText().toString()) || editText.getText().toString().trim().length() == 0) {
                    ToastUtil.show("请输入需要搜索的关键词");
                    return;
                }
                hideKeyboard();
                startSearch();
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (TextUtils.isEmpty(editText.getText().toString()) || editText.getText().toString().trim().length() == 0) {
                    ToastUtil.show("请输入需要搜索的关键词");
                    return false;
                }
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    startSearch();
                }
                return false;
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0 || s.toString().trim().length() == 0) {
                    showDefault();
                    clear.setVisibility(View.GONE);
                } else clear.setVisibility(View.VISIBLE);
            }
        });
    }

    void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0); //强制隐藏键盘
    }

    void startSearch() {
        type = "search";
        showLoadingDialog(-1);
        sendRequest();
    }

    void showDefault() {
        type = "nearby";
        showLoadingDialog(-1);
        sendRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
        header.onRefresh();
    }

    String type = "nearby";

    void sendRequest() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("type", type);
        map.put("f_lat", "" + lat);
        map.put("f_lng", "" + lng);
        map.put("s_keyword", editText.getText().toString());
        Request request = new BaseRequest(Request.Method.GET, YohoField.URL_LOCATION,
                new Gson().toJson(map),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        L.d(response);
                        header.onRefreshComplete();
                        dismissLoadingDialog();
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
                dismissLoadingDialog();
                header.onRefreshComplete();
            }
        });
        request.setShouldCache(true);
        IMApplication.getApplication().runVolleyRequest(request);
    }

    class SimpleAdapter extends BaseAdapter<AddressLocation> {
        public SimpleAdapter(Context ctx, List<AddressLocation> data) {
            super(ctx, data);
        }


        @Override
        public View getView(int position, View convertView, LayoutInflater inflater) {
            Holder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_address_item, null);
                holder = new Holder();
                holder.name = (TextView) convertView.findViewById(R.id.action_bar_button_text);
                holder.address = (TextView) convertView.findViewById(R.id.address);
                convertView.setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.action_bar_height));
                convertView.setTag(holder);
            }
            holder = (Holder) convertView.getTag();
            holder.name.setText(getItem(position).s_name);
            holder.address.setText(getItem(position).s_city + "市" + getItem(position).s_addr);
            return convertView;
        }

        class Holder {
            TextView name;
            TextView address;
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
