package com.nhd.mall.datebase;

import android.content.Context;
import android.content.SharedPreferences;

import com.nhd.mall.app.MainApplication;
import com.nhd.mall.entity.CustomerAddressEntity;
import com.nhd.mall.entity.Member;

import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 14-4-24.
 */
public class DbAddress {
    private Context context;

    public DbAddress(Context context) {
        this.context = context;
    }
    public CustomerAddressEntity getAddress() {
        SharedPreferences preferences = context.getSharedPreferences("address", 0);
        CustomerAddressEntity address = null;
        if (preferences.getString("name",null) != null) {
            address = new CustomerAddressEntity();
            address.setId(preferences.getInt("id",0));
            address.setName(preferences.getString("name", null));
            address.setMobile(preferences.getString("mobile", null));
            address.setAddress(preferences.getString("detail", null));
        }
        return address;
    }

    public void update(CustomerAddressEntity address) {

		MainApplication.getInstance().setCustomerAddress(address);
		SharedPreferences.Editor sharedata = context.getSharedPreferences("address", 0).edit();
		sharedata.clear();
        sharedata.putInt("id", address.getId());
		sharedata.putString("name", address.getName());
		sharedata.putString("mobile",address.getMobile());
		sharedata.putString("detail", address.getAddress());
		sharedata.commit();
    }

    public void delete() {
        MainApplication.getInstance().setCustomerAddress(null);
        SharedPreferences.Editor sharedata = context.getSharedPreferences("address", 0).edit();
        sharedata.clear();
        sharedata.commit();
    }
}
