package com.ssp365.android.freelight.activity;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.RemoveListener;
import com.ssp365.android.freelight.R;
import com.ssp365.android.freelight.common.SmartSportApplication;
import com.ssp365.android.freelight.db.DBModelDAO;
import com.ssp365.android.freelight.db.DBSporterDAO;
import com.ssp365.android.freelight.model.Body;
import com.ssp365.android.freelight.model.Model;
import com.ssp365.android.freelight.model.ModelDetail;
import com.ssp365.android.freelight.model.Parameter;
import com.ssp365.android.freelight.model.SpinnerData;
import com.ssp365.android.freelight.model.Sporter;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试Fragment
 *
 * @author 传博科技
 */
public class TestFragment extends Fragment {
    // Debugging
    private static final String TAG = "WeixinFragment";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "蓝牙设备";
    public static final String TOAST = "toast";

    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;

    public BootstrapButton mSendButton;

    // Name of the connected device
    public String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    public StringBuffer mOutStringBuffer;

    SmartSportApplication mApplication = null;
    // 计时模式
    public String do_type = "";

    // DB关联
    DBModelDAO modelDAO = null;
    DBSporterDAO sporterDAO = null;
    // 模式选择
    private Spinner sp_model_no;
    ArrayList<Model> listModel;
    public int selected_model_no;
    // 运动员选择
    private DragSortListView selectedSporterList;
    private ImageButton bt_sporter_select;
    // SporterListAdapter adapter = null;

    View weixinFragment = null;

    private AMDragRateAdapter adapter;
    List<Body> sporterListRes;// listview的数据源

    private ImageView mTextTopAddSporter;

    public TestFragment(SmartSportApplication mApplication, String trainType) {
        this.mApplication = mApplication;
        this.do_type = trainType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (D)
            Log.i(TAG, "+++ ON CREATE START +++");
        super.onCreate(savedInstanceState);

        weixinFragment = inflater.inflate(R.layout.test_activity, container, false);

        activityInit();

        if (D)
            Log.i(TAG, "+++ ON CREATE END +++");

        return weixinFragment;

    }

