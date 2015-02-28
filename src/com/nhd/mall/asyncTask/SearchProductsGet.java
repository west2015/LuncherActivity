package com.nhd.mall.asyncTask;

import android.content.Context;
import android.util.Log;

import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.entity.ProductDetailEntity;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.entity.StoreListEntity;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.umeng.analytics.MobclickAgent;

import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.util.ArrayList;

/**搜索结果返回的异步
 * Created by caili on 14-4-28.
 */
public class SearchProductsGet {
    private OnAsyncTaskUpdateListener listener;
    private String message;
    private Context context;
    private String query;
    private String type;
    private Integer first=0;
    private Integer max=10;
    private ProductDetailEntity[]entity;


    public SearchProductsGet(Context context,String query,String type) {
        if(!AndroidServerFactory.PRODUCTION_MODEL)
            MobclickAgent.onEvent(context, "path", "/api/v1/product/findProducts");
        this.context = context;
        this.query = query;
        this.type = type;
        new DownloadTask().execute();

    }
    public void update(ProductDetailEntity[]entity) {
        this.entity = entity;
        if(entity==null||entity.length<=0){
           first =0;
        }
        else{
            first = entity.length;
        }
        new DownloadTask().execute();

    }

    class DownloadTask extends AsyncTaskEx<Void, Void,ProductDetailEntity[]> {

        public DownloadTask() {
            super();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingProgressDialog(context);
        }

        @Override
        protected ProductDetailEntity[] doInBackground(Void... params) {
            ProductDetailEntity[] object = null;
            try {
                object = AndroidServerFactory.getServer().getSearchProducts(query,type,first,max);
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
        protected void onPostExecute(ProductDetailEntity[] result) {
            super.onPostExecute(result);
            dismissProgressDialog(context);
            if (listener != null) {
                listener.getData( merge(result),message);
            }
        }
    }
    public ProductDetailEntity[] merge(ProductDetailEntity[] result){
        if(result==null||result.length<=0){
            return entity;
        }
        if(entity==null||entity.length==0){
            entity = result;
            return result;
        }
        ArrayList<ProductDetailEntity>list = new ArrayList<ProductDetailEntity>();
        for(int i=0;i<entity.length;i++){
            list.add(entity[i]);
        }
        for(int j=0;j<result.length;j++){
            list.add(result[j]);
        }
        ProductDetailEntity[]pro = new ProductDetailEntity[list.size()];
        pro = list.toArray(pro);
        entity=pro;
        return entity;
    }
    public void setListener(OnAsyncTaskUpdateListener listener) {
        this.listener = listener;
    }
}
