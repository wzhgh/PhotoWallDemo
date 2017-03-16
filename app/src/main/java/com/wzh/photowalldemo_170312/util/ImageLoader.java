package com.wzh.photowalldemo_170312.util;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * 对图片进行管理的工具类
 * 图片LruCache缓存、图片压缩、解压
 *
 * Created by wanzihui on 2017/3/16.
 */
public class ImageLoader {

    private LruCache<String,Bitmap> mMemoryCache = null ;

    private static ImageLoader instance ;

    private  ImageLoader(){
        int maxCache = (int)Runtime.getRuntime().maxMemory() ;
        int cacheSize = maxCache / 8 ;
        mMemoryCache = new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        } ;
    }

    /**
     * 获取ImageLoader的实例
     * @return
     */
    public static ImageLoader getInstance(){
        if (instance == null){
            instance = new ImageLoader() ;
        }
        return instance ;
    }

    /**
     * 添加一个Bitmap对象到LruCache缓存里
     * @param key   LruCache缓存的键
     * @param bitmap    需要储存的Bitmap对象
     */
    public void addBitmapToMemoryCache(String key,Bitmap bitmap){
        if (getBitmapFromMemoryCache(key) == null){
            mMemoryCache.put(key,bitmap) ;
        }
    }

    /**
     * 从LruCache缓存中获取Bitmap对象
     * @param key   LruCache的键
     * @return  返回key对应的Bitmap对象；如果没有对应的Bitmap对象，这返回null
     */
    public Bitmap getBitmapFromMemoryCache(String key){
        Bitmap tempBitmap = null ;
        if (mMemoryCache != null){
            tempBitmap = mMemoryCache.get(key) ;
        }
        return tempBitmap ;
    }

    //TODO 压缩图片

    //TODO 解压图片

}
