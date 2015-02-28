package com.nhd.mall.datebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by Administrator on 14-4-27.
 */
public class DbHistory {
    private Context context;

    public DbHistory(Context context) {
        super();
        this.context = context;
    }
    /**
     * 函数名称 : insertVideo 功能描述 : 插入
     */
    public void insertHistory(String name) {
        if(isHaveValue(name))return;
        ContentValues values = new ContentValues();
        values.put("name",name);
        DateBaseUtil.getInstance(context).insert("history", null, values);
    }
    public boolean isHaveValue(String name){
        String sqlString = "select * from history";
        Cursor cursor = DateBaseUtil.getInstance(context).rawQuery(sqlString, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
               String nameSel =cursor.getString(cursor.getColumnIndex("name"));
                if(nameSel.equals(name)||nameSel.equals(name.trim())){
                    return true;
                }
            } while (cursor.moveToNext());

        }
        return false;
    }
    public String[] getHistory() {
        int i = 0;
        String sqlString = "select * from history";
        Cursor cursor = DateBaseUtil.getInstance(context).rawQuery(sqlString, null);
        String[]sName =null;
        if (cursor != null && cursor.getCount() > 0) {
            sName = new String[cursor.getCount()];
            cursor.moveToFirst();
            do {
                sName[i] = new String();
                sName[i] =cursor.getString(cursor.getColumnIndex("name"));
                i++;
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        return sName;
    }
    /**
     * 函数名称 : 功能描述 : 删除
     */
    public void delect() {
        String sqlString = "delete from history";
        DateBaseUtil.getInstance(context).execSQL(sqlString);
    }
}
