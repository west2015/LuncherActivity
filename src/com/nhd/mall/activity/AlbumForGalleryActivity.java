package com.nhd.mall.activity;

import java.util.ArrayList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.WindowManager;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.util.ImageLoader;
import com.nhd.mall.zoom.HackyViewPager;
import com.nhd.mall.zoom.ImageViewFragment;
import com.nhd.mall.zoom.SamplePagerAdapter;
import com.umeng.analytics.MobclickAgent;
/**********************************************************
 * 内容摘要 ：
 * <p>
 * 当前图片集中的大图浏览页面
 * 作者 ：vendor 创建时间 ：2012-12-4 上午10:12:11
 * 历史记录 : 日期 : 2012-12-4 上午10:12:11 修改人：vendor 描述 :
 *********************************************************** 
 */
public class AlbumForGalleryActivity extends FragmentActivity implements OnPageChangeListener {
	/**头部工具栏 */
	public String[]strArr;
	private HackyViewPager mViewPager;
	private SamplePagerAdapter mFragmentPagerAdapter;
    private ArrayList<Fragment> fragments;
    private ImageLoader imageLoader;
    private int page=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 可以让界面不被弹出的键盘挤上去
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.album_gallery_layout);
		findView();
		initValue();
	}
	private void initValue() {
		if(getIntent().getExtras()!=null){
			strArr = getIntent().getExtras().getStringArray("gallery");
            page = getIntent().getExtras().getInt("page");
		}
        deal(strArr);
		for(int i=0;i<strArr.length;i++){
			fragments.add(ImageViewFragment.getInstance(strArr[i]));
		}
		mFragmentPagerAdapter=new SamplePagerAdapter(getSupportFragmentManager(), fragments);
		mViewPager.setAdapter(mFragmentPagerAdapter);
		mViewPager.setCurrentItem(page);
	}
	private void deal(String[] url) {
	  for(int i=0;i<url.length;i++){
		  String ss = url[i];
		  if(ss!=null){
			  if(ss.contains(".xs.")){
				url[i]=  ss.replace(".xs.",".origin.");
			  }
		  }
	  }
	}
	/**
	 * 监听以及查找控件
	 */
	private void findView() {
		imageLoader = new ImageLoader(this);
        imageLoader.setFailBackgroup(R.drawable.goods_detail_mr_img);
        imageLoader.setDefaultBackgroup(R.drawable.goods_detail_mr_img);
		mViewPager = (HackyViewPager)findViewById(R.id.vpMyEvent);
		fragments=new ArrayList<Fragment>();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
        if(imageLoader!=null){
            imageLoader.clearMemory();
            imageLoader=null;
        }
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}
	@Override
	public void onPageSelected(int arg0) {
		page=arg0;
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