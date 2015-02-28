package com.nhd.mall.asyncTask;

import android.content.Context;
import android.util.Log;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.entity.Coupon;
import com.nhd.mall.entity.ProductDetailEntity;
import com.nhd.mall.entity.CouponListEntity;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.umeng.analytics.MobclickAgent;

import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.util.ArrayList;

/**获取优惠券列表异步
 * Created by caili on 14-4-21.
 */
public class CouponListGet {
    private OnAsyncTaskUpdateListener listener;
    private String message;
    private Context context;
    private CouponListEntity lstCoupon;  //优惠券列表
    private Long memberId;
    //默认1,1:未使用、2:已使用、3:已过期
    private Integer status;
    private Integer pageNum=1;   //分页
    private Integer sort;

    public CouponListGet(Context context, Integer status, Long memberId,Integer sort) {
        if(!AndroidServerFactory.PRODUCTION_MODEL)
            MobclickAgent.onEvent(context, "path", "/api/v1/coupon/memberlist");
        this.context = context;
        this.memberId=memberId;
        this.status=status;
        this.sort = sort;
        new DownloadTask().execute();
    }

    public void refresh(){
        this.pageNum=1;
        this.lstCoupon=null;
        new DownloadTask().execute();
    }

    public void getMore(CouponListEntity lstCoupon){
        this.lstCoupon=lstCoupon;
        this.pageNum++;
        new DownloadTask().execute();
    }

    class DownloadTask extends AsyncTaskEx<Void, Void,CouponListEntity> {

        public DownloadTask() {
            super();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected CouponListEntity doInBackground(Void... params) {
            CouponListEntity object = null;
            try {
                object = AndroidServerFactory.getServer().getCouponList(memberId,status,pageNum,sort);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                if (e instanceof HttpServerErrorException) {
                    message = ((HttpServerErrorException) e)
                            .getResponseBodyAsString();
                    message = ParseJson.getStatusAsString(message);
                    Log.e("HttpServerErrorException", message);
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
            if (listener != null) {
                listener.getData( merge(result),message);
            }
        }
    }
    public CouponListEntity merge(CouponListEntity result){
        if(lstCoupon==null||lstCoupon.getCoupons().length==0){
            lstCoupon = result;
            return result;
        }
        Coupon[]entity = lstCoupon.getCoupons();
        Coupon[]reEntity = result.getCoupons();
        ArrayList<Coupon> storeList = new ArrayList<Coupon>();
        for(int i=0;i<entity.length;i++){
            storeList.add(entity[i]);
        }
        for(int i=0;i<reEntity.length;i++){
            storeList.add(reEntity[i]);
        }
        Coupon[]finalEntity = new Coupon[storeList.size()];
        finalEntity = storeList.toArray(finalEntity);
        lstCoupon.setCoupons(finalEntity);
        return lstCoupon;
    }
    public void setListener(OnAsyncTaskUpdateListener listener) {
        this.listener = listener;
    }
}
