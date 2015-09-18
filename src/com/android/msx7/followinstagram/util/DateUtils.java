package com.android.msx7.followinstagram.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Josn on 2015/9/12.
 */
public class DateUtils {

    /**
     * 关于时间的显示，0-10 分钟为”刚刚”, 5-60 分钟直接显示 N 分钟前，1 小时到 24 小时， 直接显示 N 小时前，1 天-7 天，显示为 N 天前，更久的直接显示日期(2015-08-31)
     *
     * @return 获取动态显示的时间
     */
    public static final String getActivityTime(long time) {
        String desc = "";
        Date date = new Date(time * 1000);
        long cur = System.currentTimeMillis() / 1000;
        long gap = cur - time;
        if (gap < 10 * 60) {
            return "刚刚";
        } else if (gap <= 60 * 60) {
            return gap / 60 + "分钟前";
        } else if (gap <= 60 * 60 * 24) {
            return gap / (60 * 60) + "小时前";
        } else if (gap <= 60 * 60 * 24 * 7) {
            return gap / (60 * 60 * 24) + "天前";
        } else {
            desc = new SimpleDateFormat("yyyy-MM-dd").format(date);
        }
        return desc;
    }
}
