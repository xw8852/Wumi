package com.android.msx7.followinstagram.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.AddressActvity.AddressLocation;
import com.android.msx7.followinstagram.activity.ImgFindUserActivity.SimpleContact;
import com.android.msx7.followinstagram.common.BaseActivity;
import com.android.msx7.followinstagram.net.PoRequest;
import com.android.msx7.followinstagram.ui.login.BackActionButtonDrawable;
import com.android.msx7.followinstagram.ui.pic.UploadPic;
import com.android.msx7.followinstagram.ui.span.NameSpan;
import com.android.msx7.followinstagram.ui.span.TopicSpan;
import com.android.msx7.followinstagram.util.L;
import com.android.msx7.followinstagram.util.StringsUtils;
import com.android.msx7.followinstagram.util.ToastUtil;
import com.android.msx7.followinstagram.util.VolleyErrorUtils;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Josn on 2015/9/10.
 */
public class ShareImageActivity extends BaseActivity {
    public static final int QUAN_REN = 0x100;
    public static final int ADDRESS = 0x101;
    public static final String PARAM_IMG_PATH = "param_img_path";
    String path;

    ImageView shareImg;
    Pair<String, String> pair;
    EditText desc;
    AddressLocation mAddressLocation;
    TextView address1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_push_img);
        shareImg = (ImageView) findViewById(R.id.img);
        address1 = (TextView) findViewById(R.id.address1);
//        findViewById(R.id.action_bar_button_back).setBackgroundDrawable(new BackActionButtonDrawable(getResources(), false));
        path = getIntent().getStringExtra(PARAM_IMG_PATH);
//        findViewById(R.id.action_bar_button_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
        IMApplication.getApplication().displayImage(Uri.fromFile(new File(path)).toString(), shareImg);
        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
        desc = (EditText) findViewById(R.id.desc);
        findViewById(R.id.people).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareImageActivity.this, ImgFindUserActivity.class);
                intent.putExtra(ImgFindUserActivity.PARAM_PATH, path);
                startActivityForResult(intent, QUAN_REN);
            }
        });
        IMApplication.getApplication().mLocationClient.start();
        IMApplication.getApplication().location = null;
        findViewById(R.id.address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IMApplication.getApplication().location == null ||
                        (IMApplication.getApplication().location.getLongitude() == 0 && IMApplication.getApplication().location.getLatitude() == 0) ||
                        (IMApplication.getApplication().location.getLongitude() == Double.MAX_VALUE && IMApplication.getApplication().location.getLatitude() == Double.MAX_VALUE) ||
                        (IMApplication.getApplication().location.getLongitude() == Double.MIN_VALUE && IMApplication.getApplication().location.getLatitude() == Double.MIN_VALUE)) {
                    ToastUtil.show("定位失败，请稍后重新尝试");
                }
                Intent intent = new Intent(ShareImageActivity.this, AddressActvity.class);
                intent.putExtra(AddressActvity.PARAM_LAT, IMApplication.getApplication().location.getLatitude());
                intent.putExtra(AddressActvity.PARAM_LNG, IMApplication.getApplication().location.getLongitude());
                startActivityForResult(intent, ADDRESS);
            }
        });
        getTitleBar().setTitle("分享图片", null);
        desc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String desc = s.toString();

                String[] arr = StringsUtils.findString(desc);
                if (arr != null) {
                    int start = 0;
                    int lastEnd = 0;
                    for (String _arr : arr) {
                        start = desc.indexOf(_arr, start);
                        int end = _arr.length();
                        if (end == 1) continue;
                        if (start < 0) start = lastEnd;
                        if (_arr.startsWith("@")) {
                            s.setSpan(new NameSpan(_arr.substring(1).trim(), -1), start, start + end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else if (_arr.startsWith("#")) {
                            String topic = _arr.substring(1).trim();
                            s.setSpan(new TopicSpan(topic), start, start + end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        lastEnd = end;
                        start = end;
                    }
                }
            }
        });
    }

    public void share() {
        showLoadingDialog(-1);
        if (pair != null) {
            submit();
            return;
        }
        new UploadPic(path, new UploadPic.UploadListener() {
            @Override
            public void doFinish(String path, String url) {
                if (!TextUtils.isEmpty(url)) {
                    pair = new Pair<String, String>(path, url);
                    submit();
                } else {
                    dismissLoadingDialog();
                    ToastUtil.show("分享图片失败，请稍后重试");
                }
            }
        }).execute();
    }

    //圈人的图像数据
    ArrayList<SimpleContact> quanList;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == QUAN_REN && data != null) {
            String _data = data.getStringExtra("data");
            L.d(_data);
            quanList = new Gson().fromJson(_data, new TypeToken<ArrayList<SimpleContact>>() {
            }.getType());
        } else if (resultCode == RESULT_OK && requestCode == ADDRESS && data != null) {
            String _data = data.getStringExtra("data");
            L.d(_data);
            mAddressLocation = new Gson().fromJson(_data, AddressLocation.class);
            if (mAddressLocation != null && !TextUtils.isEmpty(mAddressLocation.s_addr)) {
                address1.setText(mAddressLocation.s_addr);
                address1.setVisibility(View.VISIBLE);
            } else
                address1.setVisibility(View.GONE);
        }
    }

    public void submit() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("img_url", pair.second);
        if (quanList != null && !quanList.isEmpty())
            map.put("guys", quanList);
        List list = new ArrayList();
        list.add(map);
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("l_img_info_list", list);
        map2.put("type", "insert");
        /**
         *   _item['f_loc_lat']  = '31.206308512993'
         _item['f_loc_lng']  = '121.60180408538'
         */
        if (IMApplication.getApplication().location != null) {
            map2.put("f_loc_lat", "" + IMApplication.getApplication().location.getLatitude());
            map2.put("f_loc_lng", "" + IMApplication.getApplication().location.getLongitude());
        }
        if (mAddressLocation != null) {
            /**
             *     _item['j_loc_info'] = {'loc_id':1, 'addr':'上海 滨江森林公园'}
             */
            HashMap<String, Object> map3 = new HashMap<String, Object>();
            map3.put("loc_id", mAddressLocation._id);
            map3.put("addr", mAddressLocation.s_addr);
            map2.put("j_loc_info", map3);
        }
        if (!TextUtils.isEmpty(desc.getText().toString())) {
            map2.put("s_desc", desc.getText().toString());
        }
        map2.put("chkcode", IMApplication.getApplication().getchkcode());
        //                RequestGsonUtils.getGson(new Pair<String, String>("type", "insert"),
//                        new Pair<String, String>("chkcode", IMApplication.getApplication().getchkcode()),
//                        new Pair<String, String>("l_img_info_list",new Gson().toJson(list))
        IMApplication.getApplication().runVolleyRequest(new PoRequest(new Gson().toJson(map2), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dismissLoadingDialog();
                finish();
                IMApplication.getApplication().mLocationClient.stop();
                L.d("ShareImageActivity-----" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoadingDialog();
                error.printStackTrace();
                VolleyErrorUtils.showError(error);
            }
        }));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        IMApplication.getApplication().mLocationClient.stop();
    }
}
