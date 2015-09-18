package com.android.msx7.followinstagram.common;

import com.android.msx7.followinstagram.IMApplication;

/**
 * Created by Josn on 2015/9/6.
 */
public class YohoField {
    public static final String URL_USER = "http://www.doyoho.com:80/api/user";
    public static final String URL_PO = "http://www.doyoho.com:80/api/po";
    public static final String URL_FEED = "http://www.doyoho.com:80/api/feed";
    public static final String URL_ZAN = "http://www.doyoho.com:80/api/zan";
    public static final String URL_COMMET = "http://www.doyoho.com:80/api/comment";
    public static final String URL_FOLLOW = "http://www.doyoho.com:80/api/follow";
    public static final String URL_MESSAGE = "http://www.doyoho.com:80/api/message";
    public static final String URL_CONTACT = "http://www.doyoho.com:80/api/contact";
    public static final String URL_LOCATION = "http://www.doyoho.com:80/api/location";

    public static final String BASE_DIR="wumi";

    /**
     * 获取APP的版本号
     */
    public static String getAndroidVersion() {
        return IMApplication.getApplication().getVersion();
    }

}
