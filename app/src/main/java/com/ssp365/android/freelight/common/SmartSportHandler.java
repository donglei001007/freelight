package com.ssp365.android.freelight.common;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ssp365.android.freelight.activity.StopWatchActivity;
import com.ssp365.android.freelight.activity.WifisetActivity;
import com.ssp365.android.freelight.model.Parameter;
import com.ssp365.android.freelight.wifi.WifiServer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SmartSportHandler extends Handler {
    //
    public SmartSportApplication application = null;
    //
    public WifisetActivity wifisetActivity = null;
    //
    public StopWatchActivity stopWatch = null;

    /**
     * @return the mApplication
     */
    public SmartSportApplication getApplication() {
        return application;
    }

    /**
     * @param application
     */
    public void setApplication(SmartSportApplication application) {
        this.application = application;
    }

    /**
     * @return the wifisetActivity
     */
    public WifisetActivity getWifisetActivity() {
        return wifisetActivity;
    }

    /**
     * @param wifisetActivity the wifisetActivity to set
     */
    public void setWifisetActivity(WifisetActivity wifisetActivity) {
        this.wifisetActivity = wifisetActivity;
    }

    public void setStopWatch(StopWatchActivity stopWatch) {
        this.stopWatch = stopWatch;
    }

    public StopWatchActivity getStopWatch() {
        return stopWatch;
    }

    public void handleMessage(Message msg) {

        //初期化未完成时，不做处理
        if (wifisetActivity == null) {
            return;
        }

        switch (msg.what) {
            case GlobalConstants.STATE_WIFI_START:
                Toast.makeText(wifisetActivity.getApplicationContext(), "热点启动", Toast.LENGTH_SHORT).show();
                break;
            case GlobalConstants.STATE_WIFI_ACCEPT:
                wifisetActivity.flushList();
                break;
            case GlobalConstants.STATE_WIFI_ACCEPT_FAILE:
                Toast.makeText(wifisetActivity.getApplicationContext(), "信号柱等候进程失败:" + msg.obj, Toast.LENGTH_SHORT).show();
                break;
            case GlobalConstants.STATE_WIFI_LOST:
                //测试中的有效测试柱断开，其它场合有信号柱断开时提示
                if (!Parameter.WIFI_NOT_CONTROL) {
                    if (stopWatch != null
                            && !stopWatch.stopWatchFlag
                            && msg.arg1 <= application.getModel().getModel_point_count()) {
                        Toast.makeText(wifisetActivity.getApplicationContext(), msg.arg1 + "号信号柱连接断开，计时自动停止！", Toast.LENGTH_SHORT).show();
                        stopWatch.stopStopWatch(stopWatch);
                        //2016/03/27 chenxy add start
                        int clientNo = (Integer)msg.arg1;
                        Log.i("STATE_WIFI_LOST", "WifiServer.STATE_WIFI_LOST:"+clientNo);
                        application.getmWifiService().clientSocketList.remove(clientNo - 1);
                        application.getmWifiService().clientConnectedThreadList.remove(clientNo - 1);
                        //2016/03/27 chenxy add end
                    } else {
                        Toast.makeText(wifisetActivity.getApplicationContext(), msg.arg1 + "号信号柱连接断开！", Toast.LENGTH_SHORT).show();
                        //2016/03/27 chenxy add start
                        int clientNo = (Integer)msg.arg1;
                        Log.i("STATE_WIFI_LOST", "WifiServer.STATE_WIFI_LOST:"+clientNo);
                        application.getmWifiService().clientSocketList.remove(clientNo-1);
                        application.getmWifiService().clientConnectedThreadList.remove(clientNo - 1);
                        //2016/03/27 chenxy add end
                    }
                }
                wifisetActivity.flushList();
                break;
            //连接失败时的处理
            case GlobalConstants.STATE_WIFI_CONNECT_FAILE:
                wifisetActivity.flushList();
                //测试中的有效测试柱断开，其它场合有信号柱断开时提示
                if (!Parameter.WIFI_NOT_CONTROL) {
                    if (stopWatch != null
                            && !stopWatch.stopWatchFlag
                            && msg.arg1 <= application.getModel().getModel_point_count()) {
                        Toast.makeText(wifisetActivity.getApplicationContext(), msg.arg1 + "号信号柱通信失败，计时自动停止！", Toast.LENGTH_SHORT).show();
                        stopWatch.stopStopWatch(stopWatch);
                    } else {
                        Toast.makeText(wifisetActivity.getApplicationContext(), msg.arg1 + "号信号柱通信失败！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            //收到信号的处理
            case GlobalConstants.STATE_WIFI_READ:
                String readMessage = (String) msg.obj;
                if (wifisetActivity != null) {
                    wifisetActivity.flushText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " F:" + msg.arg2 + ":" + readMessage);
                }
                if (stopWatch != null) {
                    stopWatch.flushText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " F:" + msg.arg2 + ":" + readMessage);
                }
                break;
            //发出信号的处理
            case GlobalConstants.STATE_WIFI_WRITE:
                if (wifisetActivity != null) {
                    wifisetActivity.flushText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " T:" + msg.arg2 + ":" + msg.obj);
                }
                if (stopWatch != null) {
                    stopWatch.flushText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " T:" + msg.arg2 + ":" + msg.obj);
                }
                break;
            //关闭热点的处理
            case GlobalConstants.STATE_WIFI_NONE:
                Toast.makeText(wifisetActivity.getApplicationContext(), "热点关闭", Toast.LENGTH_SHORT).show();
                wifisetActivity.flushList();
                break;
            //测试用处理
            case GlobalConstants.STATE_WIFI_TEST:
                Toast.makeText(wifisetActivity.getApplicationContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
                break;
            //扫描按钮更新
            case GlobalConstants.STATE_BUTTON_CHANGER:
                wifisetActivity.button_read_client.setText((String) msg.obj);
                if (msg.arg1 == Parameter.CHECK_TIMES) {
                    //识别按钮可以开始使用
                    wifisetActivity.button_read_client.setEnabled(true);
                } else {
                    //识别按钮可以开始使用
                    wifisetActivity.button_read_client.setEnabled(false);
                }
                wifisetActivity.button_read_client.invalidate();
                break;
            //计时器计时列表刷新
            case GlobalConstants.STATE_STOPWATCH_LIST_CHANGER:
                Toast.makeText(stopWatch.getApplicationContext(), "刷新成绩列表", Toast.LENGTH_SHORT).show();
                stopWatch.listView.invalidate();
                break;
            //处理wifi的通过信号
            case GlobalConstants.STATE_WIFI_CLICK:
                String clickMessage = (String) msg.obj;
                //接收到WIFI通过信号，并且是触发信号柱时，发出计时信号，并发出下一组信号
                if (stopWatch != null
                        && clickMessage.substring(2, 4).equals(Parameter.CONNECT_INF_POINT_PASS)) {
                    //本次测试用柱发送信息才会被处理
                    if (msg.arg2 <= application.getModel().getModel_point_count()
                            && wifisetActivity.mWifiService.clientConnectedThreadList.get(msg.arg2 - 1).
                            getOneWaitStatus().equals(Parameter.WAIT_STATUS_DO)) {
                        stopWatch.click();
                    }
                }
                break;
        }
    }
}
