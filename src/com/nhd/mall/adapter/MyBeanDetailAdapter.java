package com.nhd.mall.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.entity.BeanDetailEntity;
import com.nhd.mall.entity.CollectionEntity;
import com.nhd.mall.entity.FormEntity;
import com.nhd.mall.entity.OrderProductEntity;
import com.nhd.mall.util.ImageLoader;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Administrator on 14-4-9.
 */
public class MyBeanDetailAdapter extends BaseAdapter {
    private Context context;
    private ViewHolder holder=null;
    private BeanDetailEntity[] entity;
    public ImageLoader imageLoader;
    public List<Boolean> checkBoxesStatus;
    public HashMap<Integer,Boolean> selectMap = new HashMap<Integer, Boolean>();

    public MyBeanDetailAdapter( Context context, BeanDetailEntity[] entity){
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
    @Override
    public int getCount() {
        return entity==null?0:entity.length;
    }
    public void update(BeanDetailEntity[] entity){
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
    public void selectAll(BeanDetailEntity[] entity){
        if(entity!=null){
            this.entity = entity;
            selectMap.clear();
            checkBoxesStatus = new ArrayList<Boolean>(entity.length);
            for(int i = 0;i<entity.length;i++){
                checkBoxesStatus.add(true);
                selectMap.put(i,true);
            }
            notifyDataSetChanged();
        }
    }
    public void noSelectAll(BeanDetailEntity[] entity){
        if(entity!=null){
            this.entity = entity;
            selectMap.clear();
            checkBoxesStatus = new ArrayList<Boolean>(entity.length);
            for(int i = 0;i<entity.length;i++){
                checkBoxesStatus.add(false);
            }
            notifyDataSetChanged();
        }
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
        BeanDetailEntity bean = entity[position];
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.bean_detail_item_layout, null);
            holder.cb = (CheckBox)convertView.findViewById(R.id.check);
            holder.tvDate = (TextView)convertView.findViewById(R.id.tvDate);
            holder.useNum = (TextView)convertView.findViewById(R.id.tvUseCount);
            holder.llAdd = (LinearLayout)convertView.findViewById(R.id.ll_addView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.tvDate.setText(bean.getUseDate()==null?"":bean.getUseDate());
        holder.useNum.setText(String.valueOf(bean.getNum()));
        initProduct(holder,bean);
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
                    if(selectMap.containsKey(position)){
                        selectMap.remove(position);
                    }
                }
            }
        });
        return convertView;
    }

    private void initProduct(ViewHolder holder, BeanDetailEntity bean) {
        holder.llAdd.removeAllViews();
        FormEntity order = bean.getOrder();
        OrderProductEntity[]products = order.getProducts();
        if(products==null||products.length<=0)return;
        for(int i=0;i<products.length;i++){
            OrderProductEntity product = products[i];
            View view  = LayoutInflater.from(context).inflate(R.layout.bean_detail_product_layout,null);
            TextView tvName = (TextView)view.findViewById(R.id.goodsName);
            TextView tvPrice = (TextView)view.findViewById(R.id.goodsPrice);
            TextView tvNum = (TextView)view.findViewById(R.id.tvProductNum);
            ImageView ivImg = (ImageView)view.findViewById(R.id.imageView);
             LinearLayout devide = (LinearLayout)view.findViewById(R.id.llDevide);

            tvName.setText(product.getName()==null?"":product.getName());
            tvName.setText(String.valueOf(product.getPrice()));
            tvNum.setText(String.valueOf(product.getNum()));
            imageLoader.setBackgroup(product.getThumb(),ivImg);
            if(i==products.length-1){
                devide.setVisibility(View.GONE);
            }
            else{
                devide.setVisibility(View.VISIBLE);
            }
            holder.llAdd.addView(view);
        }
    }

    private  class ViewHolder {
        private CheckBox cb;
        private TextView tvDate;
        private TextView useNum;
        private LinearLayout llAdd;

    }
}
