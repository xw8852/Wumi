package com.android.msx7.followinstagram.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.ui.LandingRotatingBackgroundView;
import com.android.msx7.followinstagram.ui.login.SignUpFragment;

/**
 * Created by xiaowei on 2015/9/6.
 */
public class SignInActivity extends Activity {
    ImageView regImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        LandingRotatingBackgroundView backgroundView = (LandingRotatingBackgroundView) findViewById(R.id.landingRotatingBackgroundView);
        regImg = (ImageView) findViewById(R.id.RegImg);
        backgroundView.setAlignBottomView(regImg);
        findViewById(R.id.action_bar).setBackgroundColor(getResources().getColor(R.color.action_bar_transparent_background));
        getFragmentManager().beginTransaction().add(R.id.sign_in_container, new SignUpFragment()).commit();
    }

}
