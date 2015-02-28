package com.nhd.mall.asyncTask;

import android.content.Context;
import android.util.Log;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.entity.FormList;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.umeng.analytics.MobclickAgent;
import org.springframework.web.client.HttpServerErrorException;
import java.io.IOException;

/**获取我的订单列表
 * Created by Administrator on 14-4-26.
 */
public class FormListGet {
    private OnAsyncTaskUpdateListener listener;
    private String message;
    private Context context;
    private FormList form;
    private Long memberId;

    public FormListGet(Context context,Long memberId) {
        if(!AndroidServerFactory.PRODUCTION_MODEL)
            MobclickAgent.onEvent(context, "path", "/api/v1/order/list");
        this.context = context;
        this.memberId = memberId;
        new DownloadTask().execute();
    }
    public void update(Long memberId) {
        this.memberId = memberId;
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
                object = AndroidServerFactory.getServer().getFormList(memberId);
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
