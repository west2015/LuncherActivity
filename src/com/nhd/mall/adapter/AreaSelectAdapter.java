package com.nhd.mall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.entity.RegionEntity;

/**
 * Created by Administrator on 14-6-27.
 */
public class AreaSelectAdapter extends BaseAdapter {
    private Context context;
    private ViewHolder holder=null;
    private RegionEntity[] entity;

    public AreaSelectAdapter(Context context,RegionEntity[] entity){
        this.context = context;
        this.entity = entity;
    }
    public void update(RegionEntity[] entity){
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        RegionEntity region = entity[i];
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.area_selct_layout, null);
            holder.tvBranchName = (TextView)view.findViewById(R.id.textView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }
        if(region!=null){
            holder.tvBranchName.setText(region.getRegionName()==null?"":region.getRegionName());
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
