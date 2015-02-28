package com.nhd.mall.activity;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mobstat.StatService;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import java.util.ArrayList;
/**
 * Created by Administrator on 14-5-29.
 */
public class ShopMapActivity extends Activity {
    /**
     *  MapView 是地图主控件
     */
    private MapView mMapView = null;
    /**
     *  用MapController完成地图控制
     */
    private MapController mMapController = null;
    private MyOverlay mOverlay = null;
    private PopupOverlay   pop  = null;
    private ArrayList<OverlayItem>  mItems = null;
    /**
     * overlay 位置坐标
     */
    double mLon1 = 0 ;
    double mLat1 = 0 ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 使用地图sdk前需先初始化BMapManager.
         * BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
         * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
         */
        MainApplication app = (MainApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(getApplicationContext());
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            app.mBMapManager.init(MainApplication.strKey,new MainApplication.MyGeneralListener());
        }
        if(getIntent().getExtras()!=null){
            mLon1 = getIntent().getExtras().getDouble("lon");
            mLat1 = getIntent().getExtras().getDouble("lat");
        }
        /**
         * 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
         */
        setContentView(R.layout.activity_overlay);
        mMapView = (MapView)findViewById(R.id.bmapView);
        /**
         * 获取地图控制器
         */
        mMapController = mMapView.getController();
        /**
         *  设置地图是否响应点击事件  .
         */
        mMapController.enableClick(true);
        /**
         * 设置地图缩放级别
         */
        mMapController.setZoom(14);
        /**
         * 显示内置缩放控件
         */
        mMapView.setBuiltInZoomControls(true);

        initOverlay();

        /**
         * 设定地图中心点
         */
        GeoPoint p = new GeoPoint((int)(mLat1 * 1E6), (int)(mLon1* 1E6));
        mMapController.setCenter(p);
    }

    public void initOverlay(){
        /**
         * 创建自定义overlay
         */
        mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.icon_gcoding),mMapView);
        /**
         * 准备overlay 数据
         */
        GeoPoint p1 = new GeoPoint ((int)(mLat1*1E6),(int)(mLon1*1E6));
        OverlayItem item1 = new OverlayItem(p1,"覆盖物1","");
        /**
         * 设置overlay图标，如不设置，则使用创建ItemizedOverlay时的默认图标.
         */
        item1.setMarker(getResources().getDrawable(R.drawable.icon_gcoding));
        /**
         * 将item 添加到overlay中
         * 注意： 同一个itme只能add一次
         */
        mOverlay.addItem(item1);
        /**
         * 保存所有item，以便overlay在reset后重新添加
         */
        mItems = new ArrayList<OverlayItem>();
        mItems.addAll(mOverlay.getAllItem());
        mMapView.getOverlays().add(mOverlay);
        /**
         * 刷新地图
         */
        mMapView.refresh();
    }
    @Override
    protected void onPause() {
        /**
         *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
         */
        mMapView.onPause();
        super.onPause();
        if(!AndroidServerFactory.PRODUCTION_MODEL){
            StatService.onPause(this);
        }
    }

    @Override
    protected void onResume() {
        /**
         *  MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
         */
        mMapView.onResume();
        super.onResume();
        if(!AndroidServerFactory.PRODUCTION_MODEL){
            StatService.onResume(this);
        }
    }

    @Override
    protected void onDestroy() {
        /**
         *  MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
         */
        mMapView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMapView.onRestoreInstanceState(savedInstanceState);
    }

    public class MyOverlay extends ItemizedOverlay{

        public MyOverlay(Drawable defaultMarker, MapView mapView) {
            super(defaultMarker, mapView);
        }
        @Override
        public boolean onTap(GeoPoint pt , MapView mMapView){
            if (pop != null){
                pop.hidePop();
                //  mMapView.removeView(button);
            }
            return false;
        }
    }
}

