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

/**主题购物点击进去跳进去的界面
 * Created by Administrator on 14-4-4.
 */
public class ThemeGoodsActivity extends ModelActivity implements RadioGroup.OnCheckedChangeListener,ViewPager.OnPageChangeListener {
    private RadioGroup rg;
    private ViewPager mViewPager;
    private MyFragmentPagerAdapter mFragmentPagerAdapter;
    private ArrayList<Fragment> fragments;
    private String search_for;
    private Integer themeId;
    private String sort;
    private Integer categoryId;//分类ID
    private Integer brandId; //品牌ID

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.themes_goods_layout);
        setTitle("主题购物");
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
            sort = getIntent().getExtras().getString("sort");
            //从主题那边传过来的
            if(sort.equals("theme")){
                if(getIntent().getExtras().getString("title")==null){
                    setTitle("主题购物");
                }
                else{
                    setTitle(getIntent().getExtras().getString("title"));
                }
                search_for=getIntent().getExtras().getString("search_for");
                themeId=getIntent().getExtras().getInt("themeId");
                fragments=new ArrayList<Fragment>();
                fragments.add(GoodsListFragment.getInstance("theme",search_for,0,themeId,0,0,""));
                fragments.add(GoodsListFragment.getInstance("theme",search_for,0,themeId,0,0,"sales"));
                fragments.add(GoodsListFragment.getInstance("theme",search_for,0,themeId,0,0,"price"));
                mFragmentPagerAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
                mViewPager.setAdapter(mFragmentPagerAdapter);
                mViewPager.setCurrentItem(0);
                mViewPager.setOnPageChangeListener(this);
            }
            //从二维码扫描品牌后跳过来的
            else if(sort.equals("brand")){
                if(getIntent().getExtras().getString("name")!=null && !getIntent().getExtras().getString("name").equals("")){
                    setTitle(getIntent().getExtras().getString("name"));
                }else{
                    setTitle("品牌商品");
                }
                search_for=getIntent().getExtras().getString("search_for");
                brandId=getIntent().getExtras().getInt("brandId");
                fragments=new ArrayList<Fragment>();
                fragments.add(GoodsListFragment.getInstance("branch",search_for,0,0,0,brandId,""));
                fragments.add(GoodsListFragment.getInstance("branch",search_for,0,0,0,brandId,"sales"));
                fragments.add(GoodsListFragment.getInstance("branch",search_for,0,0,0,brandId,"price"));
                mFragmentPagerAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
                mViewPager.setAdapter(mFragmentPagerAdapter);
                mViewPager.setCurrentItem(0);
                mViewPager.setOnPageChangeListener(this);
            }
            //从筛选那边传过来的
            else{
                setTitle("分类筛选");
                search_for=getIntent().getExtras().getString("search_for");
                categoryId=getIntent().getExtras().getInt("category");
                fragments=new ArrayList<Fragment>();
                fragments.add(GoodsListFragment.getInstance("filter",search_for,0,0,categoryId,0,""));
                fragments.add(GoodsListFragment.getInstance("filter",search_for,0,0,categoryId,0,"sales"));
                fragments.add(GoodsListFragment.getInstance("filter",search_for,0,0,categoryId,0,"price"));
                mFragmentPagerAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
                mViewPager.setAdapter(mFragmentPagerAdapter);
                mViewPager.setCurrentItem(0);
                mViewPager.setOnPageChangeListener(this);
            }
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
