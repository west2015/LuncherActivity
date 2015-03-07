package com.nhd.mall.activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.ShopCarAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.CarListGet;
import com.nhd.mall.asyncTask.DeleteCarGet;
import com.nhd.mall.asyncTask.MainEntityGet;
import com.nhd.mall.asyncTask.StoreListGet;
import com.nhd.mall.datebase.DbStore;
import com.nhd.mall.entity.CarEntity;
import com.nhd.mall.entity.CarList;
import com.nhd.mall.entity.OrderProductEntity;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.entity.StoreListEntity;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.Utils;
import com.nhd.mall.widget.ModelActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 内容摘要 ：
 * <p>
 * 新华都购物车页面
 * 作者 ：caili 
 */
public class MallShopCarActivity extends ModelActivity implements View.OnClickListener,OnAsyncTaskUpdateListener,CompoundButton.OnCheckedChangeListener,ShopCarAdapter.checkAllPrice {
    private ListView listView;
    private TextView allPrice;
    private ShopCarAdapter sca;
    private CarListGet clg;
    private Long memberId;
    private CarList cl;
    private CarEntity[] entity;
    //加载进度
    private ImageView ivLoad;
    private CheckBox cbAll;
    private Dialog deleteDialog;
    //用来保存删除的购物车id
    ArrayList<Integer> idList = new ArrayList<Integer>();
    private Button btnSure;
    private TextView tvFerightRule;
    private StoreListGet slg;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_car_layout);
        setTitle("购物车");
        find();
    }
    
    private void find() {
        tvFerightRule = (TextView)findViewById(R.id.tvFreightRule);
        btnSure = (Button)findViewById(R.id.btnSure);
        cbAll = (CheckBox)findViewById(R.id.all_check);
        cbAll.setOnCheckedChangeListener(this);
        ivLoad = (ImageView)findViewById(R.id.ivLoad);
        listView = (ListView)findViewById(R.id.listView);
        allPrice = (TextView)findViewById(R.id.tv_all_prive);
        findViewById(R.id.all_check).setOnClickListener(this);
        //findViewById(R.id.btnSure).setOnClickListener(this);
        sca = new ShopCarAdapter(this,entity);
        listView.setAdapter(sca);
        if(getIntent().getExtras() != null){
        	getButtonBack().setVisibility(View.VISIBLE);
        }
        else{
            getButtonBack().setVisibility(View.GONE);
        }
        getButton(R.drawable.shop_car_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sca==null)return;
                if(sca.selectMap.size()<=0){
                	Toast.makeText(MallShopCarActivity.this, "请选择要删除的商品", Toast.LENGTH_SHORT).show();
                    return;
                }
                deleteDialog();
            }
        });
        //点击返回首页
        findViewById(R.id.car_nothing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.backToMain(MallShopCarActivity.this);
            }
        });
        //更新门店列表
        if(MainApplication.getInstance().getStores() == null){
            slg = new StoreListGet(this);
            slg.setListener(this);
        }
    }
    
    //删除的方法
    public void delete(){
        idList.clear();
        for(int i:sca.selectMap.keySet()){
            if(sca.selectMap.get(i)){
                CarEntity car = entity[i];
                idList.add(car.getId());
            }
        }
	    Integer[] ids = new Integer[idList.size()];
	    ids = idList.toArray(ids);
	    new DeleteCarGet(MallShopCarActivity.this,ids).setListener(MallShopCarActivity.this);
    }

    public void setFreightRule(boolean boo,double rule){
           if(boo){
               findViewById(R.id.rlFreightRule).setVisibility(View.VISIBLE);
               tvFerightRule.setText(rule+"");
           }
         else{
               findViewById(R.id.rlFreightRule).setVisibility(View.GONE);
               tvFerightRule.setText(rule+"");
           }
    }
    private void deleteDialog() {
        View view = null;
        view = LayoutInflater.from(MallShopCarActivity.this).inflate(R.layout.delete_dialog, null);
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
                sca.selectMap.clear();
            }
        });
        deleteDialog = new Dialog(MallShopCarActivity.this, R.style.planDialog);
        deleteDialog.setCancelable(true);
        deleteDialog.setContentView(view);
        deleteDialog.show();
    }
    
    //删除成功后返回进行刷新
    private void successNotify() {
        ArrayList<CarEntity> entityList = new ArrayList<CarEntity>();
        for(int i=0;i<entity.length;i++){
            entityList.add(entity[i]);
        }
        for(int i=0;i<idList.size();i++){
            for(int j=0;j<entityList.size();j++){
                if(entityList.get(j).getId() == idList.get(i)){
                    entityList.remove(j);
                    break;
                }
            }
        }
        CarEntity[] car = new CarEntity[entityList.size()];
        car = entityList.toArray(car);
        entity = car;
        if(entity.length<=0){
            findViewById(R.id.car_nothing).setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.car_nothing).setVisibility(View.GONE);
        }
        sca.update(entity);
    }
    @Override
    public void onClick(View view) {
      switch(view.getId()){
//          case R.id.btnSure:
//              if(sca.selectMap.size()<=0){
//                  Toast.makeText(MallShopCarActivity.this,"请选择商品",Toast.LENGTH_SHORT).show();
//                  return;
//              }
//              ArrayList<OrderProductEntity>order = new ArrayList<OrderProductEntity>();
//              for(int i:sca.selectMap.keySet()){
//                  if(sca.selectMap.get(i)){
//                      CarEntity car = entity[i];
//                      order.add(car.getOrderProduct());
//                  }
//              }
//              MainApplication.getInstance().setOrder(order);
//              new startIntent(MallShopCarActivity.this,ShopCarFormActivity.class);
//              break;
      }
    }

    @Override
    public void getData(Object obj, String message) {
        stop();
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (obj == null){
            findViewById(R.id.car_nothing).setVisibility(View.VISIBLE);
            return;
        }
        if(obj instanceof CarList){
            findViewById(R.id.car_nothing).setVisibility(View.GONE);
            cl = (CarList) obj;
            entity = cl.getCars();
            sortEntity();
            if(entity.length<=0){
                findViewById(R.id.car_nothing).setVisibility(View.VISIBLE);
            }
            else{
                findViewById(R.id.car_nothing).setVisibility(View.GONE);
                OrderProductEntity orderProduct  = entity[0].getOrderProduct();
            }
            setIsEvent();
            sca.update(entity);
            return;
        }
        if(obj instanceof StoreListEntity){
        	StoreListEntity sle = (StoreListEntity) obj;
            StoreEntity[] store = sle.getStores();
            if(store != null && store.length > 0){
                MainApplication.getInstance().setStores(store);
                sca.update(entity);
            }
            return ;
        }
        HashMap<String,String> map = (HashMap<String, String>) obj;
        if(map.get("success").equals("true")){
            successNotify();
        }
    }
    
    // 排序方法
    private boolean compare(OrderProductEntity op1,OrderProductEntity op2){
    	if(op1.getStoreId()<op2.getStoreId()) return true;
    	if(op1.getStoreId()>op2.getStoreId()) return false;
    	if(op1.getGetway ()<op2.getGetway() ) return true;
    	return false;
    }
    
    //按门店id+配送方式给列表排序
    private void sortEntity(){
    	if(entity == null || entity.length<=0) return ;
    	int length = entity.length;
    	for(int i=0;i<entity.length;++i)
    	if(entity[i].getOrderProduct().getGetway() == null){
    		--length;
    	}
    	CarEntity[] mEntity = new CarEntity[length];
    	for(int i=0,j=0;i<entity.length;++i)
    	if(entity[i].getOrderProduct().getGetway() != null){
    		mEntity[j++] = entity[i];
    	}
//    	for(int i=0,j=0;i<entity.length;++i,++j){
//    		mEntity[j] = entity[i];
//    		if(mEntity[j].getOrderProduct().getGetway() == null){
//    			mEntity[j].getOrderProduct().setGetway(2);
//    		}
//    	}
    	for(int i=0;i<mEntity.length;++i){
    		for(int j=0;j<mEntity.length-i-1;++j)
    		if(compare(mEntity[j].getOrderProduct(),mEntity[j+1].getOrderProduct())){
    			CarEntity temp = mEntity[j];
    			mEntity[j] = mEntity[j+1];
    			mEntity[j+1] = temp;
    		}
    	}
    	entity = mEntity;
    	// Test Code
//    	CarEntity[] tEntity = new CarEntity[2*mEntity.length];
//    	for(int i=0,j=0;i<mEntity.length;++i){
//    		tEntity[j++]=mEntity[i];
//    		tEntity[j++]=mEntity[i];
//    	}
//    	entity = tEntity;
    }
    private void setIsEvent() {
        if(entity==null||entity.length<=0){
            setFreightRule(false,0);
            return;
        }
        boolean boo = false;
        double count = 0;
        for(int i=0;i<entity.length;i++){
            OrderProductEntity orderProduct = entity[i].getOrderProduct();
            if(orderProduct!=null){
                if(orderProduct.getFreightRule()>0){
                    boo = true;
                    count = orderProduct.getFreightRule();
                    break;
                }
            }

        }
        if(boo){
            setFreightRule(true,count);
        }
        else{
            setFreightRule(false,0);
        }
    }

    public void start(){
        findViewById(R.id.rl_progress).setVisibility(View.VISIBLE);
        Animation operatingAnim = AnimationUtils.loadAnimation(MallShopCarActivity.this, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        ivLoad.startAnimation(operatingAnim);
    }
    public void stop(){
        findViewById(R.id.rl_progress).setVisibility(View.GONE);
        ivLoad.clearAnimation();
    }
    //通过全选与非全选然后修改合计的钱数量
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(sca==null)return;
        if(entity==null||entity.length<=0)return;
        //全选
        if(b){
            sca.selectAll(entity);
            double price =0;
            for(int i=0;i<entity.length;i++){
                OrderProductEntity order = entity[i].getOrderProduct();
                price+=order.getPrice()*order.getNum();
            }
            allPrice.setText("￥"+price);
            btnSure.setText("结算"+"("+sca.selectMap.size()+")");
        }
        else{
            sca.noSelectAll(entity);
            allPrice.setText("￥0");
            btnSure.setText("结算"+"("+sca.selectMap.size()+")");
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(!AndroidServerFactory.PRODUCTION_MODEL){
            StatService.onResume(this);
        }
        if(MainApplication.getInstance().getMember()!=null){
            memberId = MainApplication.getInstance().getMember().getId();
            if(clg==null){
                clg = new CarListGet(MallShopCarActivity.this,memberId);
                clg.setListener(MallShopCarActivity.this);
            }
            else{
                clg.update();
            }
//            if(sca!=null){
//                sca.selectMap.clear();
//            }
            cbAll.setChecked(false);
            btnSure.setText("结算");
            allPrice.setText("￥0");
            start();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sca!=null){
            sca.imageLoader.clearMemory();
            sca.imageLoader=null;
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
	public void submitForm(int storeId,CarList carList) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(MallShopCarActivity.this,ShopCarMakeFormActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("storeid", storeId);
		bundle.putSerializable("carlist", carList);
		intent.putExtras(bundle);
		startActivityForResult(intent, 1);
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case 1:
        	if(data == null ){
        		listView.setAdapter(sca);
        		return ;
        	}
        	if(data.getExtras() != null){
        		boolean success = data.getExtras().getBoolean("success");
        		if(success){
        			delete();
        		}
        	}
        	break;
        }
    }

}
