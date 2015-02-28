package com.nhd.mall.asyncTask;

import android.content.Context;
import android.util.Log;

import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.entity.MainEntity;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.umeng.analytics.MobclickAgent;

import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;

/**获取首页数据异步
 * Created by caili on 14-4-17.
 */
public class MainEntityGet {
    private OnAsyncTaskUpdateListener listener;
	private String message;
	private Context context;
    private Integer id;

    public MainEntityGet(Context context,Integer id) {
    	if(!AndroidServerFactory.PRODUCTION_MODEL)
			MobclickAgent.onEvent(context, "path", "/api/v1/index/show");
		this.context = context;
		this.id  = id;
		new DownloadTask().execute();

	}
    public void update(Integer id) {
        this.id  = id;
        new DownloadTask().execute();

    }

	class DownloadTask extends AsyncTaskEx<Void, Void,MainEntity> {

		public DownloadTask() {
			super();
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected MainEntity doInBackground(Void... params) {
            MainEntity object = null;
			try {
				object = AndroidServerFactory.getServer().getMainEntity(id);
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
		protected void onPostExecute(MainEntity result) {
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
