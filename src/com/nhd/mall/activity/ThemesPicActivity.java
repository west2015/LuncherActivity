package com.nhd.mall.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.util.ImageLoader;
import com.nhd.mall.util.startIntent;

/**主题购物图片页面
 * Created by Administrator on 14-6-30.
 */
public class ThemesPicActivity extends Activity implements View.OnClickListener {
    private int themeId;
    private String title;
    private ImageView ivPic;
    private ImageLoader loder;
    private String url;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.themes_pic_layout);
        findView();
    }
    private void findView() {
        loder = new ImageLoader(this);
        ivPic  = (ImageView)findViewById(R.id.imageView);
        ivPic.setOnClickListener(this);
        if(getIntent().getExtras()!=null){
            themeId = getIntent().getExtras().getInt("themeId");
            title = getIntent().getExtras().getString("themeName");
            url = getIntent().getExtras().getString("url");
        }
        loadUrl(url);
    }
    private void loadUrl(String url) {
        loder.setBackgroup(url,ivPic);
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.imageView:
                Bundle bundleTh = new Bundle();
                bundleTh.putInt("themeId",themeId);
                bundleTh.putString("sort","theme");
                bundleTh.putString("search_for","theme");
                bundleTh.putString("title",title);
                new startIntent(ThemesPicActivity.this,ThemeGoodsActivity.class,bundleTh);
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loder!=null){
            loder.clearMemory();
        }
    }
}
