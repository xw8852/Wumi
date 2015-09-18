package com.android.msx7.followinstagram.util;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.android.msx7.followinstagram.ui.dialog.CustomDialog;


/**
 * Created by Xiaowei on 2014/6/12.
 */
public class DialogUtils {

    /**
     * 弹出只有1个确定按钮的对话框
     */
    public static final Dialog showDialog(String title, String message,
                                          Context context) {
        return showDialog(title, message, false, "确定", null, context);

    }
    /**
     * 弹出只有1个确定按钮的对话框
     */
    public static final Dialog showDialog(int title, int message,
                                          Context context) {
        return showDialog(title, message, false, "确定", null, context);
    }

    /**
     * 弹出只有一个按钮的的提示框,
     *
     * @param title   dialog标题
     * @param message dialog显示内容
     * @param btnText dialog按钮文字
     * @param context
     * @return
     */
    public static final Dialog showDialog(String title, String message,
                                          String btnText, Context context) {
        return showDialog(title, message, false, btnText, null, context);
    }





    public static final Dialog showDialog(int title, int message,
                                          boolean isCancleBtn, String approve_title,
                                          View.OnClickListener approve_callback, Context context) {
        Resources resources = context.getResources();
        return showDialog(title < 0 ? "" : resources.getString(title),
                message < 0 ? "" : resources.getString(message), isCancleBtn, approve_title,
                approve_callback, context);
    }

    public static final Dialog showDialog(String title, String message,
                                          boolean isCancleBtn, String approve_title,
                                          View.OnClickListener approve_callback, Context context
                                         ) {
        CustomDialog dialog = new CustomDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        if (isCancleBtn)
            dialog.setNegativeButton("取消", null);
        dialog.setPositiveButton(approve_title, approve_callback);

        if (context instanceof Activity) {
            if (((Activity) context).isFinishing())
                // 避免Activity中dialog并未显示出来就按了返回键导致程序崩溃的BUG
                return null;
        }
        dialog.show();
        return dialog;
    }

    public static final Dialog ShowDialog(String title, String message,
                                          String cancel_title, View.OnClickListener cancel_click,
                                          String approve_title, View.OnClickListener approve_callback,
                                          Context context, OnKeyListener keyListener) {
        if (context == null)
            return null;
        CustomDialog dialog = new CustomDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setNegativeButton(cancel_title, cancel_click);
        dialog.setPositiveButton(approve_title, approve_callback);

        if (keyListener != null) {
            dialog.setOnKeyListener(keyListener);
        }
// TODO:
//    if (context instanceof Activity
//                && !(context instanceof FragmentActivity)) {
//            if (((Activity) context).isFinishing())
//                // 避免Activity中dialog并未显示出来就按了返回键导致程序崩溃的BUG
//                return null;
//        }
        dialog.show();
        return dialog;
    }

    public static final Dialog ShowDialog(String title, String message,
                                          String cancel_title, View.OnClickListener cancel_click,
                                          String approve_title, View.OnClickListener approve_callback,
                                          Context context) {
        return ShowDialog(title, message, cancel_title, cancel_click,
                approve_title, approve_callback, context, null);

    }

    public static TextView buildText(String message, Context context) {
        TextView tv = new TextView(context);
        LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
        tv.setGravity(Gravity.CENTER);
        tv.setText(message);
        tv.setLayoutParams(lp);
        return tv;
    }


    /**
     * 弹出数据加载框
     *
     * @param ctx
     * @param cancelable ,弹出框是否可以被取消,true-可以取消,false-不能取消
     * @return
     */
    public static Dialog showLoadingProgress(Context ctx,
                                                     boolean cancelable) {
        if (ctx == null ) {
            return null;
        }
        CustomDialog customDialog=new CustomDialog(ctx);
        customDialog.setCancelable(cancelable);
        customDialog.showLoadingDialog();
        return customDialog;
    }

    /**
     * 弹出数据加载框
     *
     * @param ctx
     * @param cancelable ,弹出框是否可以被取消,true-可以取消,false-不能取消
     * @return
     */
    public static Dialog showLoadingProgress(Context ctx, String msg,
                                                     boolean cancelable) {
        if (ctx == null || msg == null) {
            return null;
        }
        CustomDialog customDialog=new CustomDialog(ctx);
        customDialog.setCancelable(cancelable);
        customDialog.showLoadingDialog(msg);
        return customDialog;
    }

    /**
     * 关闭指定的数据加载框
     *
     * @param dialog
     */
    public static void dismissLoadingProgress(ProgressDialog dialog) {
        if (dialog == null) {
            return;
        }
        dialog.dismiss();
    }













}
