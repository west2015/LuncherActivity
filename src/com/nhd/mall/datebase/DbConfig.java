package com.nhd.mall.datebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;


/************************************************************
 *  内容摘要	：<p> 获取用户的配置
 *  作者	：vendor
 *  创建时间	：2013-1-31 下午4:13:05 
 *  历史记录	:
 *  	日期	: 2013-1-31 下午4:13:05 	修改人：vendor
 *  	描述	:
 ************************************************************/
public class DbConfig {
	
	private final String DB_NAME = "config";
	
	private Context context;
	
	private SharedPreferences preferences;
	
	public DbConfig(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		
		 preferences = context.getSharedPreferences(DB_NAME,Context.MODE_PRIVATE);
		init();
	}
	
	private void init(){
		if ("".equals(preferences.getString("android_id", ""))) {
			Editor sharedata = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE).edit();
			sharedata.clear();
			sharedata.putInt("sys_version", android.os.Build.VERSION.SDK_INT);
			sharedata.putString("android_id", Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
			Point point = getDisplayMetrics();
			sharedata.putInt("point_x", point.x);
			sharedata.putInt("point_y", point.y);
			sharedata.putInt("attention", 0);
			sharedata.putInt("attention_date", 0);
			sharedata.putBoolean("isNotification", true);//是否推送消息
			sharedata.putInt("ignore_version_code", 0);
			sharedata.putString("city","");
			sharedata.putString("cityCode","");
			sharedata.putString("province","");
			sharedata.putString("mAddr", "");
			sharedata.putBoolean("isFirstLivePlay", true);  //是否第一次进入直播
            sharedata.putBoolean("auto_login",true);//自动登录
			sharedata.commit();
		}
	}

    public void setAutoLogin(boolean isAuto){
        Editor sharedata = preferences.edit();
        sharedata.putBoolean("auto_login", isAuto);
        sharedata.commit();
    }

    public boolean isAutoLogin(){
        return preferences.getBoolean("auto_login", false);
    }

 public void setRecode(String code){
     Editor sharedata = preferences.edit();
     sharedata.putString("code",code);
     sharedata.commit();
   }
    public String getRecode(){
        return preferences.getString("code","");
    }
	/**
	 *  函数名称 : getSysVersion
	 *  功能描述 : 获取系统的版本 
	 *  参数及返回值说明：
	 *  	@return
	 *
	 *  修改记录：
	 *  	日期：2013-1-31 下午4:42:21	修改人：vendor
	 *  	描述	：
	 *
	 */
	public int getSysVersion(){
		return preferences.getInt("sys_version", 0);
	}
    public String getVersion(){
        return preferences.getString("verName",null);
    }
    public void setVersion(String version){
        Editor sharedata = preferences.edit();
        sharedata.putString("verName",version);
        sharedata.commit();
    }
    public Integer getStore(){
        return preferences.getInt("storeId",0);
    }
    public void setStore(Integer store){
        Editor sharedata = preferences.edit();
        sharedata.putInt("storeId",store);
        sharedata.commit();
    }
	
	/**
	* @Title: getLat
	* @Description: TODO(获取维度)
	* @return    
	* @Return String
	* @throws
	*/
	public String getLat(){
		return preferences.getString("lat", "26.102754");
	}
	public boolean getMain(){
		return preferences.getBoolean("main", true);
	}
	public void setMain(){
		Editor sharedata = preferences.edit();
		sharedata.putBoolean("main", false);
		sharedata.commit();
	}
	
	/**
	* @Title: getLng
	* @Description: TODO(获取经度)
	* @return    
	* @Return String
	* @throws
	*/
	public String getLng(){
		return preferences.getString("lng", "119.280315");
	}
	
	/**
	 *  函数名称 : getAndroidId
	 *  功能描述 : 获取手机唯一标识码 
	 *  参数及返回值说明：
	 *  	@return
	 *
	 *  修改记录：
	 *  	日期：2013-1-31 下午4:42:42	修改人：vendor
	 *  	描述	：
	 *
	 */
	public int getAndroidId(){
		return preferences.getInt("android_id", 0);
	}
	
	/**
	 *  函数名称 : getDisplayMetric
	 *  功能描述 : 获取手机分辨率 
	 *  参数及返回值说明：
	 *  	@return
	 *
	 *  修改记录：
	 *  	日期：2013-1-31 下午4:43:02	修改人：vendor
	 *  	描述	：
	 *
	 */
	public Point getDisplayMetric(){
		return new Point(preferences.getInt("point_x", 0), preferences.getInt("point_y", 0));
	}
	
	/**
	 *  函数名称 : getAttention
	 *  功能描述 : 获取关注提前类型 
	 *  参数及返回值说明：
	 *  	@return
	 *
	 *  修改记录：
	 *  	日期：2013-2-1 上午9:51:06	修改人：vendor
	 *  	描述	：
	 *
	 */
	public int getAttention(){
		return preferences.getInt("attention", 0);
	}
	
	/**
	 *  函数名称 : setAttention
	 *  功能描述 : 设置关注的提前类型 
	 *  参数及返回值说明：
	 *  	@param str
	 *  	@return
	 *
	 *  修改记录：
	 *  	日期：2013-2-1 上午9:52:37	修改人：vendor
	 *  	描述	：
	 *
	 */
	public void setAttention(int attention){
		Editor sharedata = preferences.edit();
		sharedata.putInt("attention", attention);

		sharedata.commit();
	}
	
