package com.android.msx7.followinstagram.net;

import com.android.msx7.followinstagram.common.YohoField;
import com.android.volley.Response;

/**
 * Created by Josn on 2015/9/7.
 */
public class FeedRequest extends YohoRequest {

    public FeedRequest(int method, String param, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method,  YohoField.URL_FEED, param, listener, errorListener);
    }

    public FeedRequest(String postparam, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, YohoField.URL_FEED, postparam, listener, errorListener);
    }


}
