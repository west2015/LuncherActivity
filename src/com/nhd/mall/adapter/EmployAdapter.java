package com.nhd.mall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.nhd.mall.R;
import com.nhd.mall.entity.AddCustomerAddress;
import com.nhd.mall.entity.EmployEntity;
import com.nhd.mall.entity.FormEntity;
import com.nhd.mall.entity.FormStoreEntity;
import com.nhd.mall.entity.LogisticEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**招聘适配器
 * Created by Administrator on 14-5-29.
 */
public class EmployAdapter extends BaseAdapter{
    private Context context;
    private ViewHolder holder=null;
    private folderListener listener;
    private HashMap<Integer,Boolean>mapSelect = new HashMap<Integer, Boolean>();
    private EmployEntity[] entity;


    public EmployAdapter( Context context,EmployEntity[] entity){
        this.context = context;
        this.listener = (folderListener) context;
        this.entity = entity;
        for(int i=0;i<10;i++){
            mapSelect.put(i,false);
        }
    }
    public void update(EmployEntity[] entity){
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        EmployEntity enploy = entity[position];
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.employ_item_layout, null);
            holder.tvDate = (TextView)convertView.findViewById(R.id.tvDate);
            holder.tvJob = (TextView)convertView.findViewById(R.id.tvJob);
            holder.tvCount = (TextView)convertView.findViewById(R.id.tvManNum);
            holder.tvDaiyu = (TextView)convertView.findViewById(R.id.tvDaiyu);
            holder.tvRequest = (TextView)convertView.findViewById(R.id.tvRequest);
            holder.ivIcon = (ImageView)convertView.findViewById(R.id.ivFolder);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.tvJob.setText(enploy.getName()==null?"":enploy.getName());
        holder.tvCount.setText(enploy.getPeople()==null?0+"":enploy.getPeople()+"个");
        holder.tvDaiyu.setText(enploy.getTreatment()==null?"":enploy.getTreatment());
        holder.tvDate.setText(enploy.getCreateDate()==null?"":enploy.getCreateDate());
        holder.tvRequest.setText(enploy.getRequired()==null?"":enploy.getRequired());
        //true标识已经展开
        if(mapSelect.get(position)){
            holder.ivIcon.setBackgroundResource(R.drawable.goods_down_icon);
            holder.tvRequest.setMaxLines(100);
        }
        //false标识没展开
        else{
            holder.ivIcon.setBackgroundResource(R.drawable.goods_top_icon);
            holder.tvRequest.setMaxLines(2);
        }
        holder.ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             if(mapSelect.get(position)){
                mapSelect.put(position,false);
                 holder.ivIcon.setBackgroundResource(R.drawable.goods_top_icon);
                 holder.tvRequest.setMaxLines(2);
                 notifyDataSetChanged();
             }
                else{
                 mapSelect.put(position,true);
                 holder.ivIcon.setBackgroundResource(R.drawable.goods_down_icon);
                 holder.tvRequest.setMaxLines(100);
                 notifyDataSetChanged();
             }
            }
        });
        return convertView;
    }
    private  class ViewHolder {
        private TextView tvDate,tvJob,tvCount,tvDaiyu,tvRequest;
        private ImageView ivIcon;
    }
    public interface folderListener{
        void folder(int position);
    }
}
