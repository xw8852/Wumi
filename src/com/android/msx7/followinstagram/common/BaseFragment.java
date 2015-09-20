package com.android.msx7.followinstagram.common;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.ui.actionbar.ActionBar;
import com.android.msx7.followinstagram.util.DialogUtils;
import com.android.widget.TitleView;
import com.baidu.mobstat.StatService;

/**
 * Created by Josn on 2015/9/7.
 */
public class BaseFragment extends Fragment {
    private Dialog mProgressDialog;
    protected ActionBar actionBar;

    public TitleView getTitleBar() {
        return (TitleView) getView().findViewById(R.id.TitleBar);
    }

    public void addBack() {
        TitleView titleBar = getTitleBar();
        if (titleBar == null) return;
        titleBar.setLeftImg(R.drawable.nav_arrow_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) v.getContext()).onBackPressed();
            }
        });

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView().findViewById(R.id.action_bar) != null) {
            actionBar = new ActionBar(getView().findViewById(R.id.action_bar));
        }
    }

    /**
     * showLoadingDialog:显示数据加载框. <br/>
     */
    protected void showLoadingDialog(int msgId) {
        dismissLoadingDialog();
        if (msgId > 0 && !TextUtils.isEmpty(getString(msgId))) {
            mProgressDialog = DialogUtils.showLoadingProgress(getView().getContext(), getString(msgId), true);
        } else {
            mProgressDialog = DialogUtils.showLoadingProgress(getView().getContext(), true);
        }
    }


    /**
     * dismissDialog:关闭数据加载弹出框. <br/>
     */
    protected void dismissLoadingDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }
}
