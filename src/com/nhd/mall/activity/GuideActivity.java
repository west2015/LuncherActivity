package com.nhd.mall.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.util.ImageLoader;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;
import com.nhd.mall.widget.PageControl;
import com.umeng.analytics.MobclickAgent;


/**********************************************************
 * 内容摘要 ：
 * <p>
 * 帮助引导页面
 * 
 * 作者 ：vendor 创建时间 ：2012-12-10 上午09:09:12
 * 
 * 历史记录 : 日期 : 2012-12-10 上午09:09:12 修改人：vendor 描述 :
 *********************************************************** 
 */
public class GuideActivity extends ModelActivity implements OnClickListener {
	private ImageView imGuide_One,imGuide_Two,imGuide_Three,imGuide_Four;
	private Bitmap bitmap_One,bitmap_Two,bitmap_Three,bitmap_Four;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.guide_layout);
		hideTitle(false);
		findView();
		initView();
	}
	
	PageControl pageControl;

	private void findView() {
		imGuide_Four = (ImageView) findViewById(R.id.imGuide_Four);
		imGuide_Two = (ImageView) findViewById(R.id.imGuide_Two);
		imGuide_One = (ImageView) findViewById(R.id.imGuide_One);
		imGuide_Three = (ImageView) findViewById(R.id.imGuide_Three);
		pageControl = (PageControl)findViewById(R.id.pageControl);
		
		Button btnStart = (Button) findViewById(R.id.btnOk);
		btnStart.setOnClickListener(this);
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		try{
			bitmap_One = ImageLoader.readBitMap(this, R.drawable.guide_1);
			bitmap_Two = ImageLoader.readBitMap(this, R.drawable.guide_2);
			bitmap_Three = ImageLoader.readBitMap(this, R.drawable.guide_3);
			bitmap_Four = ImageLoader.readBitMap(this, R.drawable.guide_4);
			
			imGuide_One.setBackgroundDrawable(new BitmapDrawable(bitmap_One));
			imGuide_Two.setBackgroundDrawable(new BitmapDrawable(bitmap_Two));
			imGuide_Three.setBackgroundDrawable(new BitmapDrawable(bitmap_Three));
			imGuide_Four.setBackgroundDrawable(new BitmapDrawable(bitmap_Four));
		}catch (NullPointerException e) {
			start();
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		start();
		finish();
	}

	private void start() {
	     new startIntent(this, MainActivity.class);
		finish();
	}
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		bitmap_One.recycle();
		bitmap_One = null;
		
		bitmap_Two.recycle();
		bitmap_Two = null;
		
		bitmap_Three.recycle();
		bitmap_Three = null;
		
		bitmap_Four.recycle();
		bitmap_Four = null;
		
		pageControl.removeAllViews();
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
