package com.bslee.imageload;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * 从内存读取数据速度是最快的，为了更大限度使用内存，这里使用了两层缓存。
 * 硬引用缓存不会轻易被回收，用来保存常用数据，不常用的转入软引用缓存。
 */
public class ImageMemoryCache {


    //一级缓存图片数目
    private int MAX_CAPACITY = 5;  
    //软引用缓存
    private HashMap<String, SoftReference<Bitmap>> mSoftCache = new HashMap<String, SoftReference<Bitmap>>();

    // 0.75是加载因子为经验值，true则表示按照最近访问量的高低排序，false则表示按照插入顺序排序
    //硬引用缓存
    private HashMap<String, Bitmap> mLruCache = new LinkedHashMap<String, Bitmap>(
            MAX_CAPACITY / 2, 0.75f, true) {
        private static final long serialVersionUID = 1L;

        protected boolean removeEldestEntry(Map.Entry<String, Bitmap> eldest) {
            if (size() > MAX_CAPACITY) {
                // 当超过一级缓存阈值的时候，将老的值从一级缓存搬到二级缓存
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
     * 从缓存中获取图片
     */
    public Bitmap getBitmapFromCache(String url) {
        Bitmap bitmap;
        //先从硬引用缓存中获取
        synchronized (mLruCache) {
            bitmap = mLruCache.get(url);
            if (bitmap != null) {
                //如果找到的话，把元素移到LinkedHashMap的最前面，从而保证在LRU算法中是最后被删除
                mLruCache.remove(url);
                mLruCache.put(url, bitmap);
                return bitmap;
            }
        }
        //如果硬引用缓存中找不到，到软引用缓存中找
        synchronized (mSoftCache) { 
            SoftReference<Bitmap> bitmapReference = mSoftCache.get(url);
            if (bitmapReference != null) {
                bitmap = bitmapReference.get();
                if (bitmap != null) {
                    //将图片移回硬缓存
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
     * 添加图片到缓存
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
