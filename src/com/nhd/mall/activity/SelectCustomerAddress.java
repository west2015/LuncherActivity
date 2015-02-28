package com.nhd.mall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.CustomerAddressAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.AddressListGet;
import com.nhd.mall.asyncTask.DeleteAddressPost;
import com.nhd.mall.entity.AddCustomerAddress;
import com.nhd.mall.entity.CustomerAddressEntity;
import com.nhd.mall.entity.CustomerAddressEntityList;
import com.nhd.mall.entity.MainEntity;
import com.nhd.mall.push.BaidupushUtils;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

/**选择用户地址界面
 * Created by caili on 14-4-6.
 */
public class SelectCustomerAddress extends ModelActivity implements View.OnClickListener ,CustomerAddressAdapter.OnClickAddress,OnAsyncTaskUpdateListener {
    private ListView lvAddress;
    private CustomerAddressAdapter caa;
    private int click = -1;
    //获取地址列表接口
    private AddressListGet adlg;
    private Long memberId;
    private CustomerAddressEntityList entityList;
    private CustomerAddressEntity[] entity;
    private String sort;//判断是从哪里跳过来的，如果是从订单页面传过来的 选择某一项后就直接跳回订单页面去改变收货地址

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_address_layout);
        setTitle("选择收货地址");
        find();
        register();
    }

    private void register() {
        getButton(R.drawable.add_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new startIntent(SelectCustomerAddress.this,AddAddressActivity.class);
            }
        });
    }

    private void find() {
        if(getIntent().getExtras()!=null){
            sort = getIntent().getExtras().getString("sort");
        }
        findViewById(R.id.delete).setOnClickListener(this);
        findViewById(R.id.modify).setOnClickListener(this);
        lvAddress = (ListView)findViewById(R.id.lvAddress);
        caa = new CustomerAddressAdapter(this,entity,click);
        lvAddress.setAdapter(caa);
    }

    @Override
    public void getData(Object obj, String message) {
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (obj == null)
            return;
        if(obj instanceof CustomerAddressEntityList){
            entityList = (CustomerAddressEntityList) obj;
            entity = entityList.getAddress();
            caa.update(entity,-1);
            return;
        }
        HashMap<String,String> map = (HashMap<String, String>) obj;
        if(map.get("success").equals("true")){
            Toast.makeText(SelectCustomerAddress.this,"删除成功",Toast.LENGTH_SHORT).show();
            ArrayList<CustomerAddressEntity>entityList = new ArrayList<CustomerAddressEntity>();
            for(int i=0;i<entity.length;i++){
                if(i!=click){
                    entityList.add(entity[i]);
                }
            }
            CustomerAddressEntity[]ce = new CustomerAddressEntity[entity.length-1];
            entity = entityList.toArray(ce);
            click =-1;
            caa.update(entity, click);
    }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.delete:
                if(click==-1){
                    Toast.makeText(SelectCustomerAddress.this,"请选择删除的选项",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(entity[click]==null)return;
                Integer addressId = entity[click].getId();
                new DeleteAddressPost(SelectCustomerAddress.this,addressId).setListener(SelectCustomerAddress.this); 
                break;
            case R.id.modify:
                if(click==-1){
                    Toast.makeText(SelectCustomerAddress.this,"请选择修改的选项",Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle bundle = new Bundle();
                CustomerAddressEntity address = entity[click];
                bundle.putSerializable("address",address);
                new startIntent(SelectCustomerAddress.this,ModifyAdressActivity.class,bundle);
                break;
        }
    }
    public void onPause() {
        super.onPause();
        if(!AndroidServerFactory.PRODUCTION_MODEL){
            StatService.onPause(this);
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
            if(adlg==null){
                adlg = new AddressListGet(this,memberId);
                adlg.setListener(this);
            }
            else{
                adlg.update(memberId);
            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void getClickPosition(int position) {
      click = position;
        if(sort!=null){
         Intent intent = new Intent();
            intent.setClass(SelectCustomerAddress.this,MakeFormActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("address",entity[position]);
            intent.putExtras(bundle);
            setResult(1,intent);
            finish();
        }
        else{
            caa.update(entity,click);
        }

    }
}
