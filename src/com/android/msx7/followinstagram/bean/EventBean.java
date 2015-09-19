package com.android.msx7.followinstagram.bean;

import com.android.db.annotations.DatabaseField;
import com.android.db.annotations.DatabasePrimary;
import com.android.db.annotations.DatabaseTableName;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Josn on 2015/9/19.
 */
@DatabaseTableName("db_event")
public class EventBean {
    //活动id
    @DatabasePrimary
    @DatabaseField("i_activity_id")
    @SerializedName("i_activity_id")
    public long eventId;
    //图片数量
    @DatabaseField("i_po_count")
    @SerializedName("i_po_count")
    public long poCount;
    //创建时间
    @DatabaseField("i_creat_time")
    @SerializedName("i_creat_time")
    public long creatTime;
    //活动的属性，0表示公开，1表示私密
    @DatabaseField("i_status")
    @SerializedName("i_status")
    public int status;
    //创建者id
    @DatabaseField("i_creat_uid")
    @SerializedName("i_creat_uid")
    public long uid;
    @DatabaseField("s_name")
    @SerializedName("s_name")
    public String name;
    @DatabaseField("s_desc")
    @SerializedName("s_desc")
    public String desc;

}
