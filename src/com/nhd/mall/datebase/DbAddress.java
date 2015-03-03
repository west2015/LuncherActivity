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
        SharedPreferences preferences = context.getSharedPreferences("ADDRESS", 0);
        CustomerAddressEntity address = null;
        if (preferences.getString("address_name",null) != null) {
            address = new CustomerAddressEntity();
            address.setId(preferences.getInt("address_id",0));
            address.setName(preferences.getString("address_name", null));
            address.setMobile(preferences.getString("address_mobile", null));
            address.setAddress(preferences.getString("address_detail", null));
        }
        return address;
    }

    public void update(CustomerAddressEntity address) {
		MainApplication.getInstance().setCustomerAddress(address);
		if(address == null) return ;
		SharedPreferences.Editor sharedata = context.getSharedPreferences("ADDRESS", 0).edit();
		sharedata.clear();
        sharedata.putInt("address_id", address.getId());
		sharedata.putString("address_name", address.getName());
		sharedata.putString("address_mobile",address.getMobile());
		sharedata.putString("address_detail", address.getAddress());
		sharedata.commit();
    }

    public void delete() {
        MainApplication.getInstance().setCustomerAddress(null);
        SharedPreferences.Editor sharedata = context.getSharedPreferences("address", 0).edit();
        sharedata.clear();
        sharedata.commit();
    }
}
