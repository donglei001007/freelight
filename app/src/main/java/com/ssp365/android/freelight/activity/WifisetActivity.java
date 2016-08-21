
package com.ssp365.android.freelight.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.ssp365.android.freelight.R;
import com.ssp365.android.freelight.common.GlobalConstants;
import com.ssp365.android.freelight.common.MessageHelper;
import com.ssp365.android.freelight.common.SmartSportApplication;
import com.ssp365.android.freelight.common.SmartSportHandler;
import com.ssp365.android.freelight.model.Parameter;
import com.ssp365.android.freelight.wifi.WifiServer;
import com.ssp365.android.freelight.wifi.WifiServer.ConnectedThread;

import java.net.Socket;
import java.util.ArrayList;

/**
 * Wifi热点控制、及信号柱识别控制模块
 * <p>
 *  @verison 1.0 2013/05/14
 *  @author chenxy
 */
public class WifisetActivity extends Activity implements OnClickListener {


    // TAG
    private static final String TAG = "WifisetActivity";

    //全局变量
    public static SmartSportApplication mApplication = null;
    //控制句柄
    private SmartSportHandler mHandler = null;
    // Member object for the chat services
    public WifiServer mWifiService = null;

    private ImageButton bt_wifi_set_cancel;

    //热点控制按钮
    public BootstrapButton button_ap_control;
    //扫描信号柱按钮
    public BootstrapButton button_read_client;

    //运动员选择
    public ListView listview_client_List;
    private ArrayList<ClientList> clientListLists = null;
    ClientListAdapter adapter = null;

