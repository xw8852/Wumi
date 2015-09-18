package com.android.msx7.followinstagram.ui.login;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.bean.UserInfo;
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
 * Created by xiaowei on 2015/9/8.
 */
public class ResetPasswordFragment extends VerifyPhoneFragment {

    EditText passwordEt;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().findViewById(R.id.password_divider).setVisibility(View.VISIBLE);
        passwordEt = (EditText) getView().findViewById(R.id.password);
        passwordEt.setVisibility(View.VISIBLE);
        phoneView.setEnabled(true);
        timer.cancel();
        timer = null;
        timerTask = null;
        verifycode.setInputType(EditorInfo.IME_ACTION_NONE);
        verifycode.setOnEditorActionListener(null);
        passwordEt.setOnEditorActionListener(editorActionListener);
        clock.setText("获取验证码");
        clock.setEnabled(true);
        btn.setOnClickListener(resetclickListener);
    }

    View.OnClickListener resetclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(passwordEt.getText())) {
                ToastUtil.show(R.string.reg_password_none);
                return;
            }
            if (passwordEt.getText().toString().trim().length() < 6) {
                ToastUtil.show(R.string.reg_password_not_full);
                return;
            }

            Request request = new UserRequest(
                    RequestGsonUtils.getGson(new Pair<String, String>("type", "resetpassword"),
                            new Pair<String, String>("i_tel_number", phoneView.getText().toString()),
                            new Pair<String, String>("i_verify_code", verifycode.getText().toString()),
                            new Pair<String, String>("s_password", passwordEt.getText().toString())),
                    resetListener, resetErrorListener);
            IMApplication.getApplication().runVolleyRequest(request);
            showLoadingDialog(-1);
        }
    };

    Response.Listener<String> resetListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            dismissLoadingDialog();
            L.d(response);
            BaseResponse<UserInfo> _respone = new Gson().fromJson(response, new TypeToken<BaseResponse<UserInfo>>() {
            }.getType());
            if (_respone.retcode != 0) {
                ToastUtil.show(_respone.showmsg);
            } else {
//                _respone.retbody.chkcode = _respone.chkcode;
//                IMApplication.getApplication().savechkcode(_respone.chkcode);
//                IMApplication.getApplication().saveUserInfo(_respone.retbody);
                ToastUtil.show(R.string.reset_password_success);
                ((Activity) getView().getContext()).onBackPressed();
            }
        }
    };

    Response.ErrorListener resetErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            dismissLoadingDialog();
            error.printStackTrace();
            VolleyErrorUtils.showError(error);
        }
    };


}
