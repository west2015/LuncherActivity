package com.nhd.mall.datebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings.Secure;

/************************************************************
 *  内容摘要	：<p> 第一次进入该页面标识
 *
 *  作者	：vendor
 *  创建时间	：2013-1-31 下午4:05:39 
 *
 *  历史记录	:
 *  	日期	: 2013-1-31 下午4:05:39 	修改人：vendor
 *  	描述	:
 ************************************************************/
public class DbPageFirst {
	
	private final String DB_NAME = "config_first_page";
	
	private Context context;
	public DbPageFirst(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	public void init(){
		SharedPreferences preferences = context.getSharedPreferences(DB_NAME,Context.MODE_PRIVATE);
		if (preferences.getString("android_id", null) != null) {
			this.setPagePrompt("isFirst");
			this.setPagePrompt("isShortcut");
			this.setPagePrompt("isFirstWebView");
			this.setPagePrompt("isFirstCircle");
			this.setPagePrompt("isFirstEvents");
			this.setPagePrompt("isFirstFixtures");
			this.setPagePrompt("isFirstFixtureDraw");
			this.setPagePrompt("isFirstNews");
			this.setPagePrompt("isFirstMain");
			this.setPagePrompt("isFirstVenueDetails");
			this.setPagePrompt("isFirstVenueAllRate");
			this.setPagePrompt("isFirstPlayerDetail");
			this.setPagePrompt("isFirstWeibo");
			this.setPagePrompt("isFirstEightBall");
			this.setPagePrompt("isFirstNineBall");
			this.setPagePrompt("isFirstSnooker");
			this.setPagePrompt("isFirstCompetitionCenter");
		}
	}
	
	public Boolean getPagePrompt(String key){
		SharedPreferences preferences = context.getSharedPreferences(DB_NAME,Context.MODE_PRIVATE);
		if (preferences.getString("android_id", null) == null) {
			Editor sharedata = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE).edit();
			sharedata.clear();
			sharedata.putString("android_id", Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
			sharedata.putBoolean("isFirst", true);
			sharedata.putBoolean("isShortcut", true);
			sharedata.putBoolean("isFirstWebView", true);
			sharedata.putBoolean("isFirstCircle", true);
			sharedata.putBoolean("isFirstEvents", true);
			sharedata.putBoolean("isFirstFixtures",true);
			sharedata.putBoolean("isFirstFixtureDraw",true);
			sharedata.putBoolean("isFirstNews", true);
			sharedata.putBoolean("isFirstMain", true);
			sharedata.putBoolean("isFirstVenueDetails",true);
			sharedata.putBoolean("isFirstVenueAllRate",true);
			sharedata.putBoolean("isFirstImageView",true);
			sharedata.putBoolean("isFirstPlayerDetail",true);
			sharedata.putBoolean("isFirstWeibo",true);
			sharedata.putBoolean("isFirstEightBall",true);
			sharedata.putBoolean("isFirstNineBall",true);
			sharedata.putBoolean("isFirstSnooker",true);
			sharedata.putBoolean("isFirstCompetitionCenter",true);
			sharedata.commit();
			
			return true;
		}else{
			return preferences.getBoolean(key, false);
		}
	}
	
	public void setPagePrompt(String key){
		setPagePrompt(key, false);
	}
	
	public void setPagePrompt(String key, Boolean value){
		Editor sharedata = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE).edit();
		sharedata.putBoolean(key, value);

		sharedata.commit();
	}
}
