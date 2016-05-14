package com.tl.pro.travelkit.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.util.log.L;

public class PercentActivity extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percent);
        imageView = (ImageView) findViewById(R.id.image);
        ImageLoader.getInstance().displayImage("http://pic36.nipic.com/20131217/6704106_233034463381_2.jpg", imageView);
        L.e("TAG", imageView.getWidth() + "?" + imageView.getHeight());
    }
}
