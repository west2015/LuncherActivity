package com.nhd.mall.asyncTask;

import android.content.Context;
import android.util.Log;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import org.springframework.web.client.HttpServerErrorException;
import java.io.IOException;

/**
 * Created by Administrator on 14-8-1.
 */
public class EventProductGet {

    private OnAsyncTaskUpdateListener listener;
    private String message;
    private Context context;
    private Integer id;

    public EventProductGet(Context context,Integer id) {
        this.context = context;
        this.id = id;
        new DownloadTask().execute();
    }
    public void update(Integer id){
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
        }

        @Override
        protected Object doInBackground(Void... params) {
            Object status=null;
            try {
                status = AndroidServerFactory.getServer().getEventProducts(id);
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
            if (listener != null) {
                listener.getData(result, message);
            }
        }
    }
    public void setListener(OnAsyncTaskUpdateListener listener) {
        this.listener = listener;
    }

}
