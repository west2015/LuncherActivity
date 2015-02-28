package com.nhd.mall.push;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
* @ClassName: DbBaiduPushId
* @Description: TODO(存储百度推送的ID)
* @author EP epowns@gmail.com
* @date 2014-2-27 下午5:31:26
*/
public class DbBaiduPush {
	private Context context;
	final static String regularEx = "|";  

	public DbBaiduPush(Context context) {
		this.context = context;
	}

	public void delete() {
		Editor sharedata = context.getSharedPreferences("baidupush", Context.MODE_PRIVATE).edit();
		sharedata.clear();
		sharedata.commit();
	}
	/**
	* @Title: setBaiduBindingMsg
	* @Description: TODO(保存百度推送绑定信息)
	* @param msg    
	* @Return void
	* @throws
	*/
	public void setBaiduBindingMsg(BaiduBindingMsg msg){
		
		Editor sharedata = context.getSharedPreferences("baidupush", Context.MODE_PRIVATE).edit();
		sharedata.putString("userId", msg.getUserId());
		sharedata.putString("channelId", msg.getChannelId());
		sharedata.putString("appid", msg.getAppid());
		sharedata.putString("requestId", msg.getRequestId());
		sharedata.putInt("errorCode", msg.getErrorCode());
		sharedata.commit();
	}
	/**
	* @Title: getBaiduBindingMsg
	* @Description: TODO(获取已经存储的百度推送绑定信息)
	* @Return BaiduBindingMsg
	* @throws
	*/
	public BaiduBindingMsg getBaiduBindingMsg() {
		BaiduBindingMsg msg=new BaiduBindingMsg();
		SharedPreferences preferences = context.getSharedPreferences("baidupush", Context.MODE_PRIVATE);
		msg.setUserId(preferences.getString("userId", null));
		msg.setChannelId(preferences.getString("channelId", null));
		msg.setAppid(preferences.getString("appid", null));
		msg.setRequestId(preferences.getString("requestId", null));
		msg.setErrorCode(preferences.getInt("errorCode", -1));

		return msg;
	}

}
