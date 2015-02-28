package com.nhd.mall.widget;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nhd.mall.R;
import com.nhd.mall.datebase.DbPageFirst;
import com.nhd.mall.util.AbstractAsyncActivity;
import com.nhd.mall.util.ImageLoader;

/**********************************************************
 * 内容摘要 ：
 * <p>
 * 视图模板页面,封装了头部，广播以及导航
 * 
 * 作者 ：vendor 创建时间 ：2012-11-18 上午08:21:00
 * 
 * 历史记录 : 日期 : 2012-12-18 上午09:34:00 修改人：vendor 描述 : 添加了导航图片的引用
 *********************************************************** 
 */
public class ModelActivity extends AbstractAsyncActivity {

	/** 显示子页面的容器 */
	private RelativeLayout layoutContent;
	/** 进度条的样式 */
	private ImageView imgProgress;
	/** 引导图片 */
	private ImageView imgGuide;
	/** 引导图片 */
	private Bitmap bitmap = null;
	/** 返回按钮 */
	private Button btnBack;
    private Button  btnInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.model_layout);
		findView();
		initView();
	}
	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layoutContent.addView(View.inflate(this, layoutResID, null), params);
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		if (view == null)
			return;
		layoutContent.addView(view, params);
	}

	@Override
	public void setContentView(View view) {
		setContentView(view, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	/**
	 * 
	 * 函数名称 : findView 功能描述 : 监听以及发现 参数及返回值说明：
	 * 
	 * 修改记录： 日期：2012-12-18 上午09:32:32 修改人：vendor 描述 ：
	 * 
	 */
	private void findView() {
		// 取得页面容器 用于子页面的视图添加
		layoutContent = (RelativeLayout) findViewById(R.id.layoutContent);

		imgProgress = (ImageView) findViewById(R.id.imgProgress);

		btnBack = (Button) findViewById(R.id.btnModelBack);
        btnInfo = (Button)findViewById(R.id.btnInfo);

	}

	/**
	 * 初始化
	 */
	private void initView() {
		// 监听返回键 使得子页面不必重复监听
		btnBack.setOnClickListener(clickListener);

		// 注册拿到加载状态广播接收器
		registerReceiver(broadcastReceiver, new IntentFilter("com.nhd.broadcast.progress"));
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnModelBack:
				finish();
				overridePendingTransition(R.anim.back_right_in,R.anim.back_right_out);
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 重载左边返回键的单击监听
	 * 
	 * @param listener
	 *            日期：2013-3-6 上午9:25:06 修改人：vendor
	 */
	public void setBackButtonListener(OnClickListener listener) {
		this.clickListener = listener;
		btnBack.setOnClickListener(clickListener);
	}

	/**
	 * 设置右边功能键的样式以及监听
	 * 
	 * @param listener
	 * @param btn_style
	 *            日期：2013-3-6 上午9:25:29 修改人：vendor
	 */
	public void setRightButtonListener(OnClickListener listener, int btn_style) {
		Button btnInfo = (Button) findViewById(R.id.btnInfo);
		btnInfo.setOnClickListener(listener);
		btnInfo.setBackgroundResource(btn_style);
		btnInfo.setVisibility(View.VISIBLE);
	}

	/**
	 * 使子页面可以调用到父容器的右边按钮 按钮默认隐藏
	 * 
	 * @param btn_style
	 *            传入背景样式 R.drawable.btn_back
	 * @return button
	 */
	protected Button getButton(int btn_style) {
		btnInfo.setBackgroundResource(btn_style);
		btnInfo.setVisibility(View.VISIBLE);
        findViewById(R.id.linearRight).setVisibility(View.VISIBLE);
		return btnInfo;
	}
	protected Button getButtonBack() {
		
		return btnBack;
	}
	
	protected TextView getTittleButton(){
		//TODO 
		return (TextView) findViewById(R.id.txtHeadline);
	}
	
	

	// 注册广播 ,用于接收网页加载的处理进度
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals("com.nhd.broadcast.progress")) {
				Bundle bundle = intent.getExtras();
				if (bundle.getBoolean("state")) {
					imgProgress.setVisibility(View.VISIBLE);
					Animation operatingAnim = AnimationUtils.loadAnimation(ModelActivity.this, R.anim.rotate);
					LinearInterpolator lin = new LinearInterpolator();
					operatingAnim.setInterpolator(lin);
					imgProgress.startAnimation(operatingAnim);
				} else {
					imgProgress.setVisibility(View.INVISIBLE);
					imgProgress.clearAnimation();
				}
			}
		}
	};

	/**
	 * 动态设置Activity的标题
	 * 
	 * @param title
	 *            标题名称
	 */
	protected void setTitle(String title) {
		TextView textView = (TextView) findViewById(R.id.txtHeadline);
		textView.setVisibility(View.VISIBLE);
		textView.setText(title);
	}
	
	/**
	 * 动态设置Activity的标题
	 * 
	 * @param title
	 *            标题名称
	 */
	public void setTitle(int ref) {
		TextView textView = (TextView) findViewById(R.id.txtHeadline);
		textView.setVisibility(View.VISIBLE);
		textView.setBackgroundResource(ref);
	}

	/**
	 * 隐藏头部
	 * 
	 * @param animation
	 *            日期：2013-3-6 上午9:59:17 修改人：vendor
	 */
	public void hideTitle(Boolean animation) {
		final View view = findViewById(R.id.includeHead);

		if (animation) {
			int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			view.measure(w, h);

			TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0, TranslateAnimation.ABSOLUTE, 0, TranslateAnimation.ABSOLUTE,
					0, TranslateAnimation.ABSOLUTE, -view.getMeasuredHeight());
			mAnimation.setDuration(800);
			mAnimation.setStartOffset(300);
			mAnimation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					view.setVisibility(View.GONE);
				}
			});

			view.startAnimation(mAnimation);
		} else {
			view.setVisibility(View.GONE);
		}
	}

	/**
	 * 显示头部 日期：2013-3-6 上午9:59:17 修改人：vendor
	 */
	public void showTitle() {
		final View view = findViewById(R.id.includeHead);
		view.setVisibility(View.VISIBLE);
	}

	/**
	 * 函数名称 : setGuide 功能描述 : 设置设置页面的引导图片 参数及返回值说明：
	 * 
	 * @param style
	 *            引导图片id
	 * @param key
	 *            列的名称
	 * 
	 *            修改记录： 日期：2012-12-18 上午09:49:27 修改人：vendor 描述 ：
	 * 
	 */
	@SuppressWarnings("deprecation")
	protected void setGuide(int resId, String key) {
		final String _key = key;

		imgGuide = (ImageView) findViewById(R.id.ivMasks);

		try {
			imgGuide.setVisibility(View.VISIBLE);
			bitmap = ImageLoader.readBitMap(this, resId);
			if (bitmap != null) {
				imgGuide.setBackgroundDrawable(new BitmapDrawable(bitmap));
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			imgGuide.setVisibility(View.INVISIBLE);
		}

		imgGuide.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DbPageFirst dbPageFirst = new DbPageFirst(getApplicationContext());
				dbPageFirst.setPagePrompt(_key);

				imgGuide.setVisibility(View.GONE);

				if (bitmap != null && !bitmap.isRecycled()) {
					bitmap.recycle();
					bitmap = null;
				}
			}
		});
	}

	public interface onGuideListener {
		void setGuideListener();
	}

	/**
	 * 
	 * 函数名称 : setNoContent 功能描述 : 显示一个没有任何消息的提示页面 参数及返回值说明：
	 * 
	 * 修改记录： 日期：2012-12-21 上午10:24:31 修改人：vendor 描述 ：
	 * 
	 */
	public void setNoContent() {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ImageView imageView = new ImageView(this);
		imageView.setBackgroundResource(R.drawable.model_null);
		imageView.setLayoutParams(params);

		layoutContent.addView(imageView);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (imgGuide != null && imgGuide.getVisibility() == View.VISIBLE) {
				imgGuide.performClick();
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		this.unregisterReceiver(broadcastReceiver);
	}

}
