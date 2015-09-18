package com.android.msx7.followinstagram.bean;

import com.android.msx7.followinstagram.activity.ImgFindUserActivity;
import com.android.msx7.followinstagram.activity.ImgFindUserActivity.SimpleContact;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Josn on 2015/9/11.
 */
public class ImgInfo {
    @SerializedName("img_url")
    public String imgurl;
    @SerializedName("guys")
    public  List<SimpleContact> guys;


    public class guys{

    }

    public class exif{


    }

    /**
     * 'img_url':'http://pic.yooho.me/p/249249591',
     'guys':{'+8615618675828':{'name':'徐锐','position':'caca'}},
     # exif中的字段i_take_time/device为演示所用，非必须
     'exif':{'i_take_time':int(time.time()),'device':'iPhone5S'}
     */
}
