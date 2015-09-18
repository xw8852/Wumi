package com.android.msx7.followinstagram.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.common.BaseActivity;
import com.android.msx7.followinstagram.ui.LandingRotatingBackgroundView;
import com.android.msx7.followinstagram.ui.login.BackActionButtonDrawable;
import com.android.msx7.followinstagram.ui.login.ResetPasswordFragment;
import com.android.msx7.followinstagram.ui.login.SignUpFragment;
import com.android.msx7.followinstagram.ui.login.VerifyPhoneFragment;

/**
 * Created by Josn on 2015/9/7.
 */
public class VerifyPhoneActivity extends BaseActivity {
    ImageView regImg;
    public static final String PARAM_PHONE = "param_phone";
    public static final String PARAM_PASSWORD = "param_password";
    public static final String PARAM_FOGET = "param_foget";
    String phone;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        LandingRotatingBackgroundView backgroundView = (LandingRotatingBackgroundView) findViewById(R.id.landingRotatingBackgroundView);
        regImg = (ImageView) findViewById(R.id.RegImg);
        regImg.setImageResource(R.drawable.reg_phone);
        backgroundView.setAlignBottomView(regImg);
        findViewById(R.id.action_bar).setBackgroundColor(getResources().getColor(R.color.action_bar_transparent_background));
        password = getIntent().getStringExtra(PARAM_PASSWORD);
        phone = getIntent().getStringExtra(PARAM_PHONE);
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_PASSWORD, password);
        bundle.putString(PARAM_PHONE, phone);
        VerifyPhoneFragment fragment = new VerifyPhoneFragment();
        if (getIntent().hasExtra(PARAM_FOGET)) {
            fragment = new ResetPasswordFragment();
        }
        fragment.setArguments(bundle);

        getFragmentManager().beginTransaction().add(R.id.sign_in_container, fragment).commit();
        findViewById(R.id.action_bar_button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.action_bar_button_back).setBackgroundDrawable(new BackActionButtonDrawable(getResources()));
    }

    public void callback() {
        setResult(RESULT_OK);
        finish();
    }

}
