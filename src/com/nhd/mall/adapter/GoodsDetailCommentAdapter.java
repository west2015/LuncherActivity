package com.nhd.mall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.entity.CommentEntity;
import com.nhd.mall.entity.Member;

/**商品评价适配器
 * Created by caili on 14-4-5.
 */
public class GoodsDetailCommentAdapter extends BaseAdapter {
    private Context context;
    private ViewHolder holder=null;
    private CommentEntity[]entity;

    public GoodsDetailCommentAdapter( Context context,CommentEntity[]entity){
        this.context = context;
        this.entity = entity;
    }
    public void update(CommentEntity[]entity){
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
        CommentEntity comment = entity[position];
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.detail_comment_item_layout, null);
            holder.tvName = (TextView)convertView.findViewById(R.id.tvName);
            holder.tvComment = (TextView)convertView.findViewById(R.id.tvComment);
            holder.tvDate = (TextView)convertView.findViewById(R.id.tvDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.tvComment.setText(comment.getMessage()==null?"":comment.getMessage());
        holder.tvDate.setText(comment.getCrDate()==null?"":comment.getCrDate());
        Member member = comment.getMember();
        if(member==null){
            holder.tvName.setText("");
        }
        else{
            holder.tvName.setText(member.getName()==null?"":member.getName());
        }
        return convertView;
    }
    private  class ViewHolder {
        private TextView tvName;
        private TextView tvComment;
        private TextView tvDate;

    }
}
