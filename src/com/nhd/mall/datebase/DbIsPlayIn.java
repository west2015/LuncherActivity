package com.nhd.mall.datebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/************************************************************
 *  内容摘要	：<p>
 * 3G 开关控制数据库
 *  作者	：欧祥斌
 *  创建时间	：2013-8-3 下午12:12:06 
 ************************************************************/
public class DbIsPlayIn {
	private final String DB_NAME = "open3g";
	private SharedPreferences preferences;

	public DbIsPlayIn(Context context) {
		super();
		preferences = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);

	}

	
	/**
	 *  函数名称 : getSetState
	 *  功能描述 :  得到是否允许3g播放状态
	 *  参数及返回值说明：
	 */
	public boolean getSetState() {

		return preferences.getBoolean("isopen", false);
	}

	public void setSetState(boolean state) {
		Editor editor = preferences.edit();
		editor.putBoolean("isopen", state);
		editor.commit();
		editor.clear();

	}

}
