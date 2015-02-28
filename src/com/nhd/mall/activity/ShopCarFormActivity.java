package com.nhd.mall.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.CarOrderFormPost;
import com.nhd.mall.datebase.DbAddress;
import com.nhd.mall.entity.CustomerAddressEntity;
import com.nhd.mall.entity.FormEntity;
import com.nhd.mall.entity.FormList;
import com.nhd.mall.entity.FormStoreEntity;
import com.nhd.mall.entity.Member;
import com.nhd.mall.entity.OrderProductEntity;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.util.DateTimePickerDialog;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;
import com.nhd.mall.widget.ShopFormItemView;
import com.umeng.analytics.MobclickAgent;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**确认订单页面 购物车跳转过来的订单页面
 * Created by caili on 14-4-5.
 */
public class ShopCarFormActivity extends ModelActivity implements RadioGroup.OnCheckedChangeListener,View.OnClickListener,OnAsyncTaskUpdateListener,ShopFormItemView.ShopFormCount {
    private RadioGroup rg;
    private LinearLayout llOne;//点击送货上门时出现的layout
    private LinearLayout llTwo;//点击门店提货时出现的layout
    private RelativeLayout rlChange;  //修改发货地点等信息
    private Dialog beanDialog = null; // 选择疯豆弹出框
    private Dialog isBuyDialog = null; // 是否购买弹出框
    private Dialog buySuccessDialog = null; // 交易成功弹出框
    private final int  MAIL = 1; //送货上门
    private final int STORE=2;   //门店自提
    private int currentTag = MAIL;
    private int storeId=1;
    private Long memberId;
    private TextView tvYunfei;
    //与收货地址有关系的三个控件
    private TextView tvAddressName;
    private TextView tvAddressPhone;
    private TextView tvAddressDetail;
    private CustomerAddressEntity address;
    //门店自提
    private TextView tvStoreAddress;
    private TextView storePhone;
    private TextView getGoodsTime;
    private TextView tvDetail;
    private StoreEntity store;//自提的门店
    //从购物车获取的参数
    private OrderProductEntity[] orderProduct;
    //与订单有关的控件
    private TextView allPrice;  //合计
    private EditText etComment;  //建议
    private LinearLayout llAddView;
    private double freight=0;//运费
    //与选择疯豆数量有关的
    private CheckBox cbBean;   //是否选择用疯豆支付的单选框
    private TextView tvDouCount;  //疯豆数量
    private TextView tvDouPay;   //疯豆多少钱
    private Integer beanCount;
    private Integer tempCount;   //临时选择的疯豆数两
    private Integer finalCount=0;  //剩余的疯豆数量
    //当用户选择门店自提的时候也要有一个选择收货人名字和电话的地方
    private TextView tvStoreCustomeName;
    private TextView tvStoreCustomePhone;
    private RelativeLayout rlStoreCustomerChange;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_car_form_layout);
        setTitle("确认订单");
        find();
    }
    private void find() {
        tvStoreCustomeName = (TextView)findViewById(R.id.tvStoreCustomeName);
        tvStoreCustomePhone = (TextView)findViewById(R.id.tvStoreCustomePhone);
        rlStoreCustomerChange = (RelativeLayout)findViewById(R.id.rlChangeTwoDetail);
        rlStoreCustomerChange.setOnClickListener(this);
        allPrice = (TextView)findViewById(R.id.allCount);
        etComment = (EditText)findViewById(R.id.etComment);
        llAddView = (LinearLayout)findViewById(R.id.ll_addView);
        tvAddressName = (TextView)findViewById(R.id.tvCustomeName);
        tvAddressPhone = (TextView)findViewById(R.id.tvCustomePhone);
        tvAddressDetail = (TextView)findViewById(R.id.tvCustomeAddress);
        tvStoreAddress = (TextView)findViewById(R.id.tvAddress);
        storePhone = (TextView)findViewById(R.id.tvPhone);
        getGoodsTime = (TextView)findViewById(R.id.tvTime);
        tvDetail = (TextView)findViewById(R.id.tvTixing);
        tvYunfei = (TextView)findViewById(R.id.yunfei);
        tvStoreAddress.setOnClickListener(this);
        getGoodsTime.setOnClickListener(this);
        cbBean = (CheckBox)findViewById(R.id.checkBox);
        tvDouPay = (TextView)findViewById(R.id.tvPay);
        tvDouCount = (TextView)findViewById(R.id.tvDouCount);
        tvDouCount.setOnClickListener(this);
        initDate();
        initDouPrice();
        if(MainApplication.getInstance().getStore()!=null){
            store = MainApplication.getInstance().getStore();
            storeId = store.getId();
            initFirstStoreDetail();
        }
        rg = (RadioGroup)findViewById(R.id.rg);
        tvDouCount = (TextView)findViewById(R.id.tvDouCount);
        tvDouCount.setOnClickListener(this);
        rg.setOnCheckedChangeListener(this);
        llOne = (LinearLayout)findViewById(R.id.llChangeOne);
        llTwo = (LinearLayout)findViewById(R.id.llChangeTwo);
        rlChange = (RelativeLayout)findViewById(R.id.rlChangeOneDetail);
        rlChange.setOnClickListener(this);
        findViewById(R.id.btnSend).setOnClickListener(this);
        initFormDetail();
        initCustomerAddress();
    }
    private void initFirstStoreDetail() {
        if(store!=null){
            tvStoreAddress.setText(store.getName());
            storePhone.setText(store.getTel());
        }
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
    //刚开始初始化收货地址
    private void initCustomerAddress() {
        if(MainApplication.getInstance().getCustomerAddress()!=null){
            address = MainApplication.getInstance().getCustomerAddress();
            tvAddressName.setText(address.getName());
            tvAddressPhone.setText(address.getMobile());
            tvAddressDetail.setText(address.getAddress());
            tvStoreCustomeName.setText(address.getName());
            tvStoreCustomePhone.setText(address.getMobile());
        }
    }
    private void initDate(){
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dates = sf.format(date);
        getGoodsTime.setText(dates);
    }
    //刚开始初始化订单详情
    private void initFormDetail() {
        llAddView.removeAllViews();
        if(MainApplication.getInstance().getOrder().size()<=0)return;
        if(orderProduct==null){
            orderProduct = new OrderProductEntity[MainApplication.getInstance().getOrder().size()];
            orderProduct =MainApplication.getInstance().getOrder().toArray(orderProduct);
        }
        double price  = 0;
        freight = orderProduct[0].getFreight();
        for(int i=0;i<orderProduct.length;i++){
            ShopFormItemView view = new ShopFormItemView(this,i,orderProduct[i]);
            price+=orderProduct[i].getNum()*orderProduct[i].getPrice();
            llAddView.addView(view);
        }
        if(currentTag==MAIL){
            tvYunfei.setVisibility(View.VISIBLE);
            tvYunfei.setText("(含运费"+freight+"元)");
            allPrice.setText("￥"+(price+freight));
        }
        else{
            tvYunfei.setVisibility(View.GONE);
            allPrice.setText("￥"+price);
        }
    }
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch(radioGroup.getCheckedRadioButtonId()){
            case R.id.rb_shsm:
                currentTag = MAIL;
                llOne.setVisibility(View.VISIBLE);
                llTwo.setVisibility(View.GONE);
                initFormDetail();
                break;
            case R.id.rb_mdzt:
                currentTag = STORE;
                llOne.setVisibility(View.GONE);
                llTwo.setVisibility(View.VISIBLE);
                initFormDetail();
                break;
        }
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
            case R.id.rlChangeTwoDetail:
                Intent it = new Intent();
                it.setClass(ShopCarFormActivity.this,SelectCustomerAddress.class);
                Bundle bundle = new Bundle();
                bundle.putString("sort","form");
                it.putExtras(bundle);
                startActivityForResult(it,1);
                break;
            case R.id.tvAddress: //门店自提的地址选择
                Intent itAddress = new Intent();
                itAddress.setClass(ShopCarFormActivity.this,ShopBranchActivity.class);
                Bundle bundleStore = new Bundle();
                bundleStore.putString("from","form");
                itAddress.putExtras(bundleStore);
                startActivityForResult(itAddress,2);
                break;
            case R.id.tvTime:      //门店自提的时间选择
                DateTimePickerDialog dateTimePicKDialog = new DateTimePickerDialog(	ShopCarFormActivity.this);
                dateTimePicKDialog.dateTimePicKDialog(getGoodsTime);
                break;
            case R.id.btnSend:
                if(orderProduct==null||orderProduct.length<=0)return;
                isBuyDialog();
                break;
            case R.id.rlChangeOneDetail:
                Intent itOne = new Intent();
                itOne.setClass(ShopCarFormActivity.this,SelectCustomerAddress.class);
                Bundle bundleOne = new Bundle();
                bundleOne.putString("sort","form");
                itOne.putExtras(bundleOne);
                startActivityForResult(itOne,1);
                break;
            case R.id.tvDouCount:
                selectBeanCount();
                break;
        }
    }
    //选择疯豆数量弹出框
    private void selectBeanCount() {
        Activity activity = ShopCarFormActivity.this;
        while (activity.getParent() != null) {
            activity = activity.getParent();
        }
        View view = null;
        view = LayoutInflater.from(ShopCarFormActivity.this).inflate(R.layout.select_bean_count_dialog, null);
        final TextView tvCount;//剩余量
        final Button btnBinus; //减少
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
    //判断提货时间是否合理
    private boolean isTimeIleagle(){
        Date date = new Date();
        String getTime = getGoodsTime.getText().toString();
        String tempTime = getTime.substring(11,getTime.length());
        String startTime = getTime.replace(tempTime,"08:30");
        String endTime = getTime.replace(tempTime,"22:00");
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date startDate = sf.parse(startTime);
            Date endDate = sf.parse(endTime);
            Date dateGetTime = sf.parse(getTime);
            if(startDate.getTime()>=dateGetTime.getTime()||endDate.getTime()<=dateGetTime.getTime()){
                Toast.makeText(ShopCarFormActivity.this,"请在规定时间内取货",Toast.LENGTH_SHORT).show();
                return false;
            }
            Long time = Math.abs(dateGetTime.getTime()-date.getTime());
            int totalSeconds = (int)(time / 1000);
            int  hours = totalSeconds / 3600;
            if(hours>=23){
                return true;
            }
            else{
                Toast.makeText(ShopCarFormActivity.this,"一天后才能取货",Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
    }
    //提交订单方法
    private void putForm(){
        //第一步 先去掉购买数量为0的商品
        if(MainApplication.getInstance().getMember()==null){
            new startIntent(ShopCarFormActivity.this,LoginActivity.class);
            return;
        }
        memberId = MainApplication.getInstance().getMember().getId();
        ArrayList<OrderProductEntity>selectList = new ArrayList<OrderProductEntity>();
        for(int i=0;i<orderProduct.length;i++){
            if(orderProduct[i].getNum()!=null&&orderProduct[i].getNum()>0){
                selectList.add(orderProduct[i]);
            }
        }
        if(selectList.size()<=0){
            Toast.makeText(ShopCarFormActivity.this,"请选择商品购买量",Toast.LENGTH_SHORT).show();
        }
        //计算最后的商品
        OrderProductEntity[] finalOrder = new OrderProductEntity[selectList.size()];
        finalOrder = selectList.toArray(finalOrder);
        //根据不同的门店生成不同的订单
       ArrayList<Integer>storeList = new ArrayList<Integer>();
        for(int i=0;i<finalOrder.length;i++){
            if(!storeList.contains(finalOrder[i].getStoreId())){
                storeList.add(finalOrder[i].getStoreId());
            }
        }
        //获取订单的数量
        FormEntity[] form = new FormEntity[storeList.size()];
        for(int i=0;i<storeList.size();i++){
            int storeTemp = storeList.get(i); //临时的门店id 用来做判断的
            double freightRule=0;
            form[i] = new FormEntity();
            form[i].setOrderType("1");  //1是商品
            Member member =  new Member();
            member.setId(memberId);
            form[i].setMember(member);//赋值member
            if(etComment.getText()!=null){
                form[i].setMessage(etComment.getText().toString());
            }
            //在这里给订单赋值疯豆数量
            form[i].setIsMoney("2");//是否使用现金  使用 1；不使用2
            form[i].setStoreId(storeTemp);//购买门店Id
            ArrayList<OrderProductEntity>list = new ArrayList<OrderProductEntity>();
            for(int j=0;j<orderProduct.length;j++){
                if(orderProduct[j].getStoreId()==storeTemp){
                    list.add(orderProduct[j]);
                }
            }
            OrderProductEntity[]finalEntity = new OrderProductEntity[list.size()];
            finalEntity = list.toArray(finalEntity);
            form[i].setProducts(finalEntity);
            //在这里判断是否有活动 是否免邮费
            if(finalEntity.length>0){
                freightRule = finalEntity[0].getFreightRule();
            }

            double allPrice=0;
            for(int x=0;x<finalEntity.length;x++){
                allPrice+=finalEntity[x].getPrice()*finalEntity[x].getNum();
            }
            if(currentTag==MAIL){
                allPrice+=freight;
                    if(freightRule>0){
                        if(allPrice>=freightRule){
                            allPrice=allPrice-freight;
                        }
                    }
            }
            else{
                allPrice+=0;
            }
            if(cbBean.isChecked()){
                if(i==0){
                    String sBean = tvDouCount.getText().toString();
                    int iBean = Integer.parseInt(sBean);
                    if(iBean/10.0>=allPrice){
                        Toast.makeText(ShopCarFormActivity.this,"选择的疯豆太多",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    form[i].setIsWind("1"); //是否使用风豆 使用 1；不使用2
                    form[i].setWindNum(iBean);
                    double finalPay = allPrice-iBean/10.0;
                    form[i].setPayment(finalPay);//总付款额，包含运费
                }
                else{
                    form[i].setIsWind("2"); //是否使用风豆 使用 1；不使用2
                    form[i].setPayment(allPrice);//总付款额，包含运费
                }
            }
            else{
                form[i].setIsWind("2"); //是否使用风豆 使用 1；不使用2
                form[i].setPayment(allPrice);//总付款额，包含运费
            }
            switch(currentTag){
                case MAIL:             //送货上门
                    if(address==null){
                        Toast.makeText(ShopCarFormActivity.this,"请选择收货地址",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    form[i].setGetway("2");//提货方式   自行取货 1 ；快递：2
                    form[i].setAddId(address.getId());//用户地址Id
                    form[i].setFreight(freight);//赋值运费
                    break;
                case STORE://门店自提
                    if(tvStoreCustomePhone.getText()==null||tvStoreCustomePhone.getText().toString().equals("")||tvStoreCustomeName.getText()==null||tvStoreCustomeName.getText().toString().equals("")){
                        Toast.makeText(ShopCarFormActivity.this,"请填写收货人资料",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(store==null){
                        Toast.makeText(ShopCarFormActivity.this,"请选择自提的门店",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(getGoodsTime.getText()==null||getGoodsTime.getText().toString().equals("")){
                        Toast.makeText(ShopCarFormActivity.this,"请选择提货的时间",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!isTimeIleagle()){
                        return;
                    }
                    form[i].setAddId(address.getId());//用户地址Id
                    form[i].setGetway("1");//提货方式   自行取货 1 ；快递：2
                    FormStoreEntity orderStores = new FormStoreEntity();
                    orderStores.setStoreId(store.getId());
                    orderStores.setStoreName(store.getName());
                    orderStores.setGetDate(getGoodsTime.getText().toString());
                    form[i].setOrderStores(orderStores);
                    break;
            }
        }
        FormList formList = new FormList();
        formList.setOrders(form);
        new CarOrderFormPost(ShopCarFormActivity.this,formList).setListener(ShopCarFormActivity.this);
    }
    //确认是否要购买弹出框
    private void isBuyDialog() {
        Activity activity = ShopCarFormActivity.this;
        while (activity.getParent() != null) {
            activity = activity.getParent();
        }
        View view = null;
        view = LayoutInflater.from(ShopCarFormActivity.this).inflate(R.layout.is_buy_dialog, null);
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
        Activity activity = ShopCarFormActivity.this;
        while (activity.getParent() != null) {
            activity = activity.getParent();
        }
        View view = null;
        view = LayoutInflater.from(ShopCarFormActivity.this).inflate(R.layout.buy_success_layout, null);
        view.findViewById(R.id.btnSure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buySuccessDialog.dismiss();
            }
        });
        view.findViewById(R.id.btnQuit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new startIntent(ShopCarFormActivity.this,MyFormActivity.class);
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
        //选择收货地址返回
        if(requestCode==1){
            if(data!=null){
                if(data.getExtras()!=null){
                    address = (CustomerAddressEntity) data.getExtras().getSerializable("address");
                    if(address!=null){
                        new DbAddress(ShopCarFormActivity.this).update(address);
                        tvAddressName.setText(address.getName());
                        tvAddressPhone.setText(address.getMobile());
                        tvAddressDetail.setText(address.getAddress());
                        tvStoreCustomeName.setText(address.getName());
                        tvStoreCustomePhone.setText(address.getMobile());
                    }
                }
            }
        }
        //选择门店自提的地址
        if(requestCode==2){
            if(data!=null){
                if(data.getExtras()!=null){
                    store = (StoreEntity) data.getExtras().getSerializable("store");
                    if(store!=null){
                        tvStoreAddress.setText(store.getName());
                        storePhone.setText(store.getTel());
                    }
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainApplication.getInstance().getOrder().clear();
    }
    @Override
    public void getShopFormCount(int position, OrderProductEntity product) {
     if(orderProduct!=null){
         orderProduct[position] = product;
         double price  = 0;
         for(int i=0;i<orderProduct.length;i++){
             price+=orderProduct[i].getNum()*orderProduct[i].getPrice();
         }
         //给总价初始化
         if(currentTag==MAIL){
             allPrice.setText("￥"+(price+freight));
         }
         else{
             allPrice.setText("￥"+(price));
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
}
