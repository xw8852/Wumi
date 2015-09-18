package com.android.msx7.followinstagram.net;

import com.android.msx7.followinstagram.common.YohoField;
import com.android.volley.Response;

/**
 * Created by Josn on 2015/9/7.
 */
public class PoRequest extends YohoRequest {

    public PoRequest(int method, String param, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, YohoField.URL_PO, param, listener, errorListener);
    }

    public PoRequest(String postparam, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, YohoField.URL_PO, postparam, listener, errorListener);
    }


}
