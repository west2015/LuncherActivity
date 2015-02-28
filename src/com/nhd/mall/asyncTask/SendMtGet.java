package com.nhd.mall.asyncTask;

import android.content.Context;
import android.util.Log;

import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskDataListener;
import com.nhd.mall.util.ParseJson;
import com.umeng.analytics.MobclickAgent;

import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.util.Map;

/**发送短信验证码
 * Created by linchunwei on 14-2-23.
 */
public class SendMtGet {
    private OnAsyncTaskDataListener listener;
    private String message;
    private String phone;
    private Context context;

    public SendMtGet(Context context, String phone) {
        if(!AndroidServerFactory.PRODUCTION_MODEL)
            MobclickAgent.onEvent(context, "path", "/api/v1/member/register");
        this.context = context;
        this.phone = phone;
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
                status = (Map<String, String>) AndroidServerFactory.getServer().getSendMt(phone);
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
                listener.getDataSort(result, message, null);
            }
        }
    }
    public void setListener(OnAsyncTaskDataListener listener) {
        this.listener = listener;
    }

}
