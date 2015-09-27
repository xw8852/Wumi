package com.android.msx7.followinstagram.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
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
import com.android.msx7.followinstagram.ui.span.NameSpan;
import com.android.msx7.followinstagram.ui.text.TextViewFixTouchConsume;
import com.android.msx7.followinstagram.util.DateUtils;
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

import java.text.SimpleDateFormat;
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

    EditText editText;
    TextView btn;
    View clear;

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
        getTitleBar().setRightBtn("新建", addEventListener);
        footer.updateStatus(0, 0);
//        mListView.setBackgroundResource(R.color.grey_1);
//        mListView.setDivider(new ColorDrawable(0xffedeeee));
        mListView.setSelector(new ColorDrawable(getResources().getColor(R.color.accent_blue_light)));
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setAdapter(mAdapter);
        addBack();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id < 0) return;
                position = (int) id;
                Intent intent = new Intent();
                EventBean eventBean = mAdapter.getItem(position);
                intent.putExtra("data", new Gson().toJson(eventBean));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        View view = getLayoutInflater().inflate(R.layout.layout_searchbar, null);
        ((ViewGroup) mListView.getParent().getParent()).addView(view, 1);
        btn = (TextView) view.findViewById(R.id.clear);
        clear = view.findViewById(R.id.delete);
        btn.setText("搜索");
        editText = (EditText) view.findViewById(R.id.action_bar_search_edit_text);
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
                }else clear.setVisibility(View.VISIBLE);
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
        type = "list";
        showLoadingDialog(-1);
        sendRequest();
    }
    void sendRequest(){
        mHeader.onRefresh();
    }
    @Override
    protected void onResume() {
        super.onResume();
        sendRequest();
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
    String type="list";


    PushHeader.OnRefreshListener refreshListener = new PushHeader.OnRefreshListener() {
        @Override
        public void onRefresh() {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("type", type);
            map.put("chkcode", IMApplication.getApplication().getchkcode());
            map.put("pageno", 0);
            map.put("s_keyword", editText.getText().toString());
            IMApplication.getApplication().runVolleyRequest(
                    new BaseRequest(Request.Method.POST, YohoField.URL_ACTIVITY, new Gson().toJson(map),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    L.d(response);
                                    mHeader.onRefreshComplete();
                                    dismissLoadingDialog();
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
                            dismissLoadingDialog();
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
            map.put("type", type);
            map.put("chkcode", IMApplication.getApplication().getchkcode());
            map.put("pageno", mAdapter.getCount() / 10);
            map.put("s_keyword", editText.getText().toString());
            IMApplication.getApplication().runVolleyRequest(
                    new BaseRequest(Request.Method.POST, YohoField.URL_ACTIVITY, new Gson().toJson(map),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    L.d(response);
                                    mHeader.onRefreshComplete();
                                    dismissLoadingDialog();
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
                            dismissLoadingDialog();
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
                convertView.setTag(holder);
            }
            holder = (Holder) convertView.getTag();
            EventBean bean = getItem(position);
            holder.name.setText(bean.name);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(bean.s_creat_uname);
            builder.setSpan(new NameSpan(bean.s_creat_uname, bean.uid), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append("\t" + new SimpleDateFormat("yyyy-MM-dd").format(bean.creatTime));
            holder.creatName.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
            holder.creatName.setText(builder);
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
        ImageView img;
        TextViewFixTouchConsume creatName;
    }


}
