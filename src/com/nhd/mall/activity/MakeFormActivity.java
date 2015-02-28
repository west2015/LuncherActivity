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
import com.umeng.analytics.MobclickAgent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**确认订单页面 单个物品确认订单页面
 * Created by caili on 14-4-5.
 */
public class MakeFormActivity extends ModelActivity implements RadioGroup.OnCheckedChangeListener,View.OnClickListener,OnAsyncTaskUpdateListener {
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
    private int storeId =1;
    private Long memberId;

    //与订单有关的控件
    private ImageView ivGoodImg;
    private TextView tvGoodName;
    private TextView tvPrice;
    private Button btnAdd;
    private Button btnMinus;
    private TextView tvBuyCount;
    private TextView allPrice;  //合计
    private EditText etComment;  //建议
    private ImageLoader imageLoader;
    private TextView tvYunfei;
    private double freight=0;
    //与收货地址有关系的三个控件
    private TextView tvAddressName;
    private TextView tvAddressPhone;
    private TextView tvAddressDetail;
    private  CustomerAddressEntity address;
    //门店自提
    private TextView tvStoreAddress;
    private TextView storePhone;
    private TextView getGoodsTime;
    private TextView tvDetail;
    private StoreEntity store;//自提的门店
    //从商品详情类传递过来的参数
    private ProductDetailEntity productDetailEntity;   //商品详情实体类
    private Integer buyNum=1;
    private HashMap<String,String> pamereterMap = new HashMap<String, String>();//用户选择的参数
    //与选择疯豆数量有关的
    private CheckBox cbBean;   //是否选择用疯豆支付的单选框
    private TextView tvDouCount;  //疯豆数量
    private TextView tvDouPay;   //疯豆多少钱
    private Integer beanCount;
    private Integer tempCount;   //临时选择的疯豆数两
    //与活动有关系的参数
    private double freightRule=0;
    private TextView tvFreightRule;
    //当用户选择门店自提的时候也要有一个选择收货人名字和电话的地方
    private TextView tvStoreCustomeName;
    private TextView tvStoreCustomePhone;
    private RelativeLayout rlStoreCustomerChange;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_form_layout);
        setTitle("确认订单");
        find();
    }
    private void find() {
        if(getIntent().getExtras()!=null){
            buyNum = getIntent().getExtras().getInt("count");
            productDetailEntity = (ProductDetailEntity) getIntent().getExtras().getSerializable("product");
            pamereterMap = (HashMap<String, String>) getIntent().getExtras().getSerializable("map");
            freightRule = productDetailEntity.getFreightRule();
        }
        tvStoreCustomeName = (TextView)findViewById(R.id.tvStoreCustomeName);
        tvStoreCustomePhone = (TextView)findViewById(R.id.tvStoreCustomePhone);
        rlStoreCustomerChange = (RelativeLayout)findViewById(R.id.rlChangeTwoDetail);
        rlStoreCustomerChange.setOnClickListener(this);

        tvFreightRule = (TextView)findViewById(R.id.tvFreightRule);
        imageLoader = new ImageLoader(this);
        imageLoader.setDefaultBackgroup(R.drawable.goods_mr_img);
        imageLoader.setFailBackgroup(R.drawable.goods_mr_img);
        cbBean = (CheckBox)findViewById(R.id.checkBox);
        tvDouPay = (TextView)findViewById(R.id.tvPay);
        tvDouCount = (TextView)findViewById(R.id.tvDouCount);
        tvDouCount.setOnClickListener(this);
        ivGoodImg = (ImageView)findViewById(R.id.iv_good);
        tvGoodName = (TextView)findViewById(R.id.goodsName);
        tvYunfei = (TextView)findViewById(R.id.yunfei);
        tvPrice = (TextView)findViewById(R.id.goodsPrice);
        btnAdd = (Button)findViewById(R.id.btn_count_add);
        btnMinus = (Button)findViewById(R.id.btn_count_minus);
        tvBuyCount = (TextView)findViewById(R.id.et_count);
        allPrice = (TextView)findViewById(R.id.allCount);
        etComment = (EditText)findViewById(R.id.etComment);
        tvAddressName = (TextView)findViewById(R.id.tvCustomeName);
        tvAddressPhone = (TextView)findViewById(R.id.tvCustomePhone);
        tvAddressDetail = (TextView)findViewById(R.id.tvCustomeAddress);
        tvStoreAddress = (TextView)findViewById(R.id.tvAddress);
        storePhone = (TextView)findViewById(R.id.tvPhone);
        getGoodsTime = (TextView)findViewById(R.id.tvTime);
        tvDetail = (TextView)findViewById(R.id.tvTixing);
        tvStoreAddress.setOnClickListener(this);
        getGoodsTime.setOnClickListener(this);
        tvDouCount = (TextView)findViewById(R.id.tvDouCount);
        tvDouCount.setOnClickListener(this);
        initDate();
        initDouPrice();
        if(MainApplication.getInstance().getStore()!=null){
            store = MainApplication.getInstance().getStore();
            storeId = store.getId();
            initFirstStoreDetail();
        }
        btnAdd.setOnClickListener(this);
        btnMinus.setOnClickListener(this);
        rg = (RadioGroup)findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(this);
        llOne = (LinearLayout)findViewById(R.id.llChangeOne);
        llTwo = (LinearLayout)findViewById(R.id.llChangeTwo);
        rlChange = (RelativeLayout)findViewById(R.id.rlChangeOneDetail);
        rlChange.setOnClickListener(this);
        findViewById(R.id.btnSend).setOnClickListener(this);
        initFormDetail();
        initCustomerAddress();
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

    private void initFirstStoreDetail() {
        if(store!=null){
            tvStoreAddress.setText(store.getName());
            storePhone.setText(store.getTel());
        }
    }
    //刚开始初始化收货地址
    private void initCustomerAddress() {
        if(MainApplication.getInstance().getCustomerAddress()!=null){
             address = MainApplication.getInstance().getCustomerAddress();
            tvAddressName.setText(address.getName());
            tvStoreCustomeName.setText(address.getName());
            tvAddressPhone.setText(address.getMobile());
            tvStoreCustomePhone.setText(address.getMobile());
            tvAddressDetail.setText(address.getAddress());
        }
    }
    private void initDate(){
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dates = sf.format(date);
        getGoodsTime.setText(dates);
        if(freightRule>0){
            findViewById(R.id.rlFreightRule).setVisibility(View.VISIBLE);
            tvFreightRule.setText(freightRule+"");
        }
        else{
            findViewById(R.id.rlFreightRule).setVisibility(View.GONE);
        }
    }
    //刚开始初始化订单详情
    private void initFormDetail() {
        if(productDetailEntity==null)return;
        tvGoodName.setText(productDetailEntity.getName()==null?"":productDetailEntity.getName());
        tvPrice.setText(productDetailEntity.getPrice()==null?"￥0":"￥"+String.valueOf(productDetailEntity.getPrice()));
        tvBuyCount.setText(String.valueOf(buyNum));
        freight = productDetailEntity.getFreight();
        imageLoader.setBackgroup(productDetailEntity.getThumb(),ivGoodImg);
        if(currentTag==MAIL){
            tvYunfei.setVisibility(View.VISIBLE);
            tvYunfei.setText("(含运费"+freight+"元)");
            allPrice.setText("￥" + (productDetailEntity.getPrice() * buyNum + freight));
        }
        else{
            tvYunfei.setVisibility(View.GONE);
            allPrice.setText("￥" + (productDetailEntity.getPrice() * buyNum));
        }
    }
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
          switch(radioGroup.getCheckedRadioButtonId()){
              case R.id.rb_shsm:  //送货上门有运费
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
                it.setClass(MakeFormActivity.this,SelectCustomerAddress.class);
                Bundle bundle = new Bundle();
                bundle.putString("sort","form");
                it.putExtras(bundle);
                startActivityForResult(it,1);
                break;
            case R.id.tvAddress: //门店自提的地址选择
                Intent itAddress = new Intent();
                itAddress.setClass(MakeFormActivity.this,ShopBranchActivity.class);
                Bundle bundleStore = new Bundle();
                bundleStore.putString("from","form");
                itAddress.putExtras(bundleStore);
                startActivityForResult(itAddress,2);
                break;
            case R.id.tvTime:      //门店自提的时间选择
                DateTimePickerDialog dateTimePicKDialog = new DateTimePickerDialog(	MakeFormActivity.this);
                dateTimePicKDialog.dateTimePicKDialog(getGoodsTime);
                break;
            case R.id.btnSend:
                if(Integer.parseInt(tvBuyCount.getText().toString())<=0){
                    Toast.makeText(MakeFormActivity.this,"请选择购买量",Toast.LENGTH_SHORT).show();
                    return;
                }
                isBuyDialog();
                break;
            case R.id.rlChangeOneDetail:
                Intent itOne = new Intent();
                itOne.setClass(MakeFormActivity.this,SelectCustomerAddress.class);
                Bundle bundleOne = new Bundle();
                bundleOne.putString("sort","form");
                itOne.putExtras(bundleOne);
                startActivityForResult(itOne,1);
                break;
            case R.id.tvDouCount:
                selectBeanCount();
                break;
            case R.id.btn_count_add:   //添加
                int count =  Integer.parseInt(tvBuyCount.getText().toString());
                tvBuyCount.setText(String.valueOf(count+1));
                buyNum = count+1;
                if(currentTag==MAIL){
                    allPrice.setText("￥"+(productDetailEntity.getPrice()*(count+1)+freight));
                }
                else{
                    allPrice.setText("￥"+(productDetailEntity.getPrice()*(count+1)));
                }
                break;
            case R.id.btn_count_minus:   //减少
                int countMinus =  Integer.parseInt(tvBuyCount.getText().toString());
                if(countMinus>0){
                    buyNum = countMinus-1;
                    tvBuyCount.setText(String.valueOf(countMinus-1));
                    if(currentTag==MAIL){
                        allPrice.setText("￥"+(productDetailEntity.getPrice()*(countMinus-1)+freight));
                    }
                    else{
                        allPrice.setText("￥"+(productDetailEntity.getPrice()*(countMinus-1)));
                    }
                }
                break;
        }
    }
    //选择疯豆数量弹出框
    private void selectBeanCount() {
        Activity activity = MakeFormActivity.this;
        while (activity.getParent() != null) {
            activity = activity.getParent();
        }
        View view = null;
        view = LayoutInflater.from(MakeFormActivity.this).inflate(R.layout.select_bean_count_dialog, null);
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
                Toast.makeText(MakeFormActivity.this,"请在规定时间内取货",Toast.LENGTH_SHORT).show();
                return false;
            }
            Long time = Math.abs(dateGetTime.getTime()-date.getTime());
            int totalSeconds = (int)(time / 1000);
            int  hours = totalSeconds / 3600;
            if(hours>=23){
                return true;
            }
            else{
                Toast.makeText(MakeFormActivity.this,"一天后才能取货",Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
    }
    //提交订单方法
    private void putForm(){
        if(productDetailEntity==null)return;
        if(MainApplication.getInstance().getMember()==null){
            new startIntent(MakeFormActivity.this,LoginActivity.class);
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
        form.setOrderType("1");  //1是商品
        form.setIsMoney("2");//是否使用现金  使用 1；不使用2
        form.setStoreId(productDetailEntity.getStoreId());//购买门店Id
        OrderProductEntity[]orderProducts = new OrderProductEntity[1];
        orderProducts[0] = new OrderProductEntity();
        orderProducts[0].setPrice(productDetailEntity.getPrice());
        orderProducts[0].setProductId(productDetailEntity.getProductId());
        orderProducts[0].setNum(buyNum);
        OrderFiledEntity[] fields;
        if(pamereterMap.size()>0){
            fields = new OrderFiledEntity[pamereterMap.size()];
            ArrayList<OrderFiledEntity>orderList = new ArrayList<OrderFiledEntity>();
            for(String s:pamereterMap.keySet()){
                OrderFiledEntity entity = new OrderFiledEntity();
                entity.setName(s);
                entity.setValue(pamereterMap.get(s));
                orderList.add(entity);
            }
            fields = orderList.toArray(fields);
        }
        else{
            fields = new OrderFiledEntity[1];
            fields[0] = new OrderFiledEntity();
        }
        orderProducts[0].setOrderFields(fields);
        form.setProducts(orderProducts);
        switch(currentTag){
            case MAIL:             //送货上门
                if(address==null){
                    Toast.makeText(MakeFormActivity.this,"请选择收货地址",Toast.LENGTH_SHORT).show();
                    return;
                }
                form.setGetway("2");//提货方式   自行取货 1 ；快递：2
                form.setAddId(address.getId());//用户地址Id
                form.setFreight(freight);//赋值运费
                //在这里判断是否有活动，然后如果总额超过多少可以免运费
                setTotlaPay(form,true);
                break;
            case STORE://门店自提
                if(tvStoreCustomePhone.getText()==null||tvStoreCustomePhone.getText().toString().equals("")||tvStoreCustomeName.getText()==null||tvStoreCustomeName.getText().toString().equals("")){
                    Toast.makeText(MakeFormActivity.this,"请填写收货人资料",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(store==null){
                    Toast.makeText(MakeFormActivity.this,"请选择自提的门店",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(getGoodsTime.getText()==null||getGoodsTime.getText().toString().equals("")){
                    Toast.makeText(MakeFormActivity.this,"请选择提货的时间",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!isTimeIleagle()){
                    return;
                }
                form.setAddId(address.getId());//用户地址Id
                setTotlaPay(form,false);
                form.setGetway("1");//提货方式   自行取货 1 ；快递：2
                FormStoreEntity orderStores = new FormStoreEntity();
                orderStores.setStoreId(store.getId());
                orderStores.setGetDate(getGoodsTime.getText().toString());
                orderStores.setStoreName(store.getName());
                form.setOrderStores(orderStores);
                break;
        }
        new OrderFormPost(MakeFormActivity.this,form).setListener(MakeFormActivity.this);
    }

    private void setTotlaPay(FormEntity form,boolean boo) {
        String ss = allPrice.getText().toString();
        String sss = ss.substring(1, ss.length());
        //判断是否加入了疯豆购买以及判断有都少疯豆量
        //如果是送货上门则有活动
        if(boo){
            if(freightRule>0){
                if(Double.parseDouble(sss)>freightRule){
                    double total = Double.parseDouble(sss)-freight;
                    sss = String.valueOf(total);
                }
            }
        }
        if(cbBean.isChecked()){
            String sBean = tvDouCount.getText().toString();
            int iBean = Integer.parseInt(sBean);
            if(iBean/10.0>=Double.parseDouble(sss)){
                Toast.makeText(MakeFormActivity.this,"选择的疯豆太多",Toast.LENGTH_SHORT).show();
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
    }

    //确认是否要购买弹出框
    private void isBuyDialog() {
        Activity activity = MakeFormActivity.this;
        while (activity.getParent() != null) {
            activity = activity.getParent();
        }
        View view = null;
        view = LayoutInflater.from(MakeFormActivity.this).inflate(R.layout.is_buy_dialog, null);
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
        Activity activity = MakeFormActivity.this;
        while (activity.getParent() != null) {
            activity = activity.getParent();
        }
        View view = null;
        view = LayoutInflater.from(MakeFormActivity.this).inflate(R.layout.buy_success_layout, null);
        view.findViewById(R.id.btnSure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buySuccessDialog.dismiss();
            }
        });
        view.findViewById(R.id.btnQuit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	Intent intent = new Intent(MakeFormActivity.this,FormListActivity.class);
        		intent.putExtra("orderType", 1);
        		intent.putExtra("getway", currentTag);  //自行取货 1 ；快递：2
                intent.putExtra("kind", 0); //0:未付款 1：未发货/未提货 2：全部订单
                MakeFormActivity.this.startActivity(intent);
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
                        new DbAddress(MakeFormActivity.this).update(address);
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
            imageLoader=null;
        }
    }

}
