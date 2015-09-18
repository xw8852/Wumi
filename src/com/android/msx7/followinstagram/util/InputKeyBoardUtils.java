package com.android.msx7.followinstagram.util;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by xiaowei on 2015/9/9.
 */
public class InputKeyBoardUtils {

    public static void closeKeyBoard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
    }
    /**
     *
     * @Title: autoShow
     * @Description: 绑定activity自动关闭输入法
     * @param @param activity
     * @return void
     * @throws
     */
    public static void autoDismiss(Activity activity) {
        if (activity == null || activity.getCurrentFocus() == null) {
            return;
        }
        // 关闭软键盘
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}
