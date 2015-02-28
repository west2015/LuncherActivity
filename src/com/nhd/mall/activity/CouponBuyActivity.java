package com.nhd.mall.activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.CouponBuyAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.BuyQuanGet;
import com.nhd.mall.entity.Coupon;
import com.nhd.mall.entity.CouponListEntity;
import com.nhd.mall.util.NetCheck;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.Utils;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;
import com.nhd.mall.widget.PullDownView;
/**首页点击优惠券进来的优惠券购买页面
 * Created by Administrator on 14-6-25.
 */
public class CouponBuyActivity extends ModelActivity implements View.OnClickListener,OnAsyncTaskUpdateListener,PullDownView.OnPullDownListener,AdapterView.OnItemClickListener {

    private PullDownView listView;
    private CouponBuyAdapter buyAdapter;
    private CouponListEntity lstCoupon;
    private Coupon[]coupons;
    private Integer storeId;
    private BuyQuanGet get;
    private Integer pageNum=1;
    private int  sort;
    private final int COUPON =0;
    private final int CARD=1;
    //没东西查到的时候显示的控件
    private TextView tvNoContent;
    private Button btnNoContent;
    private ImageView ivNoContent;
    private final int GETCARD =3;
    private final int GETCOUPON=4;
    private ImageView ivLoad;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_layout);
        find();
    }
    private void find() {
        listView = (PullDownView)findViewById(R.id.lvCoupon);

        if(MainApplication.getInstance().getStore()!=null){
            storeId = MainApplication.getInstance().getStore().getId();
        }
        if(getIntent().getExtras()!=null){
            sort = getIntent().getExtras().getInt("sort");
        }
        ivLoad=  (ImageView)findViewById(R.id.ivLoad);
        tvNoContent = (TextView)findViewById(R.id.tvNoContent);
        ivNoContent = (ImageView)findViewById(R.id.ivNoContent);
        btnNoContent = (Button)findViewById(R.id.btnNoContent);
        btnNoContent.setOnClickListener(this);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int displayWidth = dm.widthPixels;
        buyAdapter = new CouponBuyAdapter(this,coupons,displayWidth);
        listView.setAdapter(buyAdapter);
        listView.setOnPullDownListener(this);
        listView.setOnItemClickListener(this);
        listView.getListView().setFooterDividersEnabled(false);
        listView.enableAutoFetchMore(true, 1);
        listView.getListView().setDividerHeight(0);
        if(sort==CARD){
            setTitle("储值卡");
        }
        else{
            setTitle("优惠券");
        }
        if(NetCheck.checkNet(this)){
            if(sort==CARD){
                get = new BuyQuanGet(this,storeId,pageNum,GETCARD,true);
                get.setListener(this);
            }
            else{
                get = new BuyQuanGet(this,storeId,pageNum,GETCOUPON,true);
                get.setListener(this);
            }
            start();
        }
        else{
            Toast.makeText(this,"网络不给力",Toast.LENGTH_SHORT).show();
        }
    }
    public void start(){
        ivLoad.setVisibility(View.VISIBLE);
        Animation operatingAnim = AnimationUtils.loadAnimation(CouponBuyActivity.this, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        ivLoad.startAnimation(operatingAnim);
    }
    public void stop(){
        ivLoad.setVisibility(View.GONE);
        ivLoad.clearAnimation();
    }
    @Override
    public void getData(Object obj, String message) {
        listView.RefreshComplete();
        listView.notifyDidMore();
        stop();
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (obj == null){
            haveNoContent(true);
            return;
        }
        if(obj instanceof CouponListEntity){
            lstCoupon = (CouponListEntity) obj;
            coupons = lstCoupon.getCoupons();
            if(coupons==null||coupons.length<=0){
                haveNoContent(true);
            }
            else{
                haveNoContent(false);
            }
            buyAdapter.update(coupons);
        }
    }
    private void haveNoContent(boolean boo) {
        if(boo){
            findViewById(R.id.rl_have_no_content).setVisibility(View.VISIBLE);
            if(sort==CARD){
                tvNoContent.setText("暂无储值卡");
                ivNoContent.setBackgroundResource(R.drawable.have_no_card);
            }
            else{
                tvNoContent.setText("暂无优惠券");
                ivNoContent.setBackgroundResource(R.drawable.have_no_coupon);
            }
        }
        else{
        findViewById(R.id.rl_have_no_content).setVisibility(View.GONE);
        }
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnNoContent:
                Utils.backToMain(CouponBuyActivity.this);
                break;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(coupons==null||coupons.length<=0)return;
        Bundle bundle = new Bundle();
        bundle.putInt("couponId",coupons[i-1].getId());
        bundle.putInt("sort",sort);
        new startIntent(CouponBuyActivity.this,CouponDetailActivity.class,bundle);
    }
    @Override
    public void onRefresh() {
        pageNum=1;
        if(sort==CARD){
            get.update(storeId, pageNum, GETCARD,true);
        }
        else{
            get.update(storeId, pageNum, GETCOUPON,true);
        }
    }
    @Override
    public void onMore() {
        pageNum++;
        if(sort==CARD){
            get.getMore(lstCoupon, storeId, pageNum, GETCARD,true);
        }
        else{
            get.getMore(lstCoupon, storeId, pageNum, GETCOUPON,true);
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
        if(buyAdapter!=null){
            buyAdapter.imageLoader.clearMemory();
        }
    }
}
