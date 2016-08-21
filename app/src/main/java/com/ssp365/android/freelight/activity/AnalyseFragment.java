package com.ssp365.android.freelight.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.RemoveListener;
import com.ssp365.android.freelight.R;
import com.ssp365.android.freelight.common.SmartSportApplication;
import com.ssp365.android.freelight.db.DBChenjiDAO;
import com.ssp365.android.freelight.db.DBModelDAO;
import com.ssp365.android.freelight.db.DBSporterDAO;
import com.ssp365.android.freelight.model.Body;
import com.ssp365.android.freelight.model.Chenji;
import com.ssp365.android.freelight.model.ChenjiDetail;
import com.ssp365.android.freelight.model.Model;
import com.ssp365.android.freelight.model.ModelDetail;
import com.ssp365.android.freelight.model.Parameter;
import com.ssp365.android.freelight.model.SpinnerData;
import com.ssp365.android.freelight.model.Sporter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * 运动成绩分析碎片
 */
@SuppressLint("ValidFragment")
public class AnalyseFragment extends Fragment {

    // Debugging
    private static final String TAG = "WeixinFragment";
    private static final boolean D = true;

    // 对话框:开始日选择
    private static final int DIALOG_DAY_START = 0;
    // 对话框:终了日选择
    private static final int DIALOG_DAY_END = 1;

    // 回调区分：运动员选择
    public static final int REQUEST_SPORTER_SELECT = 0;
    public SmartSportApplication mApplication = null;

    // 最大分析运动员数量
    private final int MAX_SPORTER = 30;

    // DB关联
    DBModelDAO modelDAO = null;
    DBSporterDAO sporterDAO = null;
    DBChenjiDAO chenjiDAO = null;
    // 模式选择
    private Spinner sp_model_no;
    ArrayList<Model> listModel;
    public int selected_model_no;
    // 运动员选择
    private DragSortListView selectedSporterList;
    private ImageButton bt_sporter_select;
    private AMDragRateAdapter adapter;
    List<Body> sporterListRes;// listview的数据源

    // 分析开始日
    private TextView tv_analyse_start;
    // 分析终了日
    private TextView tv_analyse_end;

    // 日期选择区分
    int analyse_day_case = -1;
    // 期间开始日
    int year_start, month_start, day_start;
    // 期间终了日
    int year_end, month_end, day_end;

    // 曲线分析按钮
    private BootstrapButton bt_analyse_chart;
    // 表格分析按钮
    private BootstrapButton bt_analyse_table;

    // 运动员的成绩
    Vector<ArrayList<Chenji>> vecChenjiTotal = new Vector<ArrayList<Chenji>>();
    // 最好成绩和最差成绩
    ClsSpeed clsSpeed = new ClsSpeed();

    // 成绩种类选择
    RadioGroup rg_chenji_zhonglei;
    // 分析模式
    int analyse_type;

    View analyseFragment = null;

    public AnalyseFragment(SmartSportApplication mApplication) {
        this.mApplication = mApplication;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (D)
            Log.i(TAG, "+++ ON CREATE START +++");
        super.onCreate(savedInstanceState);

        analyseFragment = inflater.inflate(R.layout.analyse_activity, container, false);

        activityInit();

        if (D)
            Log.i(TAG, "+++ ON CREATE END +++");

        return analyseFragment;
    }

