package com.ssp365.android.freelight.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.ssp365.android.freelight.R;
import com.ssp365.android.freelight.chart.ChartActivity;
import com.ssp365.android.freelight.common.SmartSportApplication;
import com.ssp365.android.freelight.model.Chenji;

import java.util.ArrayList;

public class ChenjiChartActivity extends Activity {

    protected static final String TAG = "ChenjiChartActivity";

    //成绩情报
    private SmartSportApplication mApplication = null;
    private ArrayList<ArrayList<Chenji>> array_chenji_total = null;

    //初始化状态
    private int init_state = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.chenji_chart_activity);

        mApplication = (SmartSportApplication) getApplication();
        array_chenji_total = mApplication.getArray_chenji_total();

        ChartActivity chartActivity = new ChartActivity();
        chartActivity.setValue(array_chenji_total);
        /*
        Intent intent = chartActivity.execute(ChenjiChartActivity.this);
		init_state = 1;
		startActivity(intent);
		*/
        //要显示图形的View
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.chart);
        View chart = chartActivity.execute(ChenjiChartActivity.this);
        linearLayout.addView(chart);

        //添加运动员取消的处理
        ImageButton bt_chart_cancel = (ImageButton) findViewById(R.id.button_chart_cancle);
        bt_chart_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //返回本activity时直接返回前画面
        if (init_state == 1) {
            finish();
        }
    }
}

