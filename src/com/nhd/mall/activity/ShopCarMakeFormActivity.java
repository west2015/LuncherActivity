package com.nhd.mall.activity;

import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.ShopCarMakeFormAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.AddressListGet;
import com.nhd.mall.asyncTask.OrderFormPost;
import com.nhd.mall.asyncTask.StoreListGet;
import com.nhd.mall.datebase.DbAddress;
import com.nhd.mall.entity.AddCustomerAddress;
import com.nhd.mall.entity.CarEntity;
import com.nhd.mall.entity.CarList;
import com.nhd.mall.entity.CustomerAddressEntity;
import com.nhd.mall.entity.CustomerAddressEntityList;
import com.nhd.mall.entity.FormEntity;
import com.nhd.mall.entity.FormStoreEntity;
import com.nhd.mall.entity.Member;
import com.nhd.mall.entity.OrderFiledEntity;
import com.nhd.mall.entity.OrderProductEntity;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.entity.StoreListEntity;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.widget.ModelActivity;

/**确认订单页面 单个物品确认订单页面
 * Created by caili on 14-4-5.
 */
public class ShopCarMakeFormActivity extends ModelActivity implements View.OnClickListener,OnAsyncTaskUpdateListener {
	private final int STORE = 1;
	private final int MAIL = 2;
    private Dialog buySuccessDialog = null; // 交易成功弹出框
    private Long memberId;
    private Integer storeId;
    private Integer currentTag;
    // 与收货信息有关的
    private AddressListGet adlg = null;
    private CustomerAddressEntityList addressList = null;
    private CustomerAddressEntity[] addressEntity = null;
    private CustomerAddressEntity address = null;
    private TextView tvName;
    private TextView tvPhone;
    private TextView tvAddress;
    private TextView tvNoAddress;
    private RelativeLayout rlTop;
    // 订单列表
    private StoreListGet slg;
    private ListView listView;
    private ShopCarMakeFormAdapter sca;
    // 提交订单
    private Button btnSubmit;

