package com.nhd.mall.util;

import java.io.File;
import android.content.Context;
import android.os.Environment;

/**
 * 文件缓存类
 * 
 * @author vendor
 *
 */
public class FileCache {
    
    private File cacheDir;
    
    public FileCache(Context context){
        //查找和保存文件
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
	        File file = Environment.getExternalStorageDirectory();
			String picDirectory = file.getAbsolutePath() + "/yqkan/cache";
			cacheDir =  new File(picDirectory);
        }else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
    
    /**
     * 获取文件
     * 
     * @param url  网络图片的路径
     * @return
     */
    public File getFile(String url){
        //将文件名转为hashCode()
        String filename=String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        return f;
        
    }
    
    /**
     * 清理存储卡缓存
     */
    public void clear(){
        File[] files=cacheDir.listFiles();
        for(File f:files)
            f.delete();
    }

}