package com.bslee.imageload;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * ���ڴ��ȡ�����ٶ������ģ�Ϊ�˸����޶�ʹ���ڴ棬����ʹ�������㻺�档
 * Ӳ���û��治�����ױ����գ��������泣�����ݣ������õ�ת�������û��档
 */
public class ImageMemoryCache {


    //һ������ͼƬ��Ŀ
    private int MAX_CAPACITY = 5;  
    //�����û���
    private HashMap<String, SoftReference<Bitmap>> mSoftCache = new HashMap<String, SoftReference<Bitmap>>();

    // 0.75�Ǽ�������Ϊ����ֵ��true���ʾ��������������ĸߵ�����false���ʾ���ղ���˳������
    //Ӳ���û���
    private HashMap<String, Bitmap> mLruCache = new LinkedHashMap<String, Bitmap>(
            MAX_CAPACITY / 2, 0.75f, true) {
        private static final long serialVersionUID = 1L;

        protected boolean removeEldestEntry(Map.Entry<String, Bitmap> eldest) {
            if (size() > MAX_CAPACITY) {
                // ������һ��������ֵ��ʱ�򣬽��ϵ�ֵ��һ������ᵽ��������
            	mSoftCache.put(eldest.getKey(),
                        new SoftReference<Bitmap>(eldest.getValue()));
                return true;
            }
            return false;
        }
    };
                                                                                 
    public ImageMemoryCache(Context context) {
    	//clearCache();
    }
                                                                               
    /**
     * �ӻ����л�ȡͼƬ
     */
    public Bitmap getBitmapFromCache(String url) {
        Bitmap bitmap;
        //�ȴ�Ӳ���û����л�ȡ
        synchronized (mLruCache) {
            bitmap = mLruCache.get(url);
            if (bitmap != null) {
                //����ҵ��Ļ�����Ԫ���Ƶ�LinkedHashMap����ǰ�棬�Ӷ���֤��LRU�㷨�������ɾ��
                mLruCache.remove(url);
                mLruCache.put(url, bitmap);
                return bitmap;
            }
        }
        //���Ӳ���û������Ҳ������������û�������
        synchronized (mSoftCache) { 
            SoftReference<Bitmap> bitmapReference = mSoftCache.get(url);
            if (bitmapReference != null) {
                bitmap = bitmapReference.get();
                if (bitmap != null) {
                    //��ͼƬ�ƻ�Ӳ����
                    mLruCache.put(url, bitmap);
                    mSoftCache.remove(url);
                    return bitmap;
                } else {
                    mSoftCache.remove(url);
                }
            }
        }
        return null;
    } 
                                                                               
    /**
     * ���ͼƬ������
     */
    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (mLruCache) {
                mLruCache.put(url, bitmap);
            }
        }
    }
                                                                               
    public void clearCache() {
        mSoftCache.clear();
        mLruCache.clear();
    }
}
