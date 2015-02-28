package com.nhd.mall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.nhd.mall.R;
import com.nhd.mall.entity.BranchEntity;
import com.nhd.mall.entity.BrandEntity;

/**商户楼层页面分类列表
 * Created by Administrator on 14-6-26.
 */
public class BranchSortAdapter extends BaseAdapter {
    private Context context;
    private ViewHolder holder=null;
    private BranchEntity[]entity;

    public BranchSortAdapter(Context context){
        this.context = context;
    }
    public BranchSortAdapter(Context context,BranchEntity[]entity){
        this.context = context;
        this.entity = entity;
    }
    public void update(BranchEntity[]entity){
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
        BranchEntity branch = entity[i];
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.branch_sort_item_layout, null);
            holder.tvBranchName = (TextView)view.findViewById(R.id.branchDoorName);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }
        if(branch!=null){
            holder.tvBranchName.setText(branch.getBrandName()==null?"":branch.getBrandName());
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
