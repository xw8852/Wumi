package com.android.msx7.followinstagram.common;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Josn on 2015/9/7.
 */
public class BaseResponse<T> {
    @SerializedName("retcode")
    public int retcode;
    @SerializedName("message")
    public String message;
    @SerializedName("chkcode")
    public String chkcode;
    @SerializedName("showmsg")
    public String showmsg;
    @SerializedName("retbody")
    public T retbody;
    @SerializedName("list_num")
    public int list_num;
}
