package com.nhd.mall.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.nhd.mall.entity.Status;

/************************************************************
 * 内容摘要 ：
 * <p>
 * WEBVIEW 加载页面
 * 
 * 作者 ：欧祥斌 创建时间 ：2013-6-27 上午11:23:11
 ************************************************************/
public class WebviewLoad implements DownloadListener{

	private Context context;

	public WebviewLoad(Context context) {
		super();
		this.context = context;
	}

	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	public void loadWebView(String url, WebView webView) {
		// 解决webview的无法全屏显示的问题
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		// 运行运行JavaScript脚本
		webView.getSettings().setJavaScriptEnabled(true);
		// 设置支持缩放
		webView.getSettings().setBuiltInZoomControls(true);
		// 屏幕自适应网页
//		webView.getSettings().setDefaultZoom(ZoomDensity.FAR);
		// 监听脚本
		webView.addJavascriptInterface(new JavaScriptInterface(), "ImageSource");
		//监听下载
		webView.setDownloadListener(this);
		// 对于连接状态的不同处理
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				if (url.endsWith(".mp4") || url.endsWith(".m3u8")) {
					// play(address);

				} else {
					view.loadUrl(url);
				}
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				Intent intent=new Intent("com.huake.broadcast.webview");
				intent.putExtra("state", true);
				context.sendBroadcast(intent);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				Intent intent=new Intent("com.huake.broadcast.webview");
				intent.putExtra("state", false);
				context.sendBroadcast(intent);
			}
		});

		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				if (message != null) {

					Status status = ParseJson.getStatusAsModel(message);
					if (status != null) {
						if (status.getErrorMessage() != null) {
							Toast.makeText(context, status.getErrorMessage(), Toast.LENGTH_SHORT).show();
						} else if ("back".equals(status.getStatus())) {
							((Activity) context).finish();
						} else {
							Toast.makeText(context, status.getStatus(), Toast.LENGTH_SHORT).show();
							((Activity) context).finish();
						}
					}
				}
				return super.onJsAlert(view, url, message, result);
			}
		});

		webView.loadUrl(url);
	}

	public class JavaScriptInterface {

		String[] sources = null;

		/**
		 * 点击webview中的图片时候会触发
		 * 
		 * @param source
		 *            图片的url
		 */
		public void showURL(String source) {
			Bundle bundle = new Bundle();
			bundle.putString("url", source);
			// new startIntent(context.getApplicationContext(),
			// ImageViewActivity.class, bundle);
		}

	}

	@Override
	public void onDownloadStart(String url, String userAgent,
			String contentDisposition, String mimetype, long contentLength) {
		// TODO Auto-generated method stub
		Uri uri=Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
        context.startActivity(intent); 
	}
	
	

}
