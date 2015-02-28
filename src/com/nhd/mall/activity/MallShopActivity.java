package com.nhd.mall.activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import com.nhd.mall.adapter.MenuGridViewAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.asyncTask.GoodsFilterGet;
import com.nhd.mall.entity.GoodsFilterEntity;
import com.nhd.mall.entity.GoodsFilterList;
import com.nhd.mall.entity.GoodsFilterSecondEntity;
import com.nhd.mall.entity.GoodsFilterThirdEntity;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.LineGridView;

/**
 * 内容摘要 ：
 * 新华都购物页面
 * 作者 ：caili 
 */
public class MallShopActivity extends Activity  implements View.OnClickListener,AdapterView.OnItemClickListener,OnAsyncTaskUpdateListener {

    private EditText etSearch;
    private LineGridView lg;
    private MenuGridViewAdapter mga;
    //与获取物品里分类等级接口有关
    private GoodsFilterList list;
    private GoodsFilterEntity[]filter;
    private GoodsFilterGet gfg;
    //加载进度
    private ImageView ivLoad;
    private int weight=160;

     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_shop_layout);
        findView();
    }
    private void findView() {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int displayWidth = dm.widthPixels;
        weight=displayWidth/3;
        ivLoad = (ImageView)findViewById(R.id.ivLoad);
        etSearch = (EditText)findViewById(R.id.etSearch);
        findViewById(R.id.btnDaohan).setOnClickListener(this);
        findViewById(R.id.btnSearch).setOnClickListener(this);
        findViewById(R.id.btnEwm).setOnClickListener(this);
        lg = (LineGridView)findViewById(R.id.gvMenu);
        mga = new MenuGridViewAdapter(this,filter,weight);
        start();
        lg.setAdapter(mga);
        lg.setOnItemClickListener(this);
        gfg = new GoodsFilterGet(this);
        gfg.setListener(this);
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnDaohan:
                new startIntent(MallShopActivity.this,DaoHanDoorActivity.class);
                break;
            case R.id.btnSearch:
                if(etSearch.getText()==null||etSearch.getText().toString().equals("")){
                    Toast.makeText(MallShopActivity.this,"请输入搜索的内容",Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle bundleTh = new Bundle();
                bundleTh.putString("query",etSearch.getText().toString());
                new startIntent(this,SearchProductActivity.class,bundleTh);
                break;
            case R.id.btnEwm:
                Intent it = new Intent(MallShopActivity.this,TwoDimCodeActivity.class);
                startActivityForResult(it, 1);
                break;
        }
    }
    @Override
    public void getData(Object obj, String message) {
        stop();
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (obj == null)
            return;
        if(obj instanceof GoodsFilterList){
            list = (GoodsFilterList) obj;
            filter = list.getCategorys();
            mga.update(filter);
        }
    }
    public void start(){
    findViewById(R.id.rl_progress).setVisibility(View.VISIBLE);
        Animation operatingAnim = AnimationUtils.loadAnimation(MallShopActivity.this, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        ivLoad.startAnimation(operatingAnim);
    }
    public void stop(){
        findViewById(R.id.rl_progress).setVisibility(View.GONE);
        ivLoad.clearAnimation();
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(i==0){
            new startIntent(MallShopActivity.this,BeanchSearchActivity.class);
            return;
        }
        if(filter==null||filter.length<=0)return;
        if(filter[i-1].getChildren()==null||filter[i-1].getChildren().length<=0)return;
        Bundle bundle  = new Bundle();
        bundle.putSerializable("goods",list);
        bundle.putString("name",filter[i-1].getName());
        bundle.putInt("position",i-1);
       new startIntent(MallShopActivity.this,shopGoodsListActivity.class,bundle);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if(data != null){
                    String result = data.getStringExtra("result");
                    if(result != null)
                        etSearch.setText(result);
                }
                break;
            default:
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mga!=null){
            mga.imageLoader.clearMemory();
            mga.imageLoader=null;
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
