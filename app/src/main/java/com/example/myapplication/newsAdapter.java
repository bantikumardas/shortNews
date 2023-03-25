package com.example.myapplication;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jsibbold.zoomage.ZoomageView;

import java.util.ArrayList;


public class newsAdapter extends ArrayAdapter<newsClass> {
    Context context;
    ArrayList<newsClass> arrayList;
    LinearLayout share;
    LinearLayout bookmark;
    ZoomageView newsImage;

    public newsAdapter(@NonNull Context context, @NonNull ArrayList<newsClass> arrayList) {
        super(context, 0, arrayList);
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.news_layout, parent, false);
        }
        newsClass item = arrayList.get(position);
        TextView heading = view.findViewById(R.id.heading);
        heading.setText(item.getTitle());
        TextView description = view.findViewById(R.id.description);
        description.setText(item.getDescription());
        TextView date = view.findViewById(R.id.date);
        date.setText(item.getDate().substring(0, 10));
        TextView catagory = view.findViewById(R.id.catagory);
        catagory.setText(item.getCatagory());
        TextView country = view.findViewById(R.id.country);
        country.setText(item.getCountry());
        newsImage = view.findViewById(R.id.news_image);
        newsImage.setZoomable(true);
        String imageURL = item.getImageUrl();
        if (imageURL.equals("null")) {
            Glide.with(view).load(R.drawable.newsicon).into(newsImage);
        }else {
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .dontAnimate()
                    .dontTransform()
                    .placeholder(R.drawable.loading)
                    .priority(Priority.IMMEDIATE)
                    .encodeFormat(Bitmap.CompressFormat.PNG)
                    .format(DecodeFormat.DEFAULT);
            Glide.with(view).load(imageURL).apply(requestOptions).into(newsImage);
        }
        bookmark = view.findViewById(R.id.bookmark);
        share = view.findViewById(R.id.share);
        shareBtn(item.getTitle(), item.getLink(), item.getContent());
        onBookmarkClick();
        onImageClick(imageURL);
        return view;
    }

    private void onBookmarkClick() {
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onImageClick(String url) {
        newsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ImageViewActivity.class);
                intent.putExtra("urlImage",url);
                context.startActivity(intent);
            }
        });
    }

    private void shareBtn(String text, String url, String content) {
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, text.concat("\n"+url)+"\n"+content);
                // Launch the sharing dialog
                context.startActivity(Intent.createChooser(shareIntent, "Share image"));
            }
        });
    }

}
