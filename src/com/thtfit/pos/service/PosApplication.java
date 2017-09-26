package com.thtfit.pos.service;

import java.io.File;

import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

import com.baidu.frontia.FrontiaApplication;
import com.imagpay.Settings;
import com.imagpay.ttl.TTLHandler;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class PosApplication extends FrontiaApplication{
	
    public static final String LOCK = "lock";
    public static final String LOCK_KEY = "lock_key";
    private static Boolean isVerification = false;
    private static Boolean isFirGesture = false;
    
    public String patternString;
    
    private TTLHandler _handler;
	private Settings _setting;
	
	public ImageLoader imageLoader = ImageLoader.getInstance();
	
	 
	@Override
	public void onCreate() {
		super.onCreate();
		
		File CacheDir =  new File(Environment.getExternalStorageDirectory()
				+ File.separator + "THTFIT/Cache");
		if (!CacheDir.exists()) {
			CacheDir.mkdirs();
		}
		File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "THTFIT/Cache"); 
		
		if (!imageLoader.isInited()) {
			ImageLoaderConfiguration config = new ImageLoaderConfiguration
			
		     .Builder(getApplicationContext())  
			
		     .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽  
		     
		     .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null) // Can slow ImageLoader, use it carefully (Better don't use it)/设置缓存的详细信息，最好不要设置这个  
		    
		     .threadPoolSize(3)//线程池内加载的数量  
		     
		     .threadPriority(Thread.NORM_PRIORITY - 2)  
		     
		     .denyCacheImageMultipleSizesInMemory()  
		    
		     .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现  
		     
		     .memoryCacheSize(2 * 1024 * 1024)    
		    
		     .discCacheSize(50 * 1024 * 1024)    
		    
		     .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密  
		    
		     .tasksProcessingOrder(QueueProcessingType.LIFO)  
		     
		     .discCacheFileCount(100) //缓存的文件数量  
		     	
		     .discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径  
		     
		     .defaultDisplayImageOptions(DisplayImageOptions.createSimple())  
		    
		     .imageDownloader(new BaseImageDownloader(getApplicationContext(), 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间  
		    
		     .writeDebugLogs() // Remove for release app  
		     
		     .build();//开始构建  
			
			imageLoader.init(config);
		 }
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public TTLHandler get_handler() {
		return _handler;
	}

	public void set_handler(TTLHandler _handler) {
		this._handler = _handler;
	}

	public Settings get_setting() {
		return _setting;
	}

	public void set_setting(Settings _setting) {
		this._setting = _setting;
	}
	
	
	public Boolean getIsVerification() {
		return isVerification;
	}



	public void setIsVerification(Boolean isVerification) {
		this.isVerification = isVerification;
	}
	
	public Boolean getIsFirGesture() {
		return isFirGesture;
	}
	public void setIsFirGesture(Boolean isFirGesture) {
		this.isFirGesture = isFirGesture;
	}


	
}
