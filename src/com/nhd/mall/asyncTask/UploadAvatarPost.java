package com.nhd.mall.asyncTask;

import java.io.File;
import java.io.IOException;
import org.springframework.web.client.HttpServerErrorException;

import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.ImageUtil;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.umeng.analytics.MobclickAgent;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * 类<code>PostImageTask</code>用户上传头像
 * 
 * @author vendor
 * @version 2012年11月2日 23:01:51
 * @see     Class
 * @since   JDK1.0
 */
public class UploadAvatarPost {
	private File source;
	/** http返回的情况 */
	private String message;
	private Bitmap bitmap;
	private Context context;
	
	/**
	 * 类构造器
	 * 
	 * @param id  会员编号
	 */
	public UploadAvatarPost(Context context, Long id, Bitmap bitmap){
		this.context = context;

		ImageUtil imageUtil = new ImageUtil(context);
		
		try{
			this.bitmap = imageUtil.toRoundCorner(bitmap, 8);
		}catch(OutOfMemoryError e){
			this.bitmap = bitmap;
		}

		try {
			String path = imageUtil.saveBitmap(this.bitmap, null);
			source = new File(path);
			if (!AndroidServerFactory.PRODUCTION_MODEL)
				MobclickAgent.onEvent(context, "path", "/api/v1/forumUser/{id}/avatar/upload");

			new DownTask().execute(id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			//释放掉传入的bitmap
			if(bitmap != null && !bitmap.isRecycled()){
				bitmap.recycle();
				bitmap = null;
			}
		}
		
	}
    
	class DownTask extends AsyncTaskEx<Long, Void, Object> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showLoadingProgressDialog(context);
		}
	
		@Override
		protected Object doInBackground(Long... params) {
			// TODO Auto-generated method stub
			try {
				return AndroidServerFactory.getServer().postUploadAvatar(params[0], source);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				if (e instanceof HttpServerErrorException) {
					message = ((HttpServerErrorException) e).getResponseBodyAsString();
					message = ParseJson.getStatusAsString(message);
					Log.e("HttpServerErrorException", message);
				}else{
					final String TAG = "asynctask";
					final String msg = e.getMessage();
					
					if(msg != null){
						Log.e(TAG, msg);
					}
				}
			}
			
			return null;
		}
	
		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dismissProgressDialog(context);
			if (listener != null) {
				listener.getData(result, message);
			}
			
			if(bitmap != null && !bitmap.isRecycled()){
				bitmap.recycle();
				bitmap = null;
			}
				
		}
	}
	
	private OnAsyncTaskUpdateListener listener;
	public void setListener(OnAsyncTaskUpdateListener listener) {
		this.listener = listener;
	}
}
