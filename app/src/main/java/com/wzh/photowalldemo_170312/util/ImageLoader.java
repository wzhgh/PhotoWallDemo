package com.wzh.photowalldemo_170312.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

/**
 * 对图片进行管理的工具类
 * 图片LruCache缓存、图片压缩、解压
 *
 * Created by wanzihui on 2017/3/16.
 * 参考地址：郭霖 http://blog.csdn.net/guolin_blog/article/details/10470797
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


    //=================我写的=================begin
    /**
     * 计算缩放比例值
     *
     * @param options   源图片配置选项
     * @param reqWidth  目标宽度(你想给图片设置宽度)
     * @param reqHeight 目标高度(你想给图片设置的高度)
     * @return 缩放比率值
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){
        //源图片的宽高
        final int height = options.outHeight ;
        final int width = options.outWidth ;
        //缩放比例默认为1(即不缩放)
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth){
            // 计算出实际高度和目标高度的比率
            // int / int ：结果是取整，返回一个整数
            // float / float ：结果就是返回一个float，不是取整了
            final int heightRatio = Math.round((float)height / (float)reqHeight) ;
            final int widthRatio = Math.round((float)width / (float)reqHeight) ;
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio ;
        }
        return inSampleSize ;
    }

    /**
     * 压缩图片
     *
     * @param pathName  图片在sd卡上的路径
     * @param reqWidth  目标宽度
     * @param reqHeight 目标高度
     * @return          压缩后的图片
     */
    public static Bitmap compressBitmapFromResource(String pathName,
                                                    int reqWidth,int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true ;
        // 先解析一次，主要目的是获取源图片的大小(长宽)
        // 需要将inJustDecodeBounds设置为true
        BitmapFactory.decodeFile(pathName,options) ;
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight) ;
        // 使用获取到的inSampleSize值再次解析图片
        //这时需要将inJustDecodeBounds设置为false
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName,options) ;
    }

    /*
     * 使用举例
     * mImageView.setImageBitmap(
       ImageLoader.compressBitmapFromResource(imgPath,100,100));
     */
    //=================guolin大神写的=================end


    //=================guolin大神写的=================begin
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth) {
        // 源图片的宽度
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (width > reqWidth) {
            // 计算出实际宽度和目标宽度的比率
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String pathName,
                                                         int reqWidth) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }
    //=================guolin大神写的=================end

}
