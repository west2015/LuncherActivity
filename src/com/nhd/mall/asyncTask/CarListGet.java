package com.nhd.mall.asyncTask;

import android.content.Context;
import android.util.Log;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.entity.CarEntity;
import com.nhd.mall.entity.CarList;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.umeng.analytics.MobclickAgent;
import org.springframework.web.client.HttpServerErrorException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 14-4-21.
 */
public class CarListGet {
    private OnAsyncTaskUpdateListener listener;
    private String message;
    private Context context;
    private Long memberId;

    public CarListGet(Context context,Long memberId) {
        if(!AndroidServerFactory.PRODUCTION_MODEL)
            MobclickAgent.onEvent(context, "path", "/api/v1/car/list");
        this.context = context;
        this.memberId = memberId;
        new DownloadTask().execute();
    }
    public void update(){
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
                object = AndroidServerFactory.getServer().getCarList(memberId);
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
                listener.getData(result,message);
            }
        }
    }
    public void setListener(OnAsyncTaskUpdateListener listener) {
        this.listener = listener;
    }
}
