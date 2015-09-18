package com.android.msx7.followinstagram.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.android.msx7.followinstagram.IMApplication;

/**
 * 保持toast是单例显示

 * 
 * @version
 * 
 */
@SuppressLint("ShowToast")
public class ToastUtil {
    private static Toast toast;

    public enum ToastDisplayTime {
        TOAST_DISPLAY_LONG, TOAST_DISPLAY_SHORT
    }

    private ToastUtil() {
    }

    private static void checkToast() {
        if (toast == null) {
            toast = Toast.makeText(IMApplication.getApplication(), null, Toast.LENGTH_SHORT);
        }
    }

    private static void setToastDisplayTime(ToastDisplayTime time) {
        if (time == ToastDisplayTime.TOAST_DISPLAY_LONG)
            toast.setDuration(Toast.LENGTH_LONG);
        else {
            toast.setDuration(Toast.LENGTH_SHORT);
        }
    }

    public static void show( String msg, ToastDisplayTime time) {
        checkToast();
        toast.setText(msg);
        setToastDisplayTime(time);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    public static void show( int msg, ToastDisplayTime time) {
        checkToast();
        toast.setText(msg);
        setToastDisplayTime(time);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    public static void show( String msg) {
        if (!TextUtils.isEmpty(msg)) {
            show(msg, ToastDisplayTime.TOAST_DISPLAY_SHORT);
        }
    }

    public static void show( int msg) {
        show( msg, ToastDisplayTime.TOAST_DISPLAY_SHORT);
    }
}
