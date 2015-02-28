package com.nhd.mall.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.nhd.mall.R;
import com.nhd.mall.activity.GoodsFilterFragment;
import com.nhd.mall.slidemenu.SlidingFragmentActivity;
import com.nhd.mall.slidemenu.SlidingMenu;

public class BaseActivity extends SlidingFragmentActivity {

    private Fragment mFrag;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBehindContentView(R.layout.menu_frame);
        FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
        mFrag = new GoodsFilterFragment();
        t.replace(R.id.menu_frame, mFrag);
        t.commit();
        SlidingMenu sm = getSlidingMenu();
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeDegree(0.35f);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
    }

}

