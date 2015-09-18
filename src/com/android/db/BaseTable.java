package com.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import com.android.db.annotations.DatabaseField;
import com.android.db.annotations.DatabasePrimary;
import com.android.db.annotations.DatabaseTableName;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by XiaoWei on 2015/6/5.
 * <p/>
 * {@link DatabaseConfig#IDatabaseConn}
 */
public abstract class BaseTable<T> {
    Pair<String, List<Pair<String, Field>>> tablePair;
    Class<T> cls;
    /**
     * 保存基本类型的封装类型 exp：int.class Integer.class
     */
    public static HashMap<Class<? extends Object>, Class<? extends Object>> map = new HashMap<Class<? extends Object>, Class<? extends Object>>();

    static {
        map.put(double.class, Double.class);
        map.put(int.class, Integer.class);
        map.put(float.class, Float.class);
        map.put(long.class, Long.class);
        map.put(byte.class, Byte.class);
        map.put(char.class, Character.class);
        map.put(boolean.class, Boolean.class);
        map.put(short.class, Short.class);
    }

    protected Context ctx;

    public BaseTable(Context ctx) {
        analyzeModule();
        this.ctx = ctx;
    }

    public String getCreateTableInfo() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CREATE TABLE IF NOT EXISTS " + tablePair.first + " ( ");
        boolean isAddPrimary = false;
        for (Pair<String, Field> pair : tablePair.second) {
            if (pair.second.getAnnotation(DatabasePrimary.class) != null) {
                buffer.append(pair.first + " text primary key ,");
                isAddPrimary = true;
            } else
                buffer.append(pair.first + " text ,");
        }
        buffer.deleteCharAt(buffer.lastIndexOf(","));
        if (!isAddPrimary)
            buffer.append(" , __id integer primary key AutoIncrement");
        buffer.append(" ) ");
        return buffer.toString();
    }

    public int getCount() {
        Cursor cursor = DataBaseAgent.getInstance(ctx).rawQuery("select count(" + tablePair.second.get(0).first + ") from " + tablePair.first, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public List<T> getAll() {
        Cursor cursor = DataBaseAgent.getInstance(ctx).rawQuery("select * from " + tablePair.first, null);
        return parseCursor(cursor);
    }

    public List<T> getAllByPage(int size, int page) {
        Cursor cursor = DataBaseAgent.getInstance(ctx).rawQuery("select * from " + tablePair.first + " limit " + size + " offset " + page * size, null);
        return parseCursor(cursor);
    }

    public List<T> getDataFromWhere(String where) {
        Cursor cursor = DataBaseAgent.getInstance(ctx).rawQuery("select * from " + tablePair.first + " where " + where, null);
        return parseCursor(cursor);
    }

    public void insertOrUpdate(T t) {
        ContentValues values = new ContentValues();
        for (Pair<String, Field> pair : tablePair.second) {
            Field field = pair.second;
            try {
                if (field.get(t) == null) continue;
                values.put(pair.first, String.valueOf(field.get(t)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } finally {
                continue;
            }
        }
        DataBaseAgent.getInstance(ctx).insertWithOnConflict(tablePair.first, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }


    List<T> parseCursor(Cursor cursor) {
        List<T> list = new ArrayList<T>();
        cursor.moveToFirst();
        while (cursor.getPosition() > -1 && cursor.getPosition() < cursor.getCount()) {
            T t = null;
            for (Pair<String, Field> pair : tablePair.second) {
                Field field = pair.second;
                String value = cursor.getString(cursor.getColumnIndex(pair.first));
                // 设定field为可访问，如果field为私有属性是，必需设置为true，否则会造成异常
                field.setAccessible(true);
                if (isBasicType(getBasicClass(field.getType()))) {
                    // field的类型的封装类型
                    Class<? extends Object> typeClass = getBasicClass(field.getType());
                    try {
                        if (t == null) t = cls.newInstance();
                        // 八大基本类型中都包含了string的构造的方法，故有此生成对应类型
                        if (value == null) continue;

                        field.set(t, typeClass.getConstructor(String.class).newInstance(value));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            list.add(t);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    /**
     * 判断typeClass是不是基本类型
     *
     * @param typeClass
     * @return
     */
    public static boolean isBasicType(Class<? extends Object> typeClass) {
        if (typeClass.equals(Integer.class) || typeClass.equals(Long.class)
                || typeClass.equals(Float.class) || typeClass.equals(Double.class)
                || typeClass.equals(Boolean.class) || typeClass.equals(Byte.class)
                || typeClass.equals(Short.class) || typeClass.equals(String.class)) {

            return true;

        } else {
            return false;
        }
    }

    /**
     * 获取typeClass的封装类型
     *
     * @param typeClass
     * @return
     */
    public static Class<? extends Object> getBasicClass(Class<? extends Object> typeClass) {
        if (map.containsKey(typeClass)) {
            return map.get(typeClass);
        }
        return typeClass;
    }


    private void analyzeModule() {
        Type mySuperClass = getClass().getGenericSuperclass();
        cls = (Class<T>) ((ParameterizedType) mySuperClass).getActualTypeArguments()[0];
        DatabaseTableName tableName = cls.getAnnotation(DatabaseTableName.class);
        if (tableName != null) {
            tablePair = new Pair<String, List<Pair<String, Field>>>(tableName.value(), new ArrayList<Pair<String, Field>>());
        } else return;
        for (Field field : cls.getDeclaredFields()) {
            DatabaseField databaseField = field.getAnnotation(DatabaseField.class);
            if (databaseField != null) {
                Pair<String, Field> pair = new Pair<String, Field>(databaseField.value(), field);
                tablePair.second.add(pair);
            }
        }
    }

}
