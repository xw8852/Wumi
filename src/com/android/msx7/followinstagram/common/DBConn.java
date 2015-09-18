package com.android.msx7.followinstagram.common;

import android.database.sqlite.SQLiteDatabase;

import com.android.db.DatabaseConfig;
import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.bean.dbBean.Good;

import java.io.File;

/**
 * Created by Josn on 2015/9/12.
 */
public class DBConn implements DatabaseConfig.IDatabaseConn {

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(new Good.GoodDB(IMApplication.getApplication()).getCreateTableInfo());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public String getDBPath() {
        return YohoField.BASE_DIR + File.separator + "db";
    }

    @Override
    public String getDBName() {
        return "wumi";
    }

    @Override
    public int getDBVersion() {
        return 1;
    }
}
