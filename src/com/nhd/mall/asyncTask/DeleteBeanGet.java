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

/**
 * Created by Administrator on 14-4-29.
 */
public class DeleteBeanGet {
    private OnAsyncTaskUpdateListener listener;
    private String message;
    private Context context;
    private Integer[] ids;

    public DeleteBeanGet(Context context, Integer[] ids) {
        if(!AndroidServerFactory.PRODUCTION_MODEL)
            MobclickAgent.onEvent(context, "path", "/api/v1/wind/del");
        this.context = context;
        this.ids = ids;
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
            Object status=null;
            try {
                status = AndroidServerFactory.getServer().getBeanDelete(ids);
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
            return status;
        }
        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            dismissProgressDialog(context);
            if (listener != null) {
                listener.getData(result, message);
            }
        }
    }
    public void setListener(OnAsyncTaskUpdateListener listener) {
        this.listener = listener;
    }
}
