package com.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.msx7.followinstagram.common.DBConn;

/**
 * Created by XiaoWei on 2015/6/18.
 */
public class DatabaseConfig {

    private DatabaseConfig() {
    }

    private IDatabaseConn conn;

    public final void registerDatabase(IDatabaseConn conn) {
        this.conn = conn;
    }

    public static interface IDatabaseConn {
        public int getDBVersion();

        public String getDBName();

        public String getDBPath();

        public void onCreate(SQLiteDatabase db);

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }

    public SQLiteOpenHelper getSQLiteOpenHelper(Context ctx) {
        if (conn == null) {
           registerDatabase(new DBConn());
//            throw new IllegalArgumentException("you must call the method of  \"registerDatabase\" before");
        }
        return new SQLiteOpenHelper(new DatabaseContext(ctx, conn.getDBPath()), conn.getDBName(), null, conn.getDBVersion()) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                conn.onCreate(db);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                conn.onUpgrade(db, oldVersion, newVersion);
            }
        };
    }

    private static DatabaseConfig instance;

    public static final DatabaseConfig getInstance() {
        if (instance == null) instance = new DatabaseConfig();
        return instance;
    }

}
