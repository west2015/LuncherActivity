package com.nhd.mall.activity;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.BranchSortAdapter;
import com.nhd.mall.adapter.ShopBranchAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.BranchGet;
import com.nhd.mall.asyncTask.StoreListGet;
import com.nhd.mall.entity.BranchEntity;
import com.nhd.mall.entity.BranchList;
import com.nhd.mall.entity.BrandEntity;
import com.nhd.mall.util.NetCheck;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.PullDownView;

import java.util.HashMap;

/**购物页面点击首个九宫格跳转进来的品牌列表包含搜索功能
 * Created by caili on 14-6-20.
 */
public class BeanchSearchActivity extends Activity implements View.OnClickListener,TextWatcher,OnAsyncTaskUpdateListener,PullDownView.OnPullDownListener,AdapterView.OnItemClickListener {

    private EditText etSearch;
    private PullDownView listView;
    private Integer storeId;
    private BranchGet get;
    private Integer pageNum=1;
    private BranchEntity[]entity;
    private BranchSortAdapter adapter;
    private ImageView ivLoad;
    private BranchList list;
    private final int LIST=0;
    private final int SEARCH =1;
    private int currentTag=0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beanch_search_layout);
        findView();
    }
    private void findView() {
        ivLoad = (ImageView)findViewById(R.id.imageView);
        etSearch = (EditText)findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnSearch).setOnClickListener(this);
        adapter = new BranchSortAdapter(this,entity);
        listView = (PullDownView)findViewById(R.id.shopListView);
        listView.setOnPullDownListener(this);
        listView.setAdapter(adapter);
        listView.getListView().setFooterDividersEnabled(false);
        listView.enableAutoFetchMore(true, 1);
        listView.setHideHeader();
        listView.setHideFooter();
        listView.setOnItemClickListener(this);

        if(MainApplication.getInstance().getStore()!=null){
            storeId = MainApplication.getInstance().getStore().getId();
        }
        if( NetCheck.checkNet(this)){
            start();
            currentTag = LIST;
            get = new BranchGet(this,pageNum,storeId,null,null,null);
            get.setListener(this);
        }
    }
    public void start(){
        ivLoad.setVisibility(View.VISIBLE);
        Animation operatingAnim = AnimationUtils.loadAnimation(BeanchSearchActivity.this, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        ivLoad.startAnimation(operatingAnim);
    }
    public void stop(){
        ivLoad.clearAnimation();
        ivLoad.setVisibility(View.GONE);
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnSearch:
                if(etSearch.getText()==null||etSearch.getText().toString().equals(""))return;
                currentTag = SEARCH;
                pageNum=1;
                get.update(pageNum,storeId,null,null,etSearch.getText().toString());
                break;
            case R.id.btnBack:
                finish();
                break;
        }
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }
    @Override
    public void afterTextChanged(Editable editable) {
        if(editable.toString().equals("")){
             pageNum=1;
            currentTag = LIST;
            get.update(pageNum,storeId,null,null,null);
        }
    }
    @Override
    public void getData(Object obj, String message) {
        stop();
        listView.RefreshComplete();
        listView.notifyDidMore();
        if (message != null)
            Toast.makeText(BeanchSearchActivity.this, message, Toast.LENGTH_LONG).show();
        if (obj == null){
            findViewById(R.id.rl_have_no_content).setVisibility(View.VISIBLE);
            return;
        }
        if(obj instanceof BranchList){
             list = (BranchList) obj;
            entity = list.getBrands();
            adapter.update(entity);
            if(entity==null||entity.length<=0){
                findViewById(R.id.rl_have_no_content).setVisibility(View.VISIBLE);
            }
            else{
                findViewById(R.id.rl_have_no_content).setVisibility(View.GONE);
                if(entity.length<10){
                    listView.setHideFooter();
                }
                else{
                    listView.setShowFooter();
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(entity==null||entity.length<=0)return;
        position = position-1;
        Bundle bundle = new Bundle();
        bundle.putInt("brandId",entity[position].getBrandId());
        bundle.putString("sort","brand");
        bundle.putString("name",entity[position].getBrandName());
        bundle.putString("search_for","normal");
        new startIntent(BeanchSearchActivity.this,ThemeGoodsActivity.class,bundle);
    }
    @Override
    public void onRefresh() {
    }

    @Override
    public void onMore() {
        pageNum++;
        if(currentTag==LIST){
            get.getMore(list, pageNum, storeId, null, null, null);
        }
        else{
            if(etSearch.getText()==null||etSearch.getText().toString().equals(""))return;
            get.getMore(list, pageNum, storeId, null, null,etSearch.getText().toString());
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