    //查选通信信息窗口
    private TextView testText;
    //private ScrollView scrollView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON CREATE START +++");
        super.onCreate(savedInstanceState);
        // android-bootstrap
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.wifiset_activity);

        // 全局变量
        mApplication = (SmartSportApplication) getApplication();
        mHandler = mApplication.getHandler();
        mWifiService = mApplication.getmWifiService();
        if (mHandler == null) {
            mHandler = new SmartSportHandler();
            // 获得该共享变量实例
            mApplication.setHandler(mHandler);
            mHandler.setApplication(mApplication);
        }
        activityInit();
        mHandler.setWifisetActivity(this);


        Log.i(TAG, "+++ ON CREATE END +++");
    }


    private void activityInit() {

        //运动员ListView
        listview_client_List = (ListView) this.findViewById(R.id.listview_client_List);
        adapter = new ClientListAdapter(this);

        //测试用显示屏
        testText = (TextView) findViewById(R.id.text_temp_test);
        testText.setMovementMethod(ScrollingMovementMethod.getInstance());
        //scrollView = (ScrollView)findViewById(R.id.sv_show);

        //添加运动员取消的处理
        bt_wifi_set_cancel = (ImageButton) findViewById(R.id.button_wifi_set_cancle);
        bt_wifi_set_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //热点控制按钮
        button_ap_control = (BootstrapButton) findViewById(R.id.button_ap_control);
        button_ap_control.setOnClickListener(this);

        //客户端识别按钮
        button_read_client = (BootstrapButton) findViewById(R.id.button_read_client);
        button_read_client.setOnClickListener(this);

    }

    public void sendMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        return;
    }

    public void flushList() {
        try{
            if (mWifiService != null) {
                //clientListLists = mWifiService.clientSocketList;
                clientListLists = new ArrayList<ClientList>();
                for (int i = 0; i < mWifiService.clientConnectedThreadList.size(); i++) {
                    ClientList socketClient = new ClientList();
                    socketClient.value_no = i + 1 + "";
                    mWifiService.clientConnectedThreadList.get(i).no = i + 1;
                    if (mWifiService.clientConnectedThreadList.get(i).connected) {
                        socketClient.value_client_state = Parameter.CONNECT_STATE_CONNECTED;
                    } else {
                        socketClient.value_client_state = Parameter.CONNECT_STATE_LOST;
                    }
                    clientListLists.add(socketClient);
                }
                //将数据适配器与Activity进行绑定
                listview_client_List.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "flushList:", e);
        }
    }

    public void flushButton(String vaule) {
        button_read_client.setText(vaule);
        button_read_client.invalidate();
    }

    public void flushText(String text) {
        testText.setText(testText.getText() + "\r\n" + text);
        testText.setScrollY(testText.getLineCount() * testText.getLineHeight() - testText.getHeight());
    }

    @Override
    public void onStart() {
        Log.i(TAG, "++ ON START START ++");
        super.onStart();
        Log.i(TAG, "++ ON START END ++");
    }

    @Override
    public synchronized void onResume() {
        Log.i(TAG, "+ ON RESUME START+");
        super.onResume();
        //按钮显示内容的设定
        if (Parameter.apRuningFlag == false) {
            button_ap_control.setText(R.string.button_ap_open);
        } else {
            button_ap_control.setText(R.string.button_ap_close);
        }
        //信号柱列表刷新
        flushList();
        Log.i(TAG, "+ ON RESUME END+");
    }

    @Override
    public synchronized void onPause() {
        Log.i(TAG, "- ON PAUSE START-");
        super.onPause();
        Log.i(TAG, "- ON PAUSE END-");
    }

    @Override
    public void onStop() {
        Log.i(TAG, "-- ON STOP START--");
        super.onStop();
        Log.i(TAG, "-- ON STOP END--");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "--- ON DESTROY ---");
        super.onDestroy();
    }

    /**
     * 客户端List
     */
    public static class ClientList {
        // 灯柱编号
        public String value_no;
        // 连接状态
        public String value_client_state;
        // Socket
        public Socket socket;
    }

    private class ClientListItem {
        public LinearLayout head;
        //顺序（标题）
        public TextView head_no;
        //信号柱状态（标题）
        public TextView head_client_state;
        //顺序
        public TextView value_no;
        //信号柱状态
        public TextView value_client_state;
    }

    private class ClientListAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public ClientListAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return clientListLists.size();
        }

        public Object getItem(int position) {
            return clientListLists.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ClientListItem clientListItem = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.wifiset_activity_item, null);
                clientListItem = new ClientListItem();
                clientListItem.head = (LinearLayout) convertView.findViewById(R.id.clientlist_head);
                clientListItem.head_no = (TextView) convertView.findViewById(R.id.head_no);
                clientListItem.head_client_state = (TextView) convertView.findViewById(R.id.head_client_state);
                clientListItem.value_no = (TextView) convertView.findViewById(R.id.value_no);
                clientListItem.value_client_state = (TextView) convertView.findViewById(R.id.value_client_state);
                convertView.setTag(convertView);
            } else {
                clientListItem = (ClientListItem) convertView.getTag();
            }

            clientListItem.head_no.setText("编号");
            clientListItem.head_client_state.setText("状态");
            clientListItem.value_no.setText(clientListLists.get(position).value_no);
            clientListItem.value_client_state.setText(clientListLists.get(position).value_client_state);
            if (clientListLists.get(position).value_client_state.equals(Parameter.CONNECT_STATE_CONNECTED)) {
                clientListItem.value_client_state.setTextColor(Color.BLACK);
            } else {
                clientListItem.value_client_state.setTextColor(Color.BLUE);
            }

            //列头部只显示一次
            if (position > 0) {
                clientListItem.head.setVisibility(View.GONE);
            } else {
                clientListItem.head.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }


    //发送识别信号
    public class ConnectedThreadCheckWriter extends Thread {
        WifiServer mWifiService = null;

        public ConnectedThreadCheckWriter(WifiServer mWifiService) {
            this.mWifiService = mWifiService;
        }

        public synchronized void run() {
            try {
                //2016/03/27 chenxy upd start
                //MessageHelper.sendMsg(mApplication, Parameter.CONNECT_INF_CHECK, true);
                for(int i=0;i<mWifiService.clientConnectedThreadList.size();i++){
                    int ponintNo = ((ConnectedThread)mWifiService.clientConnectedThreadList.get(i)).no;
                    if(ponintNo==1){
                        MessageHelper.sendMsg(mApplication, ponintNo, Parameter.CONNECT_INF_POINT_1);
                    }else if(ponintNo==2){
                        MessageHelper.sendMsg(mApplication, ponintNo, Parameter.CONNECT_INF_POINT_2);
                    }else if(ponintNo==3){
                        MessageHelper.sendMsg(mApplication, ponintNo, Parameter.CONNECT_INF_POINT_3);
                    }else if(ponintNo==4){
                        MessageHelper.sendMsg(mApplication, ponintNo, Parameter.CONNECT_INF_POINT_4);
                    }
                }
                //2016/03/27 chenxy upd end
                this.interrupt();
            } catch (Exception e) {
                Log.e(TAG, "Exception during write", e);
            }
        }
    }

    //识别按钮倒计时
    public class ButtonReadTime extends Thread {
        WifisetActivity mWifisetActivity = null;

        public ButtonReadTime(WifisetActivity mWifisetActivity) {
            this.mWifisetActivity = mWifisetActivity;
        }

        public synchronized void run() {
            try {
                //倒计时次数
                for (int i = 0; i < Parameter.CHECK_TIMES; i++) {
                    try {
                        mHandler.obtainMessage(GlobalConstants.STATE_BUTTON_CHANGER, i, -1, "扫描信号柱(" + (Parameter.CHECK_TIMES - i) + ")").sendToTarget();
                        Thread.sleep(Parameter.CHECK_WAIT_TIME);
                    } catch (Exception e) {
                        Log.e(TAG, "ButtonReadTime error!", e);
                    }
                }
                mHandler.obtainMessage(GlobalConstants.STATE_BUTTON_CHANGER, Parameter.CHECK_TIMES, -1, "扫描信号柱").sendToTarget();
                this.interrupt();
            } catch (Exception e) {
                Log.e(TAG, "Exception during write", e);
            }
        }
    }

    /**
     * 单击处理
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_ap_control:
                // 如果热点已经开启，则为关闭热点
                if (Parameter.apRuningFlag && mWifiService != null) {
                    new AlertDialog.Builder(this)
                            .setTitle("热点关闭确认")
                            .setMessage("是否要关闭开启的热点？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //关闭WIFI服务器
                                    mWifiService.stop();
                                    Parameter.apRuningFlag = false;
                                    //修改按钮为：打开热点
                                    button_ap_control.setText(R.string.button_ap_open);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create().show();
                } else {
                    // 如果还没有打开热点，则打开WIFI服务器
                    if (mWifiService == null) {
                        mWifiService = new WifiServer(WifisetActivity.this, mApplication);
                    }
                    mWifiService.start();
                    mApplication.setmWifiService(mWifiService);
                    Parameter.apRuningFlag = true;
                    //打开WIFI后自动设为自动控制
                    Parameter.WIFI_NOT_CONTROL = false;
                    //修改按钮为：关闭热点
                    button_ap_control.setText(R.string.button_ap_close);
                }
                break;
            case R.id.button_read_client:
                if (mWifiService != null) {
                    button_read_client.setEnabled(false);
                    //删除断开了的连接
                    for (int i = mWifiService.clientConnectedThreadList.size() - 1; i >= 0; i--) {
                        if (!mWifiService.clientConnectedThreadList.get(i).connected) {
                            mWifiService.clientConnectedThreadList.get(i).cancel();
                            mWifiService.clientConnectedThreadList.remove(i);
                        }
                    }
                    flushList();
                    try {
                        (new ConnectedThreadCheckWriter(mWifiService)).start();
                        (new ButtonReadTime(WifisetActivity.this)).start();
                    } catch (Exception e) {
                        Log.e(TAG, "Exception during write", e);
                    }
                } else {
                    Toast.makeText(WifisetActivity.this, "请先打开热点", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}