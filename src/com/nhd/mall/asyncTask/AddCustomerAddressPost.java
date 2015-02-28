package com.nhd.mall.asyncTask;

import android.content.Context;
import android.util.Log;

import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.entity.AddCustomerAddress;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.umeng.analytics.MobclickAgent;

import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;

/**添加用户收货地址
 * Created by Administrator on 14-4-18.
 */
public class AddCustomerAddressPost {
    private OnAsyncTaskUpdateListener listener;
	private String message;
    private AddCustomerAddress address;
    private Context context;

	public AddCustomerAddressPost(Context context,AddCustomerAddress address) {
		if(!AndroidServerFactory.PRODUCTION_MODEL)
			MobclickAgent.onEvent(context, "path", "/api/v1/address/add");
        this.context = context;
        this.address = address;
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
                status = AndroidServerFactory.getServer().postAddCustomerAddress(address);
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
