package com.bslee.imageload;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bslee.util.AbAppUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

/**
 * 异步下载器
 * 
 * @author Administrator
 * 
 */
public class AsyncImageLoader {
	// 文件缓存
	private ImageFileCache fileCache;
	// 内存缓存
	private ImageMemoryCache memoryCache;
	// 保存正在下载的图片URL集合，避免重复下载用
	private HashSet<String> sDownloadingSet;
	// 线程池相关
	private ExecutorService sExecutorService;
	// 通知UI线程图片获取成功时使用
	private Handler handler;

	public AsyncImageLoader() {
		super();
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 * @param nThreads
	 */
	public AsyncImageLoader(Context context, String cachedir) {
		sDownloadingSet = new HashSet<String>();
		handler = new Handler();
		memoryCache = new ImageMemoryCache(context);
		fileCache = new ImageFileCache();
		if (cachedir != null) {
			fileCache.setCACHDIR(cachedir);
		}
		startThreadPoolIfNecessary(AbAppUtil.getNumCores());
	}

	/**
	 * 异步加载图片完毕的回调接口
	 */
	public interface ImageCallback {
		public void onImageLoaded(Bitmap bitmap, String imageUrl);
	}

	/**
	 * 开启线程池,采用队列形式
	 */
	public void startThreadPoolIfNecessary(int nThreads) {
		if (sExecutorService == null || sExecutorService.isShutdown()
				|| sExecutorService.isTerminated()) {
			sExecutorService = Executors.newFixedThreadPool(nThreads);
		}
	}

	/**
	 * 异步下载图片，并缓存到memory中
	 * 
	 * @param url
	 * @param callback
	 * 
	 */

	public void downloadImage(final String url, final ImageCallback callback) {

		if (sDownloadingSet.contains(url)) {
			Log.i("AsyncImageLoader", url + "该图片正在下载，不能重复下载！");
			return;
		}

		sDownloadingSet.add(url);
		sExecutorService.submit(new Runnable() {
			@Override
			public void run() {
				final Bitmap bitmap = getBitmap(url);
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (callback != null)
							callback.onImageLoaded(bitmap, url);
						// 不管下不下载成功都从里面删除
						sDownloadingSet.remove(url);
					}
				});
			}
		});

	}

	/**
	 * 预加载下一张图片，缓存至memory中
	 * 
	 * @param url
	 */
	public void preLoadNextImage(final String url) {
		// 将callback置为空，只将bitmap缓存到memory即可。
		downloadImage(url, null);
	}

	/**
	 * 获得一张图片,从三个地方获取,首先是内存缓存,然后是文件缓存,最后从网络获取
	 * 
	 */
	public Bitmap getBitmap(String url) {
		// 从内存缓存中获取图片
		Bitmap bitmap = null;
		// 文件缓存中获取
		bitmap = fileCache.getImage(url);
		if (bitmap == null) {
			// 从网络端下载图片
			bitmap = ImageGetFormHttp.downloadBitmap(url);
			if (bitmap != null) {
				fileCache.saveBitmap(bitmap, url);
				memoryCache.addBitmapToCache(url, bitmap);
			}
		} else {
			// 添加到内存缓存
			memoryCache.addBitmapToCache(url, bitmap);
		}
		return bitmap;
	}

	/**
	 * 从内存缓存中获取图片,加载速度快
	 * 
	 */
	public Bitmap getBitmap2(String url) {
		// 从内存缓存中获取图片
		return memoryCache.getBitmapFromCache(url);
	}

}
