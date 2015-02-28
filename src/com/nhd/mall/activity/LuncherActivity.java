package com.nhd.mall.activity;

import java.util.HashMap;
import java.util.Timer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.FinalStoreGet;
import com.nhd.mall.datebase.DbPageFirst;
import com.nhd.mall.datebase.DbStore;
import com.nhd.mall.entity.FinalStoreEntity;
import com.nhd.mall.entity.MainEntity;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.util.ImageLoader;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;
import com.umeng.analytics.MobclickAgent;


/**
 * 类<code>LuncherActivity</code>页面启动页
 * @author vendor
 * @version 2012年11月4日 16:12:13
 * @see     Class
 * @since   JDK1.0
 */
public class LuncherActivity extends ModelActivity  {
	private Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!AndroidServerFactory.PRODUCTION_MODEL){
            if(!AndroidServerFactory.PRODUCTION_MODEL){
                //百度统计市场渠道设置入口 推荐在第一个界面设置 不推荐在配置文件里面设置
                StatService.setAppChannel(this, "nhd", true);
                // 设置每次启动session的间隔失效时间，可以不设置默认30S
                // 测试时，可以使用1秒钟session过期，这样不断的间隔1S启动退出会产生大量日志。
                StatService.setSessionTimeOut(30);
                // 打开崩溃收集
                // setOn也可以在AndroidManifest.xml文件中填写，BaiduMobAd_EXCEPTION_LOG，打开崩溃错误收集，默认是关闭的
                StatService.setOn(this, StatService.EXCEPTION_LOG);
		 		/*
		 		 * 设置启动时日志发送延时的秒数<br/> 单位为秒，大小为0s到30s之间<br/>
		 		 * 注：请在StatService.setSendLogStrategy之前调用，否则设置不起作用
		 		 *
		 		 * 如果设置的是发送策略是启动时发送，那么这个参数就会在发送前检查您设置的这个参数，表示延迟多少秒发送。<br/>
		 		 * 这个参数的设置暂时只支持代码加入， 在您的首个启动的Activity中的onCreate函数中使用就可以。<br/>
		 		 */
                StatService.setLogSenderDelayed(5);
		 		/*
		 		 * 用于设置日志发送策略<br /> 嵌入位置：Activity的onCreate()函数中 <br />
		 		 *
		 		 * 调用方式：StatService.setSendLogStrategy(this,SendStrategyEnum.
		 		 * SET_TIME_INTERVAL, 1, false); 第二个参数可选： SendStrategyEnum.APP_START
		 		 * SendStrategyEnum.ONCE_A_DAY SendStrategyEnum.SET_TIME_INTERVAL 第三个参数：
		 		 * 这个参数在第二个参数选择SendStrategyEnum.SET_TIME_INTERVAL时生效、
		 		 * 取值。为1-24之间的整数,即1<=rtime_interval<=24，以小时为单位 第四个参数：
		 		 * 表示是否仅支持wifi下日志发送，若为true，表示仅在wifi环境下发送日志；若为false，表示可以在任何联网环境下发送日志
		 		 */
                StatService.setSendLogStrategy(this, SendStrategyEnum.APP_START, 1,false);
                // 调试百度统计SDK的Log开关，可以在Eclipse中看到sdk打印的日志，发布时去除调用，或者设置为false
                StatService.setDebugOn(false);
            }
		}
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.luncher_layout);
		hideTitle(false);
		findView();
		initView();
	}
	
	private ImageView ivLuncher;
	private Bitmap bitmap = null;
	private void findView(){
		ivLuncher = (ImageView)findViewById(R.id.ivLuncher);
	}
	private void initView(){
		bitmap = ImageLoader.readBitMap(this, R.drawable.launcher);
		
		if(bitmap == null){
			new startIntent(this, MainActivity.class);
			return;
		}
		ivLuncher.setImageBitmap(bitmap);
		handler = new Handler();
		//延迟启动
		handler.postDelayed(runnable,1500);
	}
	private Timer timer;
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
            Activity act = LuncherActivity.this;
            if(new DbPageFirst(act).getPagePrompt("isFirst")){
                //第一次进入软件要进行弹出门店选择列表选择门店
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("from","launcher");
                intent.setClass(LuncherActivity.this, ShopBranchActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
            else{
                new startIntent(act, MainActivity.class);
                finish();
            }
        }
	};
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if(resultCode==1){
                if(data.getExtras()!=null){
                    if(data.getExtras().getSerializable("store")!=null){
                        StoreEntity entity = (StoreEntity) data.getExtras().getSerializable("store");
                        MainApplication.getInstance().setStore(entity);
                        new DbStore(LuncherActivity.this).update(entity);
                        new startIntent(LuncherActivity.this, GuideActivity.class);
                        finish();
                    }
                }
            }
        }
    }
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		//清除这个回调  防止内存泄漏
		handler.removeCallbacks(runnable);
		bitmap.recycle();
		bitmap = null;
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
