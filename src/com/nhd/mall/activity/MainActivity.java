package com.nhd.mall.activity;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.datebase.DbConfig;
import com.nhd.mall.datebase.DbPageFirst;
import com.nhd.mall.entity.Member;
import com.nhd.mall.push.BaidupushUtils;
import com.nhd.mall.push.PushMsgDataHelper;
import com.nhd.mall.util.CommonUtils;
import com.nhd.mall.util.Shortcut;
import com.nhd.mall.util.startIntent;
import com.umeng.analytics.MobclickAgent;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;

/**
 * 内容摘要 ：
 * <p>
 * 新华都首页
 * 作者 ：caili 
 */
@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements OnClickListener,MainApplication.OnLoginChangeListener {

	private TabHost mTabHost;
	private View shopCar;
    private Dialog quitDialog;
    private View vMemberCenter;
    private TextView tvPoint;
    private PushMsgDataHelper mDataHelper;
    public static int CURRENT_MAIN=0x0;
    public static String TAG_CURRENT="main";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findView();
		initView();
        //自动登录
        if(new DbConfig(getApplicationContext()).isAutoLogin()){
            MainApplication.getInstance().login(this);
            MainApplication.getInstance().setLoginChangeListener(this);
        }else{
            MainApplication.getInstance().setMember(null);
            Platform[] platforms = ShareSDK.getPlatformList(getApplicationContext());
            for (int i = 0; i<platforms.length;i++){
                platforms[i].removeAccount();
            }
        }
        if(new DbConfig(this).getNotification()){
            // 开启百度云推送
            BaidupushUtils.init(this);
        }
        //解析scheme跳转
        startIntentByScheme();
	}

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if(getIntent()!=null && getIntent().getExtras()!=null &&
                getIntent().getExtras().getInt("TAG_CURRENT")==CURRENT_MAIN){
            if(mTabHost!=null){
                mTabHost.setCurrentTab(0);
            }
        }
        //解析scheme跳转
        startIntentByScheme();
    }
    private void startIntentByScheme(){
        Intent intent = getIntent();
        if(intent==null) return;
        Uri uri = intent.getData();
        if(uri!=null && uri.getHost()!=null){
            if(uri.getHost().equals("product")){//跳转到商品详情
                Bundle bundleProduct = new Bundle();
                bundleProduct.putInt("productId",Integer.valueOf(uri.getQueryParameter("id")));
                new startIntent(MainActivity.this,GoodsDetailActivity.class,bundleProduct);
            }else if(uri.getHost().equals("brand")){//跳转到品牌
                Bundle bundle = new Bundle();
                bundle.putInt("brandId",Integer.valueOf(uri.getQueryParameter("id")));
                bundle.putString("sort","brand");
                bundle.putString("name",uri.getQueryParameter("name"));
                bundle.putString("search_for","normal");
                new startIntent(MainActivity.this,ThemeGoodsActivity.class,bundle);
            }
        }
    }
    
    @Override
    public void onLoginChange(Member member) {
        MainApplication.getInstance().getBeanCountAsyn();
    }
    
    private void findView() {
	    mTabHost = getTabHost();
	    shopCar = LayoutInflater.from(this).inflate(R.layout.tab_item_shopcar,null);
		mTabHost.addTab(mTabHost.newTabSpec("tab_1").setIndicator(composeLayout(R.drawable.mall_main_btn_click)).setContent(new Intent(this, MallMainActivity.class)));
		
		mTabHost.addTab(mTabHost.newTabSpec("tab_2").setIndicator(composeLayout(R.drawable.mall_shop_btn_click)).setContent(new Intent(this, MallShopActivity.class)));
		
		mTabHost.addTab(mTabHost.newTabSpec("tab_3").setIndicator(shopCar).setContent(new Intent(this, MallShopCarActivity.class)));
		
		mTabHost.addTab(mTabHost.newTabSpec("tab_4").setIndicator(vMemberCenter=composeLayout(R.drawable.mall_member_btn_click)).setContent(new Intent(this, MallMemberActivity.class)));
		
		mTabHost.addTab(mTabHost.newTabSpec("tab_5").setIndicator(composeLayout(R.drawable.mall_setting_btn_click)).setContent(new Intent(this, MallSettingActivity.class)));
		
		mTabHost.setCurrentTab(0);

        //设置红点
        tvPoint = (TextView) vMemberCenter.findViewById(R.id.tvPoint);
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                if(s.equals("tab_4")){
                    tvPoint.setVisibility(View.GONE);
                }
            }
        });
	}
	private void initView() {
		DbPageFirst pageFirst = new DbPageFirst(this);
		// 创建快捷方式
		if (pageFirst.getPagePrompt("isFirst")) {
			new Shortcut(this).addShortcut();
			pageFirst.setPagePrompt("isFirst", false);
		}
        // 注册拿到红点状态广播接收器
        registerReceiver(broadcastReceiver, new IntentFilter("com.nhd.broadcast.point"));
        //初始化红点数据
        mDataHelper = new PushMsgDataHelper(MainApplication.getContext(),null);
        CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Integer>() {
            @Override
            protected Integer doInBackground(Object... objects) {
                return mDataHelper.getUnreadMsgCount();
            }
            @Override
            protected void onPostExecute(Integer o) {
                super.onPostExecute(o);
                if(tvPoint==null) return;
                if(o!=null && o!=0){
                    tvPoint.setText(o>10?"N":o+"");
                    tvPoint.setVisibility(View.VISIBLE);
                }else{
                    tvPoint.setVisibility(View.GONE);
                }
            }
        });
	}
	public View composeLayout(int id) {
		View view = LayoutInflater.from(this).inflate(R.layout.tabhost_item, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.imgHostItem);
		imageView.setBackgroundResource(id);
		return view;
	}

	@Override
	public void onClick(View v) {
	}
    //首页点击退出弹出是否退出框
    private void quitDialog() {
        View view = null;
        view = LayoutInflater.from(MainActivity.this).inflate(R.layout.quit_layout, null);
        view.findViewById(R.id.btnSure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });
        view.findViewById(R.id.btnQuit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitDialog.dismiss();
            }
        });
        quitDialog = new Dialog(MainActivity.this, R.style.planDialog);
        quitDialog.setCancelable(true);
        quitDialog.setContentView(view);
        quitDialog.show();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
      {
          if (keyCode == KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0 )
              {
                  quitDialog();
               }
            return false;
    }
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            quitDialog();
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    // 注册广播 ,用于处理红点显示
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("com.nhd.broadcast.point")) {
                if(tvPoint==null) return;
                if(mTabHost.getCurrentTab()==3) return;
                Bundle bundle = intent.getExtras();
                if (bundle.getBoolean("state")) {
                    tvPoint.setText(bundle.getInt("num")+"");
                    tvPoint.setVisibility(View.VISIBLE);
                } else {
                    tvPoint.setVisibility(View.GONE);
                }
            }
        }
    };
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
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.exit(0);
	}
	
}
