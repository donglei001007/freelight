package com.ssp365.android.freelight.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ssp365.android.freelight.R;

/**
 * 软件欢迎界面
 *
 * @author 传博科技
 */
public class WhatsnewActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whatnew);
    }

    /**
     * 开始按钮的响应事件
     *
     * @param v
     */
    public void startbutton(View v) {
        Intent intent = new Intent();
        intent.setClass(WhatsnewActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

}
