package com.nhd.mall.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.ShopBranchAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.StoreListGet;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.entity.StoreListEntity;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.widget.ModelActivity;
import com.nhd.mall.widget.PullDownView;
import com.umeng.analytics.MobclickAgent;

/**
 * 查询分店页面
 * caili
 */
public class ShopBranchActivity extends Activity implements PullDownView.OnPullDownListener,OnAsyncTaskUpdateListener,AdapterView.OnItemClickListener {
    private PullDownView listView;
    private ShopBranchAdapter sba;
    //获取数据
    private StoreListGet slg;
    private StoreListEntity sle;
    private StoreEntity[]store;
    private String from;   //从哪里跳进来的
    //加载进度
    private ImageView ivLoad;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(R.layout.shop_branch_layout);
        find();
    }
    private void find() {
        if(getIntent().getExtras()!=null){
            from = getIntent().getExtras().getString("from");
        }
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = (int) (dm.widthPixels*0.9);
        int height = (int) (dm.heightPixels*0.9);
        findViewById(R.id.shop).setLayoutParams(new FrameLayout.LayoutParams(width, height));

        ivLoad = (ImageView)findViewById(R.id.ivLoad);
        listView = (PullDownView)findViewById(R.id.shopListView);
        sba = new ShopBranchAdapter(this,store);
        listView.setAdapter(sba);
        listView.setOnPullDownListener(this);
        listView.getListView().setFooterDividersEnabled(false);
        listView.enableAutoFetchMore(true, 1);
        listView.setHideHeader();
        listView.setHideFooter();
        listView.setOnItemClickListener(this);
        //获取数据
        if(MainApplication.getInstance().getStores()!=null){
            store = MainApplication.getInstance().getStores();
            sba.update(store);
        }
        else{
            start();
            slg = new StoreListGet(this);
            slg.setListener(this);
        }
    }
    @Override
    public void onRefresh() {
    }
    @Override
    public void onMore() {
        slg.update();
    }
    public void start(){
        findViewById(R.id.rl_progress).setVisibility(View.VISIBLE);
        Animation operatingAnim = AnimationUtils.loadAnimation(ShopBranchActivity.this, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        ivLoad.startAnimation(operatingAnim);
    }
    public void stop(){
        findViewById(R.id.rl_progress).setVisibility(View.GONE);
        ivLoad.clearAnimation();
    }
    @Override
    public void getData(Object obj, String message) {
        listView.RefreshComplete();
        listView.notifyDidMore();
        stop();
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (obj == null)
            return;
        if(obj instanceof StoreListEntity){
            sle = (StoreListEntity) obj;
            store = sle.getStores();
            sba.update(store);
            if(store!=null&&store.length>0){
                MainApplication.getInstance().setStores(store);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
            int position =pos-1;
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
        if(from!=null){
            if(from.equals("form")){     //从立即购买提交订单那边调过来的
                bundle.putSerializable("store",store[position]);
                intent.putExtras(bundle);
                setResult(1,intent);
                finish();
            }
            else if(from.equals("daohan")){
                bundle.putSerializable("store",store[position]);
                intent.putExtras(bundle);
                setResult(1,intent);
                finish();
            }
        else if(from.equals("launcher")){
            bundle.putSerializable("store",store[position]);
            intent.putExtras(bundle);
            setResult(1,intent);
            finish();
        }
        }
        else{
            bundle.putSerializable("store",store[position]);
            intent.putExtras(bundle);
            setResult(1,intent);
            finish();
        }
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sba!=null){
            sba.imageLoader.clearMemory();
            sba.imageLoader=null;
        }
    }
}
