package com.nhd.mall.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.nhd.mall.R;
import com.nhd.mall.datebase.DbConfig;
import com.nhd.mall.entity.Version;

/**
 * 类<code>UpdateManager</code>应用程序安装及覆盖工具类
 * 
 * @author vendor
 * @version 2012年11月5日 15:42:35
 * @see     Class
 * @since   JDK1.0
 *
 */
public class UpdateManager  {

	private Context context;
	
	/** 服务器最新版本 */
	private Version version;
	
	/** 下载的文件保存的位置 */
	private String savePath ;

	/** 下载的文件的名称 */
	private String saveFileName ;

	
	private Dialog noticeDialog;

	private Dialog downloadDialog;

	/** 进度条与通知ui刷新的handler和msg常量 */
	private ProgressBar mProgress;

	/** 更新进度标识 用于进度更新 */
	private static final int DOWN_UPDATE = 1;

	/** 标识更新已完成 提示用户安装 */
	private static final int DOWN_OVER = 2;

	/** 进度百分数  0-100 */
	private int progress;
	
	/** 显示进度的文字 */
	private TextView txtProcess;

	/** 下载异步线程 */
	private Thread downLoadThread;

	/** 更新停止标识 */
	private boolean interceptFlag = false;

	/** 对于更新进度的任务处理 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				txtProcess.setText(progress+"%");
				break;
			case DOWN_OVER:
				downloadDialog.dismiss();
				installApk();
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * 类构造器
	 * 
	 * @param context
	 * @param version  当前服务器最新版本
	 */
	public UpdateManager(Context context,Version version) {
		this.context = context;
		this.version = version;
		File file;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			file = Environment.getExternalStorageDirectory();//获取跟目录
			savePath=file.getAbsolutePath()+"/download/";
		}else{
			savePath="/data/";
		}
		
		saveFileName=savePath+"app.apk";
	}

	/**
	 * 弹出友好提示,是否下载更新
	 */
	public void checkUpdateInfo() {
		final DbConfig dbConfig = new DbConfig(context);
		if(dbConfig.getVersionIgnoreCode() == version.getVerCode())
			return;
		
		Builder builder = new Builder(context);
		builder.setMessage(context.getString(R.string.find_update)+version.getVerName()+context.getString(R.string.isUpdate));
		builder.setPositiveButton(context.getString(R.string.yes), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNeutralButton(context.getString(R.string.Cancle), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(context.getString(R.string.ignore), new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//点击忽略按钮忽略本次版本
				dbConfig.setVersionIgnoreCode(version.getVerCode());
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}
	public void DownLoadApp() {

		Builder builder = new Builder(context);
		builder.setMessage("是否下载?");
		builder.setPositiveButton(context.getString(R.string.yes), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNeutralButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		noticeDialog = builder.create();
		noticeDialog.show();
	}

	/**
	 * 进行友好提示下载进度
	 */
	private void showDownloadDialog() {
		Builder builder = new Builder(context);
		builder.setTitle(context.getString(R.string.update_ing));
		
		RelativeLayout layout = new RelativeLayout(context);
		
		mProgress = new ProgressBar(context);
		int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, context.getResources().getDisplayMetrics());
		LayoutParams processLp = new LayoutParams(width, width);  
		processLp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		mProgress.setLayoutParams(processLp);
		
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
	    lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		txtProcess = new TextView(context);
		txtProcess.setLayoutParams(lp);
		
		layout.addView(mProgress);
		layout.addView(txtProcess);
		
		builder.setView(layout);
		builder.setNegativeButton(context.getString(R.string.Cancle), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.show();
		downloadApk();
	}

	/**
	 * 异步进行文件下载   通过流
	 */
	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				URL url = new URL(version.getUrl());

				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);

				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成通知安装
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载.
				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

	/**
	 * 下载apk
	 * 
	 */
	private void downloadApk() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/**
	 * 安装apk
	 * 
	 */
	private void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		context.startActivity(i);

	}
}
