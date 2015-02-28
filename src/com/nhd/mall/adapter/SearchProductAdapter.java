package com.nhd.mall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.entity.ProductDetailEntity;
import com.nhd.mall.util.ImageLoader;

/**
 * Created by Administrator on 14-4-29.
 */
public class SearchProductAdapter  extends BaseAdapter {
    private Context context;
    private ViewHolder holder=null;
    private ProductDetailEntity[]entity;
    public ImageLoader imageLoader;

    public SearchProductAdapter( Context context,ProductDetailEntity[]entity){
        this.context = context;
        this.entity = entity;
        imageLoader = new ImageLoader(context);
        imageLoader.setDefaultBackgroup(R.drawable.goods_mr_img);
        imageLoader.setFailBackgroup(R.drawable.goods_mr_img);
    }
    public void update(ProductDetailEntity[]entity){
        this.entity = entity;
        notifyDataSetChanged();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ProductDetailEntity product = entity[position];
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.search_product_item_layout, null);
            holder.image = (ImageView)convertView.findViewById(R.id.imageView);
            holder.tvName = (TextView)convertView.findViewById(R.id.goodsName);
            holder.tvPrice = (TextView)convertView.findViewById(R.id.goodsPrice);
            holder.tvOldPrice = (TextView)convertView.findViewById(R.id.tvOldPrice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        if(product.getThumb()==null){
            holder.image.setBackgroundResource(R.drawable.goods_mr_img);
        }
        else{
            imageLoader.setBackgroup(product.getThumb(),holder.image);
        }
        holder.tvName.setText(product.getName()==null?"":product.getName());
        holder.tvPrice.setText(product.getPrice()==null?"￥0":"￥"+String.valueOf(product.getPrice()));
        holder.tvOldPrice.setText("市场价: ￥"+product.getOldPrice());
        return convertView;
    }
    private  class ViewHolder {
        private ImageView image;
        private TextView tvName;
        private TextView tvPrice;
        private TextView tvOldPrice;
    }
}
