package com.nhd.mall.activity;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.entity.ProductDetailEntity;
import com.nhd.mall.entity.Status;
import com.nhd.mall.util.ParseJson;
import com.nhd.mall.widget.ModelActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 14-4-30.
 */
public class ProductWebViewActivity extends Activity {
    private WebView webView;
    private String url;
    private ImageView ivPro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_webview_layout);
        findView();
        initView();
    }

    private void findView() {
        webView = (WebView) findViewById(R.id.webView);
        ivPro = (ImageView)findViewById(R.id.imageView);
        start();
    }

    public void start(){
        ivPro.setVisibility(View.VISIBLE);
        Animation operatingAnim = AnimationUtils.loadAnimation(ProductWebViewActivity.this, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        ivPro.startAnimation(operatingAnim);
    }
    public void stop(){
        ivPro.setVisibility(View.GONE);
        ivPro.clearAnimation();
    }
    private void initView() {
        if(getIntent().getExtras()!=null){
            url = getIntent().getExtras().getString("url");
        }
        loadWebView(url);
    }

    private void loadWebView(String url) {
        // 解决webview的无法全屏显示的问题
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setJavaScriptEnabled(true);
        // 对于连接状态的不同处理
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                stop();
                super.onPageFinished(view, url);

            }
        });
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                if (message != null) {
                    Status status = ParseJson.getStatusAsModel(message);
                    if (status != null) {
                        if (status.getErrorMessage() != null) {
                        } else if ("back".equals(status.getStatus())) {
                            finish();
                        } else {
                            finish();
                        }
                    }
                }
                return super.onJsAlert(view, url, message, result);
            }
        });
        // 加载url
        webView.loadUrl(url);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.clearCache(true);
        }
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!AndroidServerFactory.PRODUCTION_MODEL){
            StatService.onResume(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!AndroidServerFactory.PRODUCTION_MODEL){
            StatService.onPause(this);
        }
    }
}