    private void activityInit() {

        // 模式选择框
        sp_model_no = (Spinner) weixinFragment.findViewById(R.id.mode_sp);
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
        selectedSporterList = (DragSortListView) weixinFragment.findViewById(R.id.sporterList);
        sporterListRes = new ArrayList<Body>();
        adapter = new AMDragRateAdapter(this.getActivity(), sporterListRes);
        selectedSporterList.setAdapter(adapter);
        selectedSporterList.setDropListener(onDrop);
        selectedSporterList.setRemoveListener(onRemove);
        selectedSporterList.setDragEnabled(true); // 设置是否可拖动。

        // 运动员选择按钮的处理
        bt_sporter_select = (ImageButton) weixinFragment.findViewById(R.id.ibt_sporter_select);
        bt_sporter_select.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(weixinFragment.getContext(), SporterListActivity.class);
                intent.putExtra("start_type", SporterListActivity.START_TYPE_GET);
                // 设定选定的运动员
                ArrayList<Sporter> arrayListSporter = new ArrayList<Sporter>();
                for (int i = 0; i < sporterListRes.size(); i++) {
                    arrayListSporter.add((sporterListRes.get(i)).getSporter());
                }
                mApplication.setArraySporter(arrayListSporter);
                startActivityForResult(intent, Parameter.REQUEST_SPORTER_SELECT);
            }
        });

        // 初始化开始按钮
        mSendButton = (BootstrapButton) weixinFragment.findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                boolean error_flag = false;
                String error_msg = "";
                // 取得主界面对象
                MainActivity mainActivity = (MainActivity) getActivity();
                // 设定选定的模式
                selected_model_no = mainActivity.getSelectedModelNo();
                for (int i = 0; i < listModel.size(); i++) {
                    if (listModel.get(i).getModel_no() == selected_model_no) {
                        mApplication.setModel(listModel.get(i));
                        ArrayList<ModelDetail> modelDetails = modelDAO.findDetail(listModel.get(i).getModel_no());
                        mApplication.setModelDetails(modelDetails);

                        // 取得柱子通过顺序（用于只响应当前信号柱信号）
                        int[] pointArray = modelDAO.getPointAccessArray(listModel.get(i).getModel_no(), listModel.get(i).getModel_count());
                        mApplication.setPointArray(pointArray);

                        // 取得各柱子信号序列
                        String[][] pointStateArray = modelDAO.getPointStateArray(listModel.get(i).getModel_no(), listModel.get(i)
                                .getModel_count() + 1, listModel.get(i).getModel_point_count());
                        mApplication.setPointStateArray(pointStateArray);

                        // 取得各柱子信号响应序列
                        String[][] waitStateArray = modelDAO.getWaitStateArray(listModel.get(i).getModel_no(),
                                listModel.get(i).getModel_count() + 1, listModel.get(i).getModel_point_count());
                        mApplication.setWaitStateArray(waitStateArray);

                        for (int m = 0; m < mApplication.getModel().getModel_point_count(); m++) {
                            for (int n = 0; n < pointStateArray[m].length; n++) {
                                Log.i(TAG, m + ":" + mApplication.getPointStateArray()[m][n] + " " + mApplication.getWaitStateArray()[m][n]);
                            }
                        }
                        break;
                    }
                }

                // 设定选定的运动员
                ArrayList<Sporter> arrayListSporter = new ArrayList<Sporter>();
                for (int i = 0; i < sporterListRes.size(); i++) {
                    arrayListSporter.add((sporterListRes.get(i)).getSporter());
                }
                mApplication.setArraySporter(arrayListSporter);

                // 是否选定模式
                if (selected_model_no == -1) {
                    error_flag = true;
                    if (error_msg.length() == 0) {
                        error_msg = error_msg.concat(weixinFragment.getResources().getText(R.string.model_err).toString());
                    } else {
                        error_msg = error_msg.concat("\r\n" + weixinFragment.getResources().getText(R.string.model_err).toString());
                    }
                }
                // 测试模式时，是否选定运动员
                if (Parameter.DO_TYPE_TEST.equals(do_type) && arrayListSporter.size() <= 0) {
                    error_flag = true;
                    if (error_msg.length() == 0) {
                        error_msg = error_msg.concat(weixinFragment.getResources().getText(R.string.sporter_err).toString());
                    } else {
                        error_msg = error_msg.concat("\r\n" + weixinFragment.getResources().getText(R.string.sporter_err).toString());
                    }
                }

                Log.i(TAG, "blueToothDebug:" + Parameter.WIFI_NOT_CONTROL + " apRuningFlag:" + Parameter.apRuningFlag);

                // 自动控制时
                if (!Parameter.WIFI_NOT_CONTROL) {
                    // 并且WIFI未连接，报错
                    if (!Parameter.apRuningFlag) {
                        error_flag = true;
                        if (error_msg.length() == 0) {
                            error_msg = error_msg.concat(weixinFragment.getResources().getText(R.string.bluetoothchat_err_notconnect).toString());
                        } else {
                            error_msg = error_msg.concat("\r\n"
                                    + weixinFragment.getResources().getText(R.string.bluetoothchat_err_notconnect).toString());
                        }
                        // 已经有测试柱连接时，并且模式已经选择，确认该模式测试柱状况是否正常
                    } else if (selected_model_no != -1) {
                        // 模式要求的测试柱是否足够？
                        if (mApplication.getHandler().getWifisetActivity().mWifiService.clientConnectedThreadList.size() < mApplication.getModel()
                                .getModel_point_count()) {
                            error_flag = true;
                            if (error_msg.length() == 0) {
                                error_msg = error_msg.concat(weixinFragment.getResources().getText(R.string.point_count_err1).toString()
                                        + mApplication.getModel().getModel_point_count()
                                        + weixinFragment.getResources().getText(R.string.point_count_err2).toString()
                                        + mApplication.getHandler().getWifisetActivity().mWifiService.clientConnectedThreadList.size()
                                        + weixinFragment.getResources().getText(R.string.point_count_err3).toString());
                            } else {
                                error_msg = error_msg.concat("\r\n" + weixinFragment.getResources().getText(R.string.point_count_err1).toString()
                                        + mApplication.getModel().getModel_point_count()
                                        + weixinFragment.getResources().getText(R.string.point_count_err2).toString()
                                        + mApplication.getHandler().getWifisetActivity().mWifiService.clientConnectedThreadList.size()
                                        + weixinFragment.getResources().getText(R.string.point_count_err3).toString());
                            }
                            // 足够的测试柱中是否有已经中断连接了的？
                        } else {
                            for (int i = 0; i < mApplication.getModel().getModel_point_count(); i++) {
                                if (!mApplication.getHandler().getWifisetActivity().mWifiService.clientConnectedThreadList.get(i).connected) {
                                    error_flag = true;
                                    if (error_msg.length() == 0) {
                                        error_msg = error_msg.concat((i + 1)
                                                + weixinFragment.getResources().getText(R.string.point_connect_err).toString());
                                    } else {
                                        error_msg = error_msg.concat("\r\n" + (i + 1)
                                                + weixinFragment.getResources().getText(R.string.sporter_err).toString());
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }

                // 有设定错误时，提示错误
                if (error_flag) {
                    new AlertDialog.Builder(weixinFragment.getContext()).setTitle("错误提示").setMessage(error_msg)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create().show();
                    // 没有设定错误时，开始测试
                } else {
                    Intent intentStopWatch = new Intent();
                    intentStopWatch.setClass(weixinFragment.getContext(), StopWatchActivity.class);
                    startActivity(intentStopWatch);

                }
            }
        });

        // 运动员选择区域的可视判断
        if (Parameter.DO_TYPE_TRAIN.equals(do_type)) {
            LinearLayout linearLayout = (LinearLayout) weixinFragment.findViewById(R.id.sporter_select);
            linearLayout.setVisibility(View.INVISIBLE);
            // 选择运动员的标题的可视判断
            TextView lb_sporter = (TextView) weixinFragment.findViewById(R.id.lb_sporter);
            TextView lb_shunxu = (TextView) weixinFragment.findViewById(R.id.lb_shunxu);
            lb_sporter.setVisibility(View.INVISIBLE);
            lb_shunxu.setVisibility(View.INVISIBLE);
            mSendButton.setHeight(100);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D)
            Log.d(TAG, "onActivityResult_requestCode " + requestCode);
        if (D)
            Log.d(TAG, "onActivityResult_resultCode " + resultCode);
        switch (requestCode) {
            // 选择运动员后操作
            case Parameter.REQUEST_SPORTER_SELECT:
                if (resultCode == Activity.RESULT_OK) {
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
                    if (sporterListRes.size() > 1) {
                        mSendButton.setText(R.string.loopTrain);
                    } else {
                        mSendButton.setText(R.string.startTrain);
                    }
                } else {
                }
                break;
        }
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
     * @param sporterListRes 选择的运动员列表
     */
    public void sporterSelected(List<Body> seletedSporterList) {
        sporterListRes = seletedSporterList;
        // 将数据适配器与Activity进行绑定
        adapter = new AMDragRateAdapter(this.getActivity(), sporterListRes);
        selectedSporterList.setAdapter(adapter);
        if (sporterListRes.size() > 1) {
            mSendButton.setText(R.string.loopTrain);
        } else {
            mSendButton.setText(R.string.startTrain);
        }
    }

}
