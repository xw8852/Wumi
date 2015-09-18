package com.android.msx7.followinstagram.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.LoginActivity;
import com.android.msx7.followinstagram.activity.VerifyPhoneActivity;
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

/**
 * Created by xiaowei on 2015/9/1.
 */
public class SignUpFragment extends BaseFragment {
    TextView next;
    EditText userPhone;
    EditText password;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.sign_in_tab, null);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userPhone = (EditText) getView().findViewById(R.id.userphone);
        password = (EditText) getView().findViewById(R.id.userpassword);
//        password.setInputType(EditorInfo.IME_ACTION_DONE);
        next = (TextView) getView().findViewById(R.id.next);
        next.setOnClickListener(nextClickListener);

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    nextClickListener.onClick(next);
                }
                return false;
            }
        });
    }

    View.OnClickListener nextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(userPhone.getText())) {
                ToastUtil.show(R.string.reg_phone_empty);
                return;
            }
            if (TextUtils.isEmpty(password.getText())) {
                ToastUtil.show(R.string.reg_password_none);
                return;
            }
            if (password.getText().toString().trim().length() < 6) {
                ToastUtil.show(R.string.reg_password_not_full);
                return;
            }

            Request request = new UserRequest(
                    RequestGsonUtils.getGson(new Pair<String, String>("type", "sendverifysms"),
                            new Pair<String, String>("i_tel_number", userPhone.getText().toString())),
                    resultListener, errorListener);
            IMApplication.getApplication().runVolleyRequest(request);
            showLoadingDialog(-1);
        }
    };
    Response.Listener<String> resultListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            dismissLoadingDialog();
            L.d(response);
            BaseResponse _respone = new Gson().fromJson(response, BaseResponse.class);
            if (_respone.retcode != 0) {
                ToastUtil.show(_respone.showmsg);
            } else {
                ToastUtil.show(R.string.reg_verify_send_success);
                Intent intent = new Intent(getView().getContext(), VerifyPhoneActivity.class);
                intent.putExtra(VerifyPhoneActivity.PARAM_PHONE, userPhone.getText().toString());
                intent.putExtra(VerifyPhoneActivity.PARAM_PASSWORD, password.getText().toString());
                ((Activity)getView().getContext()).startActivityForResult(intent, LoginActivity.CODE_REGISTER);
            }

        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            dismissLoadingDialog();
            error.printStackTrace();
            VolleyErrorUtils.showError(error);
        }
    };
}
