package com.nhd.mall.util;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import com.nhd.mall.datebase.DbIsPlayIn;

/**
 * 类<code>NetCheck</code>网络状态检查工具类
 * 
 * @author vendor
 * @version 2012年11月4日 16:43:06
 * @see Class
 * @since JDK1.0
 * 
 */
public class NetCheck {

	/**
	 * 检查网络
	 * 
	 * @param context
	 *            用于显示对话框，或是判断
	 * 
	 * @return if true 网络连接可以使用
	 */
	public static Boolean checkNet(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// if true can use
		if (wifi.isWifiEnabled()) {
			return true;
		}

		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager != null) {
			NetworkInfo infor = connManager.getActiveNetworkInfo();

			if (infor != null && infor.isConnected()) {
				return true;
			}
		}
		return false;
	}
	
	public static Boolean check3G(Context context){
		
		DbIsPlayIn dbIsPlayIn = new DbIsPlayIn(context);
		
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// if true can use
		if (wifi.isWifiEnabled()) {
			return true;
		}else {
			if(dbIsPlayIn.getSetState()){
				return true;
			}else {
				return false;
			}
		}
	}

	public static boolean isWifiActive(Context icontext) {

		Context context = icontext.getApplicationContext();

		ConnectivityManager connectivity = (ConnectivityManager) context

		.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo[] info;

		if (connectivity != null) {

			info = connectivity.getAllNetworkInfo();

			if (info != null) {

				for (int i = 0; i < info.length; i++) {

					if (info[i].getTypeName().equals("WIFI") &&

					info[i].isConnected()) {

						return true;

					}

				}

			}

		}

		return false;

	}

}
