package com.nhd.mall.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.util.Log;

public class LocalAddressUtil {

	
	/**
	 *  函数名称 : getLocalIpAddress
	 *  功能描述 :  获取网咯地址
	 *  参数及返回值说明：
	 *  	@return
	 *
	 *  修改记录：
	 *  	日期：2012-12-6 下午04:30:23	修改人：欧祥斌
	 *  	描述	：
	 * 					
	 */
	public static String getLocalIpAddress() {
		  try {
		   for (Enumeration<NetworkInterface> en = NetworkInterface
		     .getNetworkInterfaces(); en.hasMoreElements();) {
		    NetworkInterface intf = en.nextElement();
		    for (Enumeration<InetAddress> enumIpAddr = intf
		      .getInetAddresses(); enumIpAddr.hasMoreElements();) {
		     InetAddress inetAddress = enumIpAddr.nextElement();
		     if (!inetAddress.isLoopbackAddress()) {
		      return inetAddress.getHostAddress().toString();
		     }
		    }
		   }
		  } catch (SocketException ex) {
		   Log.d("TAG", ex.toString());
		  }
		  return null;
	}
}
