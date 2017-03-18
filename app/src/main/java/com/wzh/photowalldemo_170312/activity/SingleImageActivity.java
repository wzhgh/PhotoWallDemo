package com.wzh.photowalldemo_170312.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wzh.photowalldemo_170312.R;
import com.wzh.photowalldemo_170312.view.ZoomImageView;

/**
 * @author wanzihui
 */
public class SingleImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image);
        ZoomImageView zoomImageView = (ZoomImageView)findViewById(R.id.iv_zoom_photo) ;
        String imagePath = getIntent().getStringExtra("image_path") ;
        Bitmap bp = BitmapFactory.decodeFile(imagePath) ;
        zoomImageView.setImageBitmap(bp);
    }
}
