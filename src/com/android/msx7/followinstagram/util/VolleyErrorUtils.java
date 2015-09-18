package com.android.msx7.followinstagram.util;

import com.android.msx7.followinstagram.R;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

/**
 * Created by xiaowei on 2015/8/10.
 */
public class VolleyErrorUtils {

    public static void showError(VolleyError error) {
    	if(error.getMessage() != null){
    		L.d(error.getMessage());
    	}
        error.printStackTrace();
        if (error instanceof NetworkError) {
            ToastUtil.show( R.string.net_error);
        } else if (error instanceof NoConnectionError) {
            ToastUtil.show(R.string.net_error);
        } else if (error instanceof ServerError) {
            ToastUtil.show( R.string.error);
        } else if (error instanceof TimeoutError) {
            ToastUtil.show(R.string.error);
        } else if (error instanceof ParseError) {
            ToastUtil.show( R.string.error);
        } else if (error instanceof AuthFailureError) {
            ToastUtil.show( R.string.error);
        }else{
            ToastUtil.show( R.string.error);
        }
    }
}
