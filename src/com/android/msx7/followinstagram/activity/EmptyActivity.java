package com.android.msx7.followinstagram.activity;

import android.app.Fragment;
import android.os.Bundle;

import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.common.BaseActivity;

/**
 * Created by Josn on 2015/9/20.
 */
public class EmptyActivity extends BaseActivity {
    static Fragment fragment;

    Fragment baseFragment;

    public static void startFragment(Fragment fragment) {
        EmptyActivity.fragment = fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        baseFragment = fragment;
        getFragmentManager().beginTransaction().add(R.id.container, fragment).commitAllowingStateLoss();
    }
}
