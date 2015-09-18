package com.android.msx7.followinstagram.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.bean.UserInfo;
import com.android.msx7.followinstagram.common.BaseResponse;
import com.android.msx7.followinstagram.net.UserRequest;
import com.android.msx7.followinstagram.ui.LandingRotatingBackgroundView;
import com.android.msx7.followinstagram.ui.pic.SelectPicPopupWindow;
import com.android.msx7.followinstagram.ui.pic.UploadPic;
import com.android.msx7.followinstagram.util.InputKeyBoardUtils;
import com.android.msx7.followinstagram.util.L;
import com.android.msx7.followinstagram.util.RequestGsonUtils;
import com.android.msx7.followinstagram.util.ToastUtil;
import com.android.msx7.followinstagram.util.VolleyErrorUtils;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Josn on 2015/9/8.
 */
public class UpdateUserInfoActivity extends ImageSelectActivity {
    ImageView regImg;
    Pair<String, String> pair;
    TextView username;
    TextView introduce;
    RadioGroup radioGroup;
    String curPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        LandingRotatingBackgroundView backgroundView = (LandingRotatingBackgroundView) findViewById(R.id.landingRotatingBackgroundView);
        regImg = (ImageView) findViewById(R.id.RegImg);
        backgroundView.setAlignBottomView(findViewById(R.id.RegImg));
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.sign_in_container);
        getLayoutInflater().inflate(R.layout.update_user_info, viewGroup);
        findViewById(R.id.action_bar).setBackgroundColor(getResources().getColor(R.color.action_bar_transparent_background));
        findViewById(R.id.action_bar_button_back).setVisibility(View.GONE);
        findViewById(R.id.action_bar_button_action).setVisibility(View.GONE);
        regImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputKeyBoardUtils.autoDismiss(UpdateUserInfoActivity.this);
//                getInputMethodManager().hideSoftInputFromInputMethod(username.getWindowToken(),0);
                showMenu();
            }
        });
        TextView textView = (TextView) findViewById(R.id.action_bar_textview_title);
        textView.setText("更新资料");
        getLayoutInflater().inflate(R.layout.action_bar_button_text, (ViewGroup) findViewById(R.id.action_bar_textview_custom_title_container));
        findViewById(R.id.action_bar_textview_custom_title_container).setVisibility(View.VISIBLE);
        textView = (TextView) findViewById(R.id.action_bar_button_text);
        textView.setText("提交");
        textView.setOnClickListener(submitClickListener);
        username = (TextView) findViewById(R.id.username);
        introduce = (TextView) findViewById(R.id.introduce);
        radioGroup = (RadioGroup) findViewById(R.id.sex);
    }


    View.OnClickListener submitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(username.getText().toString())) {
                ToastUtil.show("用户名不能为空");
                return;
            }
            if (TextUtils.isEmpty(curPath)) {
                ToastUtil.show("请设置头像");
                return;
            }
            showLoadingDialog(-1);
            //判断用户名是否可用
            validUserName();

        }
    };

    //提交资料
    void execute() {
        if (pair != null && pair.first.equals(curPath)) {
            submit();
        } else {
            new UploadPic(mTargetFileUri.getEncodedPath(), new UploadPic.UploadListener() {
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
        final Request request = new UserRequest(
                RequestGsonUtils.getGson(new Pair<String, String>("type", "checkuname"),
                        new Pair<String, String>("s_user_name", username.getText().toString())),
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

    void submit() {

        String chkcode = IMApplication.getApplication().getchkcode();
        chkcode = java.net.URLDecoder.decode(chkcode);
        Request request = new UserRequest(
                RequestGsonUtils.getGson(new Pair<String, String>("type", "update"),
                        new Pair<String, String>("chkcode", chkcode),
                        new Pair<String, String>("s_user_name", username.getText().toString()),
                        new Pair<String, String>("s_user_image", pair.second),
                        new Pair<String, String>("s_introduce", introduce.getText().toString()),
                        new Pair<String, String>("i_sex", radioGroup.getCheckedRadioButtonId() == R.id.boy ? "0" : "1")),
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
                ToastUtil.show(R.string.update_user_info_success);
                startActivity(new Intent(UpdateUserInfoActivity.this, UpdateContactActivity.class));
                finish();
            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            dismissLoadingDialog();
            VolleyErrorUtils.showError(error);
            error.printStackTrace();
        }
    };


    @Override
    protected String getImagePrefix() {
        return "user_";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
//                        mAdapter.addPhoto(Uri.decode(Uri.fromFile(new File(imgPath)).toString()));
                        cropPhoto(Uri.fromFile(new File(_imgPath)));
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
                        //path转Uri 会把中文编码，导致图片无法加载，故需要将uri解码，图片才可以正常显示
//                        mAdapter.addPhoto(Uri.decode(Uri.fromFile(new File(imgPath)).toString()));
                        cropPhoto(Uri.fromFile(new File(imgPath)));
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
            case CROP_PHOTO_CODE:
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
                    Bitmap bitmap = BitmapFactory.decodeFile(mTargetFileUri.getEncodedPath(), options);
                    bitmap = getCroppedBitmap(bitmap, Math.min(bitmap.getHeight(), bitmap.getWidth()));
//                    String _path=mTargetFileUri.getEncodedPath();
//                    _path.replace(".jpg",".png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(mTargetFileUri.getEncodedPath()));
                    if (bitmap != null && getImageView() != null) {
                        IMApplication.getApplication().displayImage(mTargetFileUri.toString(), getImageView());
                    }
                    curPath = mTargetFileUri.toString();
                    pair = null;
                    new UploadPic(mTargetFileUri.getEncodedPath(), listener).execute();
                    //删除裁剪前的文件
                    if (mFileUri != null) {
                        String fileName = mFileUri.getEncodedPath();
                        File file = new File(fileName);
                        if (file != null && file.exists()) {
                            file.delete();
                        }
                        mFileUri = null;
                    }
                    dismissMenu();
                } catch (Exception e) {
                    e.printStackTrace();
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

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public ImageView getImageView() {
        return regImg;
    }
}
