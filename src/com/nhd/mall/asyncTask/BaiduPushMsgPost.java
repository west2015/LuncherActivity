package com.nhd.mall.asyncTask;

import android.content.Context;
import android.util.Log;

import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.entity.BaiduBindingEntity;
import com.nhd.mall.push.BaiduBindingMsg;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.umeng.analytics.MobclickAgent;

import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
/**
* @ClassName: BaiduPushMsgPost
* @Description: TODO(百度推送信息提交接口)
* @author EP epowns@gmail.com
* @date 2014-3-4 上午11:43:08
*/
public class BaiduPushMsgPost {
	private OnAsyncTaskUpdateListener listener;
	private String message;
	private Context context;
	private BaiduBindingEntity entry;
	
	public BaiduPushMsgPost(Context context, Long memberId,BaiduBindingMsg baiduBindingMsg) {
		super();
		if(!AndroidServerFactory.PRODUCTION_MODEL)
			MobclickAgent.onEvent(context, "path", "/api/tqh/v2/push/addMessage");
		this.context = context;
		this.entry=new BaiduBindingEntity();
		this.entry.setUserId(baiduBindingMsg.getUserId());
		this.entry.setChannelId(baiduBindingMsg.getChannelId());
		this.entry.setMemberId(memberId);
		this.entry.setCategory("nhd-mall");//app应用类型 tqh：台球会，tqsj：台球世界
		this.entry.setDeviceType("3");//设备类型 android:3   ios:4
		new DownloadTask().execute();
	}
	
	class DownloadTask extends AsyncTaskEx<Void, Void, Object> {
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
				object = AndroidServerFactory.getServer().postBaiDuPushMsg(entry);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (RuntimeException e) {
				if (e instanceof HttpServerErrorException) {
					message = ((HttpServerErrorException) e)
							.getResponseBodyAsString();
                    Log.e("HttpServerErrorException", message);
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
				listener.getData(result, message);
			}
		}
	}

	public void setListener(OnAsyncTaskUpdateListener listener) {
		this.listener = listener;
	}
}
