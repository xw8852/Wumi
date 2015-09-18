package com.android.msx7.followinstagram.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.android.msx7.followinstagram.R;

import java.util.HashMap;

/**
 * Created by Xiaowei on 2014/6/12.
 */
public class CustomDialog extends Dialog {
    TextView mTitleView;
    TextView mMessageView;
    Button mPositiveButton;
    Button mNegativeButton;
    RadioGroup mGroup;
//    EditText editText;

    public CustomDialog(Context context) {
        super(context, R.style.transparent_dialog);
        setContentView(R.layout.dialog_message_layout);
        View view = findViewById(R.id.ll_root);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        int with = getContext().getResources().getDisplayMetrics().widthPixels * 2 / 3;
        params.width = with;
        view.setLayoutParams(params);
        mTitleView = (TextView) findViewById(R.id.title);
        mMessageView = (TextView) findViewById(R.id.message);
        mPositiveButton = (Button) findViewById(R.id.button1);
        mNegativeButton = (Button) findViewById(R.id.button2);
//        mGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        mPositiveButton.setVisibility(View.GONE);
        mNegativeButton.setVisibility(View.GONE);
        mTitleView.setVisibility(View.GONE);
//        editText = (EditText) findViewById(R.id.edit_text);
    }

    public void showLoadingDialog() {
        findViewById(R.id.ll_root).setVisibility(View.GONE);
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        show();
    }

    public void showLoadingDialog(String msg) {
        findViewById(R.id.ll_root).setVisibility(View.GONE);
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        TextView textView = (TextView) findViewById(R.id.msg);
        textView.setVisibility(View.VISIBLE);
        textView.setText(msg);
        show();
    }

    public static interface OnEditClickListener {
        public boolean onClick(View v, String text, Dialog dialog);
    }

    public static interface OnClickListener {
        public void onClick(View v, String text);
    }

    public CustomDialog(Context context, boolean isLeft) {
        super(context, R.style.transparent_dialog);
        setContentView(R.layout.dialog_message_layout);
        View view = findViewById(R.id.ll_root);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        int with = getContext().getResources().getDisplayMetrics().widthPixels * 2 / 3;
        params.width = with;
        view.setLayoutParams(params);
        mTitleView = (TextView) findViewById(R.id.title);
        mMessageView = (TextView) findViewById(R.id.message);
        mMessageView.setGravity(Gravity.LEFT);
        mPositiveButton = (Button) findViewById(R.id.button1);
        mNegativeButton = (Button) findViewById(R.id.button2);
        mPositiveButton.setVisibility(View.GONE);
        mNegativeButton.setVisibility(View.GONE);
        mTitleView.setVisibility(View.GONE);
        mMessageView.setVisibility(View.GONE);
    }


    public void setNegativeButton(int textId, View.OnClickListener listener) {
        mNegativeButton.setOnClickListener(new CancelListener(listener));
        mNegativeButton.setText(textId);
        mNegativeButton.setVisibility(View.VISIBLE);
    }

    public void setNegativeButton(CharSequence text,
                                  View.OnClickListener listener) {
        mNegativeButton.setOnClickListener(new CancelListener(listener));
        mNegativeButton.setText(text);
        mNegativeButton.setVisibility(View.VISIBLE);
    }


    public void setPositiveButton(int textId, View.OnClickListener listener) {
        mPositiveButton.setOnClickListener(new CancelListener(listener));
        mPositiveButton.setText(textId);
        mPositiveButton.setVisibility(View.VISIBLE);
    }

    public void setPositiveButton(CharSequence text,
                                  View.OnClickListener listener) {
        mPositiveButton.setOnClickListener(new CancelListener(listener));
        mPositiveButton.setText(text);
        mPositiveButton.setVisibility(View.VISIBLE);
    }

    public void setTitle(int titleId) {
        mTitleView.setText(titleId);
        mTitleView.setVisibility(View.VISIBLE);
    }

    public void setTitle(CharSequence title) {
        if (TextUtils.isEmpty(title))
            return;
        mTitleView.setText(title);
        mTitleView.setVisibility(View.VISIBLE);
    }

    public void setMessage(int messageId) {
        mMessageView.setText(messageId);
        mMessageView.setVisibility(View.VISIBLE);
    }

    public void setMessage(CharSequence message) {
        if (TextUtils.isEmpty(message)) return;
        mMessageView.setText(message);
        mMessageView.setVisibility(View.VISIBLE);
    }

    public TextView getMessageView() {
        return mMessageView;
    }

    HashMap<String, Pair<String, Object>> map = new HashMap<String, Pair<String, Object>>();
    String[] items;
    int checkedId;


    public class CancelListener implements View.OnClickListener {
        View.OnClickListener listener;

        public CancelListener() {
            super();
        }

        public CancelListener(View.OnClickListener listener) {
            super();
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            if (listener != null)
                listener.onClick(v);
            dismiss();
        }
    }


}
