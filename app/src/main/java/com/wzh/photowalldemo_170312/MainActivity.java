package com.wzh.photowalldemo_170312;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.wzh.photowalldemo_170312.activity.PhotoFallsActivity;
import com.wzh.photowalldemo_170312.activity.PhotoWallActivity;
import com.wzh.photowalldemo_170312.adapter.PhotoWallAdapter;
import com.wzh.photowalldemo_170312.util.ImageUtils;

/**
 * 
 * @author wanzihui
 * @date 2017/3/12
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View photoWallView = findViewById(R.id.btn_photowall) ;
        View photoFallsView = findViewById(R.id.btn_photofalls) ;

        photoWallView.setOnClickListener(this);
        photoFallsView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_photowall:
                startActivity(new Intent(MainActivity.this, PhotoWallActivity.class));
                break ;
            case R.id.btn_photofalls:
                startActivity(new Intent(MainActivity.this, PhotoFallsActivity.class));
                break ;
            default:
                break ;
        }
    }
}
