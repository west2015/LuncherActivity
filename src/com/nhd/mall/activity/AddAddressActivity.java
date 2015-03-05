package com.nhd.mall.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.AreaSelectAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.AddCustomerAddressPost;
import com.nhd.mall.asyncTask.AddressListGet;
import com.nhd.mall.asyncTask.RegionGet;
import com.nhd.mall.datebase.DbAddress;
import com.nhd.mall.entity.AddCustomerAddress;
import com.nhd.mall.entity.CustomerAddressEntity;
import com.nhd.mall.entity.CustomerAddressEntityList;
import com.nhd.mall.entity.RegionEntity;
import com.nhd.mall.entity.RegionList;
import com.nhd.mall.util.OnAsyncTaskDataListener;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;
import com.umeng.analytics.MobclickAgent;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

/**添加收货地址界面
 * Created by caili on 14-4-6.
 */
public class AddAddressActivity extends ModelActivity implements View.OnClickListener,OnAsyncTaskUpdateListener,OnAsyncTaskDataListener {
    private AddCustomerAddress customer;
    private EditText etName,etPhone,etAddress,etZipCode;
    private TextView tvArea;
    private Long memberId=0L;
    private String prePage;
    private AddressListGet adlg = null;
    private CustomerAddressEntityList addressList = null;
    private CustomerAddressEntity[] addressEntity = null;
    private CustomerAddressEntity address = null;
    private RegionEntity[] provinceEntity;  //存放省份的类集合
    private RegionEntity[] cityEntity;//存放城市的类集合
    private RegionEntity[] areaEntity;//存放区域的类集合
    //与区域选择框有关的属性
    private Dialog selectDialog;
    private TextView tvProvince;   //所选择的省份
    private TextView tvCity;       //所选择的城市
    private TextView tvDiqu;       //所选择的地区
    //与省份选择列表有关的属性
    private Dialog provinceDialog;
    private ListView  provinceListView;
    private AreaSelectAdapter provinceAdapter;
    private RegionEntity entityProvince;
    //与选择城市有关的属性
    private Dialog cityDialog;
    private ListView  cityListView;
    private AreaSelectAdapter cityAdapter;
    private RegionEntity entityCity;
    //与选择区域有关的类
    private Dialog areaDialog;
    private ListView  areaListView;
    private AreaSelectAdapter areaAdapter;
    private RegionEntity entityArea;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_customer_address_layout);
        setTitle("添加收货地址");
        find();
    }

    private void find() {
        findViewById(R.id.btnSure).setOnClickListener(this);
        etName = (EditText)findViewById(R.id.etName);
        etPhone = (EditText)findViewById(R.id.etPhone);
        etAddress = (EditText)findViewById(R.id.etAdress);
        etZipCode = (EditText)findViewById(R.id.etZipCode);
        tvArea = (TextView)findViewById(R.id.tvArea);
        tvArea.setOnClickListener(this);

        getExtras();
        select();
        selectProvince();
        selectCity();
        selectArea();
    }

    private void getExtras(){
    	prePage = null;
    	if(getIntent().getExtras() != null){
    		Bundle bundle = getIntent().getExtras();
    		prePage = bundle.getString("sort");
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
            address = addressEntity[0];
        	Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("address",address);
            intent.putExtras(bundle);
            intent.setClass(AddAddressActivity.this,ShopCarMakeFormActivity.class);
            setResult(2,intent);
            finish();
            return;
        }
        HashMap<String,String>map = (HashMap<String, String>) obj;
        if(map.get("success").equals("true")){
        	if(prePage != null && prePage.equals("makeform")){
            	adlg = new AddressListGet(AddAddressActivity.this,memberId);
    	        adlg.setListener(AddAddressActivity.this);
        	}
        	else{
                Toast.makeText(AddAddressActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                finish();
        	}
        }
        else{
            Toast.makeText(AddAddressActivity.this,"添加失败",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void getDataSort(Object obj, String message, String sort) {
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (obj == null)return;
        if(obj instanceof RegionList){
            RegionList list = (RegionList) obj;
            if(sort.equals("province")){
                provinceEntity = list.getRegions();
                if(provinceAdapter!=null){
                    provinceAdapter.update(provinceEntity);
                }
            }
            if(sort.equals("city")){
                cityEntity = list.getRegions();
                if(cityAdapter!=null){
                    cityAdapter.update(cityEntity);
                }
            }
            if(sort.equals("area")){
                areaEntity = list.getRegions();
                if(areaAdapter!=null){
                    areaAdapter.update(areaEntity);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.tvProvince:
                if(provinceDialog!=null){
                    provinceDialog.show();
                }
                break;
            case R.id.tvCity:
                if(entityProvince==null||tvProvince.getText()==null||tvProvince.getText().toString().equals("")){
                    Toast.makeText(AddAddressActivity.this,"请先选择省份",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(cityDialog!=null){
                    cityDialog.show();
                }
                new RegionGet(AddAddressActivity.this,entityProvince.getRegionId(),"city").setListener(AddAddressActivity.this);
                break;
            case R.id.tvDiqu:
                if(entityCity==null||tvCity.getText()==null||tvCity.getText().toString().equals("")){
                    Toast.makeText(AddAddressActivity.this,"请先选择城市",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(areaDialog!=null){
                    areaDialog.show();
                }
                new RegionGet(AddAddressActivity.this,entityCity.getRegionId(),"area").setListener(AddAddressActivity.this);
                break;
            case R.id.tvArea://点击选择省份城市
                selectDialog.show();
                break;
            case R.id.btnSure:
                if(etName.getText()==null||etName.getText().toString().equals("")){
                    Toast.makeText(AddAddressActivity.this,"请填写您的姓名",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etPhone.getText()==null||etPhone.getText().toString().equals("")){
                    Toast.makeText(AddAddressActivity.this,"请填写您的联系电话",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etZipCode.getText()==null||etZipCode.getText().toString().equals("")){
                    Toast.makeText(AddAddressActivity.this,"请填写您所在地区的邮政编码",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tvArea.getText()==null||tvArea.getText().toString().equals("")){
                    Toast.makeText(AddAddressActivity.this,"请填写您的所在地区",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etAddress.getText()==null||etAddress.getText().toString().equals("")){
                    Toast.makeText(AddAddressActivity.this,"请填写您的详细地址",Toast.LENGTH_SHORT).show();
                    return;
                }
                if( MainApplication.getInstance().getMember()==null){
                    new startIntent(AddAddressActivity.this,LoginActivity.class);
                    return;
                }
                memberId = MainApplication.getInstance().getMember().getId();
                customer = new AddCustomerAddress();
                customer.setAddress(etAddress.getText().toString());
                customer.setName(etName.getText().toString());
                customer.setMobile(etPhone.getText().toString());
                customer.setMemberId(memberId);
                customer.setZipcode(etZipCode.getText().toString());
                customer.setArea(tvArea.getText().toString());
                new AddCustomerAddressPost(AddAddressActivity.this,customer).setListener(AddAddressActivity.this);
                break;
        }
    }
    //弹出区域选择选项框
    private void select() {
            View view = null;
            view = LayoutInflater.from(AddAddressActivity.this).inflate(R.layout.region_select_dialog, null);
            tvProvince = (TextView)view.findViewById(R.id.tvProvince);
            tvCity = (TextView)view.findViewById(R.id.tvCity);
            tvDiqu = (TextView)view.findViewById(R.id.tvDiqu);
            tvProvince.setOnClickListener(AddAddressActivity.this);
            tvCity.setOnClickListener(AddAddressActivity.this);
            tvDiqu.setOnClickListener(AddAddressActivity.this);

            view.findViewById(R.id.btnSure).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectDialog.dismiss();
                    if(tvProvince.getText()!=null&&!tvProvince.getText().toString().equals("")&&tvCity.getText()!=null&&!tvCity.getText().toString().equals("")&&tvDiqu.getText()!=null&&!tvDiqu.getText().toString().equals("")){
                        tvArea.setText(tvProvince.getText().toString()+tvCity.getText().toString()+tvDiqu.getText().toString());
                    }
                }
            });
            view.findViewById(R.id.btnQuit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectDialog.dismiss();
                }
            });
        selectDialog = new Dialog(AddAddressActivity.this, R.style.planDialog);
        selectDialog.setCancelable(true);
        selectDialog.setContentView(view);
    }
    //弹出选择省份的列表框
    private void selectProvince() {
        View view = null;
        view = LayoutInflater.from(AddAddressActivity.this).inflate(R.layout.region_province_dialog, null);
        provinceListView = (ListView)view.findViewById(R.id.listView);
        provinceAdapter = new AreaSelectAdapter(this,provinceEntity);
        provinceListView.setAdapter(provinceAdapter);
        provinceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,long l) {
                if(provinceEntity==null||provinceEntity.length<=0)return;
                entityProvince = provinceEntity[position];
                if(tvProvince!=null){
                    tvProvince.setText(entityProvince.getRegionName()==null?"":entityProvince.getRegionName());
                }
                provinceDialog.dismiss();
            }
        });
        provinceDialog = new Dialog(AddAddressActivity.this, R.style.planDialog);
        provinceDialog.setCancelable(true);
        provinceDialog.setContentView(view);
        Window dialogWindow = provinceDialog.getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.9); // 高度设置为屏幕的0.8
        p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9
        dialogWindow.setAttributes(p);
        new RegionGet(this,1,"province").setListener(this);
    }
    //弹出选择城市的列表框
    private void selectCity() {
        View view = null;
        view = LayoutInflater.from(AddAddressActivity.this).inflate(R.layout.region_province_dialog, null);
        cityListView = (ListView)view.findViewById(R.id.listView);
        cityAdapter = new AreaSelectAdapter(this,cityEntity);
        cityListView.setAdapter(cityAdapter);
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,long l) {
                if(cityEntity==null||cityEntity.length<=0)return;
                entityCity = cityEntity[position];
                if(tvCity!=null){
                    tvCity.setText(entityCity.getRegionName()==null?"":entityCity.getRegionName());
                }
                cityDialog.dismiss();
            }
        });
        cityDialog = new Dialog(AddAddressActivity.this, R.style.planDialog);
        cityDialog.setCancelable(true);
        cityDialog.setContentView(view);
        Window dialogWindow = cityDialog.getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.9); // 高度设置为屏幕的0.8
        p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9
        dialogWindow.setAttributes(p);
        new RegionGet(this,1,"province").setListener(this);
    }
    //弹出选择区域的列表框
    private void selectArea() {
        View view = null;
        view = LayoutInflater.from(AddAddressActivity.this).inflate(R.layout.region_province_dialog, null);
        areaListView = (ListView)view.findViewById(R.id.listView);
        areaAdapter = new AreaSelectAdapter(this,areaEntity);
        areaListView.setAdapter(areaAdapter);
        areaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,long l) {
                if(areaEntity==null||areaEntity.length<=0)return;
                entityArea = areaEntity[position];
                if(tvDiqu!=null){
                    tvDiqu.setText(entityArea.getRegionName()==null?"":entityArea.getRegionName());
                }
                areaDialog.dismiss();
            }
        });
        areaDialog = new Dialog(AddAddressActivity.this, R.style.planDialog);
        areaDialog.setCancelable(true);
        areaDialog.setContentView(view);
        Window dialogWindow = areaDialog.getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.9); // 高度设置为屏幕的0.8
        p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9
        dialogWindow.setAttributes(p);
        new RegionGet(this,1,"province").setListener(this);
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
