package com.ssp365.android.freelight.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import com.ssp365.android.freelight.R;
import com.ssp365.android.freelight.activity.SporterListActivity;
import com.ssp365.android.freelight.model.Sporter;

import java.util.List;

public class SporterListAdapter extends CommonAdapter<Sporter> {

    private String mType = "";

    public SporterListAdapter(Context context, List<Sporter> datas, int layoutId, String type) {
        super(context, datas, layoutId);
        this.mType = type;
    }

    @Override
    public void convert(final CommonViewHoder viewHolder, final Sporter sporter) {
        // TODO Auto-generated method stub
        //运动员姓名的拼音
        viewHolder.setText(R.id.sporter_py, sporter.getSporter_name_py());
        //运动员的姓名
        viewHolder.setText(R.id.sporter_nm, sporter.getSporter_name());
        //运动员性别的图标
        if (sporter.getSporter_xingbie().equals("0")) {
            viewHolder.setImageResource(R.id.sporter_GenderImg, R.drawable.man);
        } else {
            viewHolder.setImageResource(R.id.sporter_GenderImg, R.drawable.woman);
        }

        //运动员拼音的缩写
        //判断前后Item是否匹配，如果不匹配则设置并显示，匹配则取消
        int idx = viewHolder.getPosition() - 1;
        char previewChar = idx >= 0 ? mDatas.get(idx).getSporter_name_py().charAt(0) : ' ';
        char currentChar = mDatas.get(viewHolder.getPosition()).getSporter_name_py().charAt(0);
        //将小写字符转换为大写字符
        char newPreviewChar = Character.toUpperCase(previewChar);
        char newCurrentChar = Character.toUpperCase(currentChar);
        if (newCurrentChar != newPreviewChar) {
            viewHolder.setText(R.id.text_first_char_hint, String.valueOf(newCurrentChar));
            viewHolder.setVisible(R.id.text_first_char_hint, View.VISIBLE);
        } else {
            // 此段代码不可缺：实例化一个CurrentView后，会被多次赋值并且只有最后一次赋值的position是正确   
            viewHolder.setVisible(R.id.text_first_char_hint, View.GONE);
        }

        //选择框
        final CheckBox cb = viewHolder.getView(R.id.sporter_checked);
        cb.setChecked(sporter.isSelected());
        cb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sporter.setSelected(cb.isChecked());
            }
        });
        //设定模式时不可见
        if (SporterListActivity.START_TYPE_SET.equals(mType)) {
            viewHolder.setVisible(R.id.sporter_checked, View.INVISIBLE);
            //选择模式时可见
        } else {
            viewHolder.setVisible(R.id.sporter_checked, View.VISIBLE);
        }
    }

    public void updateListView(List<Sporter> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }
}
