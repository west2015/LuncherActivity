package com.nhd.mall.datebase;

import android.content.Context;
import android.content.SharedPreferences;

import com.nhd.mall.entity.Member;
import com.nhd.mall.entity.StoreEntity;

import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 14-5-5.
 */
public class DbStore {
    private Context context;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DbStore(Context context) {
        this.context = context;
    }

    public StoreEntity getStore() {
        SharedPreferences preferences = context.getSharedPreferences("store", 0);
        StoreEntity store = null;
        if (preferences.getInt("id", -1) != -1) {
            store = new StoreEntity();
            store.setId(preferences.getInt("id", 0));
            store.setName(preferences.getString("name", null));
            store.setTel(preferences.getString("tel", null));
        }
        return store;
    }

    public void update(StoreEntity store) {
        SharedPreferences.Editor sharedata = context.getSharedPreferences("store", 0).edit();
        sharedata.clear();
        sharedata.putInt("id", store.getId() == null ? 0 : store.getId());
        sharedata.putString("name", store.getName());
        sharedata.putString("tel", store.getTel());
        sharedata.commit();
    }

    public void delete() {
        SharedPreferences.Editor sharedata = context.getSharedPreferences("store", 0).edit();
        sharedata.clear();
        sharedata.commit();
    }
}
