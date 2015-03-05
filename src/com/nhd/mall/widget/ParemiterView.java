package com.nhd.mall.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nhd.mall.R;
import com.nhd.mall.entity.ProductFieldEntity;
import com.nhd.mall.entity.ProductFieldValue;
import java.util.HashMap;

/**商品详细的参数选择自定义view
 * Created by Administrator on 14-4-24.
 */
public class ParemiterView extends LinearLayout {
    private ProductFieldEntity[] productFieldEntity;    //参数
    private Context context;
    private getSelectParemeter listener;

    public ParemiterView(Context context) {
        super(context);
    }

    public ParemiterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("NewApi")
	public ParemiterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setProductFieldEntity(ProductFieldEntity[] productFieldEntity) {
        this.productFieldEntity = productFieldEntity;
        initView();
    }
    private void initView() {
        removeAllViews();
        if(productFieldEntity==null || productFieldEntity.length<=0)return;
        for(int i=0;i<productFieldEntity.length;i++){
            View view = LayoutInflater.from(context).inflate(R.layout.paremiter_item_layout,null);
            this.addView(view);
            TextView nameTitle = (TextView)view.findViewById(R.id.nameValue);
            LinearLayout addView  = (LinearLayout)view.findViewById(R.id.llAddView);
            nameTitle.setText(productFieldEntity[i].getName()==null?"":productFieldEntity[i].getName());
            ProductFieldValue[]values = productFieldEntity[i].getValues();
            String name  = productFieldEntity[i].getName();
            if(values!=null&&values.length>0){
                HashMap<Integer,Boolean>seleMap = new HashMap<Integer,Boolean>();
                for(int j=0;j<values.length;j++){
                    seleMap.put(j,false);
                }
                initAddView(name,seleMap,values,addView);
            }
        }
    }

    private void initAddView(final String name,final HashMap<Integer, Boolean> seleMap,final ProductFieldValue[] values,final LinearLayout addView) {
        addView.removeAllViews();
        for(int j=0;j<values.length;j++){
            final int current = j;
            TextView tv = new TextView(context);
            tv.setText(values[j].getValue()==null?"":values[j].getValue());
            if(seleMap.get(j)){
                tv.setBackgroundResource(R.drawable.good_detail_para_click);
            }
            else{
                tv.setBackgroundResource(R.drawable.good_detail_para_unclick);
            }
            tv.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(12,0,0,0);
            tv.setLayoutParams(params);
            addView.addView(tv);
            //给tv设置监听 一旦选中  则改变背景
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                   if(seleMap.get(current)){
                       seleMap.put(current,false);
                       if(listener!=null){
                           listener.getParemeter(name,null);
                       }
                   }
                    else{
                       //把其他的全部变为false
                       for(int w:seleMap.keySet()){
                           seleMap.put(w,false);
                       }
                       seleMap.put(current,true);
                       if(listener!=null){
                           listener.getParemeter(name,values[current].getValue());
                       }
                   }
                    initAddView(name,seleMap,values,addView);
                }
            });
        }
    }

    public void setContext(Context context) {
        this.context = context;
        listener = (getSelectParemeter) context;
    }
    public interface getSelectParemeter{
        void getParemeter(String name,String value);
    }
}
