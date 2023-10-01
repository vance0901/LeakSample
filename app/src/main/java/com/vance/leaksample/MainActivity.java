package com.vance.leaksample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.RectangleIndicator;

public class MainActivity extends AppCompatActivity {
    ScanningView scanningView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        scanningView = findViewById(R.id.scanningView);
        Banner banner = findViewById(R.id.banner);
        banner.setAdapter(new BannerImageAdapter<String>(Data.banners) {

            @Override
            public void onBindView(BannerImageHolder holder, String data, int position, int size) {
                Glide.with(holder.itemView)
                        .load(data)
                        .into(holder.imageView);
            }
        }).addBannerLifecycleObserver(this)
                .setIndicator(new RectangleIndicator(this));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MyAdapter adapter = new MyAdapter(this);
        recyclerView.setAdapter(adapter);

        getLifecycle().addObserver(scanningView);
    }

    public void jump(View view) {
        startActivity(new Intent(this, SettingActivity.class));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver(scanningView);
    }

}