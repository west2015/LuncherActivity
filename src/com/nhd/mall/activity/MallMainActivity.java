package com.nhd.mall.activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.MainAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.MainEntityGet;
import com.nhd.mall.asyncTask.ThemePicGet;
import com.nhd.mall.datebase.DbMainActivityCache;
import com.nhd.mall.datebase.DbStore;
import com.nhd.mall.entity.EventEntity;
import com.nhd.mall.entity.MainEntity;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.entity.TagEntity;
import com.nhd.mall.entity.ThemesEntity;
import com.nhd.mall.util.CheckUpdates;
import com.nhd.mall.util.ImageLoader;
import com.nhd.mall.util.OnAsyncTaskDataListener;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.nhd.mall.util.Utils;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.PageControl;
import java.util.HashMap;

/**
 * 内容摘要：
 * 新华都首页，展示banner以及图片的页面
 * 作者 ：caili 
 */
public class MallMainActivity extends Activity implements OnClickListener,PageControl.OnScreenSwitchListener,OnItemClickListener,OnAsyncTaskUpdateListener {
	//主要按钮控件
	private RelativeLayout rlAddress;
	private TextView tvAddress;
	private Button btnSearch;
	private View viewHead;
	private ListView listView;
    private MainAdapter ma;
    private LinearLayout linearGroup;
	//与banner有关
	private PageControl pageControl;
	private ImageView[]imgIcons;
	private LinearLayout layoutSign;
	private ImageLoader imageLoader;
    private ImageLoader imageTag;
    private int displayWidth =480;
    //获取数据
    private MainEntityGet meg;
    private Integer storeId =1;
    private MainEntity me;
    private TagEntity[]tags;
    private EventEntity[] activity;
    private ThemesEntity[] themes;
    private StoreEntity store;
    private ImageView ivSh;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mall_main_layout);
		findView();
	}
	private void findView() {
        new CheckUpdates(this).getCheckUpdate();
        viewHead = LayoutInflater.from(this).inflate(R.layout.main_lv_head, null);
		imageLoader = new ImageLoader(this);
		imageLoader.setFailBackgroup(R.drawable.banner_bg);
		imageLoader.setDefaultBackgroup(R.drawable.banner_bg);

        imageTag =  new ImageLoader(this);
        imageTag.setFailBackgroup(R.drawable.main_tag_default_img);
        imageTag.setDefaultBackgroup(R.drawable.main_tag_default_img);

		rlAddress = (RelativeLayout)findViewById(R.id.rlAddress);
		tvAddress = (TextView)findViewById(R.id.tvAddress);
		btnSearch = (Button)findViewById(R.id.btnSearch);
        linearGroup = (LinearLayout)viewHead.findViewById(R.id.radioGroup);
        ivSh = (ImageView)viewHead.findViewById(R.id.imgSh);
        ivSh.setOnClickListener(this);
		rlAddress.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
        if(MainApplication.getInstance().getStore()!=null){
            storeId = MainApplication.getInstance().getStore().getId();
            tvAddress.setText(MainApplication.getInstance().getStore().getName());
        }
        else{
            storeId=0;
            tvAddress.setText("新华都总店");
        }
		//头部按640*280的比例进行缩放
	     DisplayMetrics dm = new DisplayMetrics();
	    this.getWindowManager().getDefaultDisplay().getMetrics(dm);
	    displayWidth = dm.widthPixels;
		int height=displayWidth/(16/7);
		viewHead.findViewById(R.id.rlTopArea).setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height));

        //item按3*1比例进行缩放,对商户图片进行比例缩放
        int width = displayWidth - Utils.dip2px(this,30);//减去两边的留白
        int heightSh=width/(3/1);
        RelativeLayout.LayoutParams paramsSh = new RelativeLayout.LayoutParams(width, heightSh);
        paramsSh.addRule(Gravity.CENTER_HORIZONTAL,R.id.horizontalScrollView);
        paramsSh.setMargins(0,15,0,0);
		pageControl = (PageControl)viewHead.findViewById(R.id.pageControl);
		pageControl.setOnScreenSwitchListener(this);
	    layoutSign = (LinearLayout)viewHead.findViewById(R.id.viewPage);
	    //初始化和listview有关的控件
	    listView =(ListView)findViewById(R.id.listView);
	    ma = new MainAdapter(this,themes,displayWidth);
	    listView.addHeaderView(viewHead);
	    listView.setAdapter(ma);
	    listView.setOnItemClickListener(this);
        meg = new MainEntityGet(this,storeId);
        meg.setListener(this);
        readToObject();
	}
	/**
	 * banner滑动监听
	 */
	@Override
	public void onScreenSwitched(int screen) {
		if(imgIcons==null)return;
			if (screen <imgIcons.length) {
				for (int i = 0; i < imgIcons.length; i++) {
					if (i == screen) {
						imgIcons[i].setBackgroundResource(R.drawable.banner_btn_xx2);
					} else {
						imgIcons[i].setBackgroundResource(R.drawable.banner_btn_xx1);
					}
				}
		} 
	}
	/**
	 * banner图片点击监听
	 */
	@Override
	public void onScreenClick(int screen) {
        if(activity==null || activity.length<=screen)return;
        if(activity[screen].getEntry().equals("url")){  //是一个网页
        String url = activity[screen].getUrl();
            Bundle bundleUrl = new Bundle();
            bundleUrl.putString("url",url);
            new startIntent(MallMainActivity.this,ProductWebViewActivity.class,bundleUrl);
        }
        else{          //是一个商品组
            Bundle bundle = new Bundle();
            bundle.putInt("eventId",activity[screen].getId());
            new startIntent(MallMainActivity.this,EventProductActivity.class,bundle);
        }
    }
	/**
	 * 初始化banner
	 */
	private void updateTop(EventEntity[]activity) {
		pageControl.removeAllViews();
		layoutSign.removeAllViews();
        if(activity==null||activity.length==0)return;
		for(int i=0;i<activity.length;i++){
			View view = LayoutInflater.from(MallMainActivity.this).inflate(R.layout.main_top_item, null);
			ImageView imageView = (ImageView) view.findViewById(R.id.imageViewTop);
			imageLoader.setBackgroup(activity[i].getThumb(), imageView);
			pageControl.addView(view);
		}
		int screen =0;
			imgIcons = new ImageView[activity.length];
			for (int i = 0; i < activity.length; i++) {
				ImageView imgSign = new ImageView(MallMainActivity.this);
				android.widget.LinearLayout.LayoutParams p = new android.widget.LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				p.setMargins(2, 0, 2, 0);
				imgSign.setLayoutParams(p);
				if (i != screen) {
					imgSign.setBackgroundResource(R.drawable.banner_btn_xx1);
				} else {
					imgSign.setBackgroundResource(R.drawable.banner_btn_xx2);
				}
				imgIcons[i] = imgSign;
				layoutSign.addView(imgSign);
			}
		this.onScreenSwitched(0);
}
	/**
	 * 按钮点击监听
	 */
	@Override
	public void onClick(View v) {
		switch(v.getId()){
            case R.id.imgSh:
                new startIntent(MallMainActivity.this,ShangHuActivity.class);
                break;
		case R.id.rlAddress:
            Intent it = new Intent(MallMainActivity.this,ShopBranchActivity.class);
            startActivityForResult(it, 1);
			break;
		case R.id.btnSearch:
            new startIntent(MallMainActivity.this,SearchActivity.class);
			break;
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
            if(themes==null)return;
            position = position-1;
        if(themes[position].getEntry()==null)return;
        if(themes[position].getEntry().equals("url")){
            String url = themes[position].getUrl();
            Bundle bundleUrl = new Bundle();
            bundleUrl.putString("url",url);
            new startIntent(MallMainActivity.this,ProductWebViewActivity.class,bundleUrl);
        }
        else {
            Bundle bundleTh = new Bundle();
            bundleTh.putInt("themeId", themes[position].getId());
            bundleTh.putString("sort","theme");
            bundleTh.putString("search_for","theme");
            bundleTh.putString("title",themes[position].getName()==null?"":themes[position].getName());
            new startIntent(MallMainActivity.this,ThemeGoodsActivity.class,bundleTh);
        }
	}
    @Override
    public void getData(Object obj, String message) {
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (obj == null)
            return;
        if(obj instanceof MainEntity){
            me = (MainEntity) obj;
            tags = me.getTags();
            themes = me.getThemes();
            activity = me.getActivitys();
            ma.update(themes);
            updateTop(activity);
            updateButtonTag(tags);
            parseToJson();
        }
        if(obj instanceof StoreEntity){
            StoreEntity ss = (StoreEntity) obj;
            MainApplication.getInstance().setStore(ss);
            new DbStore(MallMainActivity.this).update(ss);
        }
    }
    //赋值各个tag，标签。
    private void updateButtonTag(TagEntity[] tag) {
        linearGroup.removeAllViews();
        if(tags == null || tags.length<=0) return;
        int len = 3;
        if(tags.length < len) len = tags.length;
        for(int i=0;i<len;i++) {
            final int count = i;
            View view = LayoutInflater.from(this).inflate(R.layout.main_tag_item_layout,null);
            int width = (this.displayWidth - Utils.dip2px(this,60)) / 3;
            int height = Utils.dip2px(this,40);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            params.setMargins(0, 0, Utils.dip2px(this,15), 0);
            RelativeLayout rl = (RelativeLayout)view.findViewById(R.id.rl_tag);
            rl.setLayoutParams(params);
            ImageView img = (ImageView)view.findViewById(R.id.imageView);
            TextView tv = (TextView)view.findViewById(R.id.textView);
            imageTag.setBackgroup(tags[i].getPic(),img);
            tv.setText(tags[i].getName() == null ? "" : tags[i].getName());
            linearGroup.addView(view);
            rl.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
		            if(tags[count] == null || tags[count].getId() == null) return;
		            if(count==0){
		            	Bundle bundle = new Bundle();
		            	bundle.putInt("sort",0);
		            	new startIntent(MallMainActivity.this,CouponListActivity.class,bundle);
		             }
		             else
		             if(count==1){
		            	 Bundle bundle = new Bundle();
		            	 bundle.putInt("sort",1);
		            	 new startIntent(MallMainActivity.this,CouponBuyActivity.class,bundle);
		             }
		             else {
		            	 Bundle bundle = new Bundle();
		            	 bundle.putInt("tagsId", tags[count].getId());
		            	 bundle.putString("search_for","tag");
		            	 String name = tags[count].getName();
		                 bundle.putString("tagsName",name);
		                 new startIntent(MallMainActivity.this,HotGoodsActivity.class,bundle);
		             }
                }
            });
        }
    }
    //从门店列表返回后出发这个方法进行相应的数据更改
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if(data != null){
                    Bundle bundle =  data.getExtras();
                    store = (StoreEntity) bundle.getSerializable("store");
                    storeId = store.getId();
                    tvAddress.setText(store.getName());
                    MainApplication.getInstance().setStore(store);
                    new DbStore(MallMainActivity.this).update(store);
                    if(meg==null){
                        meg = new MainEntityGet(this,storeId);
                        meg.setListener(this);
                    }
                    else{
                        meg.update(storeId);
                    }
                }
                break;
            default:
                break;
        }
    }
    private void parseToJson() {
        DbMainActivityCache db = new DbMainActivityCache(this);
        String jsonNews = ParseJson.getObjectJackson(me);
        db.setCache("jsonNews", jsonNews);
    }
    /**
     * @Title: readToObject
     * @Description: TODO(读取首页已保存的json数据)
     * @Return void
     * @throws
     */
    private void readToObject() {
        DbMainActivityCache db = new DbMainActivityCache(this);
        /** 新闻资讯集合 */
        me = ParseJson.parse(db.getCache("jsonNews", "{}"), MainEntity.class);
        tags = me.getTags();
        themes = me.getThemes();
        activity = me.getActivitys();
        ma.update(themes);
        updateTop(activity);
        updateButtonTag(tags);
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
        if(imageTag!=null){
            imageTag.clearMemory();
            imageTag=null;
        }
        if(ma!=null){
            ma.imageLoader.clearMemory();
            ma.imageLoader=null;
        }
	}

}
