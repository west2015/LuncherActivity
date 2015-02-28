package com.nhd.mall.activity;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.DaohanAdapter;
import com.nhd.mall.adapter.MainAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.DoorDaohanGet;
import com.nhd.mall.asyncTask.MainEntityGet;
import com.nhd.mall.datebase.DbStore;
import com.nhd.mall.entity.CollectionList;
import com.nhd.mall.entity.DoorEntity;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.PullDownView;

import org.w3c.dom.Text;

import java.util.HashMap;

/**楼层导航页面
 * Created by caili on 14-4-5.
 */
public class DaoHanDoorActivity extends Activity implements AdapterView.OnItemClickListener,OnAsyncTaskUpdateListener {
    private TextView tvTitle;
    private TextView name;
    private PullDownView lvDoor;
    private DaohanAdapter ma;
    private Integer storeId =1;
    private DoorDaohanGet ddg;
    private DoorEntity[] entity;
//    private TextView tvAddress;
//    //加载进度
//    private ImageView ivLoad;
//    private int displayWidth =480;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daohang_layout);
        findView();
    }
    private void findView() {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = (int)(dm.heightPixels*0.9);
        int width = (int)(dm.widthPixels*0.9);
        findViewById(R.id.daohan).setLayoutParams(new ViewGroup.LayoutParams(width,height));
//        ivLoad = (ImageView)findViewById(R.id.ivLoad);
//        tvAddress = (TextView)findViewById(R.id.tvAddress);
//        findViewById(R.id.btnBack).setOnClickListener(this);
//        findViewById(R.id.rlAddress).setOnClickListener(this);
        lvDoor = (PullDownView)findViewById(R.id.daoHangListView);
        ma = new DaohanAdapter(this,entity);
        lvDoor.setAdapter(ma);
        lvDoor.setOnItemClickListener(this);
        if(MainApplication.getInstance().getStore()!=null){
            storeId = MainApplication.getInstance().getStore().getId();
//            tvAddress.setText(MainApplication.getInstance().getStore().getName());
        }
        else{
            storeId=0;
//            tvAddress.setText("新华都总店");
        }
//        start();
        lvDoor.setHideFooter();
        lvDoor.setHideHeader();
        ddg = new DoorDaohanGet(this,storeId);
        ddg.setListener(this);

    }
    @Override
    public void getData(Object obj, String message) {
        stop();
        if (message != null)
            Toast.makeText(DaoHanDoorActivity.this, message, Toast.LENGTH_LONG).show();
        if (obj == null){
            return;
        }
        if(obj instanceof DoorEntity[]){
            entity = (DoorEntity[]) obj;
            ma.update(entity);
        }
    }
//    public void start(){
//        findViewById(R.id.rl_progress).setVisibility(View.VISIBLE);
//        Animation operatingAnim = AnimationUtils.loadAnimation(DaoHanDoorActivity.this, R.anim.rotate);
//        LinearInterpolator lin = new LinearInterpolator();
//        operatingAnim.setInterpolator(lin);
//        ivLoad.startAnimation(operatingAnim);
//    }
    public void stop(){
//        findViewById(R.id.rl_progress).setVisibility(View.GONE);
//        ivLoad.clearAnimation();
    }
//    public void onClick(View view) {
//        switch(view.getId()){
//            case R.id.rlAddress:
//                Intent itAddress = new Intent();
//                itAddress.setClass(DaoHanDoorActivity.this,ShopBranchActivity.class);
//                Bundle bundleStore = new Bundle();
//                bundleStore.putString("from","daohan");
//                itAddress.putExtras(bundleStore);
//                startActivityForResult(itAddress,1);
//                break;
//            case R.id.btnBack:
//                finish();
//                break;
//        }
//    }
    //从门店列表返回后出发这个方法进行相应的数据更改
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if(data != null){
                    Bundle bundle =  data.getExtras();
                    StoreEntity  store = (StoreEntity) bundle.getSerializable("store");
                    storeId = store.getId();
//                    tvAddress.setText(store.getName());
//                    start();
                    if(ddg==null){
                        ddg = new DoorDaohanGet(this,storeId);
                        ddg.setListener(this);
                    }
                    else{
                        ddg.update(storeId);
                    }
                }
                break;
            default:
                break;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(entity==null||entity.length==0)return;
        String[] url = new String[1];
        url[0] = entity[i].getFloorImg();
        Bundle bundle = new Bundle();
        bundle.putStringArray("gallery",url);
        bundle.putInt("page",0);
        new startIntent(DaoHanDoorActivity.this,AlbumForGalleryActivity.class,bundle);
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
        if(ma!=null){
            ma.imageLoader.clearMemory();
            ma.imageLoader=null;
        }
    }

}
