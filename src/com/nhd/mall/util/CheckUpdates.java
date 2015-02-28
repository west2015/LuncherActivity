package com.nhd.mall.util;

import java.io.IOException;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.widget.Toast;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.datebase.DbConfig;
import com.nhd.mall.entity.Version;


/**
 * 类<code>CheckUpdates</code>检查版本更新
 * 
 * @author vendor
 * @version 2012年11月5日 10:05:22
 * @see Class
 * @since JDK1.0
 * 
 */
public class CheckUpdates {

	private Context context;
	private final String app = "ANDROID-NHD";
	public CheckUpdates(Context context) {
		this.context = context;
	}

	/**
	 * 请求进行版本检查
	 */
	public void getCheckUpdate() {
		if (NetCheck.checkNet(context)) {
			new CheckUpdatesTask().execute();
		} else {
			Toast.makeText(context, context.getResources().getString(R.string.unNetMain), Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * 启动应用程序检查版本情况.通过remote json meta.
	 * 
	 * @author skylai
	 * 
	 */
	private class CheckUpdatesTask extends AsyncTaskEx<Void, Void, Version> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showLoadingProgressDialog(context);
		}
		

		protected Version doInBackground(Void... params) {
			try {
				/** 对当前线程进行数据休眠3秒后操作,防止线程锁 */
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				return AndroidServerFactory.getServer().getCheckUpdates(app);
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		public void onPostExecute(Version version) {
			dismissProgressDialog(context);
			if (null != version) {
				int versionCode = 0;
                String verName = version.getVerName();
                MainApplication.getInstance().setVerName(verName);
                new DbConfig(context).setVersion(verName);
				try {
					versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				/** 比对当前版本 如果大于这个版本 那么执行版本更新操作 */
				if (version.getVerCode() > versionCode) {
					UpdateManager mUpdateManager = new UpdateManager(context, version);
					mUpdateManager.checkUpdateInfo();
				} else {
					if (checkVisonlistener != null) {
						checkVisonlistener.printString();
					}

				}
			}
		}
	}

	public interface CheckVison {

		public void printString();
	}
	private CheckVison checkVisonlistener;

    public CheckVison getCheckVisonlistener() {
        return checkVisonlistener;
    }

    public void setCheckVisonlistener(CheckVison checkVisonlistener) {
        this.checkVisonlistener = checkVisonlistener;
    }
}
