package com.nhd.mall.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;


/************************************************************
 *  内容摘要	：<p> 图片下载工具类
 *
 *  作者	：vendor
 *  创建时间	：2013-2-3 上午11:07:19 
 *
 *  历史记录	:
 *  	日期	: 2013-2-3 上午11:07:19 	修改人：vendor
 *  	描述	:
 ************************************************************/
public class ImageLoader {

	private final static String TAG = "ImageLoader";

	/** 在图片下载期间 ImageView默认显示的图片编号 */
	int stub_id = 0;

	/** 在图片下载失败时候 ImageView默认显示的图片编号 */
	int fail_id = 0;

	Boolean isCompress = false;

	int requiredWidth = 0;

	int requiredHeight = 0;

	/** 图片以缩放方式显示 */
	public static final int DISPLAY_STATE_SCALE = 0;

	/** 图片以填充方式显示 */
	public static final int DISPLAY_STATE_FIX = 1;

	/** 内存缓存类 */
	MemoryCache memoryCache = new MemoryCache();

	/** 图片的显示方式 */
	private int display_state = DISPLAY_STATE_FIX;

	/** 添加圆角弧度 */
	private int pixels = 0;
	
	/** 是否启用渐变动画  */
	private Boolean isAnimation = true;

	/** 文件缓存类 */
	FileCache fileCache;
	Context context;

	private Animation animation = new AlphaAnimation(0.1f, 1.0f);
	/** key ImageView value http url */
	private Map<ImageView, String> map = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private Map<Button, String> mapButton = Collections.synchronizedMap(new WeakHashMap<Button, String>());
    private CompleteListener comListener;
	
