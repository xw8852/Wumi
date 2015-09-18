package com.android.msx7.followinstagram.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
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

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Josn on 2015/9/7.
 */
public class VerifyPhoneFragment extends BaseFragment {
    protected String phone;
    protected String password;
    protected TextView phoneView;
    protected TextView verifycode;
    protected TextView clock;
    protected TextView btn;
    protected Timer timer;
    protected TimerTask timerTask;

    int period = 60;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.verify_phone, null);
    }

    TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //执行 “提交”按钮的点击事件
                registerClickListner.onClick(btn);
            }
            return false;
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        phone = bundle.getString(VerifyPhoneActivity.PARAM_PHONE);
        password = bundle.getString(VerifyPhoneActivity.PARAM_PASSWORD);
        phoneView = (TextView) getView().findViewById(R.id.userphone);
        verifycode = (TextView) getView().findViewById(R.id.verifycode);
        clock = (TextView) getView().findViewById(R.id.clock);
        btn = (TextView) getView().findViewById(R.id.next);
        phoneView.setText(phone);
        scheduleColock();
        clock.setOnClickListener(sendVerifyListener);
        btn.setOnClickListener(registerClickListner);
        verifycode.setInputType(EditorInfo.IME_ACTION_DONE);
        verifycode.setOnEditorActionListener(editorActionListener);
    }

    String imei;
    View.OnClickListener registerClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(verifycode.getText())) {
                ToastUtil.show("验证码不能为空");
                return;
            }
            TelephonyManager tm = (TelephonyManager) v.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();
            L.d("imei----"+imei);
//            if (TextUtils.isEmpty(imei)) {
//                ToastUtil.show("无法识别手机身份，请不要禁用读取IMEI码的权限");
//                return;
//            }
/**
 * -01- 'type',          取值为'checkverifycode'
 -02- 'i_tel_number',  必填，用户的手机号码
 -03- 'i_verify_code', 必填，用户的手机验证码，4位
 -04- 's_password',    必填，用户的登陆密码
 -05- 's_devcode_id',  必填，用户的设备ID
 */
            Request request = new UserRequest(
                    RequestGsonUtils.getGson(new Pair<String, String>("type", "checkverifycode"),
                            new Pair<String, String>("i_tel_number", phoneView.getText().toString()),
                            new Pair<String, String>("i_verify_code", verifycode.getText().toString()),
                            new Pair<String, String>("s_password", password),
                            new Pair<String, String>("s_devcode_id", imei)
                    ),
                    registerResultListener, registerErrorListener);
            IMApplication.getApplication().runVolleyRequest(request);
            showLoadingDialog(-1);
        }
    };
    Response.Listener<String> registerResultListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            dismissLoadingDialog();
            L.d(response);
            BaseResponse<UserInfo> _respone = new Gson().fromJson(response, new TypeToken<BaseResponse<UserInfo>>() {
            }.getType());
            if (_respone.retcode != 0) {
                ToastUtil.show(_respone.showmsg);
            } else {
                _respone.retbody.chkcode = _respone.chkcode;
                IMApplication.getApplication().savechkcode(_respone.chkcode);
                IMApplication.getApplication().saveUserInfo(_respone.retbody);
                ToastUtil.show(R.string.reg_success);
                ((VerifyPhoneActivity)getView().getContext()).callback();
            }
        }
    };

    Response.ErrorListener registerErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            dismissLoadingDialog();
            error.printStackTrace();
            VolleyErrorUtils.showError(error);
        }
    };


    View.OnClickListener sendVerifyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Request request = new UserRequest(
                    RequestGsonUtils.getGson(new Pair<String, String>("type", "sendverifysms"),
                            new Pair<String, String>("i_tel_number", phoneView.getText().toString())),
                    verifyResultListener, verifyErrorListener);
            IMApplication.getApplication().runVolleyRequest(request);
            showLoadingDialog(-1);
        }
    };

    Response.Listener<String> verifyResultListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            dismissLoadingDialog();
            BaseResponse _respone = new Gson().fromJson(response, BaseResponse.class);
            if (_respone.retcode != 0) {
                ToastUtil.show(_respone.showmsg);
            } else {
                ToastUtil.show(R.string.reg_verify_send_success);
                scheduleColock();
            }
            L.d(response);
        }
    };

    Response.ErrorListener verifyErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            dismissLoadingDialog();
            error.printStackTrace();
            VolleyErrorUtils.showError(error);
        }
    };

    private void scheduleColock() {
        period=60;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);

            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            period--;
            clock.setText(period + "s 重新获取");
            if (period == 0) {
                clock.setText("重新获取");
                clock.setEnabled(true);
                timer.cancel();
                timer = null;
                timerTask = null;
            }
        }
    };

}
