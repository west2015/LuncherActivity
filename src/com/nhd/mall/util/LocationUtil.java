package com.nhd.mall.util;

import android.content.Context;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;


/**
 * baidu定位工具类
 * @author kl
 *
 */
public class LocationUtil {
	private LocationClient mLocationClient = null;
	private MyLocationListenner myListener = new MyLocationListenner();
	
	public LocationUtil(Context context){
		mLocationClient = new LocationClient(context);
		mLocationClient.registerLocationListener( myListener );
		setLocationOption();
		mLocationClient.start();
	}
	
	public void requestLocation(){
		mLocationClient.requestLocation();
	}
	/**
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	 */
	private class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return ;

//			if (listener != null) {
//				listener.onLocationResult(location.getAddrStr(), location.getCity(), Utils.getCityToCityCode(location.getCity()), location.getProvince(), location.getLatitude()+"", location.getLongitude()+"");
//				listener = null;
//			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null){
				return ;
			}

		}
	}
	
	//设置相关参数
	private void setLocationOption(){
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(false);				//打开gps
		option.setCoorType("bd09ll");		//设置坐标类型
		option.setAddrType("all");		//设置地址信息，仅设置为“all”时有地址信息，默认无地址信息
		option.setScanSpan(500);	//设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
		mLocationClient.setLocOption(option);
	}
	
	private LocationListener listener;
	public void setListener(LocationListener listener){
		this.listener = listener;
	}
	
	public interface LocationListener{

    	public void onLocationResult(String mAddr, String city, String cityCode, String province, String lat, String lng);
	}
	
	public void onDestroy(){
		mLocationClient.stop();
		mLocationClient.unRegisterLocationListener(myListener);
	}
}
