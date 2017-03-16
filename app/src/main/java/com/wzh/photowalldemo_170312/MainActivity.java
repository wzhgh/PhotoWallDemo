package com.wzh.photowalldemo_170312;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.wzh.photowalldemo_170312.adapter.PhotoWallAdapter;
import com.wzh.photowalldemo_170312.util.ImageUtils;

/**
 * 
 * @author wanzihui
 * @date 2017/3/12
 *
 */
public class MainActivity extends AppCompatActivity {

    private GridView mGridView ;

    private PhotoWallAdapter mAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGridView = (GridView)findViewById(R.id.gd_photowall) ;
        mAdapter = new PhotoWallAdapter(this,0, ImageUtils.imageThumbUrls,mGridView) ;
        mGridView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null)
            mAdapter.cancelAllDownTasks();
    }
}
