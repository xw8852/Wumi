package com.android.msx7.followinstagram.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.ImgFindUserActivity.SimpleContact;
import com.android.msx7.followinstagram.common.BaseActivity;
import com.android.msx7.followinstagram.common.BaseResponse;
import com.android.msx7.followinstagram.common.YohoField;
import com.android.msx7.followinstagram.net.BaseRequest;
import com.android.msx7.followinstagram.util.L;
import com.android.msx7.followinstagram.util.ToastUtil;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.widget.PinnedAdapter;
import com.android.widget.PinnedHeaderListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Josn on 2015/9/20.
 */
public class ContactActivity extends BaseActivity {
    PinnedHeaderListView listView;
    ContactAdapter mAdapter;


    EditText editText;
    TextView btn;
    View clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_contact);
        getTitleBar().setTitle("通讯录", null);
        addBack();
        listView = (PinnedHeaderListView) findViewById(R.id.pinListView);
        View _view = findViewById(R.id.pin_header);
        _view.setVisibility(View.INVISIBLE);
        listView.setPinHeader(_view);
        mAdapter = new ContactAdapter(this, new ArrayList<SimpleContact>());
        listView.setAdapter(mAdapter);
        addSearch();
        sendRequest();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id < 0) return;
                SimpleContact contact = mAdapter.getItem((int) id);
                Intent intent = new Intent();
                intent.putExtra("data", new Gson().toJson(contact));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    void addSearch() {
        View view = getLayoutInflater().inflate(R.layout.layout_searchbar, null);
        ((ViewGroup) listView.getParent().getParent()).addView(view, 1);
        btn = (TextView) view.findViewById(R.id.clear);
        editText = (EditText) view.findViewById(R.id.action_bar_search_edit_text);
        clear = view.findViewById(R.id.delete);
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
                mAdapter.filter(s.toString());
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
        mAdapter.filter(editText.getText().toString());
    }

    void showDefault() {
        mAdapter.filter("");
    }


    void sendRequest() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("type", "list");
        map.put("chkcode", IMApplication.getApplication().getchkcode());
        Request request = new BaseRequest(Request.Method.POST, YohoField.URL_CONTACT,
                new Gson().toJson(map),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        L.d(response);
                        dismissLoadingDialog();
                        BaseResponse<List<SimpleContact>> rs = new Gson().fromJson(response, new TypeToken<BaseResponse<List<SimpleContact>>>() {
                        }.getType());
                        if (rs.retcode != 0) {
                            ToastUtil.show(rs.showmsg);
                        } else {
                            contacts.clear();
                            contacts.addAll(rs.retbody);
                            mAdapter.changeData(contacts);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dismissLoadingDialog();
            }
        });
        request.setShouldCache(true);
        IMApplication.getApplication().runVolleyRequest(request);
    }

    //原始数据的联系人列表
    List<SimpleContact> contacts = new ArrayList<SimpleContact>();


    class ContactAdapter extends PinnedAdapter<SimpleContact> {
        List<SimpleContact> filters = new ArrayList<SimpleContact>();

        public ContactAdapter(Context ctx, List<SimpleContact> data) {
            super(ctx, data);
        }

        public ContactAdapter(Context ctx, SimpleContact... data) {
            super(ctx, data);
        }


        void filter(String str) {
            filters.clear();
            if (TextUtils.isEmpty(str)) {
                changeData(contacts);
                return;
            }
            for (SimpleContact contact : data) {
                if (contact.s_firstchar.equals(str.toLowerCase())
                        || contact.s_ctt_uname.contains(str.toLowerCase())
                        || contact.s_pinyin_abs.contains(str.toLowerCase())
                        || contact.s_pinyin.contains(str.toLowerCase())) {
                    filters.add(contact);
                    continue;
                }
            }
            changeData(filters);
        }

        @Override
        public void configHeaderView(final int position, View header) {
            TextView _view = (TextView) header;
            _view.setText(getItem(position).s_firstchar.toUpperCase());
            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SimpleContact contact = mAdapter.getItem(position);
                    Intent intent = new Intent();
                    intent.putExtra("data", new Gson().toJson(contact));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }

        @Override
        public View getView(final int position, View convertView, LayoutInflater inflater) {
            Holder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_contact_item, null);
                holder = new Holder();
                holder.name = (TextView) convertView.findViewById(R.id.pin_header);
                holder.address = (TextView) convertView.findViewById(R.id.action_bar_button_text);
                convertView.setTag(holder);
            } else holder = (Holder) convertView.getTag();
            holder.name.setText(getItem(position).s_firstchar.toUpperCase());
            holder.address.setText(getItem(position).s_ctt_uname);
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    SimpleContact contact = mAdapter.getItem(position);
//                    Intent intent = new Intent();
//                    intent.putExtra("data", new Gson().toJson(contact));
//                    setResult(RESULT_OK, intent);
//                    finish();
//                }
//            });
            return convertView;
        }

        class Holder {
            TextView name;
            TextView address;
        }
    }

}