    private void activityInit() {
        // 模式选择框
        sp_model_no = (Spinner) analyseFragment.findViewById(R.id.mode_sp);
        List<SpinnerData> list_model = new ArrayList<SpinnerData>();
        modelDAO = new DBModelDAO(mApplication.getDb());
        listModel = modelDAO.find();
        list_model.add(new SpinnerData("", ""));
        for (int i = 0; i < listModel.size(); i++) {
            SpinnerData c = new SpinnerData(String.valueOf(listModel.get(i).getModel_no()), listModel.get(i).getModel_name());
            list_model.add(c);
        }
        ArrayAdapter<SpinnerData> adapter_model = new ArrayAdapter<SpinnerData>(this.getActivity(), R.layout.spinner_item, list_model);
        adapter_model.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp_model_no.setAdapter(adapter_model);

        // 运动员选择框
        selectedSporterList = (DragSortListView) analyseFragment.findViewById(R.id.sporterList);
        sporterListRes = new ArrayList<Body>();
        adapter = new AMDragRateAdapter(this.getActivity(), sporterListRes);
        selectedSporterList.setAdapter(adapter);
        selectedSporterList.setDropListener(onDrop);
        selectedSporterList.setRemoveListener(onRemove);
        selectedSporterList.setDragEnabled(true); // 设置是否可拖动。

        // 运动员选择按钮的处理
        bt_sporter_select = (ImageButton) analyseFragment.findViewById(R.id.ibt_sporter_select);
        bt_sporter_select.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(analyseFragment.getContext(), SporterListActivity.class);
                intent.putExtra("start_type", SporterListActivity.START_TYPE_GET);
                startActivityForResult(intent, REQUEST_SPORTER_SELECT);
            }
        });

        tv_analyse_start = (TextView) analyseFragment.findViewById(R.id.analyse_start);
        tv_analyse_start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                analyse_day_case = DIALOG_DAY_START;
                String[] str_day_start = tv_analyse_start.getText().toString().split("-");
                year_start = Integer.valueOf(str_day_start[0]).intValue();
                month_start = Integer.valueOf(str_day_start[1]).intValue() - 1;
                day_start = Integer.valueOf(str_day_start[2]).intValue();
                showDialog(DIALOG_DAY_START);
            }
        });

        tv_analyse_end = (TextView) analyseFragment.findViewById(R.id.analyse_end);
        tv_analyse_end.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                analyse_day_case = DIALOG_DAY_END;
                String[] str_day_end = tv_analyse_end.getText().toString().split("-");
                year_end = Integer.valueOf(str_day_end[0]).intValue();
                month_end = Integer.valueOf(str_day_end[1]).intValue() - 1;
                day_end = Integer.valueOf(str_day_end[2]).intValue();
                showDialog(DIALOG_DAY_END);
            }
        });

        // 年月日没有初始化时，开始日为一月前，终了日为当日
        final Calendar c = Calendar.getInstance();
        Date day = c.getTime();
        tv_analyse_end.setText(new SimpleDateFormat("yyyy-MM-dd").format(day));
        Date before_month_day = new Date();
        month_end = c.get(Calendar.MONTH) + 1;
        tv_analyse_start.setText(new SimpleDateFormat("yyyy-MM-dd").format(day));
        // 开始日往前30日
        if (month_end == 1 || month_end == 3 || month_end == 5 || month_end == 7 || month_end == 8 || month_end == 10 || month_end == 12) {
            before_month_day.setTime(((day.getTime() / 1000) - 60 * 60 * 24 * 30) * 1000);
            // 开始日往前31日
        } else if (month_end == 2 || month_end == 4 || month_end == 6 || month_end == 9 || month_end == 11) {
            before_month_day.setTime(((day.getTime() / 1000) - 60 * 60 * 24 * 31) * 1000);
        }
        tv_analyse_start.setText(new SimpleDateFormat("yyyy-MM-dd").format(before_month_day));

        // 曲线分析按钮
        bt_analyse_chart = (BootstrapButton) analyseFragment.findViewById(R.id.button_analyse_chart);
        bt_analyse_chart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 曲线分析
                if (!analyse_chenji(analyse_type)) {
                    return;
                }
                //
                ArrayList<ArrayList<Chenji>> array_chenji_total = new ArrayList<ArrayList<Chenji>>();
                for (int i = 0; i < vecChenjiTotal.size(); i++) {
                    array_chenji_total.add(vecChenjiTotal.get(i));
                }
                mApplication.setArray_chenji_total(array_chenji_total);
                Intent intent = new Intent(analyseFragment.getContext(), ChenjiChartActivity.class);
                startActivity(intent);
            }
        });

        // 表格分析按钮
        bt_analyse_table = (BootstrapButton) analyseFragment.findViewById(R.id.button_analyse_table);
        bt_analyse_table.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 曲线分析
                if (!analyse_chenji(analyse_type)) {
                    return;
                }
                //
                ArrayList<ArrayList<Chenji>> array_chenji_total = new ArrayList<ArrayList<Chenji>>();
                for (int i = 0; i < vecChenjiTotal.size(); i++) {
                    array_chenji_total.add(vecChenjiTotal.get(i));
                }
                mApplication.setArray_chenji_total(array_chenji_total);
                Intent intent = new Intent(analyseFragment.getContext(), ChenjiListActivity.class);
                startActivity(intent);
            }
        });

        // 成绩种类选择、
        rg_chenji_zhonglei = (RadioGroup) analyseFragment.findViewById(R.id.rg_chenji_zhonglei);
        rg_chenji_zhonglei.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_total_chenji) {
                    analyse_type = Parameter.ANALYSE_TYPE_CHENJI;
                } else if (checkedId == R.id.rb_detail_chenji) {
                    analyse_type = Parameter.ANALYSE_TYPE_CHENJI_DETAIL;
                }
            }
        });
    }

    /*
     * 成绩的查询及处理 0：成绩查询 1：成绩详细查询
     */
    private boolean analyse_chenji(int search_type) {

        vecChenjiTotal = new Vector<ArrayList<Chenji>>();

        boolean re_flag = false;
        // 分析模式
        int selected_model_no = -1;
        // 设定选定的模式
        if (((SpinnerData) sp_model_no.getSelectedItem()).getValue().trim().length() == 0) {
            selected_model_no = -1;
        } else {
            selected_model_no = Integer.valueOf(((SpinnerData) sp_model_no.getSelectedItem()).getValue());
            for (int i = 0; i < listModel.size(); i++) {
                if (listModel.get(i).getModel_no() == selected_model_no) {
                    mApplication.setModel(listModel.get(i));
                    ArrayList<ModelDetail> modelDetails = modelDAO.findDetail(listModel.get(i).getModel_no());
                    mApplication.setModelDetails(modelDetails);
                    break;
                }
            }
        }

        // 分析运动员
        // 设定选定的运动员
        ArrayList<Sporter> arrayListSporter = new ArrayList<Sporter>();
        for (int i = 0; i < sporterListRes.size(); i++) {
            arrayListSporter.add((sporterListRes.get(i)).getSporter());
        }
        mApplication.setArraySporter(arrayListSporter);

        // 没有选择模式时报错
        boolean error_flag = false;
        String error_msg = "";
        if (selected_model_no == -1) {
            error_flag = true;
            if (error_msg.length() == 0) {
                error_msg = error_msg.concat(analyseFragment.getContext().getResources().getText(R.string.model_err).toString());
            } else {
                error_msg = error_msg.concat("\r\n" + analyseFragment.getContext().getResources().getText(R.string.model_err).toString());
            }
        }
        // 没有设定运动员时报错
        if (arrayListSporter.size() <= 0) {
            error_flag = true;
            if (error_msg.length() == 0) {
                error_msg = error_msg.concat(analyseFragment.getContext().getResources().getText(R.string.sporter_err).toString());
            } else {
                error_msg = error_msg.concat("\r\n" + analyseFragment.getContext().getResources().getText(R.string.sporter_err).toString());
            }
        }

        int[] sporter_no = new int[arrayListSporter.size()];
        for (int i = 0; i < arrayListSporter.size(); i++) {
            sporter_no[i] = arrayListSporter.get(i).getSporter_no();
        }

        // 分析期间
        String str_analyse_start = tv_analyse_start.getText().toString();
        String str_analyse_end = tv_analyse_end.getText().toString();
        Log.i(TAG, "str_analyse_start:" + str_analyse_start);
        Log.i(TAG, "str_analyse_end:" + str_analyse_end);
        // 期间设置不合理时报错
        if (str_analyse_end.compareTo(str_analyse_start) < 0) {
            error_flag = true;
            if (error_msg.length() == 0) {
                error_msg = error_msg.concat(analyseFragment.getContext().getResources().getText(R.string.day_err).toString());
            } else {
                error_msg = error_msg.concat("\r\n" + analyseFragment.getContext().getResources().getText(R.string.day_err).toString());
            }
        }

        // 错误信息显示
        if (error_flag) {
            new AlertDialog.Builder(analyseFragment.getContext()).setTitle("错误提示").setMessage(error_msg)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).create().show();
            return re_flag;
        }

        str_analyse_start = str_analyse_start + " 00:00:00";
        str_analyse_end = str_analyse_end + " 24:00:00";

        chenjiDAO = new DBChenjiDAO(mApplication.getDb());
        ArrayList<Chenji> arrayChenjiTotal = new ArrayList<Chenji>();
        // 成绩分析
        if (Parameter.ANALYSE_TYPE_CHENJI == search_type) {
            arrayChenjiTotal = chenjiDAO.findChenji(sporter_no, selected_model_no, str_analyse_start, str_analyse_end);
            // 成绩详细分析
        } else if (Parameter.ANALYSE_TYPE_CHENJI_DETAIL == search_type) {
            arrayChenjiTotal = chenjiDAO.findChenjiDetail(sporter_no, selected_model_no, str_analyse_start, str_analyse_end);
        }

        if (arrayChenjiTotal.size() <= 0) {
            new AlertDialog.Builder(analyseFragment.getContext()).setTitle("提示")
                    .setMessage(analyseFragment.getContext().getResources().getText(R.string.search_err).toString())
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).create().show();
            return false;
        }

        // 成绩详细时，计时点个数
        int sub_count = 0;
        if (Parameter.ANALYSE_TYPE_CHENJI_DETAIL == search_type) {
            sub_count = arrayChenjiTotal.get(0).getChenji_detail().size();
        }

        ArrayList<Chenji> arrayChenji = new ArrayList<Chenji>();
        int sporter_no_bf = -1;
        Log.i(TAG, "arrayChenjiTotal.size():" + arrayChenjiTotal.size());
        // 运动员计数器
        int sporter_count = 0;
        for (int i = 0; i < arrayChenjiTotal.size(); i++) {
            Log.i(TAG, "chenji:" + arrayChenjiTotal.get(i).getSporter_no() + " " + arrayChenjiTotal.get(i).getSporter_name() + " "
                    + arrayChenjiTotal.get(i).getChenji_day() + " " + arrayChenjiTotal.get(i).getModel_total_speed());
            if (i == 0) {
                arrayChenji.add(arrayChenjiTotal.get(i));
                // 运动员番号不变时
            } else if (sporter_no_bf == arrayChenjiTotal.get(i).getSporter_no()) {
                arrayChenji.add(arrayChenjiTotal.get(i));
                // 运动员番号变化时
            } else {
                vecChenjiTotal.add(arrayChenji);
                arrayChenji = new ArrayList<Chenji>();
                arrayChenji.add(arrayChenjiTotal.get(i));
                // 运动员数增一
                sporter_count++;
            }
            sporter_no_bf = arrayChenjiTotal.get(i).getSporter_no();
        }
        // 最后一条记录的保存
        if (arrayChenji.size() > 0) {
            vecChenjiTotal.add(arrayChenji);
            // 运动员数增一
            sporter_count++;
        }

        // 超过最大分析运动员数时报错
        if (sporter_count > MAX_SPORTER) {
            new AlertDialog.Builder(analyseFragment.getContext()).setTitle("错误提示").setMessage(R.string.search_count_err)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).create().show();
            return false;
        }

        // 日期列表
        Vector<String> vec_chenji_day = new Vector<String>();

        clsSpeed.max_speed = 0;
        clsSpeed.min_speed = 1000;

        Log.i(TAG, "vecChenjiTotal.size:" + vecChenjiTotal.size());

        // 根据本次成绩情况，列出所有的成绩列表
        for (int i = 0; i < vecChenjiTotal.size(); i++) {
            ArrayList<Chenji> array_chenji_day = vecChenjiTotal.get(i);

            int index = 0;
            for (int j = 0; ; ) {
                // 第一条记录直接添加到成绩列表中
                if (i == 0) {
                    if (j >= array_chenji_day.size()) {
                        break;
                    }
                    vec_chenji_day.add(array_chenji_day.get(j).getChenji_day().substring(0, 10));
                    // 设定最大最小的速度
                    getMaxMin(array_chenji_day.get(j), clsSpeed);
                    j++;
                } else {
                    // 成绩和列表都循环结束时，这一轮循环结束
                    if (j >= array_chenji_day.size() && index >= vec_chenji_day.size()) {
                        break;
                        // 只有列表循环结束时，把剩下的成绩都加到列表后
                    } else if (j < array_chenji_day.size() && index >= vec_chenji_day.size()) {
                        while (j < array_chenji_day.size()) {
                            vec_chenji_day.add(array_chenji_day.get(j).getChenji_day().substring(0, 10));
                            // 设定最大最小的速度
                            getMaxMin(array_chenji_day.get(j), clsSpeed);
                            j++;
                        }
                        break;
                        // 只有成绩循环结束时，这一轮循环结束
                    } else if (j >= array_chenji_day.size() && index < vec_chenji_day.size()) {
                        break;
                    }

                    // 成绩<列表的内容时，给列表添加内容，成绩和列表的指针都向后移
                    if (vec_chenji_day.get(index).compareTo(array_chenji_day.get(j).getChenji_day().substring(0, 10)) > 0) {
                        vec_chenji_day.add(index, array_chenji_day.get(j).getChenji_day().substring(0, 10));
                        // 设定最大最小的速度
                        getMaxMin(array_chenji_day.get(j), clsSpeed);
                        index++;
                        j++;
                        // 成绩=列表的内容时，列表和成绩的指针都向后移
                    } else if (vec_chenji_day.get(index).compareTo(array_chenji_day.get(j).getChenji_day().substring(0, 10)) == 0) {
                        // 设定最大最小的速度
                        getMaxMin(array_chenji_day.get(j), clsSpeed);
                        index++;
                        j++;
                        // 成绩>列表的内容时，列表内容不变，列表的指针都向后移
                    } else if (vec_chenji_day.get(index).compareTo(array_chenji_day.get(j).getChenji_day().substring(0, 10)) < 0) {
                        index++;
                    }
                }
            }
        }
        for (int i = 0; i < vec_chenji_day.size(); i++) {
            Log.i(TAG, "vec_chenji_day[" + i + "]:" + vec_chenji_day.get(i));
        }
        Log.i(TAG, "min_speed:" + clsSpeed.min_speed);
        Log.i(TAG, "max_speed:" + clsSpeed.max_speed);
        // 参照所有的成绩列表，给每个队员的成绩进行遍历，空出没有成绩的点
        for (int i = 0; i < vecChenjiTotal.size(); i++) {
            ArrayList<Chenji> array_chenji_day = vecChenjiTotal.get(i);
            Log.i(TAG, "i:" + i);
            for (int j = 0; j < array_chenji_day.size(); j++) {
                Log.i(TAG, "array_chenji_day[" + j + "]:" + array_chenji_day.get(j).getChenji_day());
            }
            int index = 0;
            int j = 0;
            while (true) {
                // 成绩和列表都循环结束时，这一轮循环结束
                if (j >= array_chenji_day.size() && index >= vec_chenji_day.size()) {
                    break;
                    // 只有成绩循环结束时，后面的点全部补充空点
                } else if (j >= array_chenji_day.size() && index < vec_chenji_day.size()) {
                    for (; index < vec_chenji_day.size(); index++) {
                        array_chenji_day.add(getTmpChenji(array_chenji_day.get(0), vec_chenji_day.get(index), sub_count, search_type));
                    }
                    break;
                }
                Log.i(TAG, "index:" + index + " " + vec_chenji_day.get(index) + " j:" + j + " " + array_chenji_day.get(j).getChenji_day());
                // 成绩=列表的内容时，成绩不变，列表和成绩的指针都向后移
                if (vec_chenji_day.get(index).compareTo(array_chenji_day.get(j).getChenji_day().substring(0, 10)) == 0) {
                    index++;
                    j++;
                    // 成绩>列表的内容时，成绩列表插入不显示点，列表的指针都向后移
                } else if (vec_chenji_day.get(index).compareTo(array_chenji_day.get(j).getChenji_day().substring(0, 10)) < 0) {
                    array_chenji_day.add(j, getTmpChenji(array_chenji_day.get(0), vec_chenji_day.get(index), sub_count, search_type));
                    index++;
                    // 追加数据，所以成绩指针也向后移动
                    j++;
                }
            }
        }
        Log.i(TAG, "vecChenjiTotal.size:" + vecChenjiTotal.size());
        // 参照所有的成绩列表，给每个队员的成绩进行遍历，空出没有成绩的点
        for (int i = 0; i < vecChenjiTotal.size(); i++) {
            ArrayList<Chenji> array_chenji_day = vecChenjiTotal.get(i);
            for (int j = 0; j < array_chenji_day.size(); j++) {
                Log.i(TAG, "vecChenjiTotal:" + array_chenji_day.get(j).getSporter_no() + "_" + array_chenji_day.get(j).getSporter_name()
                        + "_array_chenji_day[" + j + "]:" + array_chenji_day.get(j).getChenji_day() + ":"
                        + array_chenji_day.get(j).getModel_total_speed() + " " + array_chenji_day.get(j).getModel_total_length() + " "
                        + array_chenji_day.get(j).getModel_total_time());
            }
        }
        return true;
    }

    // 取得曲线中的空点
    private Chenji getTmpChenji(Chenji chenji, String chenji_day, int sub_count, int search_type) {
        Chenji chenji_tmp = new Chenji();
        chenji_tmp.setSporter_no(chenji.getSporter_no());
        chenji_tmp.setSporter_name(chenji.getSporter_name());
        chenji_tmp.setModel_no(-1);
        chenji_tmp.setModel_total_speed(-1);
        chenji_tmp.setModel_total_time(-1000);
        // 带成绩详细时，成绩详细记录补充
        if (Parameter.ANALYSE_TYPE_CHENJI_DETAIL == search_type) {
            ArrayList<ChenjiDetail> chenji_detail = new ArrayList<ChenjiDetail>();
            for (int i = 0; i < sub_count; i++) {
                ChenjiDetail chenjiDetail = new ChenjiDetail();
                chenjiDetail.setModel_sub_no(-1);
                chenjiDetail.setModel_sub_speed(-1);
                chenjiDetail.setModel_sub_time(-1000);
                chenji_detail.add(chenjiDetail);
            }
            chenji_tmp.setChenji_detail(chenji_detail);
        }
        chenji_tmp.setChenji_day(chenji_day);
        return chenji_tmp;
    }

    // 取得曲线中的空点
    private void getMaxMin(Chenji chenji, ClsSpeed clsSpeed) {
        // 取得最大速度
        if (chenji.getModel_total_speed() > clsSpeed.max_speed) {
            clsSpeed.max_speed = chenji.getModel_total_speed();
        }
        // 取得最小速度
        if (chenji.getModel_total_speed() < clsSpeed.min_speed) {
            clsSpeed.min_speed = chenji.getModel_total_speed();
        }
    }

    @Override
    public void onStart() {
        if (D)
            Log.i(TAG, "++ ON START START ++");
        super.onStart();
    }

    @Override
    public synchronized void onResume() {
        if (D)
            Log.i(TAG, "+ ON RESUME +");
        super.onResume();

    }

    @Override
    public synchronized void onPause() {
        if (D)
            Log.i(TAG, "- ON PAUSE -");
        super.onPause();
    }

    @Override
    public void onStop() {
        if (D)
            Log.i(TAG, "-- ON STOP --");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (D)
            Log.i(TAG, "--- ON DESTROY ---");
        super.onDestroy();

    }

    protected void showDialog(int id) {
        switch (id) {
            case DIALOG_DAY_START:
                Log.i(TAG, "analyseFragment.getContext():" + analyseFragment.getContext());
                Log.i(TAG, "mDateSetListener:" + mDateSetListener);
                Log.i(TAG, "year_start:" + year_start);
                Log.i(TAG, "month_start:" + month_start);
                Log.i(TAG, "day_start:" + day_start);

                new DatePickerDialog(analyseFragment.getContext(), mDateSetListener, year_start, month_start, day_start).show();
                break;
            case DIALOG_DAY_END:
                Log.i(TAG, "analyseFragment.getContext():" + analyseFragment.getContext());
                Log.i(TAG, "mDateSetListener:" + mDateSetListener);
                Log.i(TAG, "year_end:" + year_end);
                Log.i(TAG, "month_end:" + month_end);
                Log.i(TAG, "day_end:" + day_end);

                new DatePickerDialog(analyseFragment.getContext(), mDateSetListener, year_end, month_end, day_end).show();
                break;
        }
    }

    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(year, monthOfYear, dayOfMonth);
            Date day = c.getTime();
            if (analyse_day_case == DIALOG_DAY_START) {
                tv_analyse_start.setText(new SimpleDateFormat("yyyy-MM-dd").format(day));
            } else {
                tv_analyse_end.setText(new SimpleDateFormat("yyyy-MM-dd").format(day));
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D)
            Log.d(TAG, "onActivityResult_requestCode " + requestCode);
        if (D)
            Log.d(TAG, "onActivityResult_resultCode " + resultCode);
        switch (requestCode) {
            // 选择运动员后操作
            case REQUEST_SPORTER_SELECT:
                if (Activity.RESULT_OK == resultCode) {
                    Bundle bundle = data.getExtras();
                    @SuppressWarnings("unchecked")
                    ArrayList<Sporter> vecSelectedSporters = (ArrayList<Sporter>) bundle.get("arraySporter");
                    Log.i(TAG, "vecSelectedSporters:" + vecSelectedSporters);
                    sporterListRes = new ArrayList<Body>();
                    for (int i = 0; i < vecSelectedSporters.size(); i++) {
                        Body b = new Body();
                        Sporter sporter = vecSelectedSporters.get(i);
                        b.setNo(i + 1);
                        b.setCoin(sporter.getSporter_name());
                        b.setSporter(sporter);
                        sporterListRes.add(b);
                        Log.i(TAG, "value_sporter_name:" + sporter.getSporter_name());
                    }
                    // 将数据适配器与Activity进行绑定
                    adapter = new AMDragRateAdapter(this.getActivity(), sporterListRes);
                    selectedSporterList.setAdapter(adapter);
                } else {
                }
                break;
        }
    }

    private class SporterList {
        public String head_no;
        public String head_sporter_no;
        public String head_sporter_name;
        public String value_no;
        public String value_sporter_no;
        public String value_sporter_name;
        public Sporter sporter;
    }

    private class SporterListItem {
        public LinearLayout head;
        // 顺序（标题）
        public TextView head_no;
        // 运动员编号（标题）
        public TextView head_sporter_no;
        // 运动员姓名（标题）
        public TextView head_sporter_name;
        // 顺序
        public TextView value_no;
        // 运动员编号
        public TextView value_sporter_no;
        // 运动员姓名
        public TextView value_sporter_name;
    }

    class ClsSpeed {
        public double min_speed;
        public double max_speed;
    }


    /**
     * 监听器在手机拖动停下的时候触发
     */
    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {// from to 分别表示 被拖动控件原位置 和目标位置
            if (from != to) {
                Body item = (Body) adapter.getItem(from);// 得到listview的适配器
                adapter.remove(from);// 在适配器中”原位置“的数据。
                adapter.insert(item, to);// 在目标位置中插入被拖动的控件。
                // 重新排列顺序
                for (int i = 0; i < adapter.getCount(); i++) {
                    item = (Body) adapter.getItem(i);
                    item.setNo(i + 1);
                }
            }
        }
    };
    /**
     * 删除监听器，点击左边差号就触发。删除item操作
     */
    private RemoveListener onRemove = new RemoveListener() {
        @Override
        public void remove(int which) {
            adapter.remove(which);
        }
    };

    /**
     * 选择运动员之后的处理
     *
     * @param sporterListRes 选择的运动员列表
     */
    public void sporterSelected(List<Body> seletedSporterList) {
        sporterListRes = seletedSporterList;
        // 将数据适配器与Activity进行绑定
        adapter = new AMDragRateAdapter(this.getActivity(), sporterListRes);
        selectedSporterList.setAdapter(adapter);
    }
}
