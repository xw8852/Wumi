package com.android.msx7.followinstagram.bean.dbBean;

import android.content.Context;

import com.android.db.BaseTable;
import com.android.db.annotations.DatabaseField;
import com.android.db.annotations.DatabasePrimary;
import com.android.db.annotations.DatabaseTableName;

import java.util.List;

/**
 * Created by Josn on 2015/9/12.
 */
@DatabaseTableName("tb_good")
public class Good {
    @DatabasePrimary
    @DatabaseField("feedId")
    public String feedId;
    @DatabaseField("good")
    public boolean good;
    public Good(){}

    public Good(String feedId, boolean good) {
        this.feedId = feedId;
        this.good = good;
    }

    public static class GoodDB extends BaseTable<Good> {

        public GoodDB(Context ctx) {
            super(ctx);
        }

        public Good isGood(String feedId) {
            List<Good> goods = getDataFromWhere(" feedId = " + feedId);
            if (goods != null && !goods.isEmpty()) {
                return goods.get(0);
            }
            return null;
        }
    }


}
