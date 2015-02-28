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

/**收到货后调用这个  退货
 * Created by Administrator on 14-4-26.
 */
public class FormSureGet {
    private OnAsyncTaskUpdateListener listener;
    private String message;
    private Context context;
    private Integer id;

    public FormSureGet(Context context,Integer id) {
        if(!AndroidServerFactory.PRODUCTION_MODEL)
            MobclickAgent.onEvent(context, "path", "/api/v1/order/orderReceipt");
        this.context = context;
        this.id  = id;
        new DownloadTask().execute();

    }
    public void update(Integer id) {
        this.id  = id;
        new DownloadTask().execute();

    }

    class DownloadTask extends AsyncTaskEx<Void, Void,Object> {

        public DownloadTask() {
            super();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Void... params) {
            Object object = null;
            try {
                object = AndroidServerFactory.getServer().getFormSure(id);
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
            if (listener != null) {
                listener.getData( result, message);
            }
        }
    }
    public void setListener(OnAsyncTaskUpdateListener listener) {
        this.listener = listener;
    }
}
