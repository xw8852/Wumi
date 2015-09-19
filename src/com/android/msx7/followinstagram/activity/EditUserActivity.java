package com.android.msx7.followinstagram.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.bean.UserInfo;
import com.android.msx7.followinstagram.common.BaseActivity;
import com.android.msx7.followinstagram.common.BaseResponse;
import com.android.msx7.followinstagram.common.YohoField;
import com.android.msx7.followinstagram.fragment.TabProfileFragment;
import com.android.msx7.followinstagram.net.BaseRequest;
import com.android.msx7.followinstagram.net.UserRequest;
import com.android.msx7.followinstagram.ui.pic.UploadPic;
import com.android.msx7.followinstagram.util.L;
import com.android.msx7.followinstagram.util.RequestGsonUtils;
import com.android.msx7.followinstagram.util.ToastUtil;
import com.android.msx7.followinstagram.util.VolleyErrorUtils;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

/**
 * Created by Josn on 2015/9/17.
 */
public class EditUserActivity extends ImageSelectActivity {
    EditText userName;
    EditText intro;
    ImageView profileImg;
    RadioGroup sexGroup;

    Pair<String, String> pair;
    String curPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        getTitleBar().setTitle("编辑个人资料", null);
        getTitleBar().setLeftBtn("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getTitleBar().setRightBtn("完成", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(userName.getText().toString())) {
                    ToastUtil.show("用户名不能为空");
                    return;
                }
                showLoadingDialog(-1);
                //判断用户名是否可用
                validUserName();
                ;
            }
        });
        userName = (EditText) findViewById(R.id.username);
        intro = (EditText) findViewById(R.id.introduce);
        profileImg = (ImageView) findViewById(R.id.img);
        sexGroup = (RadioGroup) findViewById(R.id.sex);

        TextView phoneTv = ((TextView) findViewById(R.id.phone));
        UserInfo info = IMApplication.getApplication().getUserInfo();
        String phone = info.telNumber;
        if (phone.startsWith("86")) {
            phone = phone.replaceFirst("86", "");
        }
        phoneTv.setText(phone);
        userName.setText(info.userName);
        intro.setText(info.s_introduce);
        IMApplication.getApplication().displayImage(info.userImg, profileImg);
        sexGroup.check(info.sex == 0 ? R.id.boy : R.id.girl);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });
    }


    @Override
    public ImageView getImageView() {
        return profileImg;
    }

    protected String getImagePrefix() {
        return "user_";
    }


    void submit() {
        String chkcode = IMApplication.getApplication().getchkcode();
        chkcode = java.net.URLDecoder.decode(chkcode);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("type", "update");
        map.put("chkcode", chkcode);
        map.put("s_user_name", userName.getText().toString());
        map.put("s_introduce", intro.getText().toString());
        map.put("i_sex", sexGroup.getCheckedRadioButtonId() == R.id.boy ? 0 : 1);
        if (pair != null && !TextUtils.isEmpty(pair.second)) {
            map.put("s_user_image", pair.second);
        } else map.put("s_user_image", IMApplication.getApplication().getUserInfo().userImg);
        Request request = new UserRequest(new Gson().toJson(map),
                resultListener, errorListener);
        IMApplication.getApplication().runVolleyRequest(request);
    }

    Response.Listener<String> resultListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            L.d(response);
            dismissLoadingDialog();
            BaseResponse<UserInfo> _respone = new Gson().fromJson(response, new TypeToken<BaseResponse<UserInfo>>() {
            }.getType());
            if (_respone.retcode != 0) {
                ToastUtil.show(_respone.showmsg);
            } else {
                _respone.retbody.chkcode = _respone.chkcode;
                IMApplication.getApplication().savechkcode(_respone.chkcode);
                IMApplication.getApplication().saveUserInfo(_respone.retbody);
                ToastUtil.show("编辑资料成功");
                setResult(RESULT_OK);
                finish();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
//        loadData();
    }

    //提交资料
    void execute() {
        if (pair == null ||
                (pair != null && !TextUtils.isEmpty(curPath) && curPath.equals(pair.first))) {
            submit();
        } else {
            new UploadPic(curPath, new UploadPic.UploadListener() {
                @Override
                public void doFinish(String path, String url) {
                    if (!TextUtils.isEmpty(url)) {
                        pair = new Pair<String, String>(path, url);
                        submit();
                    } else {
                        dismissLoadingDialog();
                        ToastUtil.show("上传头像失败，请稍后重试");
                    }
                }
            }).execute();
        }
    }

    void validUserName() {
        if (userName.getText().toString().equals(IMApplication.getApplication().getUserInfo().userName)) {
            execute();
            return;
        }
        final Request request = new UserRequest(
                RequestGsonUtils.getGson(new Pair<String, String>("type", "checkuname"),
                        new Pair<String, String>("s_user_name", userName.getText().toString())),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        L.d(response);
                        BaseResponse res = new Gson().fromJson(response, BaseResponse.class);
                        if (res.retcode != 0) {
                            ToastUtil.show(res.showmsg);
                            dismissLoadingDialog();
                        } else {
                            execute();
                        }
                    }
                }, errorListener);
        IMApplication.getApplication().runVolleyRequest(request);
    }


    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            dismissLoadingDialog();
            VolleyErrorUtils.showError(error);
            error.printStackTrace();
        }
    };

    /**
     * ## 返回该用户名的用户信息
     * #_item = {}
     * #_item['type'] = 'queryuname'
     * #_item['s_user_name'] = '大水牛'
     * #response = bt.httpget(_url = myurl, _params = _item)
     * #print response.status
     * #print json.dumps(json.loads(response.read()), indent = 4)
     * <p/>
     * ## 返回结果
     * ## {
     * ##     "message": "OK",
     * ##     "retcode": 0,
     * ##     "retbody": {
     * ##         "i_status": 0,
     * ##         "s_user_name": "\u5927\u6c34\u725b",
     * ##         "s_user_image": "http://pic.yooho.me/p/22_abs",
     * ##         "i_user_id": 1,
     * ##         "s_address": ""
     * ##     }
     * ## }
     */
    void loadData() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("type", "info");
        map.put("i_user_id", IMApplication.getApplication().getUserInfo().userId);

        IMApplication.getApplication().runVolleyRequest(new BaseRequest(Request.Method.GET, YohoField.URL_USER, new Gson().toJson(map), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.d(response);
                dismissLoadingDialog();
                BaseResponse<TabProfileFragment.ProfileInfo> result = new Gson().fromJson(response, new TypeToken<BaseResponse<TabProfileFragment.ProfileInfo>>() {
                }.getType());
                if (result.retcode != 0)
                    ToastUtil.show(result.showmsg);
                else {
                    intro.setText(result.retbody.s_introduce);
                    sexGroup.check(result.retbody.sex == 0 ? R.id.boy : R.id.girl);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyErrorUtils.showError(error);
                dismissLoadingDialog();
            }
        }));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        dismissMenu();
        if (RESULT_OK != resultCode) {
            return;
        }
        switch (requestCode) {
            case OPEN_PIC:
                L.d("---OPEN_PIC---" + data.getData().toString());
                if (data != null) {
                    mFileUri = data.getData();
                    String _imgPath = getPath(this, mFileUri);
                    L.d("onActivityResult()--->imgPath=" + _imgPath);
                    L.d("MSG", "onActivityResult()--->Uri=" + mFileUri.toString());
                    if (!TextUtils.isEmpty(_imgPath)) {
                        //path转Uri 会把中文编码，导致图片无法加载，故需要将uri解码，图片才可以正常显示
//                        mAdapter.addPhoto();
                        curPath = _imgPath;
                        pair = new Pair<String, String>("", "");
                        IMApplication.getApplication().displayImage(Uri.decode(Uri.fromFile(new File(_imgPath)).toString()), getImageView());
                        new UploadPic(_imgPath, listener).execute();
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
                        curPath = imgPath;
                        pair = new Pair<String, String>("", "");
                        //path转Uri 会把中文编码，导致图片无法加载，故需要将uri解码，图片才可以正常显示
//                        mAdapter.addPhoto(Uri.decode(Uri.fromFile(new File(imgPath)).toString()));
                        IMApplication.getApplication().displayImage(Uri.decode(Uri.fromFile(new File(imgPath)).toString()), getImageView());
                        new UploadPic(imgPath, listener).execute();
                    }
                }
                break;
            case OPEN_CAMERA_CODE:
                final String path = mFileUri == null ? "" : mFileUri.getPath();
                if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                    //path转Uri 会把中文编码，导致图片无法加载，故需要将uri解码，图片才可以正常显示
//                    mAdapter.addPhoto(Uri.decode(Uri.fromFile(new File(path)).toString()));
                    cropPhoto(mFileUri);
//                } else if (data != null && data.hasExtra("data")) {
//                    Bitmap bitmap = (Bitmap) data.getParcelableExtra("data");
//                    try {
//                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(path));
////                        mAdapter.addPhoto(Uri.decode(Uri.fromFile(new File(path)).toString()));
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } finally {
//                    }
                } else {
                    ToastUtil.show("无法获取照片！");
                }
                break;

        }
    }

    UploadPic.UploadListener listener = new UploadPic.UploadListener() {
        @Override
        public void doFinish(String path, String url) {
            if (!TextUtils.isEmpty(url)) {
                pair = new Pair<String, String>(path, url);
                L.d("url----" + url);
            }
        }
    };
}
