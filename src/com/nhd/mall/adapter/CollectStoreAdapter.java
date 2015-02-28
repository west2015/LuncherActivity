package com.nhd.mall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.entity.BrandEntity;
import com.nhd.mall.entity.CollectionEntity;
import com.nhd.mall.entity.ProductDetailEntity;
import com.nhd.mall.util.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 14-4-10.
 */
public class CollectStoreAdapter extends BaseAdapter {
    private Context context;
    private ViewHolder holder=null;
    private CollectionEntity[] entity;
    public List<Boolean> checkBoxesStatus;
    public HashMap<Integer,Boolean>selectMap = new HashMap<Integer, Boolean>();
    public ImageLoader imageLoader;

    public CollectStoreAdapter( Context context, CollectionEntity[] entity){
        this.context = context;
        this.entity = entity;
        imageLoader = new ImageLoader(context);
        imageLoader.setDefaultBackgroup(R.drawable.goods_mr_img);
        imageLoader.setFailBackgroup(R.drawable.goods_mr_img);
        if(entity!=null){
            checkBoxesStatus = new ArrayList<Boolean>(entity.length);
            for(int i = 0;i<entity.length;i++){
                checkBoxesStatus.add(false);
            }
        }
    }
    public void update(CollectionEntity[] entity){
        this.entity = entity;
        if(entity!=null){
            selectMap.clear();
            checkBoxesStatus = new ArrayList<Boolean>(entity.length);
            for(int i = 0;i<entity.length;i++){
                checkBoxesStatus.add(false);
            }
        }
        notifyDataSetChanged();
    }
    public void selectAll(CollectionEntity[] entity){
        if(entity!=null){
            selectMap.clear();
            checkBoxesStatus = new ArrayList<Boolean>(entity.length);
            for(int i = 0;i<entity.length;i++){
                checkBoxesStatus.add(true);
                selectMap.put(i,true);
            }
            notifyDataSetChanged();
        }
    }
    public void noSelectAll(CollectionEntity[] entity){
        if(entity!=null){
            selectMap.clear();
            checkBoxesStatus = new ArrayList<Boolean>(entity.length);
            for(int i = 0;i<entity.length;i++){
                checkBoxesStatus.add(false);
            }
            notifyDataSetChanged();
        }
    }
    @Override
    public int getCount() {
        return entity==null?0:entity.length;
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        CollectionEntity collect = entity[position];
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.collect_store_item_layout, null);
            holder.cb = (CheckBox)convertView.findViewById(R.id.check);
            holder.ivImg = (ImageView)convertView.findViewById(R.id.imageView);
            holder.tvName = (TextView)convertView.findViewById(R.id.storeName);
            holder.tvCount = (TextView)convertView.findViewById(R.id.tvCollectCount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        //是商品
            ProductDetailEntity product = collect.getProducts();
            if(product!=null){
                holder.tvName.setText(product.getName()==null?"":product.getName());
                holder.tvCount.setText(collect.getCount()==null?"0人已收藏":collect.getCount()+"人已收藏");
                if(product.getThumb()==null){
                    holder.ivImg.setBackgroundResource(R.drawable.goods_mr_img);
                }
                else{
                    imageLoader.setBackgroup(product.getThumb(),holder.ivImg);
                }
            }
            else{
                holder.tvName.setText("");
                holder.tvCount.setText("0人已收藏");
            }
        boolean checkBoxStatus =  checkBoxesStatus.get(position);
        holder.cb.setTag(position);
        holder.cb.setChecked(checkBoxStatus);
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBoxesStatus.set((Integer)buttonView.getTag(), isChecked);
                if(isChecked){
                    selectMap.put(position,true);
                }
                else{
                    selectMap.put(position,false);
                }
            }
        });
        return convertView;
    }
    private  class ViewHolder {
        CheckBox cb;
        TextView tvName;
        TextView tvCount;
        ImageView ivImg;
    }
}
