package com.nhd.mall.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.RadioGroup;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.MyFragmentPagerAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.widget.ModelActivity;

import java.util.ArrayList;

/**搜索商品跳进去的页面
 * Created by Administrator on 14-4-28.
 */
public class SearchProductActivity extends ModelActivity implements RadioGroup.OnCheckedChangeListener,ViewPager.OnPageChangeListener{
    private RadioGroup rg;
    private ViewPager mViewPager;
    private MyFragmentPagerAdapter mFragmentPagerAdapter;
    private ArrayList<Fragment> fragments;
    private String sort;
    private String query;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.themes_goods_layout);
        findView();
        initView();
    }
    private void findView() {
        rg = (RadioGroup)findViewById(R.id.theme_rg);
        rg.setOnCheckedChangeListener(this);
        mViewPager=(ViewPager)findViewById(R.id.themesviewPager);
    }
    private void initView(){
        if(getIntent().getExtras()!=null){
            query = getIntent().getExtras().getString("query");
            setTitle(query);
                fragments=new ArrayList<Fragment>();
                fragments.add(SearchProductFragment.getInstance(query,null));
                fragments.add(SearchProductFragment.getInstance(query,"1"));
                fragments.add(SearchProductFragment.getInstance(query,"2"));
                mFragmentPagerAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
                mViewPager.setAdapter(mFragmentPagerAdapter);
                mViewPager.setCurrentItem(0);
                mViewPager.setOnPageChangeListener(this);
        }
    }
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch(radioGroup.getCheckedRadioButtonId()){
            case R.id.theme_rbMr:  //默认
                mViewPager.setCurrentItem(0);
                break;
            case R.id.theme_rbxl:   //销量
                mViewPager.setCurrentItem(1);
                break;
            case R.id.theme_rbJg:   //价格
                mViewPager.setCurrentItem(2);
                break;
        }
    }
    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }
    @Override
    public void onPageSelected(int i) {
//当滑动完毕时设置相应按钮的背景
        if(i==0){
            rg.check(R.id.theme_rbMr);
        }else if(i==1){
            rg.check(R.id.theme_rbxl);
        }
        else{
            rg.check(R.id.theme_rbJg);
        }
    }
    @Override
    public void onPageScrollStateChanged(int i) {
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
