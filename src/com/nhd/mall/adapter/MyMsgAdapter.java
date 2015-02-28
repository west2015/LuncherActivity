
package com.nhd.mall.adapter;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.entity.Message;
import com.nhd.mall.push.PushMsgCategory;
import com.nhd.mall.push.PushMsgDataHelper;

import java.util.HashMap;

/**
 * Created by Issac on 7/18/13.
 */
public class MyMsgAdapter extends CursorAdapter {
    private LayoutInflater mLayoutInflater;
    // 用来控制CheckBox的选中状况
    public HashMap<Long,Boolean> selectedMap=new HashMap<Long, Boolean>();
    private ListView mListView;

    public MyMsgAdapter(Context context/*, ListView listView*/) {
        super(context, null, false);
        mLayoutInflater = ((Activity) context).getLayoutInflater();
//        mListView = listView;
    }

    public void selectAll(Boolean b){
        Cursor cursor = this.getCursor();
        if(cursor==null) return;
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
        {
            Long key = this.getCursor().getLong(cursor.getColumnIndex("_id"));
            this.selectedMap.put(key,b?true:false);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public Message getItem(int position) {
        mCursor.moveToPosition(position);
        return Message.fromCursor(mCursor);
    }

    public long getItemId(int position){
        mCursor.moveToPosition(position);
        return mCursor.getLong(mCursor.getColumnIndex("_id"));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mLayoutInflater.inflate(R.layout.message_item_layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Holder holder = getHolder(view);
        final  Long position = cursor.getLong(cursor.getColumnIndex("_id"));
//        view.setEnabled(!mListView.isItemChecked(cursor.getPosition()
//                + mListView.getHeaderViewsCount()));

        Message msg = Message.fromCursor(cursor);
        if(msg.getCrDate()!=null && !msg.getCrDate().equals("")){
            holder.time.setText(msg.getCrDate());
        }
        int category= cursor.getInt(cursor.getColumnIndex(PushMsgDataHelper.CacheDBInfo.CATEGORY));
        if(category == PushMsgCategory.UNREAD.ordinal()){
            holder.tvContent.setTextColor(context.getResources().getColor(R.color.black));
            holder.time.setTextColor(context.getResources().getColor(R.color.black));
        }else{
            holder.tvContent.setTextColor(context.getResources().getColor(R.color.gray));
            holder.time.setTextColor(context.getResources().getColor(R.color.gray));
        }
        holder.tvContent.setText(msg.getMessage());
        holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                selectedMap.put(position, b);
            }
        });
        boolean isCheck = selectedMap.get(position)==null?false:selectedMap.get(position);
        holder.check.setChecked(isCheck);
    }

    private Holder getHolder(final View view) {
        Holder holder = (Holder) view.getTag();
        if (holder == null) {
            holder = new Holder(view);
            view.setTag(holder);
        }
        return holder;
    }

    private class Holder {

        public TextView tvContent;

        public TextView time;

        public CheckBox check;

        public Holder(View view) {
            tvContent = (TextView) view.findViewById(R.id.tvContent);
            time = (TextView) view.findViewById(R.id.tvDate);
            check = (CheckBox) view.findViewById(R.id.check);
        }
    }
}
