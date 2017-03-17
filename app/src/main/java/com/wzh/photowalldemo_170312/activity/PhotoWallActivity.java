package com.wzh.photowalldemo_170312.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.wzh.photowalldemo_170312.R;
import com.wzh.photowalldemo_170312.adapter.PhotoWallAdapter;
import com.wzh.photowalldemo_170312.util.ImageUtils;

public class PhotoWallActivity extends AppCompatActivity {
    private GridView mGridView ;

    private PhotoWallAdapter mAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_wall);
        mGridView = (GridView)findViewById(R.id.gd_photowall) ;
        mAdapter = new PhotoWallAdapter(this,0, ImageUtils.imageThumbUrls,mGridView) ;
        mGridView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cancelAllDownTasks();
    }
}
