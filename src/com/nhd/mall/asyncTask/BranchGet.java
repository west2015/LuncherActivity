package com.nhd.mall.asyncTask;
import android.content.Context;
import android.util.Log;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.entity.BranchEntity;
import com.nhd.mall.entity.BranchList;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import org.springframework.web.client.HttpServerErrorException;
import java.io.IOException;
import java.util.ArrayList;
/**品牌获取异步
 * Created by caili on 14-6-27.
 */
public class BranchGet {
    private OnAsyncTaskUpdateListener listener;
    private String message;
    private Context context;
    private Integer pageNum;
    private BranchList list;
    private Integer storeId;
    private Integer floorId;
    private Integer categoryId;
    private String name;

    public BranchGet(Context context,Integer pageNum ,Integer storeId,Integer floorId,Integer categoryId,String name) {
        this.context = context;
        this.pageNum  = pageNum;
        this.categoryId = categoryId;
        this.storeId = storeId;
        this.floorId = floorId;
        this.name = name;
        new DownloadTask().execute();

    }
    public void getMore(BranchList list,Integer pageNum,Integer storeId,Integer floorId,Integer categoryId,String name) {
        this.pageNum  = pageNum;
        this.list = list;
        this.categoryId = categoryId;
        this.storeId = storeId;
        this.floorId = floorId;
        this.name = name;
        new DownloadTask().execute();
    }
    public void update(Integer pageNum,Integer storeId,Integer floorId,Integer categoryId,String name) {
        this.pageNum  = pageNum;
        this.list = null;
        this.categoryId = categoryId;
        this.storeId = storeId;
        this.floorId = floorId;
        this.name = name;
        new DownloadTask().execute();
    }
    class DownloadTask extends AsyncTaskEx<Void, Void,BranchList> {

        public DownloadTask() {
            super();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingProgressDialog(context);
        }

        @Override
        protected BranchList doInBackground(Void... params) {
            BranchList object = null;
            try {
                object = AndroidServerFactory.getServer().getBranch(storeId, floorId, categoryId, pageNum, name);
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
        protected void onPostExecute(BranchList result) {
            super.onPostExecute(result);
            dismissProgressDialog(context);
            if (listener != null) {
                listener.getData( merge(result),message);
            }
        }
    }
    public BranchList merge(BranchList result){
        if(result==null)  return list;
        if(list==null||list.getBrands().length==0){
            list = result;
            return result;
        }
        BranchEntity[]entity = list.getBrands();
        BranchEntity[]reEntity = result.getBrands();
        ArrayList<BranchEntity> storeList = new ArrayList<BranchEntity>();
        for(int i=0;i<entity.length;i++){
            storeList.add(entity[i]);
        }
        for(int i=0;i<reEntity.length;i++){
            storeList.add(reEntity[i]);
        }
        BranchEntity[]finalEntity = new BranchEntity[storeList.size()];
        finalEntity = storeList.toArray(finalEntity);
        list.setBrands(finalEntity);
        return list;
    }
    public void setListener(OnAsyncTaskUpdateListener listener) {
        this.listener = listener;
    }
}
