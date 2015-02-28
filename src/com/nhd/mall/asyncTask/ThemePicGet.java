package com.nhd.mall.asyncTask;

import android.content.Context;
import android.util.Log;

import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskDataListener;
import com.nhd.mall.util.ParseJson;

import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;

/**获取首页主题购物跳转时的图片
 * Created by Administrator on 14-6-30.
 */
public class ThemePicGet {
    private OnAsyncTaskDataListener listener;
    private String message;
    private Context context;
    private Integer themeId;
    private int position;

    public ThemePicGet(Context context,Integer themeId,int position) {
        this.context = context;
        this.themeId = themeId;
        this.position = position;
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
                object = AndroidServerFactory.getServer().getThemePic(themeId);
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
                listener.getDataSort( result, message,String.valueOf(position));
            }
        }
    }
    public void setListener(OnAsyncTaskDataListener listener) {
        this.listener = listener;
    }
}
