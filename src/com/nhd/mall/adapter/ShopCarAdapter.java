package com.nhd.mall.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nhd.mall.R;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.entity.CarEntity;
import com.nhd.mall.entity.CarList;
import com.nhd.mall.entity.OrderProductEntity;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.util.ImageLoader;

/**购物车适配器
 * Created by caili on 14-4-8.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ShopCarAdapter  extends BaseAdapter {
	private final int DELIVERY = 2;
	
    private Context context;
    private ViewHolder holder=null;
    private CarEntity[] entity;
    public List<Boolean> checkBoxesStatus;
    public HashMap<Integer,Boolean> selectMap = new HashMap<Integer, Boolean>();
    private checkAllPrice listenerPrice;
    //用一个map来保存所有订单的总价格
    private HashMap<Integer,Double>priveMap = new HashMap<Integer, Double>();
    public ImageLoader imageLoader;
    private boolean[] isTop;
    private boolean[] isEdit;
    private HashMap<Integer,String> storeName = new HashMap<Integer,String>();
    
    public ShopCarAdapter( Context context, CarEntity[] entity){
        this.context = context;
        this.entity = entity;
        listenerPrice = (checkAllPrice) context;
        imageLoader = new ImageLoader(context);
        imageLoader.setDefaultBackgroup(R.drawable.goods_mr_img);
        imageLoader.setFailBackgroup(R.drawable.goods_mr_img);
        if(entity != null){
            checkBoxesStatus = new ArrayList<Boolean>(entity.length);
            for(int i = 0;i<entity.length;i++){
                checkBoxesStatus.add(false);
            }
            initGroup(entity);
        }
    }
    public void update(CarEntity[] entity){
        this.entity = entity;
        if(entity!=null){
            selectMap.clear();
            checkBoxesStatus = new ArrayList<Boolean>(entity.length);
            for(int i = 0;i<entity.length;i++){
                checkBoxesStatus.add(false);
            }
            initGroup(entity);
        }
        notifyDataSetChanged();
    }
    public void selectAll(CarEntity[] entity){
        if(entity != null){
            selectMap.clear();
            checkBoxesStatus = new ArrayList<Boolean>(entity.length);
            for(int i = 0;i<entity.length;i++){
                checkBoxesStatus.add(true);
                selectMap.put(i,true);
            }
            initGroup(entity);
            notifyDataSetChanged();
        }
    }
    public void noSelectAll(CarEntity[] entity){
        if(entity!=null){
            selectMap.clear();
            checkBoxesStatus = new ArrayList<Boolean>(entity.length);
            for(int i = 0;i<entity.length;i++){
                checkBoxesStatus.add(false);
            }
            initGroup(entity);
            notifyDataSetChanged();
        }
    }
    private void initGroup(CarEntity[] entity){
        isTop = new boolean[entity.length+1];
        isTop[0] = true;
        isTop[entity.length] = true;
        for(int i=1;i<entity.length;++i)
        if(entity[i].getOrderProduct().getStoreId() != entity[i-1].getOrderProduct().getStoreId()
        || entity[i].getOrderProduct().getGetway() != entity[i-1].getOrderProduct().getGetway()){
        	isTop[i] = true;
        }
        else{
        	isTop[i] = false;
        }
        isEdit = new boolean[entity.length+1];
        for(int i=0;i<isEdit.length;++i){
        	isEdit[i] = false;
        }
        storeName.clear();
        StoreEntity[] storeEntity = MainApplication.getInstance().getStores();
        if(storeEntity != null){
	        for(int i=0;i<entity.length;++i) {
	        	Integer mId = entity[i].getOrderProduct().getStoreId();
	        	if(storeName.get(mId) == null || storeName.get(mId).equals("")){
	        		for(int j=0;j<storeEntity.length;++j)
	        		if(mId == storeEntity[j].getId()){
	        			storeName.put(mId, storeEntity[j].getName());
	        			break;
	        		}
	        	}
	        }
        }
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
        final OrderProductEntity orderProduct = entity[position].getOrderProduct();
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.shop_car_item_layout, null);
            holder.cb_all = (CheckBox)convertView.findViewById(R.id.cb_all);
            holder.rlTop = (RelativeLayout)convertView.findViewById(R.id.rl_top);
            holder.btnEdit = (Button)convertView.findViewById(R.id.btn_edit);
            holder.rlTotal = (RelativeLayout)convertView.findViewById(R.id.rl_total);
            holder.ivImg = (ImageView)convertView.findViewById(R.id.imageView);
            holder.cb = (CheckBox)convertView.findViewById(R.id.checkBox);
            holder.tvStore = (TextView)convertView.findViewById(R.id.tv_store);
            holder.tvName = (TextView)convertView.findViewById(R.id.tv_name);
            holder.tvPrice = (TextView)convertView.findViewById(R.id.tv_price);
            holder.tvOldPrice = (TextView)convertView.findViewById(R.id.tv_oldprice);
            holder.tvWay = (TextView)convertView.findViewById(R.id.tv_way);
            holder.btnAdd = (Button)convertView.findViewById(R.id.btn_count_add);
            holder.btnMinus = (Button)convertView.findViewById(R.id.btn_count_minus);
            holder.tvCount = (TextView)convertView.findViewById(R.id.tv_count);
            holder.tvNumber = (TextView)convertView.findViewById(R.id.tv_number);
            holder.tvTotalNumber = (TextView)convertView.findViewById(R.id.tv_total_number);
            holder.tvTotalPrice = (TextView)convertView.findViewById(R.id.tv_total_price);
            holder.rl_number = (RelativeLayout)convertView.findViewById(R.id.rl_number);
            holder.btnCheckOut = (ImageButton)convertView.findViewById(R.id.btn_checkout);
            holder.tvFreight = (TextView)convertView.findViewById(R.id.tv_freight);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        
        if(orderProduct.getThumb()==null){
            holder.ivImg.setBackgroundResource(R.drawable.goods_mr_img);
        }
        else{
            imageLoader.setBackgroup(orderProduct.getThumb(),holder.ivImg);
        }
        holder.tvName.setText(orderProduct.getName()==null?"":orderProduct.getName());
        holder.tvPrice.setText(orderProduct.getPrice()==null?"￥0":"￥"+String.valueOf(orderProduct.getPrice()));
        holder.tvNumber.setText(orderProduct.getNum()==null?0+"":"×"+orderProduct.getNum());
        holder.tvCount.setText(orderProduct.getNum()==null?0+"":orderProduct.getNum()+"");
        if(!isTop[position]) {
        	if(holder.rlTop != null) {
        		holder.rlTop.setVisibility(View.GONE);
        	}
        }
        else { // 分组开头
        	if(holder.rlTop != null){
        		holder.rlTop.setVisibility(View.VISIBLE);
        	}
        	if(holder.tvStore != null){
        		if(storeName.get(orderProduct.getStoreId()) != null){
        			holder.tvStore.setText(storeName.get(orderProduct.getStoreId()));
        		}
        	}
        	if(orderProduct.getGetway() == DELIVERY){
        		holder.tvWay.setText("配送方式：快递");
        	}
        	else{
        		holder.tvWay.setText("配送方式：自提");
        	}
        	// 编辑监听
        	if(holder.btnEdit !=null){
        		holder.btnEdit.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						boolean status = !isEdit[position];
						isEdit[position] = status;
						for(int i=position+1;i<isEdit.length;++i){
							if(isTop[i]) break;
							isEdit[i] = status;
						}
						notifyDataSetChanged();
					}
				});
        	}
        	if(holder.cb_all != null){
        		holder.cb_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// TODO Auto-generated method stub
						checkBoxesStatus.set(position, isChecked);
						for(int i=position+1;i<isTop.length;++i){
							if(isTop[i]) break;
							checkBoxesStatus.set(i, isChecked);
						}
						notifyDataSetChanged();
					}
				});
        	}
        }
        if(!isTop[position+1] || isEdit[position]) {
        	if(holder.rlTotal != null){
        		holder.rlTotal.setVisibility(View.GONE);
        	}
        }
        else{ // 分组结尾
        	if(holder.rlTotal != null){
        		holder.rlTotal.setVisibility(View.VISIBLE);
        	}
        	// 计算商品总数目、总价
        	int totalNumber = 0;
        	double totalPrice = 0;
        	double totalFreight = 0;
        	for(int i=position;i>=0;--i){
        		if(checkBoxesStatus.get(i)){
        			totalNumber += entity[i].getOrderProduct().getNum();
        			totalPrice += entity[i].getOrderProduct().getNum()*Double.parseDouble(entity[i].getOrderProduct().getPrice()+"");
        			totalFreight += entity[i].getOrderProduct().getFreight();
        		}
        		if(isTop[i]) break;
        	}
        	if(entity[position].getOrderProduct().getGetway() == DELIVERY){
        		totalPrice += totalFreight;
        		holder.tvFreight.setText("(含运费" + entity[position].getOrderProduct().getFreight()+"元)");
        	}
        	else{
        		holder.tvFreight.setText("");
        	}
        	holder.tvTotalNumber.setText("共计"+totalNumber+"件商品");
        	holder.tvTotalPrice.setText("￥"+totalPrice);
        	// 结算监听
        	final int number = totalNumber;
        	if(holder.btnCheckOut != null){
        		holder.btnCheckOut.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(isEdit[position] || number==0) return ;
						List<CarEntity> listEntity = new ArrayList<CarEntity>();
						for(int i=position;i>=0;--i){
							if(checkBoxesStatus.get(i)){
								listEntity.add(entity[i]);
							}
							if(isTop[i]) break;
						}
						CarEntity[] mEntity = new CarEntity[listEntity.size()];
						mEntity = listEntity.toArray(mEntity);
						CarList carList = new CarList();
						carList.setCars(mEntity);
	                    if(listenerPrice != null){
	                    	listenerPrice.submitForm(orderProduct.getStoreId(),carList);
	                    }
					}
				});
        	}
        }
        // 编辑中
        if(isEdit[position]){
        	if(holder.btnEdit != null){
        		holder.btnEdit.setText("完成");
        	}
        	if(holder.rl_number != null){
        		holder.rl_number.setVisibility(View.VISIBLE);
        	}
        	if(holder.tvNumber != null){
        		holder.tvNumber.setVisibility(View.GONE);
        	}
        	if(holder.btnCheckOut != null){
        		holder.btnCheckOut.setBackground(context.getResources().getDrawable(R.drawable.btn_unpressed_bg));
        	}
        	if(holder.tvPrice != null){
        		holder.tvPrice.setVisibility(View.GONE);
        	}
        }
        else{
        	if(holder.btnEdit != null){
        		holder.btnEdit.setText("编辑");
        	}
        	if(holder.rl_number != null){
        		holder.rl_number.setVisibility(View.GONE);
        	}
        	if(holder.tvNumber != null){
        		holder.tvNumber.setVisibility(View.VISIBLE);
        	}
        	if(holder.btnCheckOut != null){
        		holder.btnCheckOut.setBackground(context.getResources().getDrawable(R.drawable.my_form_btn_bg));
        	}
        	if(holder.tvPrice != null){
        		holder.tvPrice.setVisibility(View.VISIBLE);
        	}
        }
        final boolean checkBoxStatus = checkBoxesStatus.get(position);
        holder.cb.setTag(position);
        holder.cb.setChecked(checkBoxStatus);
        //单选框监听
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBoxesStatus.set((Integer)buttonView.getTag(), isChecked);
                notifyDataSetChanged();
                if(isChecked){
                    boolean b = false;
                    if(selectMap.size()==entity.length){
                        b = true;
                    }
                    selectMap.put(position,true);
                    if(b) return;
                    if(listenerPrice != null){
                    	listenerPrice.getCheckPrice(position,isChecked);
                    }
                }
                else{
                    if(selectMap.containsKey(position)){
                        selectMap.remove(position);
                        if(listenerPrice!=null){
                        	listenerPrice.getCheckPrice(position,isChecked);
                        }
                    }
                }

            }
        });

        if(isEdit[position]){
	        final TextView etCount = holder.tvCount;
	        final CheckBox cb = holder.cb;
	        //增加数量监听
	        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
	                OrderProductEntity order = entity[position].getOrderProduct();
	                int currentCount =  Integer.parseInt(etCount.getText().toString());
	                if(currentCount>=order.getBuyLimit()){
	                    Toast.makeText(context,"不能超过限购量",Toast.LENGTH_SHORT).show();
	                    return;
	                }
	                if(currentCount>=order.getTotal()){
	                    Toast.makeText(context,"库存不够",Toast.LENGTH_SHORT).show();
	                    return;
	                }
	                etCount.setText(String.valueOf(Integer.parseInt(etCount.getText().toString())+1));
	                int count = Integer.parseInt(etCount.getText().toString());
	                //相应的改变实体类里面的参数
	                order.setNum(count);
	                entity[position].setOrderProduct(order);
	                if(listenerPrice!=null){
	                	listenerPrice.getAllPrice(position,count,order.getPrice(),entity[position].getOrderProduct(),cb.isChecked(),1);
	                }
	            }
	        });
	        //减少数量监听
	        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
	                if(etCount.getText().toString().equals("0")){
	                    return;
	                }
	                etCount.setText(String.valueOf(Integer.parseInt(etCount.getText().toString())-1));
	                OrderProductEntity order = entity[position].getOrderProduct();
	                int count = Integer.parseInt(etCount.getText().toString());
	                //相应的改变实体类里面的参数
	                order.setNum(count);
	                entity[position].setOrderProduct(order);
	                if(listenerPrice!=null){
	                	listenerPrice.getAllPrice(position,count,order.getPrice(),entity[position].getOrderProduct(),cb.isChecked(),2);
	                }
	            }
	        });
        }
        return convertView;
    }
    private  class ViewHolder {
    	// Top
        private RelativeLayout rlTop;
        private Button btnEdit;
        private TextView tvStore,tvWay;
        private CheckBox cb_all;
        // Content
        private RelativeLayout rl_number;
        private CheckBox cb;
        private ImageView ivImg;
        private TextView tvName,tvPrice,tvOldPrice,tvCount,tvNumber;
        private Button btnAdd,btnMinus;
        // Total
        private RelativeLayout rlTotal;
        private TextView tvTotalNumber,tvTotalPrice,tvFreight;
        private ImageButton btnCheckOut;
    }
    public interface checkAllPrice{
        void getAllPrice(int position,int count,double price,OrderProductEntity order,boolean boo,int sort);
        void getCheckPrice(int position,boolean boo);
        void submitForm(int storeId,CarList carList);
    }
}