    private CarList carList;
    private CarEntity[] carEntity;
    private Dialog isBuyDialog = null; // 是否购买弹出框
    private String storeName;
    private double allPrice = 0;
    private double freight = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_car_make_form_layout);
        setTitle("确认订单");
        find();
    }

    private void find() {
    	tvName = (TextView)findViewById(R.id.tv_name);
    	tvPhone = (TextView)findViewById(R.id.tv_phone);
    	tvAddress = (TextView)findViewById(R.id.tv_address);
    	tvNoAddress = (TextView)findViewById(R.id.tv_no_address);
    	rlTop = (RelativeLayout)findViewById(R.id.rl_top);
    	listView = (ListView)findViewById(R.id.lv_goods);
    	btnSubmit = (Button)findViewById(R.id.btn_send);
    	// 设置监听
    	rlTop.setOnClickListener(this);
    	btnSubmit.setOnClickListener(this);
    	// 初始化
        if(getIntent().getExtras() != null){
        	storeId = (int)getIntent().getExtras().getInt("storeid");
        	carList = (CarList)getIntent().getExtras().getSerializable("carlist");
        	carEntity = carList.getCars();
        	allPrice = 0;
        	freight = 0;
        	for(int i=0;i<carEntity.length;++i){
        		OrderProductEntity product = carEntity[i].getOrderProduct();
        		freight += product.getFreight();
        		allPrice += product.getFreight() + product.getPrice()*product.getNum();
//        		this.currentTag = product.getGetway();
        		this.currentTag = 1;
        	}
        }
        storeName = getStoreName(storeId);
        sca = new ShopCarMakeFormAdapter(this,storeName,carEntity);
        listView.setAdapter(sca);
        // 获取接口数据
        if(MainApplication.getInstance().getMember() != null){
        	Member mMember = MainApplication.getInstance().getMember();
        	adlg = new AddressListGet(this,mMember.getId());
	        adlg.setListener(this);
        }
        // 获取本地数据
        initCustomerAddress();
	}
    // 获取店名
    private String getStoreName(int storeId){
        if(MainApplication.getInstance().getStores() != null){
        	StoreEntity[] stores = MainApplication.getInstance().getStores();
//        	String temp = "";
//        	for(int i=0;i<stores.length;++i){
//        		temp += stores[i].getId()+ "\n";
//        	}
//        	Toast.makeText(this, temp, Toast.LENGTH_SHORT).show();
        	for(int i=0;i<stores.length;++i)
        	if(stores[i].getId().equals(storeId)){
        		storeName = stores[i].getName();
        		return storeName;
        	}
        }
        else{
            slg = new StoreListGet(this);
            slg.setListener(this);
        }
    	return "";
    }
    // 初始化收货信息
    private void initCustomerAddress(CustomerAddressEntity address) {
    	this.address = address;
    	new DbAddress(getApplicationContext()).update(address);
        if(address != null){
            tvName.setText("收货人： " + address.getName());
            tvPhone.setText(address.getMobile());
            tvAddress.setText("地址：" + address.getAddress());
            tvNoAddress.setText("");
        }
        else{
    		tvName.setText("");
            tvPhone.setText("");
        	tvAddress.setText("");
        	tvNoAddress.setText("您还没有收货地址，去添加地址吧!");
        }
    }
    
    private void initCustomerAddress() {
        if(MainApplication.getInstance().getCustomerAddress() == null){
        	if(addressEntity == null || addressEntity.length <=0){
        		initCustomerAddress(null);
        	}
        	else{
        		initCustomerAddress(addressEntity[0]);
        	}
        }
        else{
        	if(addressEntity != null){
        		CustomerAddressEntity address = MainApplication.getInstance().getCustomerAddress();
        		for(int i=0;i<addressEntity.length;++i)
        		if(addressEntity[i].getId().equals(address.getId())){
            		initCustomerAddress(addressEntity[i]);
            		return ;
        		}
        	}
    		initCustomerAddress(MainApplication.getInstance().getCustomerAddress());
        }
    }

    @Override
    public void getData(Object obj, String message) {
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (obj == null)
        	return;
        if(obj instanceof CustomerAddressEntityList){
            addressList = (CustomerAddressEntityList) obj;
            addressEntity = addressList.getAddress();
            initCustomerAddress();
            return;
        }
        else
        if(obj instanceof StoreListEntity){
        	StoreListEntity sle = (StoreListEntity) obj;
            StoreEntity[] stores = sle.getStores();
            if(stores != null && stores.length>0){
            	MainApplication.getInstance().setStores(stores);
            }
            storeName = getStoreName(storeId);
            sca.update(storeName);
            return ;
         }
         else{
	        HashMap<String,String> map = (HashMap<String, String>) obj;
	    	Intent intent = new Intent();
	        intent.setClass(ShopCarMakeFormActivity.this,MallShopCarActivity.class);
	        Bundle bundle = new Bundle();
	        if(map.get("success").equals("true")){
	        	
	        	
	            Toast.makeText(this, "订单已生成，请及时完成付款!", Toast.LENGTH_SHORT).show();
	            bundle.putBoolean("success", true);
	            
	            Intent i = new Intent(ShopCarMakeFormActivity.this, FormListActivity.class);
				i.putExtra("orderType", 1); //1:商品
				i.putExtra("getway",carEntity[0].getOrderProduct().getGetway());  //自行取货 1 ；快递：2
				i.putExtra("kind", 0);
				startActivity(i);
				finish();
	        }
	        else
	        if(map.get("success").equals("nostock")){
	        	String s = map.get("message");
	        	Toast.makeText(this, s==null?"":s, Toast.LENGTH_LONG).show();
	         	bundle.putBoolean("success", false);
	        }
	        intent.putExtras(bundle);
	        setResult(1,intent);
	        
	        
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
        case R.id.rl_top:
            Intent intent = new Intent();
            if(address != null){
	            intent.setClass(ShopCarMakeFormActivity.this,MakeFormSelectAddressActivity.class);
	            startActivityForResult(intent,1);
            }
            else{
	            Bundle bundle = new Bundle();
	            bundle.putString("sort", "makeform");
	            intent.putExtras(bundle);
	            intent.setClass(ShopCarMakeFormActivity.this,AddAddressActivity.class);
	            startActivityForResult(intent,2);
            }
            break;
        case R.id.btn_send:
            if(address==null){
                Toast.makeText(ShopCarMakeFormActivity.this,"请选择收货地址",Toast.LENGTH_SHORT).show();
                return;
            }
            isBuyDialog();
            break;
        }
    }

    //提交订单方法
    private void setTotlaPay(FormEntity form,boolean boo) {
        form.setIsWind("2"); //是否使用风豆 使用 1；不使用2
        form.setPayment(allPrice);//总付款额，包含运费
    }

    //确认是否要购买弹出框
    private void isBuyDialog() {
    	if(address == null){
	          Toast.makeText(ShopCarMakeFormActivity.this,"请选择收货地址",Toast.LENGTH_SHORT).show();
	          return;
    	}
        Activity activity = ShopCarMakeFormActivity.this;
        while (activity.getParent() != null) {
            activity = activity.getParent();
        }
        View view = null;
        view = LayoutInflater.from(ShopCarMakeFormActivity.this).inflate(R.layout.is_buy_dialog, null);
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

//    private void putForm(){
//        memberId = MainApplication.getInstance().getMember().getId();
//        FormEntity form = new FormEntity();
//        Member member =  new Member();
//        member.setId(memberId);
//        form.setMember(member);//赋值member
//        form.setOrderType("1");  //1是商品
//        form.setIsMoney("2");	//是否使用现金  使用 1；不使用2
//        form.setStoreId(storeId);//购买门店Id
//        OrderProductEntity[] orderProducts = new OrderProductEntity[carEntity.length];
//        for(int i=0;i<carEntity.length;++i){
//        	OrderProductEntity product = carEntity[i].getOrderProduct();
////        	orderProducts[i] = new OrderProductEntity();
////        	orderProducts[i].setPrice(product.getPrice());
////        	orderProducts[i].setNum(product.getNum());
////        	orderProducts[i].setProductId(product.getProductId());
////        	orderProducts[i].setGetway(product.getGetway());
//        	orderProducts[i] = product;
//            OrderFiledEntity[] fields = new OrderFiledEntity[1];
//            fields[0] = new OrderFiledEntity();
//            orderProducts[i].setOrderFields(fields);
//        }
//        form.setProducts(orderProducts);
//        switch(this.currentTag){
//        case MAIL: //送货上门
//            if(address==null){
//                Toast.makeText(ShopCarMakeFormActivity.this,"请选择收货地址",Toast.LENGTH_SHORT).show();
//                return;
//            }
//            form.setGetway("2");//提货方式   自行取货 1 ；快递：2
//            form.setAddId(address.getId());//用户地址Id
//            form.setFreight(freight);//赋值运费
//            //在这里判断是否有活动，然后如果总额超过多少可以免运费
//            setTotlaPay(form,true);
//            break;
//        case STORE://门店自提
//            if(address==null){
//                Toast.makeText(ShopCarMakeFormActivity.this,"请选择收货地址",Toast.LENGTH_SHORT).show();
//                return;
//            }
//            form.setGetway("1");//提货方式   自行取货 1 ；快递：2
//            form.setAddId(address.getId());//用户地址Id
//            setTotlaPay(form,false);
//            FormStoreEntity orderStores = new FormStoreEntity();
//            orderStores.setStoreId(storeId);
//            orderStores.setStoreName(storeName);
//            form.setOrderStores(orderStores);
//            break;
//        }
//        new OrderFormPost(ShopCarMakeFormActivity.this,form).setListener(ShopCarMakeFormActivity.this);
//    }
    //提交订单方法
    private void putForm(){
        if(carEntity==null || carEntity.length <= 0)return;
        memberId = MainApplication.getInstance().getMember().getId();
        FormEntity form = new FormEntity();
        Member member =  new Member();
        member.setId(memberId);
        form.setMember(member);//赋值member
        form.setOrderType("1");  //1是商品
        form.setIsMoney("2");//是否使用现金  使用 1；不使用2
        form.setStoreId(storeId);//购买门店Id
        AddCustomerAddress ad  = new AddCustomerAddress();
        ad.setAddress(address.getAddress());
        ad.setArea(address.getArea());
        ad.setMobile(address.getMobile());
        ad.setName(address.getName());
        ad.setTel(address.getTel());
        ad.setZipcode(address.getZipcode());
        ad.setMemberId(address.getMemberId());
        form.setAddress(ad);
//        form.setGetway("1");
        OrderProductEntity[]orderProducts = new OrderProductEntity[1];
        orderProducts[0] = new OrderProductEntity();
        OrderProductEntity productDetailEntity = carEntity[0].getOrderProduct();
		orderProducts[0].setPrice(productDetailEntity .getPrice());
        orderProducts[0].setProductId(productDetailEntity.getProductId());
        orderProducts[0].setNum(productDetailEntity.getNum());
        OrderFiledEntity[] fields;
        fields = new OrderFiledEntity[1];
        fields[0] = new OrderFiledEntity();
        orderProducts[0].setOrderFields(fields);
        form.setProducts(orderProducts);
        new OrderFormPost(ShopCarMakeFormActivity.this,form).setListener(ShopCarMakeFormActivity.this);
    }


    //从选择收货地址页面回来后调用这个方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       //选择收货地址返回
        if(requestCode == 1){
            if(data != null && data.getExtras() != null){
            	address = (CustomerAddressEntity) data.getExtras().getSerializable("address");
            	initCustomerAddress(address);
            }
            else{
                memberId = MainApplication.getInstance().getMember().getId();
            	adlg.update(memberId);
            }
        }
        else
        if(requestCode == 2){
        	if(data != null && data.getExtras() != null){
        		address = (CustomerAddressEntity) data.getExtras().getSerializable("address");
            	initCustomerAddress(address);
        	}
            else{
                memberId = MainApplication.getInstance().getMember().getId();
            	adlg.update(memberId);
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
    }

}
