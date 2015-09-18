package com.android.msx7.followinstagram.net;

import com.android.msx7.followinstagram.common.YohoField;
import com.android.volley.Response;

/**
 * Created by Josn on 2015/9/7.
 */
public class UserRequest extends YohoRequest {

    public UserRequest(int method, String param, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, YohoField.URL_USER, param, listener, errorListener);
    }

    public UserRequest(String postparam, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST,  YohoField.URL_USER, postparam,listener, errorListener);
    }


}
