package com.nhd.mall.datebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
/**
* @ClassName: DbMainActivityCache
* @Description: TODO(缓存数据，保存MainActivity的相关json)
* @author EP epowns@gmail.com
* @date 2014-3-5 上午9:36:49
*/
public class DbMainActivityCache {
	private Context context;
	final static String regularEx = "|";  

	public DbMainActivityCache(Context context) {
		this.context = context;
	}

	public void delete() {
		Editor sharedata = context.getSharedPreferences("MainActivityJson", Context.MODE_PRIVATE).edit();
		sharedata.clear();
		sharedata.commit();
	}
	
	/**
	* @Title: setCache
	* @Description: TODO(保存数据)
	* @param key
	* @param value    
	* @Return void
	*/
	public void setCache(String key, String value){
		Editor sharedata = context.getSharedPreferences("MainActivityJson", Context.MODE_PRIVATE).edit();
		sharedata.putString(key, value);
		sharedata.commit();
	}
	
	/**
	* @Title: getCache
	* @Description: TODO(读取数据)
	* @param key
	* @param defValue
	* @Return String
	*/
	public String getCache(String key, String defValue){
		SharedPreferences preferences = context.getSharedPreferences("MainActivityJson", Context.MODE_PRIVATE);
		return preferences.getString(key, defValue);
	}

}
