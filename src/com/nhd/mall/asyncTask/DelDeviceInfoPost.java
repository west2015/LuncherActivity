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
import java.util.Map;

/**注销时删除用户绑定的设备信息
 * Created by linchunwei on 14-4-24.
 */
public class DelDeviceInfoPost {
    private OnAsyncTaskUpdateListener listener;
    private String message;
    private Long memberId;
    private Context context;

    public DelDeviceInfoPost(Context context, Long memberId) {
        if(!AndroidServerFactory.PRODUCTION_MODEL)
            MobclickAgent.onEvent(context, "path", "/api/push/delDeviceInfo");
        this.context = context;
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
            Object status=null;
            try {
                status = (Map<String, String>) AndroidServerFactory.getServer().postDelDeviceInfo(memberId);
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
            Map<String, String> map = (Map<String, String>) result;
            if(map!=null && map.get("message")!=null){
                message = map.get("message");
            }
            if (listener != null) {
                listener.getData(result, message);
            }
        }
    }
    public void setListener(OnAsyncTaskUpdateListener listener) {
        this.listener = listener;
    }

}
