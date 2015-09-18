package com.android.msx7.followinstagram.common;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.android.layoutlib.bridge.bars.TitleBar;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.util.DialogUtils;
import com.android.widget.TitleView;
import com.baidu.mobstat.StatService;

/**
 * Created by Josn on 2015/9/7.
 */
public class BaseActivity extends Activity {
    private Dialog mProgressDialog;

    public TitleView getTitleBar() {
        return (TitleView) findViewById(R.id.TitleBar);
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
    /**
     * showLoadingDialog:显示数据加载框. <br/>
     */
    protected void showLoadingDialog(int msgId) {
        dismissLoadingDialog();
        if (msgId > 0 && !TextUtils.isEmpty(getString(msgId))) {
            mProgressDialog = DialogUtils.showLoadingProgress(this, getString(msgId), false);
        } else {
            mProgressDialog = DialogUtils.showLoadingProgress(this, false);
        }
    }

    public InputMethodManager getInputMethodManager() {
        return (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }
}
