package com.nhd.mall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.entity.DoorEntity;
import com.nhd.mall.util.ImageLoader;

/**
 * Created by Administrator on 14-5-5.
 */
public class DaohanAdapter extends BaseAdapter {
    private Context context;
    private ViewHolderLeft holderLeft=null;
    public ImageLoader imageLoader;
    private DoorEntity[] entity;


    public DaohanAdapter(Context context ,DoorEntity[] entity) {
        this.context = context;
        this.entity = entity;
        imageLoader = new ImageLoader(context);
        imageLoader.setDefaultBackgroup(R.drawable.main_theme_default);
        imageLoader.setFailBackgroup(R.drawable.main_theme_default);
    }
    public void update(DoorEntity[] entity){
        this.entity = entity;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return entity==null?0:entity.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DoorEntity door = entity[position];
        if (convertView == null) {
            holderLeft = new ViewHolderLeft();
            convertView = LayoutInflater.from(context).inflate(R.layout.daohang_lv_item_layout, null);
            holderLeft.tv = (TextView) convertView.findViewById(R.id.floorName);
            convertView.setTag(holderLeft);
        }
        else {
            holderLeft = (ViewHolderLeft)convertView.getTag();
        }
//        if(door.getNavigateImg()==null){
//            holderLeft.iv.setBackgroundResource(R.drawable.main_theme_default);
//        }
//        else{
//            imageLoader.setBackgroup(door.getNavigateImg(),holderLeft.iv);
//        }
//        //item按3*1比例进行缩放
//        int width = displayWidth - Utils.dip2px(context, 30);//减去两边的留白
//        int height=width/(3/1);
//        holderLeft.iv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        holderLeft.tv.setText(door.getName()==null?"":door.getName());
        return convertView;
    }
    //要更换的图片在左边的布局
    private  class ViewHolderLeft {
        private TextView tv;
    }
}
