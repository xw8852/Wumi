package com.android.msx7.followinstagram.common;

import android.database.sqlite.SQLiteDatabase;

import com.android.db.BaseTable;
import com.android.db.DatabaseConfig;
import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.bean.EventBean;
import com.android.msx7.followinstagram.bean.dbBean.ActionDB;
import com.android.msx7.followinstagram.bean.dbBean.Good;

import java.io.File;

/**
 * Created by Josn on 2015/9/12.
 */
public class DBConn implements DatabaseConfig.IDatabaseConn {

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(new Good.GoodDB(IMApplication.getApplication()).getCreateTableInfo());
        db.execSQL(new BaseTable<EventBean>(IMApplication.getApplication()) {
        }.getCreateTableInfo());
        db.execSQL(new BaseTable<ActionDB>(IMApplication.getApplication()) {
        }.getCreateTableInfo());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
            case 2:
            case 3:
                db.execSQL(new BaseTable<EventBean>(IMApplication.getApplication()) {
                }.getCreateTableInfo());
            case 4:
                db.execSQL(new BaseTable<ActionDB>(IMApplication.getApplication()) {
                }.getCreateTableInfo());
            case 5:
                db.execSQL("ALTER TABLE `db_event` ADD `s_creat_uname` text ;");
        }
    }

    @Override
    public String getDBPath() {
        return YohoField.BASE_DIR + File.separator + IMApplication.getApplication().getUserInfo().userId + File.separator + "db";
    }

    @Override
    public String getDBName() {
        return "wumi";
    }

    @Override
    public int getDBVersion() {
        return 6;
    }
}
