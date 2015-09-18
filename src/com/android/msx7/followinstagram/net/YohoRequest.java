package com.android.msx7.followinstagram.net;

import android.util.Base64;

import com.android.msx7.followinstagram.common.YohoField;
import com.android.msx7.followinstagram.util.L;
import com.android.msx7.followinstagram.util.MD5Util;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Josn on 2015/9/6.
 */
public class YohoRequest extends StringRequest {
    protected static final String PROTOCOL_CHARSET = "utf-8";


    /**
     * Content type for request.
     */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("charset=%s", PROTOCOL_CHARSET);

    String param;

    public YohoRequest(int method, String url, String param, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, method == Method.GET ? getGetUrl(param, url) : url, listener, errorListener);
        this.param = param;
    }

    public static String getGetUrl(String param, String url) {
        try {
            url += "?version=" + "Android" + YohoField.getAndroidVersion();
            String data = new String(Base64.encode(param.getBytes("UTF-8"), Base64.NO_WRAP), "UTF-8");
            // + 替换成-
           // "/"替换成_防止url中传输base64失败
            data = data.replace("+", "-");
            data = data.replace("/", "_");
            url += "&data=" + data;
            url += "&sign=" + MD5Util.getStringMD5String(data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
        }
        return url;
    }
//    @Override
//    public String getBodyContentType() {
//        return PROTOCOL_CONTENT_TYPE;
//    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            if (getMethod() == Method.POST) {
                params.put("version", "Android " + YohoField.getAndroidVersion());
                String data = new String(Base64.encode(param.getBytes("UTF-8"), Base64.NO_WRAP), "UTF-8");
                params.put("data", data);
                params.put("sign", MD5Util.getStringMD5String(data));
                L.d("网络地址：" + getUrl());
                L.d("网络参数：" + param);
                L.d("网络参数 data ：" + data);
                L.d("网络参数 data ：" + new Gson().toJson(params));
                L.d("网络参数 sign ：" + MD5Util.getStringMD5String(data));
                return params;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
        }
        return super.getParams();
    }


}
