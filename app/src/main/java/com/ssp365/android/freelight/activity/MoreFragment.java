package com.ssp365.android.freelight.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.ssp365.android.freelight.R;
import com.ssp365.android.freelight.common.SmartSportApplication;
import com.ssp365.android.freelight.db.DBChenjiDAO;
import com.ssp365.android.freelight.db.DBModelDAO;
import com.ssp365.android.freelight.db.DBSporterDAO;
import com.ssp365.android.freelight.model.CsvData;
import com.ssp365.android.freelight.model.Parameter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MoreFragment extends Fragment {

    // Debugging
    private static final String TAG = "SettingFragment";
    private static final boolean D = true;


    View moreFragment = null;

    RelativeLayout rlSetTeam = null;
    RelativeLayout rlSetSporter = null;
    RelativeLayout rlSetTrainModel = null;
    RelativeLayout rlSetControlModel = null;
    RelativeLayout rlSetWifi = null;
    RelativeLayout rlDataOutput = null;
    RelativeLayout rlAbout = null;

    //DB关联
    DBModelDAO modelDAO = null;
    DBSporterDAO sporterDAO = null;
    DBChenjiDAO chenjiDAO = null;

    public SmartSportApplication mApplication = null;

    public MoreFragment(SmartSportApplication mApplication) {
        this.mApplication = mApplication;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moreFragment = inflater.inflate(R.layout.more_fragment, container, false);

        activityInit();

        if (D) Log.i(TAG, "+++ ON CREATE END +++");
        return moreFragment;
    }

    private void activityInit() {
        rlSetTeam = (RelativeLayout) moreFragment.findViewById(R.id.setTeam);
        rlSetSporter = (RelativeLayout) moreFragment.findViewById(R.id.setSporter);
        rlSetTrainModel = (RelativeLayout) moreFragment.findViewById(R.id.setTrainModel);
        rlSetControlModel = (RelativeLayout) moreFragment.findViewById(R.id.setControlModel);
        rlSetWifi = (RelativeLayout) moreFragment.findViewById(R.id.setWifi);
        rlDataOutput = (RelativeLayout) moreFragment.findViewById(R.id.dataOutput);
        rlAbout = (RelativeLayout) moreFragment.findViewById(R.id.about);

        rlSetTeam.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(moreFragment.getContext(), TeamActivity.class);
                startActivity(intent);
            }
        });

        rlSetSporter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(moreFragment.getContext(), SporterListActivity.class);
                intent.putExtra("start_type", SporterListActivity.START_TYPE_SET);
                startActivity(intent);
            }
        });

        rlSetTrainModel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(moreFragment.getContext(), ModelActivity.class);
                intent.putExtra("start_type", ModelActivity.START_TYPE_SET);
                startActivity(intent);
            }
        });

        //WIFI自动计时
        ToggleButton mTogBtn = (ToggleButton) moreFragment.findViewById(R.id.mTogBtn);
        mTogBtn.setChecked(!Parameter.WIFI_NOT_CONTROL);
        mTogBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                Parameter.WIFI_NOT_CONTROL = !isChecked;
            }
        });

        rlSetWifi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(moreFragment.getContext(), WifisetActivity.class);
                startActivity(intent);
            }
        });
        rlDataOutput.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent intent = new Intent(moreFragment.getContext(), CSVActivity.class);
				startActivity(intent);
				*/
                getCSVData();
            }
        });
        // 关于
        rlAbout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(moreFragment.getContext(), AboutActivity.class);
                startActivity(intent);
            }
        });

    }


    //CSV文件输出
    private void getCSVData() {

        sporterDAO = new DBSporterDAO(mApplication.getDb());
        modelDAO = new DBModelDAO(mApplication.getDb());
        chenjiDAO = new DBChenjiDAO(mApplication.getDb());
        //取得全体运动员
        int[] sporter_nos = sporterDAO.findSporterNo();
        //取得全部项目
        int[] model_nos = modelDAO.findModelNo();
        //开始时间
        String str_analyse_start = "1900-01-01";
        //结束时间
        String str_analyse_end = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        //当有运动员信息时
        if (sporter_nos.length > 0) {

            ArrayList<CsvData> list_csvData = chenjiDAO.find(sporter_nos, model_nos, str_analyse_start, str_analyse_end);

            //有关联数据时，数据输出
            if (list_csvData != null && list_csvData.size() > 0) {
                String file_path = outputCSVData(list_csvData);
                if (file_path == null || file_path.length() == 0) {
                    new AlertDialog.Builder(moreFragment.getContext())
                            .setTitle("错误提示")
                            .setMessage(R.string.msg_csv_out_ng)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create().show();
                } else {
                    String msg = moreFragment.getContext().getResources().getText(R.string.msg_csv_out_ok).toString() + "\r\n保存路径：" + file_path;
                    new AlertDialog.Builder(moreFragment.getContext())
                            .setTitle("提示")
                            .setMessage(msg)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create().show();
                }
                //没有关联数据时，提示信息
            } else if (list_csvData == null || list_csvData.size() == 0) {
                new AlertDialog.Builder(moreFragment.getContext())
                        .setTitle("提示")
                        .setMessage(R.string.msg_csv_out_inf)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create().show();
            }
        } else {
            new AlertDialog.Builder(moreFragment.getContext())
                    .setTitle("提示")
                    .setMessage(R.string.msg_csv_out_inf)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create().show();
        }
    }

    /*
     * CSV文件输出
     * @par	输出信息
     * @ret 保存路径
     */
    private String outputCSVData(ArrayList<CsvData> list_csvData) {
        String re_path = "";
        try {
            String file_path_str = null;
            File file_path = null;
            // 优先保存到SD卡中
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                file_path_str = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "AssistantCoach";
                // 如果SD卡不存在，就保存到本应用的目录下
            } else {
                file_path_str = (moreFragment.getContext()).getFilesDir().getAbsolutePath();
            }
            Log.i(TAG, "file_path:" + file_path_str);
            file_path = new File(file_path_str);
            if (!file_path.exists()) {
                file_path.mkdirs();
            }

            final Calendar c = Calendar.getInstance();
            Date day = c.getTime();
            String time_str = new SimpleDateFormat("yyyyMMddhhmmss").format(day);
            File csv_file = new File(file_path_str + File.separator + "csvData" + time_str + ".csv");
            re_path = csv_file.getAbsolutePath();
            BufferedWriter csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(csv_file), "GB2312"), 1024);

            csvFileOutputStream.write("\"测试项名称\",\"队名\",\"运动员姓名\",\"测试时间\",\"平均成绩(秒)\",\"详细成绩(秒)\",\"成绩编号\",\"测试项编号\",\"测试子项编号\",\"队伍编号\",\"运动员编号\"");
            csvFileOutputStream.newLine();
            for (int i = 0; i < list_csvData.size(); i++) {
                CsvData csvData = list_csvData.get(i);
                csvFileOutputStream.write(csvData.getModel_name() + "," + csvData.getTeam_name() + "," + csvData.getSporter_name() + "," + csvData.getChenji_day()
                        + "," + csvData.getModel_total_speed() + "," + csvData.getModel_sub_speed() + "," + csvData.getChenji_no() + "," + csvData.getModel_no()
                        + "," + csvData.getModel_sub_no() + "," + csvData.getSporter_team_no() + "," + csvData.getSporter_no());
                csvFileOutputStream.newLine();

            }
            csvFileOutputStream.flush();
            csvFileOutputStream.close();
        } catch (Exception e) {
            re_path = "";
        }
        return re_path;
    }

}
