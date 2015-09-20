package com.android.msx7.followinstagram.activity;

import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
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
import com.android.msx7.followinstagram.bean.EventBean;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Josn on 2015/9/10.
 */
public class ShareImageActivity extends ImageSelectActivity {

    public static final int QUAN_REN = 0x100;
    public static final int ADDRESS = 0x101;
    public static final int EVENT = 0x102;
    public static final String PARAM_IMG_PATH = "param_img_path";
    String path;
    ImageView shareImg;
    Pair<String, String> pair;
    EditText desc;
    AddressLocation mAddressLocation;
    EventBean mEvent;
    TextView address1;
    TextView action1;
    TextView people1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_push_img);
        addBack();
        shareImg = (ImageView) findViewById(R.id.img);
        address1 = (TextView) findViewById(R.id.address1);
        action1 = (TextView) findViewById(R.id.action1);
        people1 = (TextView) findViewById(R.id.people1);
        path = getIntent().getStringExtra(PARAM_IMG_PATH);
//        path = Uri.decode(Uri.fromFile(new File(path)).toString());
        if (getIntent().hasExtra("EVENT")) {
            mEvent = new Gson().fromJson(getIntent().getStringExtra("EVENT"), EventBean.class);
            if (mEvent != null) {
                action1.setText(mEvent.name);
                action1.setVisibility(View.VISIBLE);
            }
        }
        if (!TextUtils.isEmpty(path))
            IMApplication.getApplication().displayImage(Uri.decode(Uri.fromFile(new File(path)).toString()), shareImg);
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
                if (TextUtils.isEmpty(path)) {
                    ToastUtil.show("请先选择图片");
                    return;
                }
                Intent intent = new Intent(ShareImageActivity.this, ImgFindUserActivity.class);
                if (quanList != null && !quanList.isEmpty()) {
                    intent.putExtra("data", new Gson().toJson(quanList));
                }
                intent.putExtra(ImgFindUserActivity.PARAM_PATH, Uri.decode(Uri.fromFile(new File(path)).toString()));
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
                    return;
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
        findViewById(R.id.action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ShareImageActivity.this, EventListActivity.class), EVENT);
            }
        });
        shareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });
    }

    @Override
    protected String getImagePrefix() {
        return "ac_";
    }

    @Override
    public View getImageView() {
        return shareImg;
    }


    public void share() {
        if (TextUtils.isEmpty(path)) {
            ToastUtil.show("请先选择图片");
            return;
        }
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
            if (!data.hasExtra("data")) {
                quanList = null;
                people1.setVisibility(View.GONE);
                return;
            }
            String _data = data.getStringExtra("data");
            L.d(_data);
            quanList = new Gson().fromJson(_data, new TypeToken<ArrayList<SimpleContact>>() {
            }.getType());
            if (quanList != null && !quanList.isEmpty()) {
                people1.setText("已圈出"+quanList.size()+"人");
                people1.setVisibility(View.VISIBLE);
            } else
                people1.setVisibility(View.GONE);
        } else if (resultCode == RESULT_OK && requestCode == ADDRESS && data != null) {
            if (!data.hasExtra("data")) {
                mAddressLocation = null;
                address1.setVisibility(View.GONE);
                return;
            }
            String _data = data.getStringExtra("data");
            L.d(_data);
            mAddressLocation = new Gson().fromJson(_data, AddressLocation.class);
            if (mAddressLocation != null && !TextUtils.isEmpty(mAddressLocation.s_addr)) {
                address1.setText(mAddressLocation.s_name);
                address1.setVisibility(View.VISIBLE);
            } else
                address1.setVisibility(View.GONE);
        } else if (resultCode == RESULT_OK && requestCode == EVENT && data != null) {
            if (!data.hasExtra("data")) {
                mEvent = null;
                action1.setVisibility(View.GONE);
                return;
            }
            String _data = data.getStringExtra("data");
            L.d(_data);
            mEvent = new Gson().fromJson(_data, EventBean.class);
            if (mEvent != null && !TextUtils.isEmpty(mEvent.name)) {
                action1.setText(mEvent.name);
                action1.setVisibility(View.VISIBLE);
            } else
                action1.setVisibility(View.GONE);
        } else {
            dismissMenu();
            switch (requestCode) {
                case OPEN_PIC:
                    L.d("---OPEN_PIC---" + data.getData().toString());
                    if (data != null) {
                        mFileUri = data.getData();
                        String _imgPath = getPath(this, mFileUri);
                        L.d("onActivityResult()--->imgPath=" + _imgPath);
                        L.d("MSG", "onActivityResult()--->Uri=" + mFileUri.toString());
                        if (!TextUtils.isEmpty(_imgPath)) {
                            path = _imgPath;
                            IMApplication.getApplication().displayImage(Uri.decode(Uri.fromFile(new File(path)).toString()), shareImg);
                        }
                    }
                    break;
                case OPEN_PIC_KITKAT:
                    L.d("---OPEN_PIC_KITKAT---" + data.getData().toString());
                    if (data != null) {
                        mFileUri = data.getData();
                        String imgPath = getPath(this, mFileUri);
                        L.d("onActivityResult()--->imgPath=" + imgPath);
                        L.d("MSG", "onActivityResult()--->Uri=" + mFileUri.toString());
                        if (!TextUtils.isEmpty(imgPath)) {
                            path = imgPath;
                            IMApplication.getApplication().displayImage(Uri.decode(Uri.fromFile(new File(path)).toString()), shareImg);
                        }
                    }
                    break;
                case OPEN_CAMERA_CODE:
                    final String path = mFileUri == null ? "" : mFileUri.getPath();
                    if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                        ShareImageActivity.this.path = path;
                        IMApplication.getApplication().displayImage(Uri.decode(Uri.fromFile(new File(path)).toString()), shareImg);
                    } else {
                        ToastUtil.show("无法获取照片！");
                    }
                    break;


            }
        }
    }

    public void submit() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("img_url", pair.second);
        if (quanList != null && !quanList.isEmpty())
            map.put("guys", quanList);
        if (new File(path).exists()) {
            HashMap<String, Object> map3 = new HashMap<String, Object>();
            map3.put("i_take_time", new File(path).lastModified());
            map3.put("device", Build.MODEL);
            map.put("exif", getExif());
        }
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
            map3.put("addr", mAddressLocation.s_name);
            map2.put("j_loc_info", map3);
        }
        if (mEvent != null) {
//            _item['j_activity']  = {'id':10,'name':'02-617中秋大连行'}
            HashMap<String, Object> map4 = new HashMap<String, Object>();
            map4.put("id", mEvent.eventId);
            map4.put("name", mEvent.name);
            map2.put("j_activity", map4);

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

    HashMap<String, String> getExif() {
        HashMap<String, String> map = new HashMap<String, String>();

        /**
         * 　TAG_APERTURE：光圈值。
         　　TAG_DATETIME：拍摄时间，取决于设备设置的时间。
         　　TAG_EXPOSURE_TIME：曝光时间。
         　　TAG_FLASH：闪光灯。
         　　TAG_FOCAL_LENGTH：焦距。
         　　TAG_IMAGE_LENGTH：图片高度。
         　　TAG_IMAGE_WIDTH：图片宽度。
         　　TAG_ISO：ISO。
         　　TAG_MAKE：设备品牌。
         　　TAG_MODEL：设备型号，整形表示，在ExifInterface中有常量对应表示。
         　　TAG_ORIENTATION：旋转角度，整形表示，在ExifInterface中有常量对应表示。
         TAG_GPS_LATITUDE
         TAG_GPS_LONGITUDE
         */
        try {
            L.d(path);
            ExifInterface exifInterface = new ExifInterface(path);
            String APERTURE = exifInterface.getAttribute(ExifInterface.TAG_APERTURE);
            if (!TextUtils.isEmpty(APERTURE)) map.put("APERTURE", APERTURE);
            String DATETIME = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            if (!TextUtils.isEmpty(DATETIME)) map.put("DATETIME", DATETIME);
            String EXPOSURE_TIME = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
            if (!TextUtils.isEmpty(EXPOSURE_TIME)) map.put("EXPOSURE_TIME", EXPOSURE_TIME);
            String FLASH = exifInterface.getAttribute(ExifInterface.TAG_FLASH);
            if (!TextUtils.isEmpty(FLASH)) map.put("FLASH", FLASH);
            String FOCAL_LENGTH = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
            if (!TextUtils.isEmpty(FOCAL_LENGTH)) map.put("FOCAL_LENGTH", FOCAL_LENGTH);
            String IMAGE_LENGTH = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            if (!TextUtils.isEmpty(IMAGE_LENGTH)) map.put("IMAGE_LENGTH", IMAGE_LENGTH);
            String IMAGE_WIDTH = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            if (!TextUtils.isEmpty(IMAGE_WIDTH)) map.put("IMAGE_WIDTH", IMAGE_WIDTH);
            String ISO = exifInterface.getAttribute(ExifInterface.TAG_ISO);
            if (!TextUtils.isEmpty(ISO)) map.put("ISO", ISO);
            String MAKE = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
            if (!TextUtils.isEmpty(MAKE)) map.put("MAKE", MAKE);
            String MODEL = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
            if (!TextUtils.isEmpty(MODEL)) map.put("MODEL", MODEL);
            String ORIENTATION = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
            if (!TextUtils.isEmpty(ORIENTATION)) map.put("ORIENTATION", ORIENTATION);
            String WHITE_BALANCE = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
            if (!TextUtils.isEmpty(WHITE_BALANCE)) map.put("WHITE_BALANCE", WHITE_BALANCE);

            String GPS_LATITUDE = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            if (!TextUtils.isEmpty(ORIENTATION)) map.put("GPS_LATITUDE", GPS_LATITUDE);
            String GPS_LONGITUDE = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            if (!TextUtils.isEmpty(GPS_LONGITUDE)) map.put("GPS_LONGITUDE", GPS_LONGITUDE);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return map;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        IMApplication.getApplication().mLocationClient.stop();
    }
}
