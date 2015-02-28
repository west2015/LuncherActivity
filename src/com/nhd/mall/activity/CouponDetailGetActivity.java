package com.nhd.mall.activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.CouponDetailGet;
import com.nhd.mall.asyncTask.CouponGet;
import com.nhd.mall.entity.Coupon;
import com.nhd.mall.entity.CouponDetailEntity;
import com.nhd.mall.entity.QuanStatus;
import com.nhd.mall.entity.Status;
import com.nhd.mall.util.ImageLoader;
import com.nhd.mall.util.NetCheck;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.Utils;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;
import com.umeng.common.Log;
/**
 * 优惠券详情页面
 * Created by caili on 14-7-1.
 * Revised by Mcginn on 15-1-29
 */
public class CouponDetailGetActivity extends ModelActivity implements View.OnClickListener,OnAsyncTaskUpdateListener {

	private final int limitFalse = 1;
	private final int limitTrue = 2;
    private Coupon coupon;
    private Integer couponId;
    //系统控件
    private TextView tvName;  	//商品名称
    private TextView tvTotal;  	//库存
    private TextView tvOldPrice;
    private TextView tvCount; 	//购买数量
    private TextView tvNumber;	//编号
    private TextView tvDetail;	//使用规则
    private TextView tvSueDate; //生效日期
    private TextView tvDueDate; //截止日期
    private Button btnAdd;
    private Button btnminus;
    //优惠券
    private TextView tvQuanName;
    private ImageView ivImg;
    private String name;
    private RelativeLayout detail_head;
    private int  sort;
    private final int COUPON =0;
    private final int CARD=1;
    private CouponGet get;

    private ImageLoader imageLoader;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_detail_get_layout);
        find();
    }
    private void find() {
        if(getIntent().getExtras()!=null){
            couponId = getIntent().getExtras().getInt("couponId");
            sort = getIntent().getExtras().getInt("sort");
        }
        setTitle("优惠券详情");
        imageLoader = new ImageLoader(this);
        imageLoader.setFailBackgroup(R.drawable.default_quan);
        imageLoader.setDefaultBackgroup(R.drawable.default_quan);
        tvName = (TextView)findViewById(R.id.tvCouponName);
        tvOldPrice = (TextView)findViewById(R.id.tvOldPrice);
        tvTotal = (TextView)findViewById(R.id.countFinal);
        tvCount = (TextView)findViewById(R.id.et_count);
        tvNumber = (TextView)findViewById(R.id.tvNumber);
        tvDetail = (TextView)findViewById(R.id.useDetail);
        tvSueDate = (TextView)findViewById(R.id.tvSueDateGet);
        tvDueDate = (TextView)findViewById(R.id.tvDueDateGet);
        btnAdd = (Button)findViewById(R.id.btn_count_add);
        btnminus = (Button)findViewById(R.id.btn_count_minus);
        //优惠券相关控件初始化
        detail_head=(RelativeLayout)findViewById(R.id.detail_head);
        tvQuanName = (TextView)findViewById(R.id.detail_couponName);
        ivImg = (ImageView)findViewById(R.id.detail_couponImg);
        findViewById(R.id.ll_buy_now).setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnminus.setOnClickListener(this);
        if(NetCheck.checkNet(this)){
            new CouponDetailGet(this,couponId).setListener(this);
        }
        initSize();
    }
    private void initSize() {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int displayWidth = dm.widthPixels;
        int width = displayWidth - Utils.dip2px(this,24);//减去两边的留白
        int height=width/2;
        detail_head.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
    }
    private void initDetail() {
        if(coupon == null)return;
        tvNumber.setText(coupon.getNumber()==null?"":coupon.getNumber());
        tvOldPrice.setText(coupon.getOldPrice()+"");
        tvTotal.setText(coupon.getCirculation()+"件");
        tvDetail.setText(coupon.getDetail()==null?"":coupon.getDetail());
        tvSueDate.setText("生效日期: " + coupon.getIssueDate());
        tvDueDate.setText("截止日期: " + coupon.getDueDate());
        imageLoader.setBackgroup(coupon.getThumb(),ivImg);
        if(coupon.getName()!=null && !coupon.getName().equals("")){
        	name = coupon.getName();
        }
        else{
        	name = "新华都优惠券";
        }
    	tvName.setText(name);
    	tvQuanName.setText(name);
    }
    @Override
    public void getData(Object obj, String message) {
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (obj == null)
            return;
        if(obj instanceof CouponDetailEntity){
            CouponDetailEntity entity = (CouponDetailEntity) obj;
            coupon = entity.getCoupon();
            initDetail();
        }
        if(obj instanceof QuanStatus){
        	QuanStatus status = (QuanStatus) obj;
        	if(status.getSuccess().equals("true")){
        		Toast.makeText(this, "领取成功!", Toast.LENGTH_SHORT).show();
        	}
        	else{
        		if(status.getMessage().contains("取0张")){
        			Toast.makeText(this, "对不起，您的领取限额已经使用完！", Toast.LENGTH_SHORT).show();
        		}
        		else{
        			Toast.makeText(this, status.getMessage(), Toast.LENGTH_SHORT).show();
        		}
        	}
        }
        else{
        	Log.e("CouponDetailGetActivity", "Nothing");
        }
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_count_add:  //增加购买量
                int buyCount = Integer.parseInt(tvCount.getText().toString());
                if(coupon==null)return;
                if(coupon.getCirculation()!=null && coupon.getCirculation()>=0){
                	if(coupon.getIsLimit()==limitTrue && buyCount>=coupon.getLimitNum()){
                        Toast.makeText(CouponDetailGetActivity.this, 
                        		"对不起，每个账号限领"+coupon.getLimitNum()+"张！", Toast.LENGTH_SHORT).show();
                        return;
                	}
                	if(buyCount>=coupon.getCirculation()){
                        Toast.makeText(CouponDetailGetActivity.this, "库存不够", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    tvCount.setText(String.valueOf(buyCount + 1));
                }
                else{
                    Toast.makeText(CouponDetailGetActivity.this, "暂无库存", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_count_minus:   //减少购买量
                int count = Integer.parseInt(tvCount.getText().toString());
                if(count==0)return;
                tvCount.setText(String.valueOf(count-1));
                break;
            case R.id.ll_buy_now:   //立即领取
                if(coupon==null)return;
                int nowCount = Integer.parseInt(tvCount.getText().toString());
                if(nowCount==0){
                    Toast.makeText(CouponDetailGetActivity.this,"请选择数量",Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                	if(MainApplication.getInstance().getMember()==null){
                        new startIntent(CouponDetailGetActivity.this,LoginActivity.class);
                        return;
                    }
                	get = new CouponGet(CouponDetailGetActivity.this,
                			MainApplication.getInstance().getMember().getId(),
                			coupon.getId(),nowCount);
                	get.setListener(CouponDetailGetActivity.this);
//                	get = new CouponGet(CouponDetailGetActivity.this, , buyCount);
//                    Bundle bundleForm = new Bundle();
//                    bundleForm.putInt("count", nowCount);
//                    bundleForm.putString("name", name);
//                    bundleForm.putSerializable("product",coupon);
//                    new startIntent(CouponDetailGetActivity.this,CouponFormActivity.class,bundleForm);
                }
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
        if(imageLoader!=null){
            imageLoader.clearMemory();
        }
    }
}
