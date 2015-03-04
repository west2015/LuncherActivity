package com.nhd.mall.app;
import android.content.Context;
import android.widget.Toast;

import com.baidu.frontia.FrontiaApplication;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.nhd.mall.R;
import com.nhd.mall.asyncTask.LoginPost;
import com.nhd.mall.asyncTask.MemberBeanGet;
import com.nhd.mall.datebase.DatabaseHelper;
import com.nhd.mall.datebase.DbAddress;
import com.nhd.mall.datebase.DbConfig;
import com.nhd.mall.datebase.DbMember;
import com.nhd.mall.datebase.DbStore;
import com.nhd.mall.entity.CustomerAddressEntity;
import com.nhd.mall.entity.Member;
import com.nhd.mall.entity.MemberRespondEntity;
import com.nhd.mall.entity.OrderProductEntity;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.common.Log;

import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.ShareSDK;
/**
 * 类<code>MainApplication</code>运行于整个软件的生命周期内
 * 建议存放全局对象
 * @author vendor
 * @version 2012年11月2日 22:36:50
 * @see     Class
 * @since   JDK1.0
 */
public class MainApplication extends FrontiaApplication {
	protected final String TAG = MainApplication.class.getSimpleName();
	private static Context mContext;
	private static MainApplication instance;
	private Member member;
    private CustomerAddressEntity customerAddress = null;
    //购物车中选中的要生成订单的商品保存在这里
    private ArrayList<OrderProductEntity>order = new ArrayList<OrderProductEntity>();
    private String verName;   //当前版本名称
    private StoreEntity store;//保存最新的门店信息
    private StoreEntity[] stores;//保存最新的门店信息
    private Integer beanCount;//全局疯豆数量变量
    public BMapManager mBMapManager = null; //百度地图引擎
    public static final String strKey = "msyWTbq2Bwg5YdjFzmP9HAjr";  //百度地图appkey
   // private boolean needBack = false;


//    public boolean isNeedBack() {
//        return needBack;
//    }
//
//    public void setNeedBack(boolean needBack) {
//        this.needBack = needBack;
//    }

    public static MainApplication getInstance() {
		if (instance == null) {
			instance = new MainApplication();
		}
		return instance;
	}
	@Override
	public void onCreate() {
        super.onCreate();
		instance = this;
        mContext=getApplicationContext();
		MobclickAgent.onResume(this);
		DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext(), getResources().getString(R.string.db_name));
		databaseHelper.getWritableDatabase();
		databaseHelper.close();
		
		DbConfig dbConfig=new DbConfig(getApplicationContext());
		member = new DbMember(getApplicationContext()).getMember();
        store = new DbStore(getApplicationContext()).getStore();
        customerAddress = new DbAddress(getApplicationContext()).getAddress();
        verName = new DbConfig(getApplicationContext()).getVersion();
        //初始化第三方分享工具
        ShareSDK.initSDK(getApplicationContext());
        initEngineManager(this);
	}

    public StoreEntity[] getStores() {
        return stores;
    }

    public void setStores(StoreEntity[] stores) {
        this.stores = stores;
    }

    public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }
        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(MainApplication.getInstance().getApplicationContext(), "BMapManager初始化错误!", Toast.LENGTH_LONG).show();
        }
    }
    public Integer getBeanCount() {
        return beanCount;
    }

    public void setBeanCount(Integer beanCount) {
        this.beanCount = beanCount;
    }

    public StoreEntity getStore() {
        return store;
    }

    public void setStore(StoreEntity store) {
        this.store = store;
    }

    public String getVerName() {
        return verName;
    }

    public void setVerName(String verName) {
        this.verName = verName;
    }

    public ArrayList<OrderProductEntity> getOrder() {
        return order;
    }

    public void setOrder(ArrayList<OrderProductEntity> order) {
        this.order = order;
    }

	public void login(final Context context){
        Member user = MainApplication.getInstance().getMember();
        if(user==null){
            if(loginListener!=null){
                loginListener.onLoginChange(null);
            }
            return;
        }
        new LoginPost(context,member).setListener(new OnAsyncTaskUpdateListener() {
            @Override
            public void getData(Object obj, String message) {
                if(obj == null && message == null)
                {
                    if (loginListener != null) {
                        loginListener.onLoginChange(null);
                    }
                    return;
                }
                if(message!=null){
                    if (loginListener != null) {
                        loginListener.onLoginChange(null);
                    }
                    setMember(null);
                    Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
                }
                if(obj==null){
                    return;
                }
                if(obj instanceof MemberRespondEntity){
                    MemberRespondEntity status=(MemberRespondEntity) obj;
                    if(status.getStatus().getSuccess().equals("true")){
                        setMember(status.getDatas());
                        Toast.makeText(context,"登录成功",Toast.LENGTH_SHORT).show();
                        if (loginListener != null) {
                            loginListener.onLoginChange(MainApplication.getInstance().getMember());
                        }
                    }else{
                        setMember(null);
                        Toast.makeText(context,"登录失败",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
	}
    //每次成功登陆后都要调用这个方法查询当前账号的疯豆数量
    public void getBeanCountAsyn(){
        Member user = MainApplication.getInstance().getMember();
        if(user==null){
            return;
        }
        new MemberBeanGet(getApplicationContext(),user.getId()).setListener(new OnAsyncTaskUpdateListener() {
            @Override
            public void getData(Object obj, String message) {
                if(obj==null){
                    return;
                }
                HashMap<String,String> map = (HashMap<String, String>) obj;
                if(map.get("success").equals("true")){
                    Object objCount = map.get("marks");
                    String sObj = objCount.toString();
                    setBeanCount(Integer.parseInt(sObj));
                }
            }
        });
    }

      public static class MyGeneralListener implements MKGeneralListener {

        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
            }
        }
        @Override
        public void onGetPermissionState(int iError) {
            //非零值表示key验证未通过
            if (iError != 0) {
                //授权Key错误：
            }
        }
    }

	private OnLoginChangeListener loginListener;
	public void setLoginChangeListener(OnLoginChangeListener listener){
		this.loginListener = listener;
	}
	
	public interface OnLoginChangeListener{
		void onLoginChange(Member member);
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		MobclickAgent.onPause(this);
        //终止第三方分享工具
        ShareSDK.stopSDK(getApplicationContext());
	}
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
        DbMember db = new DbMember(getApplicationContext());
        if(member==null){
            db.delete();
        }else{
            db.update(member);
        }
	}

    public static Context getContext(){
        return  mContext;
    }

    public CustomerAddressEntity getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(CustomerAddressEntity customerAddress) {
        this.customerAddress = customerAddress;
    }

}
