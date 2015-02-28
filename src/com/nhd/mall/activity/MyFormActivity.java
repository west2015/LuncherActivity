package com.nhd.mall.activity;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.alipay.android.app.sdk.AliPay;
import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.MyFormAdapter;
import com.nhd.mall.alipay.Keys;
import com.nhd.mall.alipay.Result;
import com.nhd.mall.alipay.Rsa;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.DeleteFormGet;
import com.nhd.mall.asyncTask.FinalStoreGet;
import com.nhd.mall.asyncTask.FormListGet;
import com.nhd.mall.asyncTask.FormSureGet;
import com.nhd.mall.asyncTask.FromBeanCountGet;
import com.nhd.mall.asyncTask.PaySuccessGet;
import com.nhd.mall.entity.FormEntity;
import com.nhd.mall.entity.FormList;
import com.nhd.mall.entity.OrderProductEntity;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.util.OnAsyncTaskDataListener;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.Utils;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;
/**我的订单页面
 * Created by caili on 14-4-8.
 */
public class MyFormActivity extends ModelActivity implements MyFormAdapter.commentListener,OnAsyncTaskUpdateListener,AdapterView.OnItemClickListener,FromBeanCountGet.getCount,OnAsyncTaskDataListener {
    private ListView lvForm;
    private MyFormAdapter ma;
    private FormListGet flg;
    private FormList list;
    private FormEntity[] entity;
    private Long memberId;
    //用来保存删除的订单id
    ArrayList<Integer> idList = new ArrayList<Integer>();
    private ProgressDialog mProgress = null;
    private static final int RQF_PAY = 1;
    private static final int RQF_LOGIN = 2;
    private Integer payId;
    private String out_trade_no;
    private double  total_fee;
    //查询疯豆时候弹出的加载框
    private ImageView ivBeanPro;
    private Dialog beanDialog;
    private Dialog deleteDialog;
    //查询支付宝账号时弹出的加载框
    private ImageView ivAliPro;
    private Dialog aliDialog;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle("卡劵订单");
        setContentView(R.layout.my_form_layout);
        find();
        register();
    }
    private void find() {
        lvForm = (ListView)findViewById(R.id.lv_form);
        ma = new MyFormAdapter(this,entity);
        lvForm.setAdapter(ma);
        lvForm.setOnItemClickListener(this);
        if(MainApplication.getInstance().getMember()!=null){
            memberId = MainApplication.getInstance().getMember().getId();
            flg = new FormListGet(this,memberId);
            flg.setListener(this);
        }
        findViewById(R.id.rl_no_form).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.backToMain(MyFormActivity.this);
            }
        });
        initBeanDialog();
        initAliDialog();
    }
    private void register() {
        getButton(R.drawable.shop_car_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ma==null)return;
                if(ma.selectMap.size()<=0){
                    return;
                }
                deleteDialog();
            }
        });
    }
    //删除的方法
    public void delete(){
        idList.clear();
        for(int i:ma.selectMap.keySet()){
            if(ma.selectMap.get(i)){
                FormEntity form = entity[i];
                idList.add(form.getId());
            }
        }
        Integer[]ids = new Integer[idList.size()];
        ids = idList.toArray(ids);
        new DeleteFormGet(MyFormActivity.this,ids).setListener(MyFormActivity.this);
    }
    private void deleteDialog() {
        View view = null;
        view = LayoutInflater.from(MyFormActivity.this).inflate(R.layout.delete_dialog, null);
        view.findViewById(R.id.btnSure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
                delete();
            }
        });
        view.findViewById(R.id.btnQuit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
            }
        });
        deleteDialog = new Dialog(MyFormActivity.this, R.style.planDialog);
        deleteDialog.setCancelable(true);
        deleteDialog.setContentView(view);
        deleteDialog.show();
    }
    //支付前都要去查询一次疯豆数量看看够不够支付
    @Override
    public void getBeanCount(Object result, String message, int position) {
        stop();
        if (result == null){
            return;
        }
        HashMap<String,String> map = (HashMap<String, String>) result;
        if(map.get("success").equals("true")){
            Object objCount = map.get("marks");
            String sCount = objCount.toString();
            MainApplication.getInstance().setBeanCount(Integer.parseInt(sCount));
            if(entity[position].getWindNum()!=null){
                if(Integer.parseInt(sCount)>=entity[position].getWindNum()){
                    payAli(position);
                }
                else{
                    Toast.makeText(MyFormActivity.this,"您的剩余疯豆不够支付该订单里的疯豆量",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void sortEntity(){
    	if(entity == null || entity.length<=0) return;
    	ArrayList<FormEntity> mList = new ArrayList<FormEntity>();
    	for(int i=0;i<entity.length;++i)
    	if(entity[i].getOrderType().equals("2")){
    		mList.add(entity[i]);
    	}
    	FormEntity[] mEntity = new FormEntity[mList.size()];
    	mEntity = mList.toArray(mEntity);
    	entity = mEntity;
    }
    @Override
    public void getData(Object obj, String message) {
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (obj == null){
            findViewById(R.id.rl_no_form).setVisibility(View.GONE);
            return;
        }
        if(obj instanceof FormList){
            list = (FormList) obj;
            entity = list.getOrders();
            sortEntity();
            ma.update(entity);
            if(entity.length<=0){
                findViewById(R.id.rl_no_form).setVisibility(View.VISIBLE);
            }
            else{
                findViewById(R.id.rl_no_form).setVisibility(View.GONE);
            }
            return;
        }
        HashMap<String,String> map = (HashMap<String, String>) obj;
        if(map.get("success").equals("true")){
            Toast.makeText(MyFormActivity.this,"操作成功",Toast.LENGTH_SHORT).show();
            idList.clear();
            if(flg!=null){
                flg.update(memberId);
            }
        }
    }
    @Override
    public void comment(final int position,String state) {
        if(entity==null||entity.length<=0)return;
        if(state.equals("0")){
            //点击这里进行第三方支付宝登录与支付在这之前先调用接口获取淘宝账号
            startAliDialog();
            new FinalStoreGet(MyFormActivity.this,entity[position].getStoreId(),MyFormActivity.this,position);
        }
        else if(state.equals("1")||state.equals("2")){   //去退货
            //弹出一个弹出框确认是不是确认收货
            AlertDialog.Builder builder = new AlertDialog.Builder(MyFormActivity.this);
            builder.setMessage("确认退货");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Integer formId = entity[position].getId();
                    new FormSureGet(MyFormActivity.this, formId).setListener(MyFormActivity.this);
                }
            });
            builder.setNegativeButton("取消",null);
            builder.show();
        }
    }
    private void payAli(int position) {
        try {
            String info = getNewOrderInfo(position);
            String sign = Rsa.sign(info, Keys.PRIVATE);
            sign = URLEncoder.encode(sign);
            info += "&sign=\"" + sign + "\"&" + getSignType();
            final String orderInfo = info;
            payId = entity[position].getId();
            out_trade_no = getOutTradeNo(position);
            total_fee =getTotalFee(position);
            new Thread() {
                public void run() {
                    AliPay alipay = new AliPay(MyFormActivity.this, mHandler);
                    //设置为沙箱模式，不设置默认为线上环境
                    //alipay.setSandBox(true);
                    String result = alipay.pay(orderInfo);
                    Log.i(TAG, "result = " + result);
                    Message msg = new Message();
                    msg.what = RQF_PAY;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            }.start();

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(MyFormActivity.this,"商户私钥有问题", Toast.LENGTH_SHORT).show();
        }
    }
    private String getNewOrderInfo(int position) {
        if(entity==null||entity.length<=0)return "";
        StringBuilder sb = new StringBuilder();
        sb.append("partner=\"");
        sb.append(Keys.DEFAULT_PARTNER);
        sb.append("\"&out_trade_no=\"");
        sb.append(getOutTradeNo(position));
        sb.append("\"&subject=\"");
        sb.append(getOutTradeNo(position));
        if(entity[position].getOrderType()!=null&&entity[position].getOrderType().equals("1")){
            OrderProductEntity[]products = entity[position].getProducts();
            StringBuffer sbBody = new StringBuffer();
            String sBody="";
            if(products.length<=0){
                sbBody.append("");
                sBody="新华都";
            }
            else{
                for(int i=0;i<products.length;i++){
                    if(i!=products.length-1){
                        sbBody.append(products[i].getDescription()==null?"":products[i].getDescription()+",");
                    }
                    else{
                        sbBody.append(products[i].getDescription()==null?"":products[i].getDescription());
                    }
                }
                if(sbBody.toString().length()>30){
                    sBody = sbBody.toString().substring(0,30)+"...";
                }
                else{
                    sBody = sbBody.toString();
                }
            }
            if(sBody.equals("")){
                sBody="新华都";
            }
            sb.append("\"&body=\"");
            sb.append(sBody);
        }
        else{
            sb.append("\"&body=\"");
            sb.append(entity[position].getOrderDescription()==null?"新华都优惠券":entity[position].getOrderDescription());
        }
        sb.append("\"&total_fee=\"");
//        sb.append("0.01");
        sb.append(getTotalFee(position));
        sb.append("\"&notify_url=\"");
        // 网址需要做URL编码
        sb.append(URLEncoder.encode("http://www.nhd-mart.com"));
        sb.append("\"&service=\"mobile.securitypay.pay");
        sb.append("\"&_input_charset=\"UTF-8");
        sb.append("\"&return_url=\"");
       sb.append(URLEncoder.encode("http://m.alipay.com"));
        sb.append("\"&payment_type=\"1");
        sb.append("\"&seller_id=\"");
        sb.append(Keys.DEFAULT_SELLER);
        sb.append("\"&it_b_pay=\"1m");
        sb.append("\"");
        return new String(sb);
    }
    private String getOutTradeNo(int position) {
        if(entity==null||entity.length<=0)return "";
        return entity[position].getOrderNumber();
    }
    private double getTotalFee(int position) {
        if(entity==null||entity.length<=0)return 0;
        return entity[position].getPayment();
    }
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }
    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Result result = new Result((String) msg.obj);
            switch (msg.what) {
                case RQF_PAY:
                case RQF_LOGIN: {
                    if(result.parseResult()){
                        Toast.makeText(MyFormActivity.this,"交易成功",Toast.LENGTH_SHORT).show();
                        new PaySuccessGet(MyFormActivity.this,payId,(String) msg.obj,out_trade_no,total_fee).setListener(MyFormActivity.this);
                    }
                    else{
                        Toast.makeText(MyFormActivity.this,"交易失败",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
                default:
                    break;
            }
        };
    };
    public void start(){
        beanDialog.show();
        Animation operatingAnim = AnimationUtils.loadAnimation(MyFormActivity.this, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        ivBeanPro.startAnimation(operatingAnim);
    }
    public void stop(){
        beanDialog.dismiss();
        ivBeanPro.clearAnimation();
    }
    //跳到我的优惠券页面去查看当前优惠券
    @Override
    public void getFreight(int position) {
        if(entity==null)return;
        Bundle bundle = new Bundle();
        if(entity[position].getOrderClass()==null)return;
        if(entity[position].getOrderClass().equals("coupon")){
            bundle.putInt("sort",4);
            new startIntent(MyFormActivity.this,MyCouponActivity.class,bundle);
        }
        else if(entity[position].getOrderClass().equals("card")){
            bundle.putInt("sort",3);
            new startIntent(MyFormActivity.this,MyCouponActivity.class,bundle);
        }

    }
    //点击进入某一项订单查看具体商品
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Bundle bundle = new Bundle();
//        bundle.putInt("formId",entity[i].getId());
//        bundle.putString("sort",entity[i].getOrderType());
//        new startIntent(MyFormActivity.this,FormDetailActivity.class,bundle);
    }
    private void initBeanDialog() {
        View view = null;
        view = LayoutInflater.from(MyFormActivity.this).inflate(R.layout.ask_bean_layout, null);
        ivBeanPro = (ImageView)view.findViewById(R.id.progressBar);
        beanDialog = new Dialog(MyFormActivity.this, R.style.planDialog);
        beanDialog.setCancelable(false);
        beanDialog.setContentView(view);
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

    public void startAliDialog(){
        aliDialog.show();
        Animation operatingAnim = AnimationUtils.loadAnimation(MyFormActivity.this, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        ivAliPro.startAnimation(operatingAnim);
    }
    public void stopAliDialog(){
        aliDialog.dismiss();
        ivAliPro.clearAnimation();
    }
    private void initAliDialog() {
        View view = null;
        view = LayoutInflater.from(MyFormActivity.this).inflate(R.layout.ask_ali_layout, null);
        ivAliPro = (ImageView)view.findViewById(R.id.aliProgressBar);
        aliDialog = new Dialog(MyFormActivity.this, R.style.planDialog);
        aliDialog.setCancelable(false);
        aliDialog.setContentView(view);
    }

    //从服务端获取支付宝账号和密钥
    @Override
    public void getDataSort(Object obj, String message, String sort) {
        stopAliDialog();
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (obj == null)
            return;
        if(obj instanceof StoreEntity){
            StoreEntity ss = (StoreEntity) obj;
            int position = Integer.parseInt(sort);
            if(ss.getPartner()!=null){
                Keys.DEFAULT_PARTNER = ss.getPartner();
            }
            if(ss.getSeller()!=null){
                Keys.DEFAULT_SELLER = ss.getSeller();
            }
            if(ss.getPrivateKey()!=null){
                Keys.PRIVATE = ss.getPrivateKey();
            }
            //在这里进行支付
            if(entity[position].getIsWind().equals("2")){
                payAli(position);
            }
            else{
                if(MainApplication.getInstance().getMember()==null){
                    new startIntent(MyFormActivity.this,LoginActivity.class);
                    return;
                }
                start();
                memberId = MainApplication.getInstance().getMember().getId();
                new FromBeanCountGet(MyFormActivity.this,memberId,position);
            }
        }
    }
}