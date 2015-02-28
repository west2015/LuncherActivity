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
import com.nhd.mall.asyncTask.CouponDetailGet;
import com.nhd.mall.entity.Coupon;
import com.nhd.mall.entity.CouponDetailEntity;
import com.nhd.mall.util.ImageLoader;
import com.nhd.mall.util.NetCheck;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.Utils;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;
/**优惠券详情页面
 * Created by caili on 14-7-1.
 */
public class CouponDetailActivity extends ModelActivity implements View.OnClickListener,OnAsyncTaskUpdateListener {

    private Coupon coupon;
    private Integer couponId;
    //系统控件
    private TextView tvName;  //商品名称
    private TextView tvPrice;  //现价
    private TextView tvTotal;  //库存
    private TextView tvOldPrice;
    private TextView tvCount; //要买多少件
    private TextView tvNumber;//编号
    private TextView tvDetail;//使用规则
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

    private ImageLoader imageLoader;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_detail_layout);
        find();
    }
    private void find() {
        if(getIntent().getExtras()!=null){
            couponId =getIntent().getExtras().getInt("couponId");
            sort = getIntent().getExtras().getInt("sort");
        }
        if(sort==COUPON){
            setTitle("优惠券详情");
        }
        else{
            setTitle("储值卡详情");
        }
        imageLoader = new ImageLoader(this);
        imageLoader.setFailBackgroup(R.drawable.default_quan);
        imageLoader.setDefaultBackgroup(R.drawable.default_quan);
        tvName = (TextView)findViewById(R.id.tvCouponName);
        tvPrice = (TextView)findViewById(R.id.tvPrice);
        tvOldPrice = (TextView)findViewById(R.id.tvOldPrice);
        tvTotal = (TextView)findViewById(R.id.countFinal);
        tvCount = (TextView)findViewById(R.id.et_count);
        tvNumber = (TextView)findViewById(R.id.tvNumber);
        tvDetail = (TextView)findViewById(R.id.useDetail);
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
        if(coupon==null)return;
        tvNumber.setText(coupon.getNumber()==null?"":coupon.getNumber());
        tvPrice.setText("￥"+coupon.getPrice()+"");
        tvOldPrice.setText(coupon.getOldPrice()+"");
        tvTotal.setText(coupon.getCirculation()+"件");
        tvDetail.setText(coupon.getDetail()==null?"":coupon.getDetail());
        imageLoader.setBackgroup(coupon.getThumb(),ivImg);
        //初始化优惠券控件
        switch (coupon.getType()){
            case 1:
                name = "新华都"+coupon.getMoney()+"元优惠券";
                tvName.setText(name);
                tvQuanName.setText(name);
                break;
            case 2:
                name = "新华都"+coupon.getMoney()+"折优惠券";
                tvName.setText(name);
                tvQuanName.setText(name);
                break;
            case 3:
                name = coupon.getName();
                tvName.setText(name==null?"":name);
                tvQuanName.setText(name==null?"":name);
                break;
            default:
                break;
        }
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
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_count_add:  //增加购买量
                int buyCount = Integer.parseInt(tvCount.getText().toString());
                if(coupon==null)return;
                if(coupon.getCirculation()!=null&&coupon.getCirculation()>=0){
                    if(buyCount>=coupon.getCirculation()){
                        Toast.makeText(CouponDetailActivity.this, "库存不够", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        tvCount.setText(String.valueOf(buyCount + 1));
                    }
                }
                else{
                    Toast.makeText(CouponDetailActivity.this, "暂无库存", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_count_minus:   //减少购买量
                int count = Integer.parseInt(tvCount.getText().toString());
                if(count==0)return;
                tvCount.setText(String.valueOf(count-1));
                break;
            case R.id.ll_buy_now:   //立即购买
                if(coupon==null)return;
                int nowCount = Integer.parseInt(tvCount.getText().toString());
                if(nowCount==0){
                    Toast.makeText(CouponDetailActivity.this,"请选择购买量",Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    Bundle bundleForm = new Bundle();
                    bundleForm.putInt("count", nowCount);
                    bundleForm.putString("name", name);
                    bundleForm.putSerializable("product",coupon);
                    new startIntent(CouponDetailActivity.this,CouponFormActivity.class,bundleForm);
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
