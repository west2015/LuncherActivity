package com.nhd.mall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.nhd.mall.R;
import com.nhd.mall.entity.DoorEntity;

/**商户楼层页面默认列表
 * Created by Administrator on 14-6-25.
 */
public class BranchDoorAdapter extends BaseAdapter {
    private Context context;
    private ViewHolder holder=null;
    private DoorEntity[] doorEntity;

    public BranchDoorAdapter(Context context, DoorEntity[] doorEntity){
        this.context = context;
        this.doorEntity = doorEntity;
    }
    public void update(DoorEntity[] doorEntity){
        this.doorEntity = doorEntity;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return doorEntity==null?0:doorEntity.length;
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
        DoorEntity door = doorEntity[i];
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.branch_door_item_layout, null);
            holder.tvDoorName = (TextView) view.findViewById(R.id.branchDoorName);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }
        if(door!=null){
            holder.tvDoorName.setText(door.getName()==null?"":door.getName());
        }
        else{
            holder.tvDoorName.setText("");
        }
        return view;
    }
    private  class ViewHolder {
        TextView tvDoorName;
    }
}
