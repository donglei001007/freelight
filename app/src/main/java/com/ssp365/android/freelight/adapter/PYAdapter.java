package com.ssp365.android.freelight.adapter;

import android.content.Context;

import com.ssp365.android.freelight.R;

import java.util.ArrayList;
import java.util.List;

public class PYAdapter extends CommonAdapter<String> {

    private List<String> mDatas = null;
    private int mHeight = 0;
    private String py[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R"
            , "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public PYAdapter(Context context, int layoutId, int height) {
        super(context, null, layoutId);
        mDatas = new ArrayList<String>();
        for (int i = 0; i < py.length; i++) {
            mDatas.add(py[i]);
        }
        super.setData(mDatas);
        this.mHeight = height;
    }

    @Override
    public void convert(final CommonViewHoder viewHolder, String t) {
        // TODO Auto-generated method stub
        viewHolder.setText(R.id.py, t);
        //随着屏幕的大小设置字体的大小
        float textSize = 0f;
        //1920*1080时
        if (mHeight > 1500) {
            textSize = 14.5f;
            //1280*720时
        } else if (mHeight > 1000) {
            textSize = 13f;
            //540X960时
        } else if (mHeight > 900) {
            textSize = 12f;
            //其它
        } else {
            textSize = 11f;
        }
        viewHolder.setTextSize(R.id.py, textSize);
    }
}
