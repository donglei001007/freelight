package com.ssp365.android.freelight.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.ssp365.android.freelight.R;

/**
 * 软件加载界面
 *
 * @author 传博科技
 */
public class Appstart extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.appstart);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Appstart.this,
                        WhatsnewActivity.class);
                startActivity(intent);
                Appstart.this.finish();
            }
        }, 1000);
    }
}