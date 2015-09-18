package com.android.msx7.followinstagram.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

import com.android.db.DatabaseConfig;
import com.android.layoutlib.bridge.bars.TitleBar;
import com.android.msx7.followinstagram.common.BaseFragment;
import com.android.msx7.followinstagram.fragment.TabHomeFragment;
import com.android.msx7.followinstagram.fragment.TabNewsFragment;
import com.android.msx7.followinstagram.fragment.TabProfileFragment;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.common.DBConn;
import com.android.msx7.followinstagram.ui.drawable.DockDrawable;
import com.android.msx7.followinstagram.util.L;
import com.android.msx7.followinstagram.util.ToastUtil;
import com.android.widget.TitleView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josn on 2015/9/9.
 */
public class MainTabActivity extends ImageSelectActivity implements View.OnClickListener {
    ImageView mDockHome;
    ImageView mDockNews;
    ImageView mDockProfile;
    ImageView mDockCamera;

    SparseArray<Fragment> sparseArray = new SparseArray<Fragment>();
    Fragment mCurFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDockHome = (ImageView) findViewById(R.id.home);
        mDockNews = (ImageView) findViewById(R.id.news);
        mDockProfile = (ImageView) findViewById(R.id.profile);
        mDockCamera = (ImageView) findViewById(R.id.camera);
        mDockHome.setBackgroundDrawable(new DockDrawable(getResources().getColor(R.color.grey_5), getResources().getColor(R.color.grey_7)));
        mDockNews.setBackgroundDrawable(new DockDrawable(getResources().getColor(R.color.grey_5), getResources().getColor(R.color.grey_7)));
        mDockProfile.setBackgroundDrawable(new DockDrawable(getResources().getColor(R.color.grey_5), getResources().getColor(R.color.grey_7)));
        mDockCamera.setBackgroundDrawable(new DockDrawable(getResources().getColor(R.color.accent_blue_6), getResources().getColor(R.color.accent_blue_5)));
        mDockHome.setOnClickListener(this);
        mDockNews.setOnClickListener(this);
        mDockCamera.setOnClickListener(this);
        mDockProfile.setOnClickListener(this);
        onClick(mDockHome);
        //注册数据库
        DatabaseConfig.getInstance().registerDatabase(new DBConn());
    }

    @Override
    public void onClick(View v) {
        mDockHome.setSelected(false);
        mDockNews.setSelected(false);
        mDockProfile.setSelected(false);
        findViewById(R.id.tab_home_container).setVisibility(View.GONE);
        findViewById(R.id.tab_news_container).setVisibility(View.GONE);
        findViewById(R.id.tab_profile_container).setVisibility(View.GONE);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (v.getId() == R.id.home) {
            mDockHome.setSelected(true);
            if (sparseArray.get(R.id.home) == null) {
                sparseArray.append(R.id.home, new TabHomeFragment());
                ft.add(R.id.tab_home_container, sparseArray.get(R.id.home));
            }
            ft.show(sparseArray.get(R.id.home));
            mCurFragment = sparseArray.get(R.id.home);
            findViewById(R.id.tab_home_container).setVisibility(View.VISIBLE);
        }
        if (v.getId() == R.id.news) {
            mDockNews.setSelected(true);
            if (sparseArray.get(R.id.news) == null) {
                sparseArray.append(R.id.news, new TabNewsFragment());
                ft.add(R.id.tab_news_container, sparseArray.get(R.id.news));
            }
            ft.show(sparseArray.get(R.id.news));
            mCurFragment = sparseArray.get(R.id.news);
            findViewById(R.id.tab_news_container).setVisibility(View.VISIBLE);
        }
        if (v.getId() == R.id.profile) {
            mDockProfile.setSelected(true);
            if (sparseArray.get(R.id.profile) == null) {
                sparseArray.append(R.id.profile, new TabProfileFragment());
                ft.add(R.id.tab_profile_container, sparseArray.get(R.id.profile));
            }
            ft.show(sparseArray.get(R.id.profile));
            mCurFragment = sparseArray.get(R.id.profile);
            findViewById(R.id.tab_profile_container).setVisibility(View.VISIBLE);
        }
        if (v.getId() == R.id.camera) {
            showMenu();
        }
        ft.commit();
    }

    List<String> pofileTags = new ArrayList<String>();
    List<String> homeTags = new ArrayList<String>();
    List<String> newsTags = new ArrayList<String>();
    int fragmentIndex;

    public void addFragmentToBackStack(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        String popName = fragment.getClass().getName() + "_" + fragmentIndex;
        fragmentIndex++;
        if (mDockProfile.isSelected()) {
            pofileTags.add(popName);
            ft.add(R.id.tab_profile_container, fragment);
        } else if (mDockHome.isSelected()) {
            homeTags.add(popName);
            ft.add(R.id.tab_home_container, fragment);
        } else if (mDockNews.isSelected()) {
            newsTags.add(popName);
            ft.add(R.id.tab_news_container, fragment);
        }
        ft.hide(mCurFragment);
        ft.addToBackStack(popName);
        ft.commitAllowingStateLoss();
        mCurFragment = fragment;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            if (mDockProfile.isSelected() && pofileTags.size() > 0) {
                getFragmentManager().popBackStack(pofileTags.remove(pofileTags.size() - 1), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                return;
            } else if (mDockHome.isSelected() && homeTags.size() > 0) {
                getFragmentManager().popBackStack(homeTags.remove(homeTags.size() - 1), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                return;
            } else if (mDockNews.isSelected() && newsTags.size() > 0) {
                getFragmentManager().popBackStack(newsTags.remove(newsTags.size() - 1), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                return;
            }
            FragmentManager.BackStackEntry entry = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1);
            if (pofileTags.contains(entry.getName())) {
                onClick(mDockProfile);
                return;
            } else if (newsTags.contains(entry.getName())) {
                onClick(mDockNews);
                return;
            } else if (homeTags.contains(entry.getName())) {
                onClick(mDockHome);
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    protected String getImagePrefix() {
        return "ac_";
    }

    @Override
    public View getImageView() {
        return mDockCamera;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK != resultCode) {
            return;
        }
        dismissMenu();
        switch (requestCode) {
            case OPEN_PIC:
                L.d("---OPEN_PIC---" + data.getData().toString());
                if (data != null) {
                    mFileUri = data.getData();
                    String _imgPath = getPath(this, mFileUri);
                    L.d("onActivityResult()--->imgPath=" + _imgPath);
                    L.d("MSG", "onActivityResult()--->Uri=" + mFileUri.toString());
                    if (!TextUtils.isEmpty(_imgPath)) {
                        startShareActivity(_imgPath);
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
                        startShareActivity(imgPath);
                    }
                }
                break;
            case OPEN_CAMERA_CODE:
                final String path = mFileUri == null ? "" : mFileUri.getPath();
                if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                    startShareActivity(path);
                } else {
                    ToastUtil.show("无法获取照片！");
                }
                break;


        }
    }

    void startShareActivity(String path) {
        Intent intent = new Intent(this, ShareImageActivity.class);
        intent.putExtra(ShareImageActivity.PARAM_IMG_PATH, path);
        startActivity(intent);
    }
}
