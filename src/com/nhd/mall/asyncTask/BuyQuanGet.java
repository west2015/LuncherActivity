package com.nhd.mall.asyncTask;

import android.content.Context;
import android.util.Log;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.entity.Coupon;
import com.nhd.mall.entity.CouponListEntity;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.entity.StoreListEntity;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import org.springframework.web.client.HttpServerErrorException;
import java.io.IOException;
import java.util.ArrayList;

/**首页点击查看优惠券加载异步
 * Created by Administrator on 14-6-30.
 */
public class BuyQuanGet {
	private final int UPDATE = 1;
	private final int GETMORE = 2;
	private OnAsyncTaskUpdateListener listener;
    private String message;
    private Integer pageNum;
    private CouponListEntity oldCoupon;
    private Context context;
    private Integer storeId;
    private int type;
    private boolean flag;
    private int operation;
    public BuyQuanGet(Context context,Integer storeId,Integer pageNum,int type,boolean flag){
    	this.context = context;
    	operation = UPDATE;
    	update(storeId,pageNum,type,flag);
    }
    public void getMore(CouponListEntity oldCoupon,Integer storeId,Integer pageNum,int type,boolean flag){
    	this.oldCoupon = oldCoupon;
    	this.storeId = storeId;
    	this.pageNum = pageNum;
    	this.type = type;
    	this.flag = flag;
    	Log.e("BuyQaunGet", "storeId = " + storeId + ",pageNum = " + pageNum + ",type = " + type
    			+ ",flag = " + flag);
    	operation = GETMORE;
    	new DownloadTask().execute();
    }
    public void update(Integer storeId,Integer pageNum,int type,boolean flag) {
    	oldCoupon = null;
    	this.storeId = storeId;
    	this.pageNum = pageNum;
    	this.type = type;
    	this.flag = flag;
    	operation = UPDATE;
    	new DownloadTask().execute();
    }
    class DownloadTask extends AsyncTaskEx<Void, Void,CouponListEntity> {
    	public DownloadTask() {
    		super();
	    }
	    @Override
	    protected void onPreExecute() {
	    	super.onPreExecute();
	    	showLoadingProgressDialog(context);
	    }
	    @Override
	    protected CouponListEntity doInBackground(Void... params) {
	    	CouponListEntity object = null;
	        try {
	        	object = AndroidServerFactory.getServer().getBuyQuan(storeId,pageNum,type,flag);
	        } catch (IOException e) {
	        	e.printStackTrace();
	        } catch (RuntimeException e) {
	        	if (e instanceof HttpServerErrorException) {
	        		message = ((HttpServerErrorException) e).getResponseBodyAsString();
	        		message = ParseJson.getStatusAsString(message);
	        	} else {
	        		final String TAG = "asynctask";
	        		final String msg = e.getMessage();
	        		if (msg != null) {
	        			Log.e(TAG, msg);
	                }
	        	}
	        }
	        return object;
	    }
	    @Override
	    protected void onPostExecute(CouponListEntity result) {
	    	super.onPostExecute(result);
	        dismissProgressDialog(context);
	        if (listener != null) {
	        	if(operation == UPDATE) oldCoupon = null;
	        	listener.getData(merge(result),message);
	        }
	    }
	}    
    public CouponListEntity merge(CouponListEntity result){
    	if(oldCoupon == null || oldCoupon.getCoupons().length == 0) {
    		Log.e("BuyQuanGet", "oldCoupon is NULL");
    		oldCoupon = result;
    		return result;
		}
		Coupon[] entity = oldCoupon.getCoupons();
		Coupon[] reEntity = result.getCoupons();
		ArrayList<Coupon> storeList = new ArrayList<Coupon>();
		for(int i=0;i<entity.length;i++){
			storeList.add(entity[i]);
		}
		for(int i=0;i<reEntity.length;i++){
			storeList.add(reEntity[i]);
		}
		Coupon[] finalEntity = new Coupon[storeList.size()];
		finalEntity = storeList.toArray(finalEntity);
		oldCoupon.setCoupons(finalEntity);
		return oldCoupon;
    }
	public void setListener(OnAsyncTaskUpdateListener listener) {
		this.listener = listener;
	}

}
