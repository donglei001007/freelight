package com.ssp365.android.freelight.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ssp365.android.freelight.R;
import com.ssp365.android.freelight.common.SmartSportApplication;
import com.ssp365.android.freelight.db.DBSporterDAO;
import com.ssp365.android.freelight.db.DBTeamDAO;
import com.ssp365.android.freelight.model.Pinyin4jUtil;
import com.ssp365.android.freelight.model.SpinnerData;
import com.ssp365.android.freelight.model.Sporter;
import com.ssp365.android.freelight.model.Team;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SporterActivity extends Activity {

    protected static final String TAG = "SporterActivity";

    // 运动员登陆画面的启动种类：添加
    public static final String SPORTER_TYPE_ADD = "ADD";
    // 运动员登陆画面的启动种类：修改
    public static final String SPORTER_TYPE_UPDATE = "UPDATE";

    protected static final int SPORTER_ADD = 0;
    protected static final int SPORTER_UPDATE = 1;

    // 性别
    public static final String MALE = "0";
    public static final String FEMALE = "1";

    // 生日输入对话框
    private static final int BIRTHDAY_DIALOG = 0;
    // 错误信息提示对话框
    private static final int ERROR_DIALOG = 1;
    private String error_messsage = "";

    public static String[] XING_BIE_KEY = {" ", "0", "1"};
    public static String[] XING_BIE_VALUE = {" ", "男", "女"};
    public String[] team_inf;

    // 生日输入按钮
    private ImageButton bt_sporter_birthday;
    // 确定、返回按钮
    private ImageButton bt_sporter_cancel;
    private TextView bt_sporter_ok;
    // 姓名
    private EditText et_sporter_name;
    // 性别
    // private Spinner sp_xingbie;
    private TextView tw_xingbie_text;
    private ImageButton ib_xingbie_nan;
    private ImageButton ib_xingbie_nv;
    // 年月日文本框
    private TextView tv_sporter_birthday;
    // 所属队
    private Spinner sp_team_no;
    // 身高
    private EditText td_sporter_shengao;
    // 体重
    private EditText td_sporter_tizhong;

    // DB关联
    DBTeamDAO teamDAO = null;
    // DB关联
    DBSporterDAO sporterDAO = null;

    // 年月日
    private int mYear, mMonth, mDay;
    // 状态
    private String start_type = "";
    // 运动员信息
    private Sporter sporter = null;

    public SmartSportApplication mApplication = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sporter_activity);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        start_type = bundle.getString("start_type");
        sporter = (Sporter) bundle.get("sporter");
        if (SPORTER_TYPE_UPDATE.equals(start_type)) {
            Log.i(TAG, "Sporter_name:" + sporter.getSporter_name());
        } else {
            sporter = new Sporter();
        }
        mApplication = (SmartSportApplication) getApplication();
        activityInit();

    }

    private void activityInit() {

        et_sporter_name = (EditText) findViewById(R.id.sporter_name);
        // 性别
        tw_xingbie_text = (TextView) findViewById(R.id.sporter_xingbie_text);
        // 性别：男
        ib_xingbie_nan = (ImageButton) findViewById(R.id.sporter_xinbie_nan);
        ib_xingbie_nan.setOnClickListener(new GenderSelectListener());
        // 性别：女
        ib_xingbie_nv = (ImageButton) findViewById(R.id.sporter_xinbie_nv);
        ib_xingbie_nv.setOnClickListener(new GenderSelectListener());
        // 初始化默认选择为男
        ib_xingbie_nan.setImageResource(R.drawable.register_male_checked);
        ib_xingbie_nv.setImageResource(R.drawable.register_female_normal);
        tw_xingbie_text.setText(MALE);

        tv_sporter_birthday = (TextView) findViewById(R.id.sporter_birthday);
        // 年月日没有初始化时，设定16年前为初始值
        if (mYear == 0 && mMonth == 0 && mDay == 0) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR) - 16;
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        }
        tv_sporter_birthday = (TextView) findViewById(R.id.sporter_birthday);
        tv_sporter_birthday.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(BIRTHDAY_DIALOG);
            }
        });

        sp_team_no = (Spinner) findViewById(R.id.sporter_team);
        List<SpinnerData> list_team = new ArrayList<SpinnerData>();
        teamDAO = new DBTeamDAO(mApplication.getDb());
        sporterDAO = new DBSporterDAO(mApplication.getDb());
        ArrayList<Team> listTeam = teamDAO.find();
        team_inf = new String[listTeam.size() + 1];
        list_team.add(new SpinnerData("", ""));
        team_inf[0] = new String("");
        for (int i = 0; i < listTeam.size(); i++) {
            SpinnerData c = new SpinnerData(String.valueOf(listTeam.get(i).getTeam_no()), listTeam.get(i).getTeam_name());
            list_team.add(c);
            team_inf[i + 1] = String.valueOf(listTeam.get(i).getTeam_no());
        }
        ArrayAdapter<SpinnerData> adapter_team = new ArrayAdapter<SpinnerData>(this, android.R.layout.simple_spinner_item, list_team);
        adapter_team.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_team_no.setAdapter(adapter_team);

        td_sporter_shengao = (EditText) findViewById(R.id.sporter_shengao);
        td_sporter_tizhong = (EditText) findViewById(R.id.sporter_tizhong);

        // 取消按钮
        bt_sporter_cancel = (ImageButton) findViewById(R.id.button_sporter_cancle);
        bt_sporter_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 确定按钮
        bt_sporter_ok = (TextView) findViewById(R.id.button_sporter_ok);
        bt_sporter_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getViewValue();
                Log.i(TAG, "tizhong:" + sporter.getSporter_tizhong());
                error_messsage = checkSporter();
                Log.i(TAG, "tizhong:" + sporter.getSporter_tizhong());
                Log.i(TAG, "error_messsage:" + error_messsage);
                if (error_messsage.length() == 0) {
                    if (start_type.equals(SPORTER_TYPE_ADD)) {
                        sporterDAO.add(sporter);
                        Toast.makeText(SporterActivity.this, "运动员添加成功！", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.putExtra("sporter", sporter);
                        setResult(SPORTER_ADD, intent);
                    } else if (start_type.equals(SPORTER_TYPE_UPDATE)) {
                        Log.i(TAG, "Sporter_no:" + sporter.getSporter_no());
                        Log.i(TAG, "tizhong:" + sporter.getSporter_tizhong());
                        sporterDAO.update(sporter);
                        Toast.makeText(SporterActivity.this, "运动员修改成功！", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.putExtra("sporter", sporter);
                        setResult(SPORTER_UPDATE, intent);
                    }
                    finish();
                } else {
                    new AlertDialog.Builder(SporterActivity.this).setTitle("错误提示").setMessage(error_messsage)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create().show();
                }
            }
        });

        if (SPORTER_TYPE_UPDATE.equals(start_type)) {
            setViewValue();
        }
    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(this);
        switch (id) {
            case BIRTHDAY_DIALOG:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
        }
        return null;
    }

    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            tv_sporter_birthday.setText(new StringBuilder()
                    // Month is 0 based so add 1
                    .append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
        }
    };

    private void getViewValue() {
        sporter.setSporter_name(et_sporter_name.getText().toString());
        sporter.setSporter_name_py(Pinyin4jUtil.getPinYinHeadChar(sporter.getSporter_name()));
        sporter.setSporter_xingbie((String) tw_xingbie_text.getText());
        sporter.setSporter_birthday(tv_sporter_birthday.getText().toString());
        if (((SpinnerData) sp_team_no.getSelectedItem()).getValue().trim().length() == 0) {
            sporter.setSporter_team_no(-1);
        } else {
            sporter.setSporter_team_no(Integer.valueOf(((SpinnerData) sp_team_no.getSelectedItem()).getValue()));
        }
        sporter.setSporter_shengao(td_sporter_shengao.getText().toString());
        sporter.setSporter_tizhong(td_sporter_tizhong.getText().toString());
    }

    private void setViewValue() {
        et_sporter_name.setText(sporter.getSporter_name());
        // sp_xingbie.setSelection(SpinnerData.getSpinnerPosition(sporter.getSporter_xingbie(),XING_BIE_KEY));
        if (MALE.equals(sporter.getSporter_xingbie())) {
            // 男性
            ib_xingbie_nan.setImageResource(R.drawable.register_male_checked);
            ib_xingbie_nv.setImageResource(R.drawable.register_female_normal);
            tw_xingbie_text.setText(MALE);
        } else if (FEMALE.equals(sporter.getSporter_xingbie())) {
            // 女性
            ib_xingbie_nv.setImageResource(R.drawable.register_female_checked);
            ib_xingbie_nan.setImageResource(R.drawable.register_male_normal);
            tw_xingbie_text.setText(FEMALE);
        }
        tv_sporter_birthday.setText(sporter.getSporter_birthday());
        sp_team_no.setSelection(SpinnerData.getSpinnerPosition(String.valueOf(sporter.getSporter_team_no()), team_inf));
        td_sporter_shengao.setText(sporter.getSporter_shengao());
        td_sporter_tizhong.setText(sporter.getSporter_tizhong());
    }

    private String checkSporter() {
        String reString = "";

        if (sporter.getSporter_name() == null || sporter.getSporter_name().trim().length() == 0) {
            reString = "姓名没有输入！";
        }
        if (sporter.getSporter_xingbie() == null || sporter.getSporter_xingbie().trim().length() == 0) {
            if (reString.length() == 0) {
                reString = "性别没有输入！";
            } else {
                reString = reString + "\r\n性别没有输入！";
            }
        }
        if (sporter.getSporter_team_no() == -1) {
            if (reString.length() == 0) {
                reString = "所属队没有输入！";
            } else {
                reString = reString + "\r\n所属队没有输入！";
            }
        }
        return reString;
    }

    /**
     * 性别选择监听类
     */
    private class GenderSelectListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sporter_xinbie_nan:
                    // 男性
                    ((ImageButton) v).setImageResource(R.drawable.register_male_checked);
                    ib_xingbie_nv.setImageResource(R.drawable.register_female_normal);
                    tw_xingbie_text.setText(MALE);
                    break;
                case R.id.sporter_xinbie_nv:
                    // 女性
                    ((ImageButton) v).setImageResource(R.drawable.register_female_checked);
                    ib_xingbie_nan.setImageResource(R.drawable.register_male_normal);
                    tw_xingbie_text.setText(FEMALE);
                    break;
            }
        }
    }

}
