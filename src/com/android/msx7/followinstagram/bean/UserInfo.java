package com.android.msx7.followinstagram.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Josn on 2015/9/7.
 */
public class UserInfo {
    @SerializedName("s_user_name")
    public String userName;
    @SerializedName("i_birth_year")
    public int birthyear;
    @SerializedName("i_tel_number")
    public String telNumber;
    @SerializedName("s_user_image")
    public String userImg;
    @SerializedName("i_user_id")
    public int userId;
    @SerializedName("i_uid")
    public int uid;
    @SerializedName("i_sex")
    public int sex;
    @SerializedName("chkcode")
    public  String chkcode;
    @SerializedName("s_introduce")
    public  String s_introduce;

}
