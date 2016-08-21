package com.ssp365.android.freelight.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CommonViewHoder {

    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    public CommonViewHoder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    public static CommonViewHoder get(Context context, View convertView, ViewGroup viewGroup, int layoutId, int position) {
        if (convertView == null) {
            return new CommonViewHoder(context, viewGroup, layoutId, position);
        } else {
            CommonViewHoder viewHoder = (CommonViewHoder) convertView.getTag();
            viewHoder.mPosition = position;
            return viewHoder;
        }
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public int getPosition() {
        return mPosition;
    }

    public View getConvertView() {
        return mConvertView;
    }

    public CommonViewHoder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    public CommonViewHoder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    public CommonViewHoder setCheckBox(int viewId, boolean checked) {
        CheckBox view = getView(viewId);
        view.setChecked(checked);
        return this;
    }

    public CommonViewHoder setImageButton(int viewId, int resId) {
        ImageButton view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }


    public CommonViewHoder setVisible(int viewId, int visibility) {
        TextView view = getView(viewId);
        view.setVisibility(visibility);
        return this;
    }

    public CommonViewHoder setTextSize(int viewId, float textSize) {
        TextView view = getView(viewId);
        view.setTextSize(textSize);
        return this;
    }

    public CommonViewHoder setTextColor(int viewId, int color) {
        TextView view = getView(viewId);
        view.setTextColor(color);
        return this;
    }

}
