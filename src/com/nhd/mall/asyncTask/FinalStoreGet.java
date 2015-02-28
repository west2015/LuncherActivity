package com.nhd.mall.asyncTask;
import android.content.Context;
import android.util.Log;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskDataListener;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.umeng.analytics.MobclickAgent;
import org.springframework.web.client.HttpServerErrorException;
import java.io.IOException;

/**获取总店异步
 * Created by Administrator on 14-5-5.
 */
public class FinalStoreGet {
    private OnAsyncTaskUpdateListener listener;
    private String message;
    private Context context;
    private Integer id;
    private OnAsyncTaskDataListener dataListener;
    private Integer position;

    public FinalStoreGet(Context context, Integer id) {
        if(!AndroidServerFactory.PRODUCTION_MODEL)
        this.context = context;
        this.id = id;
        new DownloadTask().execute();
    }
    public FinalStoreGet(Context context, Integer id,OnAsyncTaskDataListener dataListener,Integer position) {
        if(!AndroidServerFactory.PRODUCTION_MODEL)
        this.context = context;
        this.id = id;
        this.position = position;
        this.dataListener = dataListener;
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
                status = AndroidServerFactory.getServer().getFinalStore(id);
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
            if (dataListener != null) {
                dataListener.getDataSort(result, message,String.valueOf(position));
            }
        }
    }
    public void setListener(OnAsyncTaskUpdateListener listener) {
        this.listener = listener;
    }
}
