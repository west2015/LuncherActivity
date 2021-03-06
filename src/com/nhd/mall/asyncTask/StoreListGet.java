package com.nhd.mall.asyncTask;

import android.content.Context;
import android.util.Log;

import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.entity.MainEntity;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.entity.StoreListEntity;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.umeng.analytics.MobclickAgent;

import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**获取门店列表的异步
 * Created by caili on 14-4-17.
 */
public class StoreListGet {
    private OnAsyncTaskUpdateListener listener;
    private String message;
    private Context context;
    private int pageSize=9999;

    public StoreListGet(Context context) {
        this.context = context;
        new DownloadTask().execute();

    }
    public void update() {
        new DownloadTask().execute();
    }

    class DownloadTask extends AsyncTaskEx<Void, Void,StoreListEntity> {

        public DownloadTask() {
            super();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingProgressDialog(context);
        }

        @Override
        protected StoreListEntity doInBackground(Void... params) {
            StoreListEntity object = null;
            try {
                object = AndroidServerFactory.getServer().getStoreList(pageSize);
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
        protected void onPostExecute(StoreListEntity result) {
            super.onPostExecute(result);
            dismissProgressDialog(context);
            if (listener != null) {
                listener.getData( result,message);
            }
        }
    }
    public void setListener(OnAsyncTaskUpdateListener listener) {
        this.listener = listener;
    }
}
