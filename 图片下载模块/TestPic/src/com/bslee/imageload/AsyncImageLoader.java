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
 * �첽������
 * 
 * @author Administrator
 * 
 */
public class AsyncImageLoader {
	// �ļ�����
	private ImageFileCache fileCache;
	// �ڴ滺��
	private ImageMemoryCache memoryCache;
	// �����������ص�ͼƬURL���ϣ������ظ�������
	private HashSet<String> sDownloadingSet;
	// �̳߳����
	private ExecutorService sExecutorService;
	// ֪ͨUI�߳�ͼƬ��ȡ�ɹ�ʱʹ��
	private Handler handler;

	public AsyncImageLoader() {
		super();
	}

	/**
	 * ���캯��
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
	 * �첽����ͼƬ��ϵĻص��ӿ�
	 */
	public interface ImageCallback {
		public void onImageLoaded(Bitmap bitmap, String imageUrl);
	}

	/**
	 * �����̳߳�,���ö�����ʽ
	 */
	public void startThreadPoolIfNecessary(int nThreads) {
		if (sExecutorService == null || sExecutorService.isShutdown()
				|| sExecutorService.isTerminated()) {
			sExecutorService = Executors.newFixedThreadPool(nThreads);
		}
	}

	/**
	 * �첽����ͼƬ�������浽memory��
	 * 
	 * @param url
	 * @param callback
	 * 
	 */

	public void downloadImage(final String url, final ImageCallback callback) {

		if (sDownloadingSet.contains(url)) {
			Log.i("AsyncImageLoader", url + "��ͼƬ�������أ������ظ����أ�");
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
						// �����²����سɹ���������ɾ��
						sDownloadingSet.remove(url);
					}
				});
			}
		});

	}

	/**
	 * Ԥ������һ��ͼƬ��������memory��
	 * 
	 * @param url
	 */
	public void preLoadNextImage(final String url) {
		// ��callback��Ϊ�գ�ֻ��bitmap���浽memory���ɡ�
		downloadImage(url, null);
	}

	/**
	 * ���һ��ͼƬ,�������ط���ȡ,�������ڴ滺��,Ȼ�����ļ�����,���������ȡ
	 * 
	 */
	public Bitmap getBitmap(String url) {
		// ���ڴ滺���л�ȡͼƬ
		Bitmap bitmap = null;
		// �ļ������л�ȡ
		bitmap = fileCache.getImage(url);
		if (bitmap == null) {
			// �����������ͼƬ
			bitmap = ImageGetFormHttp.downloadBitmap(url);
			if (bitmap != null) {
				fileCache.saveBitmap(bitmap, url);
				memoryCache.addBitmapToCache(url, bitmap);
			}
		} else {
			// ��ӵ��ڴ滺��
			memoryCache.addBitmapToCache(url, bitmap);
		}
		return bitmap;
	}

	/**
	 * ���ڴ滺���л�ȡͼƬ,�����ٶȿ�
	 * 
	 */
	public Bitmap getBitmap2(String url) {
		// ���ڴ滺���л�ȡͼƬ
		return memoryCache.getBitmapFromCache(url);
	}

}
