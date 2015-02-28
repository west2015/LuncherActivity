package com.nhd.mall.adapter;

import java.util.ArrayList;

import android.app.ActionBar;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nhd.mall.R;
import com.nhd.mall.entity.GoodsFilterEntity;
import com.nhd.mall.util.ImageLoader;

public class MenuGridViewAdapter extends BaseAdapter {
	private Context context;
	private String[] menuTittle;
    private GoodsFilterEntity[]entity;
    public ImageLoader imageLoader;
    private  int weight=160;

	public MenuGridViewAdapter(Context context,GoodsFilterEntity[]entity,int weight) {
		this.entity=entity;
		this.context = context;
        this.weight = weight;
        imageLoader = new ImageLoader(context);
        imageLoader.setDefaultBackgroup(R.drawable.goods_mr_img);
        imageLoader.setFailBackgroup(R.drawable.goods_mr_img);
	}
	public void update(GoodsFilterEntity[]entity) {
        this.entity=entity;
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
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.main_menu_item, null);
			vh.tvMenuTitle = (TextView) convertView.findViewById(R.id.tvMenuTitle);
			vh.ivMenuIcon = (ImageView) convertView.findViewById(R.id.ivMenuIcon);
            vh.rlLayout = (RelativeLayout) convertView.findViewById(R.id.rl_layout);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
        if(position==0){
            vh.ivMenuIcon.setBackgroundResource(R.drawable.shop_branch_icon);
            vh.tvMenuTitle.setText("品牌搜索");
        }
        else{
            GoodsFilterEntity goods = entity[position-1];
            if(goods.getThumb()==null){
                vh.ivMenuIcon.setBackgroundResource(R.drawable.goods_mr_img);
            }
            else{
                imageLoader.setBackgroup(goods.getThumb(),vh.ivMenuIcon);
            }
            vh.tvMenuTitle.setText(goods.getName()==null?"":goods.getName());
    }
		return convertView;
	}
	class ViewHolder {
		TextView tvMenuTitle;
		ImageView ivMenuIcon;
        RelativeLayout rlLayout;
	}
}
