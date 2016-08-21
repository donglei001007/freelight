package com.ssp365.android.freelight.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter {
    protected List<T> mDatas = null;
    private Context mContext;
    int mLayoutId;

    public CommonAdapter(Context context, List<T> datas, int layoutId) {
        this.mContext = context;
        this.mDatas = datas;
        this.mLayoutId = layoutId;
    }

    public int getCount() {
        return this.mDatas.size();
    }

    public Object getItem(int position) {
        return mDatas.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        CommonViewHoder viewHolder = CommonViewHoder.get(mContext, convertView, parent, mLayoutId, position);

        convert(viewHolder, mDatas.get(position));

        return viewHolder.getConvertView();

    }

    public abstract void convert(final CommonViewHoder viewHolder, T t);

    public void setData(List<T> datas) {
        this.mDatas = datas;
    }
}