package com.android.msx7.followinstagram.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.ui.LandingRotatingBackgroundView;
import com.android.msx7.followinstagram.ui.TabbedTab;
import com.android.msx7.followinstagram.ui.login.LoginFragment;
import com.android.msx7.followinstagram.ui.login.SignUpFragment;
import com.android.msx7.followinstagram.util.InputKeyBoardUtils;


public class LoginActivity extends Activity {
    public static final int CODE_REGISTER = 0x0001;
    TextView mTexView;
    TabbedTab mLogin;
    TabbedTab mSignup;
    SignUpFragment signUpFragment;
    LoginFragment loginFragment;
    View signupContainer;
    View loginContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        LandingRotatingBackgroundView backgroundView = (LandingRotatingBackgroundView) findViewById(R.id.landingRotatingBackgroundView);
        backgroundView.setAlignBottomView(findViewById(R.id.tabbed_landing_tab_header));
        mTexView = (TextView) findViewById(R.id.tabbed_landing_caption);
        mLogin = (TabbedTab) findViewById(R.id.tabbed_landing_log_in);
        mSignup = (TabbedTab) findViewById(R.id.tabbed_landing_sign_up);
        mLogin.setOnClickListener(loginClickListener);
        mSignup.setOnClickListener(signupClickListener);
        signupContainer = findViewById(R.id.sign_up_tab_container);
        loginContainer = findViewById(R.id.log_in_tab_container);
        loginClickListener.onClick(mLogin);
        if(IMApplication.getApplication().isLogin()){
            startActivity(new Intent(this, MainTabActivity.class));
            finish();
        }
    }

    View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (loginFragment == null) {
                loginFragment = new LoginFragment();
                ft.add(R.id.log_in_tab_container, loginFragment);
            }
            if (signUpFragment != null) ft.hide(signUpFragment);
            ft.show(loginFragment);
            mLogin.setSelected(true);
            mSignup.setSelected(false);
            signupContainer.setVisibility(View.INVISIBLE);
            loginContainer.setVisibility(View.VISIBLE);
            ft.commitAllowingStateLoss();
            mTexView.setText(R.string.tabbed_tab_subtitle_log_in);
            InputKeyBoardUtils.autoDismiss(LoginActivity.this);
        }
    };

    View.OnClickListener signupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (signUpFragment == null) {
                signUpFragment = new SignUpFragment();
                ft.add(R.id.sign_up_tab_container, signUpFragment);
            }
            if (loginFragment != null) ft.hide(loginFragment);
            ft.show(signUpFragment);
            mSignup.setSelected(true);
            mLogin.setSelected(false);
            signupContainer.setVisibility(View.VISIBLE);
            loginContainer.setVisibility(View.INVISIBLE);
            ft.commitAllowingStateLoss();
            mTexView.setText(R.string.tabbed_tab_subtitle_sign_up);
            InputKeyBoardUtils.autoDismiss(LoginActivity.this);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CODE_REGISTER) {
            startActivity(new Intent(this, UpdateUserInfoActivity.class));
            finish();
        }
    }
}
