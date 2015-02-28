package com.nhd.mall.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.OrderFormPost;
import com.nhd.mall.datebase.DbAddress;
import com.nhd.mall.entity.Coupon;
import com.nhd.mall.entity.CouponOrder;
import com.nhd.mall.entity.CustomerAddressEntity;
import com.nhd.mall.entity.FormEntity;
import com.nhd.mall.entity.FormStoreEntity;
import com.nhd.mall.entity.Member;
import com.nhd.mall.entity.OrderFiledEntity;
import com.nhd.mall.entity.OrderProductEntity;
import com.nhd.mall.entity.ProductDetailEntity;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.util.DateTimePickerDialog;
import com.nhd.mall.util.ImageLoader;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**购买优惠券时跳进来的确认订单页面
 * Created by Administrator on 14-7-1.
 */
public class CouponFormActivity extends ModelActivity implements View.OnClickListener,OnAsyncTaskUpdateListener {
    private RelativeLayout rlChange;  //选择个人信息
    private Dialog beanDialog = null; // 选择疯豆弹出框
    private Dialog isBuyDialog = null; // 是否购买弹出框
    private Dialog buySuccessDialog = null; // 交易成功弹出框
    private int storeId =1;
    private Long memberId;
    private StoreEntity store;
    //与订单有关的控件
    private TextView tvGoodName;
    private TextView tvPrice;
    private Button btnAdd;
    private Button btnMinus;
    private TextView tvBuyCount;
    private TextView allPrice;  //合计
    private EditText etComment;  //建议
    //从优惠券传递过来的参数
    private Coupon coupon;   //商品详情实体类
    private Integer buyNum=1;
    private String name;
    //与选择疯豆数量有关的
    private CheckBox cbBean;   //是否选择用疯豆支付的单选框
    private TextView tvDouCount;  //疯豆数量
    private TextView tvDouPay;   //疯豆多少钱
    private Integer beanCount;
    private Integer tempCount;   //临时选择的疯豆数两
    private TextView tvTx;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_form_layout);
        setTitle("确认订单");
        find();
    }
    private void find() {
        if(getIntent().getExtras()!=null){
            buyNum = getIntent().getExtras().getInt("count");
            coupon= (Coupon) getIntent().getExtras().getSerializable("product");
            name = getIntent().getExtras().getString("name");
        }
        tvTx = (TextView)findViewById(R.id.tvTixing);
        setTextColor();
        cbBean = (CheckBox)findViewById(R.id.checkBox);
        tvDouPay = (TextView)findViewById(R.id.tvPay);
        tvDouCount = (TextView)findViewById(R.id.tvDouCount);
        tvDouCount.setOnClickListener(this);
        tvGoodName = (TextView)findViewById(R.id.goodsName);
        tvPrice = (TextView)findViewById(R.id.goodsPrice);
        btnAdd = (Button)findViewById(R.id.btn_count_add);
        btnMinus = (Button)findViewById(R.id.btn_count_minus);
        tvBuyCount = (TextView)findViewById(R.id.et_count);
        allPrice = (TextView)findViewById(R.id.allCount);
        etComment = (EditText)findViewById(R.id.etComment);
        tvDouCount = (TextView)findViewById(R.id.tvDouCount);
        tvDouCount.setOnClickListener(this);
        initDouPrice();
        if(MainApplication.getInstance().getStore()!=null){
            store = MainApplication.getInstance().getStore();
            storeId = store.getId();
        }
        btnAdd.setOnClickListener(this);
        btnMinus.setOnClickListener(this);
        rlChange = (RelativeLayout)findViewById(R.id.rlChangeOneDetail);
        rlChange.setOnClickListener(this);
        findViewById(R.id.btnSend).setOnClickListener(this);
        initFormDetail();
    }
    private void setTextColor() {
        SpannableStringBuilder style=new SpannableStringBuilder(tvTx.getText().toString());
        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.txtclick)),14,24, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTx.setText(style);
    }

    //初始化疯豆数量和价格
    private void initDouPrice() {
        if(MainApplication.getInstance().getBeanCount()==null||MainApplication.getInstance().getBeanCount()==0){
            tvDouCount.setText("0");
            tvDouPay.setText("0元");
            beanCount=0;
            tempCount=0;
        }
        else{
            Integer b = MainApplication.getInstance().getBeanCount();
            tvDouCount.setText(String.valueOf(b));
            tvDouPay.setText(b/10.0+"元");
            beanCount=b;
            tempCount=beanCount;
        }
    }
    //刚开始初始化订单详情
    private void initFormDetail() {
        if(coupon==null)return;
        tvGoodName.setText(name==null?"":name);
        tvPrice.setText("￥"+coupon.getPrice());
        tvBuyCount.setText(String.valueOf(buyNum));
       allPrice.setText("￥" + (coupon.getPrice() * buyNum));
    }
    @Override
    public void getData(Object obj, String message) {
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (obj == null)
            return;
        HashMap<String,String> map = (HashMap<String, String>) obj;
        if(map.get("success").equals("true")){
            buySuccessDialog();
        }
        else if(map.get("success").equals("nostock")){
            String s = map.get("message");
            Toast.makeText(this, s==null?"":s, Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnSend:
                if(Integer.parseInt(tvBuyCount.getText().toString())<=0){
                    Toast.makeText(CouponFormActivity.this,"请选择购买量",Toast.LENGTH_SHORT).show();
                    return;
                }
                isBuyDialog();
                break;
            case R.id.rlChangeOneDetail:
                Intent it = new Intent();
                it.setClass(CouponFormActivity.this,SelectCustomerAddress.class);
                Bundle bundle = new Bundle();
                bundle.putString("sort","form");
                it.putExtras(bundle);
                startActivityForResult(it,1);
                break;
            case R.id.tvDouCount:
                selectBeanCount();
                break;
            case R.id.btn_count_add:   //添加
                int count =  Integer.parseInt(tvBuyCount.getText().toString());
                tvBuyCount.setText(String.valueOf(count+1));
                buyNum = count+1;
                allPrice.setText("￥"+(coupon.getPrice()*buyNum));
                break;
            case R.id.btn_count_minus:   //减少
                int countMinus =  Integer.parseInt(tvBuyCount.getText().toString());
                if(countMinus>0){
                    buyNum = countMinus-1;
                    tvBuyCount.setText(String.valueOf(countMinus-1));
                     allPrice.setText("￥"+(coupon.getPrice()*buyNum));
                }
                break;
        }
    }
    //选择疯豆数量弹出框
    private void selectBeanCount() {
        Activity activity = CouponFormActivity.this;
        while (activity.getParent() != null) {
            activity = activity.getParent();
        }
        View view = null;
        view = LayoutInflater.from(CouponFormActivity.this).inflate(R.layout.select_bean_count_dialog, null);
        final TextView tvCount;//剩余量
        final  Button btnBinus; //减少
        final  Button btnAdd;  //增加
        final  EditText etCount;//选择的疯豆数量
        tvCount = (TextView)view.findViewById(R.id.tvShengyu);
        tvCount.setText(beanCount+"");
        btnBinus = (Button)view.findViewById(R.id.btnBinus);
        btnAdd =  (Button)view.findViewById(R.id.btnAdd);
        etCount = (EditText)view.findViewById(R.id.etCount);
        etCount.setText(beanCount+"");
        //减少监听
        btnBinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etCount.getText().toString().equals("0"))return;
                Integer count = Integer.parseInt(etCount.getText().toString());
                etCount.setText((count-1)+"");
                tempCount = count-1;
            }
        });
        //增加监听
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer count = Integer.parseInt(etCount.getText().toString());
                if(count>=beanCount)return;
                etCount.setText((count+1)+"");
                tempCount = count+1;
            }
        });
        //直接选择疯豆数量
        etCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().trim().equals(""))return;
                Integer finalCount  = Integer.parseInt(editable.toString());
                if(finalCount>beanCount){
                    etCount.setText(beanCount+"");
                    tempCount = beanCount;
                }
                else{
                    tempCount = finalCount;
                }
            }
        });
        view.findViewById(R.id.btnSure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beanDialog.dismiss();
                tvDouCount.setText(tempCount+"");
                tvDouPay.setText(tempCount/10.0+"元");
            }
        });
        view.findViewById(R.id.btnQuit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempCount=0;
                beanDialog.dismiss();
            }
        });
        beanDialog = new Dialog(activity, R.style.planDialog);
        beanDialog.setCancelable(true);
        beanDialog.setContentView(view);
        beanDialog.show();
    }
    //提交订单方法
    private void putForm(){
        if(coupon==null)return;
        if(MainApplication.getInstance().getMember()==null){
            new startIntent(CouponFormActivity.this,LoginActivity.class);
            return;
        }
        memberId = MainApplication.getInstance().getMember().getId();
        FormEntity form = new FormEntity();
        Member member =  new Member();
        member.setId(memberId);
        form.setMember(member);//赋值member
        if(etComment.getText()!=null){
            form.setMessage(etComment.getText().toString());
        }
        form.setGetway("2");//提货方式   自行取货 1 ；快递：2
        form.setIsMoney("2");//是否使用现金  使用 1；不使用2
        form.setStoreId(coupon.getStoreId());//购买门店Id
        form.setOrderType("2");  //2是优惠券
        CouponOrder[]orderProducts = new CouponOrder[1];
        orderProducts[0] = new CouponOrder();
       // orderProducts[0].setPrice(coupon.getPrice());
        orderProducts[0].setId(coupon.getId());
        orderProducts[0].setNum(buyNum);
        form.setCoupons(orderProducts);
        String ss = allPrice.getText().toString();
        String sss = ss.substring(1, ss.length());
        //判断是否加入了疯豆购买以及判断有都少疯豆量
        if(cbBean.isChecked()){
            String sBean = tvDouCount.getText().toString();
            int iBean = Integer.parseInt(sBean);
            if(iBean/10.0>=Double.parseDouble(sss)){
                Toast.makeText(CouponFormActivity.this,"选择的疯豆太多",Toast.LENGTH_SHORT).show();
                return;
            }
            form.setIsWind("1"); //是否使用风豆 使用 1；不使用2
            form.setWindNum(iBean);
            double allPay = Double.parseDouble(sss);
            double finalPay = allPay-iBean/10.0;
            form.setPayment(finalPay);//总付款额，包含运费
        }
        else{
            form.setIsWind("2"); //是否使用风豆 使用 1；不使用2
            form.setPayment(Double.parseDouble(sss));//总付款额，包含运费
        }
        new OrderFormPost(CouponFormActivity.this,form).setListener(CouponFormActivity.this);
    }
    //确认是否要购买弹出框
    private void isBuyDialog() {
        Activity activity = CouponFormActivity.this;
        while (activity.getParent() != null) {
            activity = activity.getParent();
        }
        View view = null;
        view = LayoutInflater.from(CouponFormActivity.this).inflate(R.layout.is_buy_dialog, null);
        view.findViewById(R.id.btnSure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                putForm();
                isBuyDialog.dismiss();
            }
        });
        view.findViewById(R.id.btnQuit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isBuyDialog.dismiss();
            }
        });
        isBuyDialog = new Dialog(activity, R.style.planDialog);
        isBuyDialog.setCancelable(true);
        isBuyDialog.setContentView(view);
        isBuyDialog.show();
    }
    //交易成功弹出框
    private void buySuccessDialog() {
        Activity activity = CouponFormActivity.this;
        while (activity.getParent() != null) {
            activity = activity.getParent();
        }
        View view = null;
        view = LayoutInflater.from(CouponFormActivity.this).inflate(R.layout.buy_success_layout, null);
        view.findViewById(R.id.btnSure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buySuccessDialog.dismiss();
            }
        });
        view.findViewById(R.id.btnQuit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new startIntent(CouponFormActivity.this,MyFormActivity.class);
                buySuccessDialog.dismiss();
            }
        });
        buySuccessDialog = new Dialog(activity, R.style.planDialog);
        buySuccessDialog.setCancelable(true);
        buySuccessDialog.setContentView(view);
        buySuccessDialog.show();
    }
    //从选择收货地址页面回来后调用这个方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    }


}
