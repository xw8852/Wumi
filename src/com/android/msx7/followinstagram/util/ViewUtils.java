package com.android.msx7.followinstagram.util;

import android.app.Activity;
import android.graphics.Rect;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.android.msx7.followinstagram.ui.span.NameSpan;
import com.android.msx7.followinstagram.ui.span.TopicSpan;
import com.android.msx7.followinstagram.ui.text.TextViewFixTouchConsume;

import java.util.Arrays;


/**
 * Created by Xiaowei on 2014/5/8.
 */
public class ViewUtils {


    public static void autoLine(TextViewFixTouchConsume tv, String desc, String curTag) {
        if (tv == null) return;
        if (TextUtils.isEmpty(desc)) {
            tv.setText("");
            return;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(desc);
        String[] arr = StringsUtils.findString(desc);
        if (arr != null) {
            int start = 0;
            int lastEnd = 0;
            for (String _arr : arr) {
                start = desc.indexOf(_arr, start);
                int end = _arr.length();
                if (end == 1) continue;
                if (start < 0) start = lastEnd;
                if (_arr.startsWith("@")) {
                    builder.setSpan(new NameSpan(_arr.substring(1).trim(), -1), start, start + end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else if (_arr.startsWith("#")) {
                    String topic = _arr.substring(1).trim();
                    builder.setSpan(new TopicSpan(topic).setEnable(topic.equals(curTag)), start, start + end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                start = end;
                lastEnd = end;
            }
        }
        tv.setText(builder);
        tv.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
    }

    //测量视图的高度
    public static final int measureView(View child) {
        //获取头部视图属性
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {  //如果视图的高度大于0
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
        return child.getMeasuredHeight();
    }

    /**
     * @param activity
     * @return > 0 success; <= 0 fail
     */
    public static final int getStatusHeight(Activity activity) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }


}
