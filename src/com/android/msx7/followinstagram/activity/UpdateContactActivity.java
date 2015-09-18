package com.android.msx7.followinstagram.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.view.View;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.common.BaseActivity;
import com.android.msx7.followinstagram.common.BaseResponse;
import com.android.msx7.followinstagram.common.YohoField;
import com.android.msx7.followinstagram.net.BaseRequest;
import com.android.msx7.followinstagram.net.UserRequest;
import com.android.msx7.followinstagram.ui.actionbar.ActionBar;
import com.android.msx7.followinstagram.util.DialogUtils;
import com.android.msx7.followinstagram.util.L;
import com.android.msx7.followinstagram.util.ToastUtil;
import com.android.msx7.followinstagram.util.VolleyErrorUtils;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Josn on 2015/9/13.
 */
public class UpdateContactActivity extends BaseActivity {
    ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list);
        getTitleBar().setTitle("关注通讯录好友", null);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, 1000);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0)
                DialogUtils.ShowDialog("通讯录", "是否上传通讯录，匹配通讯录中的好友？", "取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stop();
                    }
                }, "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        update();
                    }
                }, UpdateContactActivity.this);
            else if (msg.what == 100) {
                List<ContactUserInfo> infos = (List<ContactUserInfo>) msg.obj;
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("type", "insert");
                map.put("chkcode", IMApplication.getApplication().getchkcode());
                map.put("l_contact_list", infos);
                IMApplication.getApplication().runVolleyRequest(new BaseRequest(Request.Method.POST, YohoField.URL_CONTACT, new Gson().toJson(map), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        L.d(response);
                        dismissLoadingDialog();
                        BaseResponse result = new Gson().fromJson(response, BaseResponse.class);
                        if (result.retcode == 0) {
                            startActivity(new Intent(UpdateContactActivity.this, MainTabActivity.class));
                            IMApplication.getApplication().saveLoginState(true);
                            finish();
                        } else {
                            handler.sendEmptyMessage(0);
                            ToastUtil.show(result.showmsg);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        dismissLoadingDialog();
                        VolleyErrorUtils.showError(error);
                    }
                }));
            }
        }
    };

    public void update() {
        showLoadingDialog(-1);
        new Thread() {
            @Override
            public void run() {
                super.run();
                List<ContactUserInfo> infos = new ArrayList<ContactUserInfo>();
                infos.addAll(getPhoneContacts());
                infos.addAll(getSIMContacts());
                Message msg = new Message();
                msg.what = 100;
                msg.obj = infos;
                handler.sendMessage(msg);
                L.d("MSG----", msg.obj.toString());
            }
        }.start();


    }

    public void stop() {
        startActivity(new Intent(this, MainTabActivity.class));
        finish();
    }

    private static final String[] PHONES_PROJECTION = new String[]{
            Phone.DISPLAY_NAME, Phone.NUMBER, Phone.CONTACT_ID};

    /**
     * 得到手机通讯录联系人信息
     **/
    private List<ContactUserInfo> getPhoneContacts() {
        List<ContactUserInfo> infos = new ArrayList<ContactUserInfo>();
        ContentResolver resolver = getContentResolver();

// 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);


        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                //得到手机号码
                String phoneNumber = phoneCursor.getString(1);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;

                //得到联系人名称
                String contactName = phoneCursor.getString(0);

                //得到联系人ID
                Long contactid = phoneCursor.getLong(2);

                ContactUserInfo info = new ContactUserInfo();
                info.name = contactName;
                info.nums = new String[]{phoneNumber};
                infos.add(info);
            }

            phoneCursor.close();
        }
        return infos;
    }

    /**
     * 得到手机SIM卡联系人人信息
     **/
    private List<ContactUserInfo> getSIMContacts() {
        List<ContactUserInfo> infos = new ArrayList<ContactUserInfo>();
        ContentResolver resolver = getContentResolver();
// 获取Sims卡联系人
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
                null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                // 得到手机号码
                String phoneNumber = phoneCursor.getString(1);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                // 得到联系人名称
                String contactName = phoneCursor.getString(0);
                ContactUserInfo info = new ContactUserInfo();
                info.name = contactName;
                info.nums = new String[]{phoneNumber};
                infos.add(info);
            }

            phoneCursor.close();
        }
        return infos;
    }

    public class ContactUserInfo {
        @SerializedName("s_ctt_uname")
        public String name;
        @SerializedName("l_telno_list")
        public String[] nums;
    }

}
