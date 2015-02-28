package com.nhd.mall.asyncTask;

import android.content.Context;
import android.util.Log;

import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.entity.BeanDetailEntity;
import com.nhd.mall.entity.BeanDetailList;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.umeng.analytics.MobclickAgent;

import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.util.ArrayList;

/**获取疯豆消息异步
 * Created by Administrator on 14-4-28.
 */
public class BeanDetailGet {
    private OnAsyncTaskUpdateListener listener;
    private String message;
    private Context context;
    private Integer pageNum;
    private BeanDetailList bean;
    private Long memberId;

    public BeanDetailGet(Context context,Integer pageNum,Long memberId) {
        if(!AndroidServerFactory.PRODUCTION_MODEL)
            MobclickAgent.onEvent(context, "path", "/api/v1/wind/list");
        this.context = context;
        this.pageNum  = pageNum;
        this.memberId  = memberId;
        new DownloadTask().execute();

    }
    public void update(Integer pageNum,Long memberId) {
        this.pageNum  = pageNum;
        this.memberId  = memberId;
        new DownloadTask().execute();
    }

    class DownloadTask extends AsyncTaskEx<Void, Void,BeanDetailList> {

        public DownloadTask() {
            super();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingProgressDialog(context);
        }

        @Override
        protected BeanDetailList doInBackground(Void... params) {
            BeanDetailList object = null;
            try {
                object = AndroidServerFactory.getServer().getbBeanDetail(memberId,pageNum);
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
        protected void onPostExecute(BeanDetailList result) {
            super.onPostExecute(result);
            dismissProgressDialog(context);
            if (listener != null) {
                listener.getData( merge(result),message);
            }
        }
    }
    public BeanDetailList merge(BeanDetailList result){
        if(bean==null||bean.getRecords().length==0){
            bean = result;
            return result;
        }
        BeanDetailEntity[]entity = bean.getRecords();
        BeanDetailEntity[]reEntity = result.getRecords();
        ArrayList<BeanDetailEntity> storeList = new ArrayList<BeanDetailEntity>();
        for(int i=0;i<entity.length;i++){
            storeList.add(entity[i]);
        }
        for(int i=0;i<reEntity.length;i++){
            storeList.add(reEntity[i]);
        }
        BeanDetailEntity[]finalEntity = new BeanDetailEntity[storeList.size()];
        finalEntity = storeList.toArray(finalEntity);
        bean.setRecords(finalEntity);
        return bean;
    }
    public void setListener(OnAsyncTaskUpdateListener listener) {
        this.listener = listener;
    }
}
