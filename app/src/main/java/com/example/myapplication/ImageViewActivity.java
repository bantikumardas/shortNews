package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {
    ZoomageView img;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        String url=getIntent().getStringExtra("urlImage");
        img=(ZoomageView)findViewById(R.id.imageview);
        progressBar=(ProgressBar) findViewById(R.id.progress_for_image_view);
        if(url.equals("null")){
            Glide.with(this).load(R.drawable.newsicon).into(img);
            progressBar.setVisibility(View.GONE);
        }else{
            Picasso.get().load(url).into(img, new Callback() {
                @Override
                public void onSuccess() {
                    // Image loaded successfully
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    // Image failed to load
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ImageViewActivity.this, "Image loading failed", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }
}