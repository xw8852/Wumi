package com.android.msx7.followinstagram.common;

/**
 * Created by Josn on 2015/9/7.
 */
public class ErrorCode {
    // GENERAL
    public static final int E_VALUE_NONE = 1;
    public static final int E_KEY_NOT_EXIST = 2;
    public static final int E_VALUE_TYPE_ERROR = 3;
    public static final int E_VALUE_IS_EMPTY = 4;
    public static final int E_MYSQL_FAIL = 5;
    public static final int E_MONGO_ERROR = 6;
    public static final int E_DUP_OPERATION = 7;
    public static final int E_DUP_CONTENT = 8;
    public static final int E_METHOD_NOT_EXIST = 9;
    public static final int E_KEY_EMPTY = 10;
    public static final int E_KEY_EXISTS = 11;
    public static final int E_PARAM_ERROR = 12;
    public static final int E_ROW_NOT_EXIST = 13;
    public static final int E_DATA_READONLY = 14;
    public static final int E_NO_PERMISSION = 15;
    public static final int E_JSON_INVALID = 16;
    public static final int E_SUBTYPE_NOT_EXIST = 17;
    public static final int E_NEED_LOGIN = 18;
    public static final int E_DATA_INVALID = 19;
    // PO
    public static final int E_PO_NOT_EXIST = 101;

    // MESSAGE
    public static final int E_MESSAGE_SELF2SELF = 301;

    // COMMENT
    public static final int E_COMMENT_NOT_EXIST = 401;

    // ACTIVITY
    public static final int E_ACTIVITY_NOT_EXIST = 501;
    public static final int E_ACTIVITY_USER_NOT_IN = 502;

    // VERIFY_CODE
    public static final int E_VERIFY_CODE_ERROR = 601;
    public static final int E_VERIFY_CODE_EXPIRED = 602;
    public static final int E_VERIFY_TOO_MANY = 603;

    // USER
    public static final int E_REGISTER_BASE64_DECODE_FAIL = 600;
    public static final int E_REGISTER_BASE64_STRING_INVALID = 601;
    public static final int E_REGISTER_OPENID_ALREADY_EXIST = 602;
    public static final int E_REGISTER_OPENID_LENGTH_ZERO = 603;
    public static final int E_REGISTER_UNAME_ALREADY_EXIST = 604;
    public static final int E_REGISTER_UNAME_LENGTH_ZERO = 605;
    public static final int E_REGISTER_UNAME_LENGTH_TOO_LONG = 606;
    public static final int E_REGISTER_UNAME_INVALID = 607;
    public static final int E_REGISTER_TELNO_ALREADY_EXIST = 608;

    public static final int E_CHECKIN_USER_NOT_EXIST = 620;
    public static final int E_CHECKIN_BASE64_DECODE_FAIL = 621;
    public static final int E_CHECHIN_BASE64_STRING_INVALID = 622;
    public static final int E_CHECKIN_PASSWORD_FAIL = 623;
    public static final int E_CHECKIN_USER_FORBIDEN = 624;
    public static final int E_CHECKIN_DECRYPT_FAIL = 625;
    public static final int E_CHECKIN_EXCEPTION = 626;

    public static final int E_USER_NOT_EXIST = 640;
    // GROUP ERROR
    public static final int E_EXCEED_MAX_GROUP_20 = 700;

    // MESSAGE_TYPE
    // 系统广播消息
    public static final int T_MESSAGE_BROADCAST = 100;
    // 有人点赞
    public static final int T_MESSAGE_PO_ZAN = 101;
    // 有人评论
    public static final int T_MESSAGE_PO_COMMENT = 102;
    // 评论被回复
    public static final int T_MESSAGE_PO_COMMENT_REPLY = 103;
    // 图片中有我
    public static final int T_MESSAGE_PO_ABOUTME = 104;
    // ABOUTME 会判断发布内容的用户与潜在接受者的关系，仅互为好友时才发送消息
    public static final int T_MESSAGE_PO_COMMENT_ABOUTME = 105;
    // 有人关注你
    public static final int T_MESSAGE_FOLLOW = 106;
    // 手机通信录中的好友加入了
    public static final int T_MESSAGE_CONTACT_HAS_JOIN_IN = 107;
    // 申请加入活动的权限
    public static final int T_MESSAGE_APPLY_INTO_ACTIVITY = 108;
    // 申请加入活动通过
    public static final int T_MESSAGE_ACTIVITY_APPLY_ACCEPTED = 109;
    // 申请已处理
    public static final int T_MESSAGE_ACTIVITY_APPLY_PASS = 110;
    // 申请已拒接
    public static final int T_MESSAGE_ACTIVITY_APPLY_FAIL = 111;
    // FOLLOW_TYPE
    public static final int T_FOLLOW_SOLO = 0;
    public static final int T_FOLLOW_BOTH = 1;
    // ACTIVITY
    public static final int T_ACTIVITY_APPLY_INTO = 0;
    public static final int T_ACTIVITY_APPLY_ACCEPTED = 1;
}
