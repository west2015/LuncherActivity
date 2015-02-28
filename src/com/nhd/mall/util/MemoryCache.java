package com.nhd.mall.util;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import android.graphics.Bitmap;

/**
 * 类<code>MemoryCache</code>图片内存缓存类
 * 
 * @author vendor
 * @version 2012年11月5日 14:40:09
 * @see     Class
 * @since   JDK1.0
 *
 */
public class MemoryCache {
//    private WeakHashMap<String, SoftReference<Bitmap>> cache=new WeakHashMap<String, SoftReference<Bitmap>>();
    private Map<String, SoftReference<Bitmap>> cache = Collections.synchronizedMap(new WeakHashMap<String, SoftReference<Bitmap>>());
    
    /** 
     * 从map中取值
     * 
     * @param id  图片的名称编号
     * @return  Bitmap
     */
    public synchronized Bitmap get(String id){
        if(!cache.containsKey(id))
            return null;
        SoftReference<Bitmap> ref=cache.get(id);
        
        if(ref != null){
        	return ref.get();
        }else{
        	return null;
        }
    }
    
    /**
     * 添加一个新的bitmap进入map中缓存
     * 
     * @param id
     * @param bitmap
     */
    public synchronized void put(String id, Bitmap bitmap){
        cache.put(id, new SoftReference<Bitmap>(bitmap));
    }

    /**
     * 清理当前map的所有内容
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized void clear() {
    	Iterator i = cache.entrySet().iterator();  
        while (i.hasNext()) {  
            Map.Entry en = (Map.Entry)i.next();  
            SoftReference<Bitmap> softReference=(SoftReference<Bitmap>) en.getValue();
            clearSoftReference(softReference);
        }
    	cache.clear();
//    	cache=null;
    }
    
    public synchronized void clear(String url){
    	clearSoftReference(cache.get(url));
    	cache.remove(url);
    }
    
    /**
     * 清理map集合里的softReference对象
     * @param softReference
     */
    public void clearSoftReference(SoftReference<Bitmap> softReference){
    	if(softReference != null){
	    	clear(softReference.get());
	    	softReference.clear();
	    	softReference=null;
    	}
    }
    
    /**
     * 清理Bitmap的内存
     * @param bitmap
     */
    private void clear(Bitmap bitmap){
    	if(bitmap == null)
    		return;
    	
    	if(!bitmap.isRecycled()){
    		bitmap.recycle();
    	}
    	
    	bitmap=null;
    }
}