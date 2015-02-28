package com.nhd.mall.asyncTask;
import android.content.Context;
import android.util.Log;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.entity.ProductDetailEntity;
import com.nhd.mall.entity.ProductList;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.entity.StoreListEntity;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.umeng.analytics.MobclickAgent;
import org.springframework.web.client.HttpServerErrorException;
import java.io.IOException;
import java.util.ArrayList;

/**获取商品列表异步
 * Created by caili on 14-4-21.
 */
public class ProductListGet {
    private OnAsyncTaskUpdateListener listener;
    private String message;
    private Context context;
    private ProductList list;  //商品列表
    private Integer pageNum;   //分页
    private  Integer storeId;  //门店ID
    private String search_for;   //搜索参数  null，price,sales;
    private  Integer categoryId;  //分类id
    private Integer brandId;    //品牌ID
    private Integer counterId;  //专柜id
    private   String brandName;//品牌名称
    private  String name;   //商品名称
    private String orderBy;   //排序字段
    private Integer tagId;   //标签ID
    private  Integer themeId;  //主题ID

    //是标签那边跳进来的
    public ProductListGet(Context context,Integer storeId ,Integer pageNum,String search_for) {
        if(!AndroidServerFactory.PRODUCTION_MODEL)
            MobclickAgent.onEvent(context, "path", "/api/v1/product/list");
        this.context = context;
        this.storeId = storeId;
        this.pageNum = pageNum;
        this.search_for = search_for;
    }
    //是品牌商店那里传过来的
    public void setBranchParemeter( Integer brandId ,String orderBy){
        this.brandId = brandId;
        this.orderBy = orderBy;
        new DownloadTask().execute();
    }
    //是标签那边跳进来的
    public void setTagParemeter( Integer tagId ,String orderBy){
        this.tagId = tagId;
        this.orderBy = orderBy;
        new DownloadTask().execute();
    }
    //是主题那边跳进来的
    public void setThemesParemeter( Integer themeId ,String orderBy){
        this.themeId = themeId;
        this.orderBy = orderBy;
        new DownloadTask().execute();
    }
    //是商品筛选那边跳进来的
    public void setFilterParemeter( Integer categoryId ,String orderBy){
        this.categoryId = categoryId;
        this.orderBy = orderBy;
        new DownloadTask().execute();
    }
    public void update(Integer pageNum,ProductList list) {
        this.pageNum = pageNum;
        this.list = list;
        new DownloadTask().execute();
    }

    class DownloadTask extends AsyncTaskEx<Void, Void,ProductList> {

        public DownloadTask() {
            super();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ProductList doInBackground(Void... params) {
            ProductList object = null;
            try {
                object = AndroidServerFactory.getServer().getProductList(pageNum,storeId,search_for,categoryId,brandId,counterId,brandName, name,orderBy,tagId,themeId);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                if (e instanceof HttpServerErrorException) {
                    message = ((HttpServerErrorException) e)
                            .getResponseBodyAsString();
                    message = ParseJson.getStatusAsString(message);
                    Log.e("HttpServerErrorException", message);
                } else {
                    final String TAG = "asynctask";
                    final String msg = e.getMessage();

                    if (msg != null) {
                        Log.e(TAG, msg);
                    }
                }
            }
            return object;
        }
        @Override
        protected void onPostExecute(ProductList result) {
            super.onPostExecute(result);
            if (listener != null) {
                listener.getData( merge(result),message);
            }
        }
    }
    public ProductList merge(ProductList result){
        if(list==null||list.getProducts().length==0){
            list = result;
            return result;
        }
        if(result==null)return list;
        ProductDetailEntity[]entity = list.getProducts();
        ProductDetailEntity[]reEntity = result.getProducts();
        ArrayList<ProductDetailEntity> storeList = new ArrayList<ProductDetailEntity>();
        for(int i=0;i<entity.length;i++){
            storeList.add(entity[i]);
        }
        for(int i=0;i<reEntity.length;i++){
            storeList.add(reEntity[i]);
        }
        ProductDetailEntity[]finalEntity = new ProductDetailEntity[storeList.size()];
        finalEntity = storeList.toArray(finalEntity);
        list.setProducts(finalEntity);
        return list;
    }
    public void setListener(OnAsyncTaskUpdateListener listener) {
        this.listener = listener;
    }
}