	/**
	* @Title: setNotificationString
	* @Description: TODO(设置百度通知内容)
	* @param msg    
	* @Return void
	* @throws
	*/
	public void setNotificationString(String msg){
		Editor sharedata = preferences.edit();
		sharedata.putString("notification_string", msg);

		sharedata.commit();
	}
	/**
	* @Title: setNotificationTitle
	* @Description: TODO(设置百度通知标题)
	* @param msg    
	* @Return void
	* @throws
	*/
	public void setNotificationTitle(String msg){
		Editor sharedata = preferences.edit();
		sharedata.putString("notification_title", msg);

		sharedata.commit();
	}
	/**
	 *  函数名称 : getAttentionDate
	 *  功能描述 : 获取提前时间 
	 *  参数及返回值说明：
	 *  	@return
	 *
	 *  修改记录：
	 *  	日期：2013-2-1 上午9:52:57	修改人：vendor
	 *  	描述	：
	 *
	 */
	public int getAttentionDate(){
		return preferences.getInt("attention_date", 0);
	}
	
	/**
	 *  函数名称 : setAttentionDate
	 *  功能描述 : 设置提前时间 
	 *  参数及返回值说明：
	 *  	@param date
	 *  	@return
	 *
	 *  修改记录：
	 *  	日期：2013-2-1 上午9:53:25	修改人：vendor
	 *  	描述	：
	 *
	 */
	public void setAttentionDate(int date){
		Editor sharedata = preferences.edit();
		sharedata.putInt("attention_date", date);

		sharedata.commit();
	}
	/**
	 *  函数名称 : getNotification
	 *  功能描述 : 获取服务器推送的配置 
	 *  参数及返回值说明：
	 *  	@return
	 *
	 *  修改记录：
	 *  	日期：2013-1-31 下午4:44:02	修改人：vendor
	 *  	描述	：
	 *
	 */

	/**
	 *  函数名称 : getVersionIgnoreCode
	 *  功能描述 : 获取被忽略的版本号（只保存最新的版本） 
	 *  参数及返回值说明：
	 *  	@return
	 *
	 *  修改记录：
	 *  	日期：2013-1-31 下午4:44:42	修改人：vendor
	 *  	描述	：
	 *
	 */
	public int getVersionIgnoreCode(){
		return preferences.getInt("ignore_version_code", 0);
	}
	
	/**
	 *  函数名称 : setVersionIgnoreCode
	 *  功能描述 : 设置忽略的版本号 
	 *  参数及返回值说明：
	 *  	@param code
	 *
	 *  修改记录：
	 *  	日期：2013-1-31 下午4:45:50	修改人：vendor
	 *  	描述	：
	 *
	 */
	public void setVersionIgnoreCode(int code){
		Editor sharedata = preferences.edit();
		sharedata.putInt("ignore_version_code", code);

		sharedata.commit();
	}
	
	/**
	 *  函数名称 : getCity
	 *  功能描述 : 获取最新一次的所在城市 
	 *  参数及返回值说明：
	 *  	@return
	 *
	 *  修改记录：
	 *  	日期：2013-1-31 下午4:46:09	修改人：vendor
	 *  	描述	：
	 *
	 */
	public String getCity(){
		return preferences.getString("city", null);
	}
	
	/**
	 *  函数名称 : getCityCode
	 *  功能描述 : 获取最新一次的所在城市编号 
	 *  参数及返回值说明：
	 *  	@return
	 *
	 *  修改记录：
	 *  	日期：2013-1-31 下午4:46:29	修改人：vendor
	 *  	描述	：
	 *
	 */
	public String getCityCode(){
		return preferences.getString("cityCode", null);
	}
	
	/**
	 *  函数名称 : setCity
	 *  功能描述 : 设置最新一次所在城市的城市名以及编号 
	 *  参数及返回值说明：
	 *  	@param city
	 *  	@param cityCode
	 *
	 *  修改记录：
	 *  	日期：2013-1-31 下午4:46:51	修改人：vendor
	 *  	描述	：
	 *
	 */
	public void setCity(String city, String cityCode,String province){
		Editor sharedata = preferences.edit();
		sharedata.putString("city", city);
		sharedata.putString("cityCode", cityCode);
		sharedata.putString("province", province);

		sharedata.commit();
	}
	
	/**
	 *  函数名称 : getDisplayMetrics
	 *  功能描述 : 获取屏幕分辨率
	 *  参数及返回值说明：
	 *  	@return
	 *
	 *  修改记录：
	 *  	日期：2013-1-31 下午4:16:19	修改人：vendor
	 *  	描述	：
	 *
	 */
	private Point getDisplayMetrics() {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		return new Point(screenWidth, screenHeight);
	}
	public String getmAddr(){
        return preferences.getString("mAddr", null);
    }
    public boolean getNotification(){
        return preferences.getBoolean("isNotification", true);
    }
    public void setNotification(boolean boo){
        Editor sharedata = preferences.edit();
        sharedata.putBoolean("isNotification", boo);
        sharedata.commit();
    }


}
