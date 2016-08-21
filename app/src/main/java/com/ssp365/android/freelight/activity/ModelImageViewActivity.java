package com.ssp365.android.freelight.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssp365.android.freelight.R;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ModelImageViewActivity extends Activity {
    private ImageView imageView;
    private ImageButton bt_mode_cancel;
    private TextView mode_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.model_imageview_activity);
        imageView = (ImageView) findViewById(R.id.imageView);
        Intent intent = getIntent();
        String model_pic = intent.getStringExtra("pic");


        InputStream inputStream = null;
        try {
            inputStream = getResources().getAssets().open(model_pic + ".png");
            //inputStream = getResources().getAssets().open(model_pic+".png");
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        imageView.setImageBitmap(bitmap);

        //添加运动员取消的处理
        bt_mode_cancel = (ImageButton) findViewById(R.id.button_mode_cancle);
        bt_mode_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //添加运动员取消的处理
        String model_name = intent.getStringExtra("name");
        mode_name = (TextView) findViewById(R.id.mode_name);
        mode_name.setText(model_name);


        //int imageId = intent.getIntExtra("id", R.drawable.pb_cc);
        //imageView.setImageResource(R.drawable.pb_cc);
        //imageView.setImageDrawable(drawable)


    }
}
