package com.ssp365.android.freelight.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssp365.android.freelight.R;
import com.ssp365.android.freelight.model.Body;

import java.util.List;

/**
 * 运动员列表的适配器
 *
 * @author Dl
 */
public class AMDragRateAdapter extends BaseAdapter {

    private Context context;
    List<Body> items;// 适配器的数据源

    public AMDragRateAdapter(Context context, List<Body> list) {
        this.context = context;
        this.items = list;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public void remove(int arg0) {// 删除指定位置的item
        items.remove(arg0);
        this.notifyDataSetChanged();// 不要忘记更改适配器对象的数据源
    }

    public void insert(Body item, int arg0) {// 在指定位置插入item
        items.add(arg0, item);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Body item = (Body) getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.sport_list_item, null);
            viewHolder.tvNo = (TextView) convertView.findViewById(R.id.tvNo);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.ivDragHandle = (ImageView) convertView.findViewById(R.id.drag_handle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvNo.setText(String.valueOf(item.getNo()));
        viewHolder.tvTitle.setText(item.getCoin());

        return convertView;
    }

    class ViewHolder {
        TextView tvNo;
        TextView tvTitle;
        ImageView ivDragHandle;
    }
}
