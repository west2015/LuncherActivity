package com.nhd.mall.util;
import android.content.Context;
import android.widget.Toast;
import com.baidu.mapapi.search.*;

/**
 * @author Administrator
 * 通过城市名获取经纬度类
 */
public class MySearchListener implements MKSearchListener {
	private Context context;
	
	public MySearchListener(Context context) {
		super();
		this.context = context;
	}

	private OnCityGetCoordinatesListener listener;
	
	public void setListener(OnCityGetCoordinatesListener listener) {
		this.listener = listener;
	}

	public interface OnCityGetCoordinatesListener{
		public void toCoordinates(Double lat, Double lng);
	}

	@Override
	public void onGetAddrResult(MKAddrInfo info, int arg1) {
		if (info!=null && info.geoPt!=null) {
			listener.toCoordinates(info.geoPt.getLatitudeE6()/1e6, info.geoPt.getLongitudeE6()/1e6);
		}else {
			listener.toCoordinates(null, null);
			Toast.makeText(context, "获取不到该城市经纬度！", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetPoiDetailSearchResult(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
