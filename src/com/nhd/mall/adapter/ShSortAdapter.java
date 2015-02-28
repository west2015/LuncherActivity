package com.nhd.mall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.entity.GoodsFilterEntity;

/**
 * Created by Administrator on 14-6-30.
 */
public class ShSortAdapter extends BaseAdapter {
    private Context context;
    private ViewHolder holder=null;
    private GoodsFilterEntity[]filters;

    public ShSortAdapter(Context context,GoodsFilterEntity[]filter){
        this.context = context;
        this.filters = filter;
    }
    public void update(GoodsFilterEntity[]filter){
        this.filters = filter;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return filters==null?0:filters.length;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        GoodsFilterEntity filter = filters[i];
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.branch_sort_item_layout, null);
            holder.tvBranchName = (TextView)view.findViewById(R.id.branchDoorName);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }
        if(filter!=null){
            holder.tvBranchName.setText(filter.getName()==null?"":filter.getName());
        }
        else{
            holder.tvBranchName.setText("");
        }
        return view;
    }
    private  class ViewHolder {
        TextView tvBranchName;
    }
}
