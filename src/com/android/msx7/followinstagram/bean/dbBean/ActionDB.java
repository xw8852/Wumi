package com.android.msx7.followinstagram.bean.dbBean;

import android.content.Context;

import com.android.db.BaseTable;
import com.android.db.annotations.DatabaseField;
import com.android.db.annotations.DatabasePrimary;
import com.android.db.annotations.DatabaseTableName;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Josn on 2015/9/19.
 */
@DatabaseTableName("db_action")
public class ActionDB {
    @DatabasePrimary
    @DatabaseField("action")
    public long action;

    public ActionDB() {
    }

    public ActionDB(long action) {
        this.action = action;
    }

    public static class ActionDatabase extends BaseTable<ActionDB> {
        public ActionDatabase(Context ctx) {
            super(ctx);
        }

        public boolean hasRead(long action) {
            List<ActionDB> list = getDataFromWhere(" action = " + action);
            if (list != null && list.size() > 0) {
                for (ActionDB dbs : list) {
                    if (dbs.action == action) return true;
                }
            }
            return false;
        }
    }

}
