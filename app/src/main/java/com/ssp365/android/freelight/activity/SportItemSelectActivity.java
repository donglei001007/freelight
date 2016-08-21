package com.ssp365.android.freelight.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.ssp365.android.freelight.R;
import com.ssp365.android.freelight.common.SmartSportApplication;
import com.ssp365.android.freelight.db.DBModelDAO;
import com.ssp365.android.freelight.model.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SportItemSelectActivity extends ListActivity {

    ListView mListView = null;
    ArrayList<Model> listModelData;
    SmartSportApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myApplication = (SmartSportApplication) getApplication();
        // mListView = getListView();
        mListView = getListView();


        ArrayList<Map<String, Object>> mData = new ArrayList<>();
        // 查询数据库，得到运动模式信息
        DBModelDAO modelDAO = new DBModelDAO(myApplication.getDb());

        listModelData = modelDAO.find();
        for (int i = 0; i < listModelData.size(); i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("mode_pic", R.drawable.runner);
            item.put("mode_no", String.valueOf(listModelData.get(i).getModel_no()));
            item.put("mode_name", listModelData.get(i).getModel_name());
            mData.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, mData, R.layout.sport_item_content, new String[]{"mode_pic", "mode_no", "mode_name"}, new int[]{R.id.mode_pic, R.id.mode_no, R.id.mode_name});
        setListAdapter(adapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Toast.makeText(SportItemSelectActivity.this, "您选择了标题：" + listModelData.get(position).getModel_no() + "内容：" + listModelData.get(position).getModel_name(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra("selected_model_no", listModelData.get(position).getModel_no());
                intent.putExtra("selected_model_name", listModelData.get(position).getModel_name());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}
