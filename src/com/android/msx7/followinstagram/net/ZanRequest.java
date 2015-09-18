package com.android.msx7.followinstagram.net;

import com.android.msx7.followinstagram.common.YohoField;
import com.android.volley.Response;

/**
 * Created by Josn on 2015/9/7.
 */
public class ZanRequest extends YohoRequest {

    public ZanRequest(int method,String param, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, YohoField.URL_ZAN, param, listener, errorListener);
    }

    public ZanRequest(String postparam, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, YohoField.URL_ZAN, postparam, listener, errorListener);
    }


}
