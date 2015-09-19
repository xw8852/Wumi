package com.android.msx7.followinstagram.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.android.db.BaseTable;
import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.bean.EventBean;
import com.android.msx7.followinstagram.common.BaseActivity;
import com.android.msx7.followinstagram.common.BaseResponse;
import com.android.msx7.followinstagram.common.YohoField;
import com.android.msx7.followinstagram.net.BaseRequest;
import com.android.msx7.followinstagram.util.L;
import com.android.msx7.followinstagram.util.ToastUtil;
import com.android.msx7.followinstagram.util.VolleyErrorUtils;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Josn on 2015/9/19.
 */
public class AddEventActivity extends BaseActivity {
    EditText mName;
    EditText mdesc;
    RadioGroup mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);
        getTitleBar().setTitle("新建活动", null);
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        mName = (EditText) findViewById(R.id.eventName);
        mdesc = (EditText) findViewById(R.id.eventDesc);
        mType = (RadioGroup) findViewById(R.id.type);
    }

    void submit() {
        if (TextUtils.isEmpty(mName.getText().toString())) {
            ToastUtil.show("活动名称不能为空");
            return;
        }
        if (mName.getText().toString().length() < 2) {
            ToastUtil.show("活动名称不能少于2个字符");
            return;
        }
        if (TextUtils.isEmpty(mdesc.getText().toString())) {
            ToastUtil.show("活动简介不能为空");
            return;
        }
        if (mdesc.getText().toString().length() < 4) {
            ToastUtil.show("活动简不能少于4个字符");
            return;
        }
        showLoadingDialog(-1);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("s_name", mName.getText().toString());
        map.put("type", "add");
        map.put("i_status", mType.getCheckedRadioButtonId() == R.id.open ? 0 : 1);
        map.put("s_desc", mdesc.getText().toString());
        map.put("chkcode", IMApplication.getApplication().getchkcode());
        map.put("pageno", 0);

        IMApplication.getApplication().runVolleyRequest(
                new BaseRequest(Request.Method.POST, YohoField.URL_ACTIVITY, new Gson().toJson(map),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                L.d(response);
                                dismissLoadingDialog();
                                BaseResponse<EventBean> re = new Gson().fromJson(response, new TypeToken<BaseResponse<EventBean>>() {
                                }.getType());
                                if (re.retcode != 0) {
                                    ToastUtil.show(re.showmsg);
                                } else {
                                    ToastUtil.show("活动创建成功");
                                    Intent intent = new Intent();
                                    intent.putExtra("data", new Gson().toJson(re.retbody));
                                    setResult(RESULT_OK, intent);
                                    finish();
                                    new BaseTable<EventBean>(AddEventActivity.this) {
                                    }.insertOrUpdate(re.retbody);
                                }
                            }
                        }
                        , new Response.ErrorListener()

                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyErrorUtils.showError(error);
                        dismissLoadingDialog();
                    }
                }

                )
        );


    }


}
