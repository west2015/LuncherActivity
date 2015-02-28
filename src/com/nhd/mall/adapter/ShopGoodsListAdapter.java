package com.nhd.mall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.nhd.mall.R;
import com.nhd.mall.entity.GoodsFilterSecondEntity;

/**
 * Created by Administrator on 14-5-29.
 */
public class ShopGoodsListAdapter extends BaseAdapter {
    private Context context;
    private ViewHolder holder=null;
    private GoodsFilterSecondEntity[]childrens;

    public ShopGoodsListAdapter( Context context,GoodsFilterSecondEntity[]children){
        this.context = context;
        this.childrens = children;
    }

    @Override
    public int getCount() {
        return childrens==null?0:childrens.length;
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
        GoodsFilterSecondEntity children  =childrens[position];
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.goods_filter_item_detail_layout, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tvNameDetail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvName.setText(children.getName()==null?"":children.getName());
        convertView.setEnabled(false);
        return convertView;
    }
    private  class ViewHolder {
        private TextView tvName;
    }
}