	/**
	 * 内适配器
	 * 
	 * @param context
	 */
	public ImageLoader(Context context) {
		this.context = context;

		// Make the background thead low priority. This way it will not affect
		// the UI performance
		photoLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);
		animation.setDuration(500);
		fileCache = new FileCache(context);
	}
    public ImageLoader(Context context,CompleteListener comListener) {
        this.context = context;
        this.comListener = comListener;
        // Make the background thead low priority. This way it will not affect
        // the UI performance
        photoLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);
        animation.setDuration(500);
        fileCache = new FileCache(context);
    }

	/**
	 * 
	 * 函数名称 : setDisplay 
	 * 功能描述 : 图片的显示方式 参数及返回值说明：
	 * 
	 * @param display
	 * 
	 *            修改记录： 日期：2012-12-5 下午02:11:53 修改人：vendor 描述 ：
	 * 
	 */
	public void setDisplay(int display) {
		this.display_state = display;
	}

	/**
	 * 
	 * 函数名称 : setDefaultBackgroup 
	 * 功能描述 : 设置图片的未下载完成之前的显示方式 参数及返回值说明：
	 * 
	 * @param stub_id
	 * 
	 *            修改记录： 日期：2012-12-5 下午02:13:50 修改人：vendor 描述 ：
	 * 
	 */
	public void setDefaultBackgroup(int stub_id) {
		Bitmap bitmap = readBitMap(context, stub_id);
		memoryCache.put(String.valueOf(stub_id), bitmap);
		
		this.stub_id = stub_id;

		// 对于图片下载失败也使用这一个 如果有特殊的 则调用setFailBackgroup(int fail_id)方法
		if (fail_id == 0) {
			this.fail_id = stub_id;
		}
	}

	/**
	 *  函数名称 : setFailBackgroup
	 *  功能描述 : 添加图片下载失败之后的默认显示 参数及返回值说明： 
	 *  参数及返回值说明：
	 *  	@param fail_id
	 *
	 *  修改记录：
	 *  	日期：2013-2-3 上午11:04:11	修改人：vendor
	 *  	描述	：
	 *
	 */
	public void setFailBackgroup(int fail_id) {
		if(memoryCache.get(String.valueOf(fail_id)) == null){
			Bitmap bitmap = readBitMap(context, fail_id);
			memoryCache.put(String.valueOf(fail_id), bitmap);
		}
		
		this.fail_id = fail_id;
	}
	
	/**
	 *  函数名称 : isAnimation
	 *  功能描述 : 设置是否启动动画效果 
	 *  参数及返回值说明：
	 *  	@param isAnimation
	 *
	 *  修改记录：
	 *  	日期：2013-2-3 下午5:06:10	修改人：vendor
	 *  	描述	：
	 *
	 */
	public void isAnimation(Boolean isAnimation){
		this.isAnimation = isAnimation;
	}

	/**
	 *  函数名称 : setBackgroup
	 *  功能描述 : 处理图片下载 参数及返回值说明：
	 *  参数及返回值说明：
	 *  	@param url
	 *  	@param imageView
	 *
	 *  修改记录：
	 *  	日期：2013-2-3 上午11:03:46	修改人：vendor
	 *  	描述	：
	 *
	 */
	public void setBackgroup(String url, ImageView imageView) {
		// 判断url是否合法
		if (null != url && !"".equals(url)) {
			map.put(imageView, url);

			// 遍历查找是否已下载该图片
			Bitmap bitmap = memoryCache.get(url);

			if (bitmap != null && bitmap.isRecycled()) {
				bitmap = null;
				memoryCache.clear(url);
			}

			if (bitmap != null) {
				setDisplayImage(imageView, bitmap);
			} else {
				queuePhoto(url, imageView);
				setDisplayImage(imageView, stub_id);
			}
		} else {
			setFailDisplayImage(imageView);
		}
	}
    //给button添加提片背景
    public void setBackgroup(String url, Button imageView) {
        // 判断url是否合法
        if (null != url && !"".equals(url)) {
            mapButton.put(imageView, url);

            // 遍历查找是否已下载该图片
            Bitmap bitmap = memoryCache.get(url);

            if (bitmap != null && bitmap.isRecycled()) {
                bitmap = null;
                memoryCache.clear(url);
            }

            if (bitmap != null) {
                setDisplayImage(imageView, bitmap);
            } else {
                queuePhoto(url, imageView);
                setDisplayImage(imageView, stub_id);
            }
        } else {
            setFailDisplayImage(imageView);
        }
    }

	/**
	 *  函数名称 : setBackgroup 
	 *  功能描述 : 对于出现大图也使用这个下载类来使用，避免oom
	 *  如果图片下载失败以及显示默认图片也会用这个方法 参数及返回值说明：
	 *  	@param imageView
	 *  	@param resId
	 *
	 *  修改记录：
	 *  	日期：2013-2-3 上午11:04:25	修改人：vendor
	 *  	描述	：
	 *
	 */
	public void setBackgroup(ImageView imageView, int resId) {

		if(memoryCache.get(String.valueOf(resId)) == null){
			Bitmap bitmap = readBitMap(context, resId);
			memoryCache.put(String.valueOf(resId), bitmap);
		}
		Bitmap bitmap = memoryCache.get(String.valueOf(resId));
		setDisplayImage(imageView, bitmap);
	}
    public void setBackgroup(Button imageView, int resId) {

        if(memoryCache.get(String.valueOf(resId)) == null){
            Bitmap bitmap = readBitMap(context, resId);
            memoryCache.put(String.valueOf(resId), bitmap);
        }
        Bitmap bitmap = memoryCache.get(String.valueOf(resId));
        setDisplayImage(imageView, bitmap);
    }

	/**
	 *  函数名称 : readBitMap
	 *  功能描述 : 将id图片转换为bitmap资源 参数及返回值说明： 
	 *  参数及返回值说明：
	 *  	@param context
	 *  	@param resId
	 *  	@return
	 *
	 *  修改记录：
	 *  	日期：2013-2-3 上午11:04:44	修改人：vendor
	 *  	描述	：
	 *
	 */
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;

		try{
			// 获取资源图片
			InputStream is = context.getResources().openRawResource(resId);
			return BitmapFactory.decodeStream(is, null, opt);
		}catch(NotFoundException e){
			Log.e(TAG, "NotFoundException");
		}
		
		return null;
	}

	/**
	 *  函数名称 : setDisplayImage
	 *  功能描述 : 设置图片显示 默认 
	 *  参数及返回值说明：
	 *  	@param imageView
	 *  	@param stub_id
	 *
	 *  修改记录：
	 *  	日期：2013-2-3 上午11:05:08	修改人：vendor
	 *  	描述	：
	 *
	 */
	private void setDisplayImage(ImageView imageView, int stub_id) {
		setBackgroup(imageView, stub_id);
	}
    private void setDisplayImage(Button imageView, int stub_id) {
        setBackgroup(imageView, stub_id);
    }

	/**
	 *  函数名称 : setFailDisplayImage
	 *  功能描述 : 设置图片的下载失败之后的样式 
	 *  参数及返回值说明：
	 *  	@param imageView
	 *
	 *  修改记录：
	 *  	日期：2013-2-3 上午11:05:22	修改人：vendor
	 *  	描述	：
	 *
	 */
	private void setFailDisplayImage(ImageView imageView) {
		setBackgroup(imageView, fail_id);
	}
    private void setFailDisplayImage(Button imageView) {
        setBackgroup(imageView, fail_id);
    }

	/**
	 *  函数名称 : setDisplayImage
	 *  功能描述 : 设置图片显示 下载 
	 *  参数及返回值说明：
	 *  	@param imageView
	 *  	@param bitmap
	 *
	 *  修改记录：
	 *  	日期：2013-2-3 上午11:05:16	修改人：vendor
	 *  	描述	：
	 *
	 */
	@SuppressWarnings("deprecation")
	public void setDisplayImage(ImageView imageView, Bitmap bitmap) {
		
		if(bitmap == null){
			return;
		}

		if (pixels != 0) {
			ImageUtil imageUtil = new ImageUtil(context);
			try {
				bitmap = imageUtil.toRoundCorner(bitmap, pixels);
			} catch (OutOfMemoryError e) {
				Log.e(TAG, "OutOfMemoryError");
			}
		}

		switch (display_state) {
			case DISPLAY_STATE_SCALE :
				imageView.setImageBitmap(bitmap);
				if(isAnimation){
					imageView.setAnimation(animation);
				}
				break;
			case DISPLAY_STATE_FIX :
				imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
				if(isAnimation){
					imageView.setAnimation(animation);
				}
				break;
			default :
				break;
		}
        if (comListener != null){
            comListener.onComplete();
        }

		// 给一个监听 判断是否下载完成
		if (listener != null)
			listener.onUpdateBitmap(map.get(imageView), imageView, bitmap);
	}
    public void setDisplayImage(Button imageView, Bitmap bitmap) {
        if(bitmap == null){
            return;
        }
        if (pixels != 0) {
            ImageUtil imageUtil = new ImageUtil(context);
            try {
                bitmap = imageUtil.toRoundCorner(bitmap, pixels);
            } catch (OutOfMemoryError e) {
                Log.e(TAG, "OutOfMemoryError");
            }
        }

        switch (display_state) {
            case DISPLAY_STATE_FIX :
                imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
                if(isAnimation){
                    imageView.setAnimation(animation);
                }
                break;
            default :
                break;
        }
        if (comListener != null){
            comListener.onComplete();
        }

        // 给一个监听 判断是否下载完成
        if (listener != null)
            listener.onUpdateBitmap(mapButton.get(imageView), imageView, bitmap);
    }
	/**
	 *  函数名称 : queuePhoto
	 *  功能描述 : 清理队列 并且执行下载线程 
	 *  参数及返回值说明：
	 *  	@param url
	 *  	@param imageView
	 *
	 *  修改记录：
	 *  	日期：2013-2-3 上午11:05:45	修改人：vendor
	 *  	描述	：
	 *
	 */
	private void queuePhoto(String url, ImageView imageView) {
		// This ImageView may be used for other images before. So there may be
		// some old tasks in the queue. We need to discard them.
		photosQueue.Clean(imageView);
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		synchronized (photosQueue.photosToLoad) {
			photosQueue.photosToLoad.push(p);
			photosQueue.photosToLoad.notifyAll();
		}

		// start thread if it's not started yet
		if (photoLoaderThread.getState() == Thread.State.NEW)
			photoLoaderThread.start();
	}
    private void queuePhoto(String url, Button imageView) {
        // This ImageView may be used for other images before. So there may be
        // some old tasks in the queue. We need to discard them.
        photosQueueButton.Clean(imageView);
        PhotoToLoadButton p = new PhotoToLoadButton(url, imageView);
        synchronized (photosQueueButton.photosToLoad) {
            photosQueueButton.photosToLoad.push(p);
            photosQueueButton.photosToLoad.notifyAll();
        }

        // start thread if it's not started yet
        if (photoLoaderThread.getState() == Thread.State.NEW)
            photoLoaderThread.start();
    }

	/**
	 *  函数名称 : downLoad
	 *  功能描述 : 取得请求图片url 获取图片资源Bitmap 
	 *  参数及返回值说明：
	 *  	@param url
	 *  	@return
	 *
	 *  修改记录：
	 *  	日期：2013-2-3 上午11:05:55	修改人：vendor
	 *  	描述	：
	 *
	 */
	public Bitmap downLoad(String url) {
		File f = fileCache.getFile(url);

		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;

		// from web
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			return bitmap;
		}catch (Exception ex) {
			if (ex instanceof SocketException) {
				Log.e(TAG, "fail to connect!");
			}
		}
		
		return null;
	}
	
	private void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception e) {

		}
	}


	/**
	 *  函数名称 : setCompress
	 *  功能描述 : 压缩显示 参数 
	 *  参数及返回值说明：
	 *  	@param isCompress
	 *  	@param requiredWidth
	 *  	@param requiredHeight
	 *
	 *  修改记录：
	 *  	日期：2013-2-3 上午11:06:06	修改人：vendor
	 *  	描述	：
	 *
	 */
	public void setCompress(Boolean isCompress, int requiredWidth, int requiredHeight) {
		if (isCompress) {
			this.requiredWidth = requiredWidth;
			this.requiredHeight = requiredHeight;
		}

		this.isCompress = isCompress;
	}

	public void setCompress(Boolean isCompress, View view) {
		this.requiredWidth = view.getLayoutParams().width;
		this.requiredHeight = view.getLayoutParams().height;

		if (requiredWidth == 0)
			return;

		this.isCompress = isCompress;
	}

	/**
	 *  函数名称 : decodeFile
	 *  功能描述 : 对图片进行压缩处理 
	 *  参数及返回值说明：
	 *  	@param f
	 *  	@return
	 *
	 *  修改记录：
	 *  	日期：2013-2-3 上午11:06:15	修改人：vendor
	 *  	描述	：
	 *
	 */
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();

			if (isCompress) {
				o.inJustDecodeBounds = true;
				Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o);

				// Find the correct scale value. It should be the power of 2.
				int width_tmp = o.outWidth, height_tmp = o.outHeight;
				int scale = 1;
				while (true) {
					if (width_tmp / 2 < requiredWidth || height_tmp / 2 < requiredHeight)
						break;
					width_tmp /= 2;
					height_tmp /= 2;
					scale *= 2;
				}

				// decode with inSampleSize
				BitmapFactory.Options o2 = new BitmapFactory.Options();
				o2.inSampleSize = scale;
				o2.inPreferredConfig = Bitmap.Config.RGB_565;
				
				if(bitmap != null && !bitmap.isRecycled()){
					bitmap.recycle();
					bitmap = null;
				}
				
				return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
			} else {
				o.inPreferredConfig = Bitmap.Config.RGB_565;
				return BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			}
		} catch (FileNotFoundException e) {
			Log.e(TAG, "No such file or directory");
		} catch (OutOfMemoryError e) {
			Log.e(TAG, "OutOfMemoryError");
			System.gc();
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;
		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}
    private class PhotoToLoadButton {
        public String url;
        public Button imageView;
        public PhotoToLoadButton(String u, Button i) {
            url = u;
            imageView = i;
        }
    }

	PhotosQueue photosQueue = new PhotosQueue();
    PhotosQueueButton photosQueueButton = new PhotosQueueButton();

	/**
	 * 停止线程
	 */
	public void stopThread() {
		photoLoaderThread.interrupt();
	}

	/**
	 * 所有即将进行下载的照片列表
	 */
	class PhotosQueue {
		private Stack<PhotoToLoad> photosToLoad = new Stack<PhotoToLoad>();

		// removes all instances of this ImageView
		public void Clean(ImageView image) {
			try { // 防止数组越界
				for (int j = 0; j < photosToLoad.size();) {
					if (photosToLoad.get(j).imageView == image)
						photosToLoad.remove(j);
					else
						++j;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
	}
    class PhotosQueueButton {
        private Stack<PhotoToLoadButton> photosToLoad = new Stack<PhotoToLoadButton>();

        // removes all instances of this ImageView
        public void Clean(Button image) {
            try { // 防止数组越界
                for (int j = 0; j < photosToLoad.size();) {
                    if (photosToLoad.get(j).imageView == image)
                        photosToLoad.remove(j);
                    else
                        ++j;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

	/**********************************************************
	 *  内容摘要	：<p> 对队列中的所有url遍历查找并进行下载
	 *
	 *  作者	：vendor
	 *  创建时间	：2013-2-3 上午11:06:47 
	 *
	 *  历史记录	:
	 *  	日期	: 2013-2-3 上午11:06:47 	修改人：vendor
	 *  	描述	:
	 ***********************************************************
	 */
	class PhotosLoader extends Thread {
		public void run() {
			try {
				while (true) {
					// thread waits until there are any images to load in the
					// queue
					if (photosQueue.photosToLoad.size() == 0)
						synchronized (photosQueue.photosToLoad) {
							photosQueue.photosToLoad.wait();
						}
					if (photosQueue.photosToLoad.size() != 0) {
						PhotoToLoad photoToLoad;
						synchronized (photosQueue.photosToLoad) {
							photoToLoad = photosQueue.photosToLoad.pop();
						}
						Bitmap bmp = downLoad(photoToLoad.url);
						memoryCache.put(photoToLoad.url, bmp);
						String tag = map.get(photoToLoad.imageView);
						if (tag != null && tag.equals(photoToLoad.url)) {
							BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad.imageView);
							((Activity) context).runOnUiThread(bd);
						}
					}
					if (Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {
				// allow thread to exit
			}
		}
	}

	PhotosLoader photoLoaderThread = new PhotosLoader();

	// Used to display bitmap in the UI thread
	/**
	 * 处理填充当前已经完成的任务,并且显示效果
	 */
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		ImageView imageView;
		public BitmapDisplayer(Bitmap b, ImageView i) {
			bitmap = b;
			imageView = i;
		}
		public void run() {
			if (bitmap != null)
				setDisplayImage(imageView, bitmap);
			else
				setFailDisplayImage(imageView);
		}
	}

	/**
	 * 清理当前map中已经缓存的bitmap
	 */
	public void clearMemory() {
		map.clear();
		memoryCache.clear();
		
		System.gc();
	}

	/**
	 * 清理当前map中已经缓存的bitmap,连带这连sd卡中的缓存也清理掉
	 */
	public void clearCache() {
		map.clear();
		memoryCache.clear();
		fileCache.clear();
		
		System.gc();
	}
	
	/**
	 *  函数名称 : clearBitmap
	 *  功能描述 : 清理单一的一张Bitmap 
	 *  参数及返回值说明：
	 *  	@param imageView
	 *
	 *  修改记录：
	 *  	日期：2013-2-18 上午9:01:41	修改人：vendor
	 *  	描述	：
	 *
	 */
	public void clearBitmap(ImageView imageView){
		if(imageView != null){
			imageView.setImageBitmap(null);
			map.remove(imageView);
			
			String url = this.getUrl(imageView);
			
			if(url != null){
				memoryCache.clear(url);
			}
		}
	}

	/**
	 * 下载当前请求的图片
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getBitmap(String url) {
		if (memoryCache != null) {
			return memoryCache.get(url);
		} else {
			return null;
		}
	}

	public String getUrl(ImageView view) {
		if (map.get(view) != null) {
			return map.get(view);
		}
		return null;
	}

	public void setBackgroupRound(String url, ImageView imageView, int pixels) {
		this.pixels = pixels;
		// 判断url是否合法
		if (null != url && !"".equals(url)) {
			map.put(imageView, url);

			// 遍历查找是否已下载该图片
			Bitmap bitmap = memoryCache.get(url);

			if (bitmap != null && bitmap.isRecycled()) {
				bitmap = null;
				memoryCache.clear(url);
			}

			if (bitmap != null){
				setDisplayImage(imageView, bitmap);
			} else {
				queuePhoto(url, imageView);
			}
		} else {
			setFailDisplayImage(imageView);
		}
	}

	public interface ImageLoaderListener {
		/**
		 * 更新图片
		 * 
		 * @param bitmap
		 *            当前请求url所对应的图片
		 */
		public void onUpdateBitmap(String url, ImageView imageView, Bitmap bitmap);
        public void onUpdateBitmap(String url, Button imageView, Bitmap bitmap);
	}
    public interface CompleteListener {
        /**
         * 更新图片
         *
         * @param bitmap
         *            当前请求url所对应的图片
         */
        public void onComplete();
    }

	private ImageLoaderListener listener;
	public void setListener(ImageLoaderListener l) {
		this.listener = l;
	}
}
