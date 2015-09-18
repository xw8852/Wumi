package com.android.msx7.followinstagram.ui.login;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.MainTabActivity;
import com.android.msx7.followinstagram.activity.VerifyPhoneActivity;
import com.android.msx7.followinstagram.bean.UserInfo;
import com.android.msx7.followinstagram.common.BaseFragment;
import com.android.msx7.followinstagram.common.BaseResponse;
import com.android.msx7.followinstagram.net.UserRequest;
import com.android.msx7.followinstagram.util.L;
import com.android.msx7.followinstagram.util.RequestGsonUtils;
import com.android.msx7.followinstagram.util.ToastUtil;
import com.android.msx7.followinstagram.util.VolleyErrorUtils;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by xiaowei on 2015/9/1.
 */
public class LoginFragment extends BaseFragment {
    public static final int MODE_FORGOT = 0x01;
    public static final int MODE_NEXT = 0x02;
    public static final int MODE_LOAD = 0x03;
    EditText mUserNameEt;
    EditText mPasswdEt;
    View mForgetView;
    ImageView mNextView;
    View mProgressView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.log_in_tab, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserNameEt = (EditText) getView().findViewById(R.id.username);
        mPasswdEt = (EditText) getView().findViewById(R.id.password);
        mForgetView = getView().findViewById(R.id.forgot);
        mNextView = (ImageView) getView().findViewById(R.id.next);
        mProgressView = getView().findViewById(R.id.progress);
        mUserNameEt.addTextChangedListener(watcher);
        mPasswdEt.addTextChangedListener(watcher);
        ColorFilter colorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.accent_blue_medium), PorterDuff.Mode.SRC_ATOP);
        mNextView.getDrawable().mutate().setColorFilter(colorFilter);
        showPasswordMode(MODE_FORGOT);
        mForgetView.setOnClickListener(forgotClickLister);
        mNextView.setOnClickListener(nextClickLister);
//        mPasswdEt.setInputType(EditorInfo.IME_ACTION_DONE);
        mPasswdEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    nextClickLister.onClick(mNextView);
                }
                return false;
            }
        });
    }

    View.OnClickListener forgotClickLister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getView().getContext(), VerifyPhoneActivity.class);
            intent.putExtra(VerifyPhoneActivity.PARAM_PHONE, mUserNameEt.getText().toString());
            intent.putExtra(VerifyPhoneActivity.PARAM_FOGET, "forgot");
//            intent.putExtra(VerifyPhoneActivity.PARAM_PASSWORD, password.getText().toString());
            startActivity(intent);
            //TODO:跳转到忘记密码
        }
    };
    View.OnClickListener nextClickLister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(mUserNameEt.getText().toString())) {
                ToastUtil.show("手机号码不能为空");
                return;
            }
            if (TextUtils.isEmpty(mPasswdEt.getText().toString())) {
                ToastUtil.show("密码不能为空");
                return;
            }
            //TODO:执行登陆
            Request request = new UserRequest(
                    RequestGsonUtils.getGson(new Pair<String, String>("type", "checkpass"),
                            new Pair<String, String>("i_tel_number", mUserNameEt.getText().toString()),
                            new Pair<String, String>("s_password", mPasswdEt.getText().toString())),
                    resultListener, errorListener);
            IMApplication.getApplication().runVolleyRequest(request);
            showLoadingDialog(-1);
        }
    };

    Response.Listener<String> resultListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            L.d(response);
            dismissLoadingDialog();
            BaseResponse<UserInfo> _respone = new Gson().fromJson(response, new TypeToken<BaseResponse<UserInfo>>() {
            }.getType());
            if (_respone.retcode != 0) {
                ToastUtil.show(_respone.showmsg);
                showPasswordMode(MODE_FORGOT);
            } else {
                _respone.retbody.chkcode = _respone.chkcode;
                IMApplication.getApplication().savechkcode(_respone.chkcode);
                IMApplication.getApplication().saveUserInfo(_respone.retbody);
                ToastUtil.show(R.string.log_in_success);
                IMApplication.getApplication().saveLoginState(true);
                startActivity(new Intent(getView().getContext(), MainTabActivity.class));
                ((Activity) getView().getContext()).finish();

            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            dismissLoadingDialog();
            error.printStackTrace();
            VolleyErrorUtils.showError(error);
            showPasswordMode(MODE_FORGOT);
        }
    };


    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(mUserNameEt.getText()) && !TextUtils.isEmpty(mPasswdEt.getText())) {
                showPasswordMode(MODE_NEXT);
            } else {
                showPasswordMode(MODE_FORGOT);
            }
        }
    };

    void showPasswordMode(int state) {
        mNextView.setVisibility(View.INVISIBLE);
        mProgressView.setVisibility(View.INVISIBLE);
        mForgetView.setVisibility(View.INVISIBLE);
        switch (state) {
            case MODE_FORGOT:
                mForgetView.setVisibility(View.VISIBLE);
                break;
            case MODE_NEXT:
                mNextView.setVisibility(View.VISIBLE);
                break;
            case MODE_LOAD:
                mProgressView.setVisibility(View.VISIBLE);
                break;
        }
    }


}
