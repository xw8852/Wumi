package com.android.msx7.followinstagram.net;

import com.android.msx7.followinstagram.common.YohoField;
import com.android.volley.Response;

/**
 * Created by Josn on 2015/9/7.
 */
public class BaseRequest extends YohoRequest {

    public BaseRequest(int method, String url, String param, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, param, listener, errorListener);
    }

    public BaseRequest(String postparam, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, postparam, listener, errorListener);
    }


}
