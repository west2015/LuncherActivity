package com.nhd.mall.activity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
/**
 * 首页点击优惠券进来的优惠券列表页面
 * Created by Mcginn on 15-1-28.
 */
public class CouponListActivity extends ModelActivity implements View.OnClickListener,OnAsyncTaskUpdateListener,PullDownView.OnPullDownListener,AdapterView.OnItemClickListener {

	private final int CARD = 1;
	private final int COUPON = 2;
	private final int[] colorId = {R.color.gray,R.color.red};
    private PullDownView listView;
    private CouponBuyAdapter buyAdapter;
    private CouponListEntity lstCoupon;
    private Button btnBuyCoupon;
    private Button btnGetCoupon;
    private BuyQuanGet get;
    private Coupon[] coupons;
    private Integer storeId;
    private Integer pageNum=1;
    private int type;
    private boolean flag;
    //无结果显示界面
    private TextView tvNoContent;
    private Button btnNoContent;
    private ImageView ivNoContent;
    private ImageView ivLoad;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_list_layout);
        find();
    }
    @SuppressLint("ResourceAsColor")
	private void find() {
        setTitle("优惠券");
        type = CARD;
        flag = true;
        listView = (PullDownView)findViewById(R.id.lvCoupon);

        if(MainApplication.getInstance().getStore() != null){
            storeId = MainApplication.getInstance().getStore().getId();
            Log.e("CouponListActivity", "storeId = " + storeId);
        }
        btnBuyCoupon = (Button)findViewById(R.id.btnBuyCoupon);
        btnGetCoupon = (Button)findViewById(R.id.btnGetCoupon);
        tvNoContent = (TextView)findViewById(R.id.tvNoContent);
        ivNoContent = (ImageView)findViewById(R.id.ivNoContent);
        btnNoContent = (Button)findViewById(R.id.btnNoContent);
        ivLoad = (ImageView)findViewById(R.id.ivLoad);
        btnNoContent.setOnClickListener(this);
        btnBuyCoupon.setOnClickListener(this);
        btnGetCoupon.setOnClickListener(this);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int displayWidth = dm.widthPixels;
        buyAdapter = new CouponBuyAdapter(this,coupons,displayWidth,flag);
        listView.setAdapter(buyAdapter);
        listView.setOnPullDownListener(this);
        listView.setOnItemClickListener(this);
        listView.getListView().setFooterDividersEnabled(false);
        listView.enableAutoFetchMore(true, 1);
        listView.getListView().setDividerHeight(0);
        if(NetCheck.checkNet(this)){
        	get = new BuyQuanGet(this,storeId,pageNum,type,flag);
            get.setListener(this);
            start();
        }
        else{
            Toast.makeText(this,"网络不给力",Toast.LENGTH_SHORT).show();
        }
    }
    public void start(){
        ivLoad.setVisibility(View.VISIBLE);
        Animation operatingAnim = AnimationUtils.loadAnimation(CouponListActivity.this, R.anim.rotate);
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
            if(coupons == null || coupons.length <= 0){
                haveNoContent(true);
            }
            else{
                haveNoContent(false);
            }
            buyAdapter.update(coupons,flag);
        }
    }
    private void haveNoContent(boolean boo) {
    	if(boo){
        	findViewById(R.id.rl_have_no_content).setVisibility(View.VISIBLE);
        	tvNoContent.setText("暂无优惠券");
	        ivNoContent.setBackgroundResource(R.drawable.have_no_coupon);
        }
        else{
        	findViewById(R.id.rl_have_no_content).setVisibility(View.GONE);
        }
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnNoContent:
                Utils.backToMain(CouponListActivity.this);
                break;
            case R.id.btnBuyCoupon:
            	type = CARD;
            	flag = true;
            	btnBuyCoupon.setBackgroundColor(CouponListActivity.this.getResources().getColor(colorId[1]));
            	btnGetCoupon.setBackgroundColor(CouponListActivity.this.getResources().getColor(colorId[0]));
            	onRefresh();
            	break;
            case R.id.btnGetCoupon:
            	type = COUPON;
            	flag = false;
            	btnBuyCoupon.setBackgroundColor(CouponListActivity.this.getResources().getColor(colorId[0]));
            	btnGetCoupon.setBackgroundColor(CouponListActivity.this.getResources().getColor(colorId[1]));
            	onRefresh();
            	break;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(coupons == null || coupons.length <= 0) return;
        Bundle bundle = new Bundle();
        bundle.putInt("couponId",coupons[i-1].getId());
        bundle.putInt("sort",type);
        if(flag){
        	new startIntent(CouponListActivity.this,CouponDetailBuyActivity.class,bundle);
        }
        else{
        	new startIntent(CouponListActivity.this,CouponDetailGetActivity.class,bundle);
        }
    }
    @Override
    public void onRefresh() {
        pageNum = 1;
        get.update(storeId, pageNum, type, flag);
    }
    @Override
    public void onMore() {
        pageNum++;
        get.getMore(lstCoupon, storeId, pageNum, type, flag);
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
        if(buyAdapter != null){
            buyAdapter.imageLoader.clearMemory();
        }
    }
}