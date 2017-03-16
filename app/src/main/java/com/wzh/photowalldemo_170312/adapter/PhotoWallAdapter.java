package com.wzh.photowalldemo_170312.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.wzh.photowalldemo_170312.R;
import com.wzh.photowalldemo_170312.util.ImageUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wzh on 2017/3/12.
 * 参考地址：郭霖博客：http://blog.csdn.net/guolin_blog/article/details/9526203
 */
public class PhotoWallAdapter extends ArrayAdapter<String> implements AbsListView.OnScrollListener {

    /**
     * 下载任务集合
     */
    private Set<BitmapWorkerTask> mTaskCollection ;

    private GridView mGridView ;

    /**
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在
     * 程序内存达到设定值时会将最少最近使用的图片移除掉。
     */
    private LruCache<String,Bitmap> mMemoryCache ;

    /**
     * 第一张可见图片的下标
     */
    private int mFirstVisibleItem;

    /**
     * 一屏有多少张图片可见
     */
    private int mVisibleItemCount;

    /**
     * 记录是否刚打开程序
     * 用于解决进入程序不滚动屏幕，不会下载图片的问题。
     */
    private boolean mIsFirstEnter = true ;

    /**
     * 数据源，图片url数组
     */
//    private String[] mImageUrls ;

    /**
     * textViewResourceId 应该是item的布局文件id，
     * 但是这里我们在get里面自定义了，所以转的时候就直接给个0
     */
    public PhotoWallAdapter(Context context, int textViewResourceId, String[] objects, GridView photoWall){
        super(context, textViewResourceId, objects);
        mGridView = photoWall ;
        mTaskCollection = new HashSet<BitmapWorkerTask>() ;
        long maxMemory = Runtime.getRuntime().maxMemory() ;
        int cacheSize = (int)(maxMemory / 8) ;
        mMemoryCache = new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        mGridView.setOnScrollListener(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String url = getItem(position) ;
        View itemView ;
        if (convertView == null){
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo,null) ;
        }else {
            itemView = convertView ;
        }
        final ImageView photo = (ImageView)itemView.findViewById(R.id.iv_photo_item) ;
        //给ImageView控件设置一个Tag(算是一个标志吧)，
        //然后图片下载完后根据这个Tag(标志)来找到这个ImageView控件，然后设置图片
        //这样可确保图片不会错乱
        photo.setTag(url);
        setImageView(url, photo) ;
        return itemView;
    }

    /**
     * 给ImageView设置图片。首先从LruCache中取出图片的缓存，设置到ImageView上。如果LruCache中没有该图片的缓存，
     * 就给ImageView设置一张默认图片。
     * @param imgUrl  图片的url
     * @param imageView 显示图片的控件
     */
    private void setImageView(String imgUrl,ImageView imageView){
        Bitmap bitmap = getBitmapFromMemoryCache(imgUrl);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.mipmap.empty_photo);
        }
    }

    /**
     * 从LruCache中取出图片的缓存，
     * @param imageUrl   图片的url
     * @return   没有就返回null
     */
    private Bitmap getBitmapFromMemoryCache(String imageUrl){
        return mMemoryCache.get(imageUrl) ;
    }

    /**
     * 将图片Bitmap放到LruCache中，
     * @param imgUrl    图片url
     * @param bitmap    图片的Bitmap
     */
    private void addBitmapToMemoryCache(String imgUrl , Bitmap bitmap){
        if (getBitmapFromMemoryCache(imgUrl) == null){
            mMemoryCache.put(imgUrl,bitmap) ;
        }
    }
    /**
     * onScroll() 滚动完成时回调
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mFirstVisibleItem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;
        // 下载的任务应该由onScrollStateChanged里调用，但首次进入程序时onScrollStateChanged并不会调用，
        // 因此在这里为首次进入程序开启下载任务。
        if (mIsFirstEnter && visibleItemCount > 0) {
            loadBitmaps(firstVisibleItem, visibleItemCount);
            mIsFirstEnter = false;
        }
    }

    /**
     * onScrollStateChanged() 正在滚动时回调该方法
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //不再滚动,下载图片
        if (scrollState == SCROLL_STATE_IDLE){
            loadBitmaps(mFirstVisibleItem,mVisibleItemCount) ;
        }else{
            //表示还在滚动，那么取消所有下载任务
            cancelAllDownTasks() ;
        }
    }

    /**
     * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
     * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
     *
     * @param firstVisibleItem 第一个可见的ImageView的下标
     * @param visibleItemCount 屏幕中总共可见的元素数
     */
    private void loadBitmaps(int firstVisibleItem,int visibleItemCount){
        try {
            for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
                String imgUrl = ImageUtils.imageThumbUrls[i];
                Bitmap bitmap = getBitmapFromMemoryCache(imgUrl);
                //如果LruCache没有缓冲的Bitmap对象，那么就会开启异步线程去下载图片
                if (bitmap == null) {
                    BitmapWorkerTask task = new BitmapWorkerTask();
                    mTaskCollection.add(task);
                    task.execute(imgUrl);
                } else {
                    ImageView imageView = (ImageView) mGridView.findViewWithTag(imgUrl);
                    if (imageView != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 取消所有的下载任务
     * 异步任务要cancel
     */
    public void cancelAllDownTasks(){
        if (mTaskCollection != null){
            for (BitmapWorkerTask task : mTaskCollection){
                //传入false，理解结束AsyncTask任务
                task.cancel(false) ;
            }
        }
    }

    class BitmapWorkerTask extends AsyncTask<String,Void,Bitmap>{
        /**
         * 图片的URL地址
         */
        private String imageUrl;

        @Override
        protected Bitmap doInBackground(String... params) {
            imageUrl = params[0];
            Bitmap bm = downloadBitmap(params[0]) ;
            if (bm != null){
                addBitmapToMemoryCache(params[0],bm) ;
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView)mGridView.findViewWithTag(imageUrl) ;
            if (imageView != null && bitmap != null){
                imageView.setImageBitmap(bitmap);
            }
            //从taskCollection中移除该Task
            mTaskCollection.remove(this) ;
        }
    }

    /**
     * 建立http请求，获取图片Bitmap对象
     * @param imageUrl     图片url
     * @return  如果没有或出现异常，那么返回null
     */
    private Bitmap downloadBitmap(String imageUrl){
        Bitmap bitmap = null ;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(imageUrl) ;
            conn = (HttpURLConnection)url.openConnection() ;
            //之所以转成HttpURLConnection，是为了设置超时时间
            conn.setConnectTimeout(5 * 1000);
            conn.setReadTimeout(10 * 1000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            bitmap = BitmapFactory.decodeStream(conn.getInputStream()) ;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null){
                conn.disconnect();
            }
        }
        return bitmap ;
    }
}

