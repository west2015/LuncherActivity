package com.nhd.mall.share;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.util.ImageLoader;
import com.nhd.mall.widget.ModelActivity;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.HashMap;

/**
* @ClassName: SinaShareContentActivity
* @Description: TODO(微博微信分享内容编辑页面)
* @author EP epowns@gmail.com
* @date 2013-12-23 上午9:31:53
*/
public class ShareContentActivity extends ModelActivity implements OnClickListener{

	private Button btnSure;
	private EditText edtContent;
	private Button btnBack;
    private ShareUtil shareUtil;
    private int shareType=-1;
    public static final int SHARE_TO_SINA=0;
    public static final int SHARE_TO_WECHAT=1;
    public static final int SHARE_TO_WECHAT_MOMENTS=2;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!AndroidServerFactory.PRODUCTION_MODEL){
			MobclickAgent.onError(this);
		}

        //初始化第三方分享工具
//        ShareSDK.initSDK(getApplicationContext());

		setContentView(R.layout.share_edit_layout);
        shareType = getIntent().getExtras().getInt("shareto");
		init();
		registerListener();
	}

	private void init() {
        shareUtil = new ShareUtil(this);
        shareUtil.setOnShareListener(mOnShareListener);
		edtContent = (EditText) findViewById(R.id.edtContent);
		btnSure = getButton(0);
        switch (shareType){
            case SHARE_TO_SINA:
                setTitle("分享到新浪微博");
                break;
            case SHARE_TO_WECHAT:
                setTitle("分享到微信");
                break;
            case SHARE_TO_WECHAT_MOMENTS:
                setTitle("分享到微信朋友圈");
                break;
        }
        btnSure.setText("分享");
		btnSure.setTextColor(getResources().getColor(R.color.white));
		btnBack = getButtonBack();
        edtContent.setText("我正在使用新华都百货APP,这里优惠多多,好礼多多,更多活动等你!");
	}
	private void registerListener() {

		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				closeSoftInput();
				overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
			}
		});

		btnSure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                switch (shareType){
                    case SHARE_TO_SINA:
                        shareUtil.setPlatform(SinaWeibo.NAME);
                        break;
                    case SHARE_TO_WECHAT:
                        shareUtil.setPlatform(Wechat.NAME);
                        break;
                    case SHARE_TO_WECHAT_MOMENTS:
                        shareUtil.setPlatform(WechatMoments.NAME);
                        break;
                }
                shareUtil.setText(edtContent.getText().toString().trim());
                shareUtil.share();
			}
		});
	}
    //分享回调
    ShareUtil.OnShareListener mOnShareListener = new ShareUtil.OnShareListener() {
        @Override
        public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
            ShareContentActivity.this.finish();
        }
        @Override
        public void onError(Platform platform, int action, Throwable t) {

        }
        @Override
        public void onCancel(Platform platform, int action) {

        }
    };
	@Override
	protected void onResume() {
		super.onResume();
		if (!AndroidServerFactory.PRODUCTION_MODEL){
			MobclickAgent.onResume(this);
		StatService.onResume(this);
		}
	}
	@Override
	public void onClick(View v) {

	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	public void closeSoftInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen && this.getCurrentFocus() != null
				&& this.getCurrentFocus().getWindowToken() != null) {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(this.getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	@Override
	public void onPause() {
		super.onPause();
		if (!AndroidServerFactory.PRODUCTION_MODEL){
			MobclickAgent.onPause(this);
		StatService.onPause(this);
		}
	}
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ShareSDK.stopSDK(getApplicationContext());
    }
}
