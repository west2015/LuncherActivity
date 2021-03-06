package com.nhd.mall.asyncTask;

import android.content.Context;
import android.util.Log;

import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.umeng.analytics.MobclickAgent;

import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;

/**获取物流异步类
 * Created by Administrator on 14-4-30.
 */
public class WuliuGet {
    private OnAsyncTaskUpdateListener listener;
    private String message;
    private Context context;
    private Integer id;

    public WuliuGet(Context context,Integer id) {
        if(!AndroidServerFactory.PRODUCTION_MODEL)
            MobclickAgent.onEvent(context, "path", "/api/v1/order/getLogistic");
        this.context = context;
        this.id = id;
        new DownloadTask().execute();
    }
    class DownloadTask extends AsyncTaskEx<Void, Void,Object> {

        public DownloadTask() {
            super();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingProgressDialog(context);
        }

        @Override
        protected Object doInBackground(Void... params) {
            Object object = null;
            try {
                object = AndroidServerFactory.getServer().getWuliu(id);
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
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            dismissProgressDialog(context);
            if (listener != null) {
                listener.getData(result,message);
            }
        }
    }
    public void setListener(OnAsyncTaskUpdateListener listener) {
        this.listener = listener;
    }
}
