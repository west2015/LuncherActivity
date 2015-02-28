package com.nhd.mall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.MyFragmentPagerAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.datebase.DbHistory;
import com.nhd.mall.util.startIntent;

import java.util.ArrayList;
/**
 * 物品搜索界面
 * Created by caili on 14-4-4.
 */
public class SearchActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener,ViewPager.OnPageChangeListener {
    private RadioGroup rg;
    private ViewPager mViewPager;
    private MyFragmentPagerAdapter mFragmentPagerAdapter;
    private ArrayList<Fragment> fragments;
    private EditText etSearch;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        findView();
        initView();
    }
    private void findView() {
        etSearch = (EditText)findViewById(R.id.etSearch);
        rg = (RadioGroup)findViewById(R.id.search_rg);
        rg.setOnCheckedChangeListener(this);
        mViewPager=(ViewPager)findViewById(R.id.searchPager);
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnSearch).setOnClickListener(this);
        findViewById(R.id.btnEwm).setOnClickListener(this);
    }
    private void initView(){
        fragments=new ArrayList<Fragment>();
        fragments.add(SearchHotFragment.getInstance());
        fragments.add(SearchHistoryFragment.getInstance());
        mFragmentPagerAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(this);
    }
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
       switch(radioGroup.getCheckedRadioButtonId()){
           case R.id.search_history:
               mViewPager.setCurrentItem(1);
               break;
           case R.id.search_hot:
               mViewPager.setCurrentItem(0);
               break;
       }
    }
    @Override
    public void onClick(View view) {
     switch(view.getId()){
         case R.id.btnBack:
             finish();
             break;
          case R.id.btnSearch:
             if(etSearch.getText()==null||etSearch.getText().toString().equals("")){
                 Toast.makeText(SearchActivity.this,"请输入搜索的内容",Toast.LENGTH_SHORT).show();
                 return;
             }
              new DbHistory(SearchActivity.this).insertHistory(etSearch.getText().toString());
              Bundle bundleTh = new Bundle();
              bundleTh.putString("query",etSearch.getText().toString());
              new startIntent(SearchActivity.this,SearchProductActivity.class,bundleTh);
             break;
         case R.id.btnEwm:
             Intent it = new Intent(SearchActivity.this,TwoDimCodeActivity.class);
             startActivityForResult(it, 1);
             break;
     }
    }
    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        if(i==0){
            rg.check(R.id.search_hot);
        }
        else{
            rg.check(R.id.search_history);
        }
    }
    @Override
    public void onPageScrollStateChanged(int i) {
    }
    @Override
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
