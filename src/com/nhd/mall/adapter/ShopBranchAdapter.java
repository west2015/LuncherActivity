package com.nhd.mall.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.activity.ShopMapActivity;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.util.ImageLoader;
import com.nhd.mall.util.startIntent;

/**分店适配器
 * Created by caili on 14-4-3.
 */
public class ShopBranchAdapter extends BaseAdapter {
    private Context context;
    private ViewHolder holder=null;
    private StoreEntity[]store;
    public ImageLoader imageLoader;

    public ShopBranchAdapter( Context context,StoreEntity[]store){
        this.context = context;
        this.store = store;
        imageLoader = new ImageLoader(context);
        imageLoader.setDefaultBackgroup(R.drawable.store_default);
        imageLoader.setFailBackgroup(R.drawable.store_default);
    }
    public void update(StoreEntity[]store){
        this.store = store;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return store==null?0:store.length;
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
        final StoreEntity entity = store[position];
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.shop_item_layout, null);
            holder.tvName = (TextView)convertView.findViewById(R.id.shopName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.tvName.setText(entity.getName()==null?"":entity.getName());
        return convertView;
    }
    private  class ViewHolder {
        private TextView tvName;
    }

}
