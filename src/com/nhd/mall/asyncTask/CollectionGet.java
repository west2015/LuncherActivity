package com.nhd.mall.asyncTask;

import android.content.Context;
import android.util.Log;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.entity.CollectionEntity;
import com.nhd.mall.entity.CollectionList;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.entity.StoreListEntity;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.umeng.analytics.MobclickAgent;
import org.springframework.web.client.HttpServerErrorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Administrator on 14-4-22.
 */
public class CollectionGet {
    private OnAsyncTaskUpdateListener listener;
    private String message;
    private Context context;
    private Integer pageNum;
    private Integer type;
    private Long memberId;
    private CollectionList list;

    public CollectionGet(Context context,Integer pageNum,long memberId,Integer type) {
        if(!AndroidServerFactory.PRODUCTION_MODEL)
            MobclickAgent.onEvent(context, "path", "/api/v1/collection/list");
        this.context = context;
        this.pageNum  = pageNum;
        this.memberId  = memberId;
        this.type  = type;
        new DownloadTask().execute();
    }
    public void update(Integer pageNum) {
        this.pageNum  = pageNum;
        new DownloadTask().execute();

    }

    class DownloadTask extends AsyncTaskEx<Void, Void,CollectionList> {

        public DownloadTask() {
            super();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingProgressDialog(context);
        }

        @Override
        protected CollectionList doInBackground(Void... params) {
            CollectionList object = null;
            try {
                object = AndroidServerFactory.getServer().getCollectionList(pageNum,memberId,type);
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
        protected void onPostExecute(CollectionList result) {
            super.onPostExecute(result);
            dismissProgressDialog(context);
            if (listener != null) {
                listener.getData( merge(result),message);
            }
        }
    }
    public CollectionList merge(CollectionList result){
        if(list==null||list.getCollections().length==0){
            list = result;
            return result;
        }
        CollectionEntity[]entity =list.getCollections();
        CollectionEntity[]reEntity = result.getCollections();
        ArrayList<CollectionEntity> storeList = new ArrayList<CollectionEntity>();
        for(int i=0;i<entity.length;i++){
            storeList.add(entity[i]);
        }
        for(int i=0;i<reEntity.length;i++){
            storeList.add(reEntity[i]);
        }
        CollectionEntity[]finalEntity = new CollectionEntity[storeList.size()];
        finalEntity = storeList.toArray(finalEntity);
        list.setCollections(finalEntity);
        return list;
    }
    public void setListener(OnAsyncTaskUpdateListener listener) {
        this.listener = listener;
    }
}
