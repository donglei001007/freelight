package com.ssp365.android.freelight.wifi;

import android.os.Handler;

import com.ssp365.android.freelight.common.CharHelper;
import com.ssp365.android.freelight.common.DebugLog;
import com.ssp365.android.freelight.common.GlobalConstants;
import com.ssp365.android.freelight.model.Parameter;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * ConnectedThreadWriter
 * 发送消息线程类
 *
 * @author donglei 2016/08/21
 * @version 1.0.0.1
 */
public class ConnectedThreadWriter extends Thread {
    // 消息
    private String msg;
    // 输入流
    private InputStream ins;
    // 输出流
    private OutputStream ous;
    // 消息句柄
    private Handler handler;
    // 灯柱编号
    private int lightNO;
    // 客户端通讯线程
    private ConnectedThread connectedThread;

    public ConnectedThreadWriter(Handler mhandler, ConnectedThread connectedThread, int lightNO, InputStream mmInStream, OutputStream mmOutStream, String msg) {
        this.msg = msg;
        this.ins = mmInStream;
        this.ous = mmOutStream;
        this.handler = mhandler;
        this.lightNO = lightNO;
        this.connectedThread = connectedThread;
    }

    /**
     * 线程入口
     */
    public synchronized void run() {
        try {
            // 发送OK和NG消息时，只发送不再接收
            if (msg.substring(2, 4).equals(Parameter.CONNECT_INF_OK) || msg.substring(2, 4).equals(Parameter.CONNECT_INF_NG)) {
                byte[] send = CharHelper.hexStringToBytes(msg);
                ous.write(send);
                ous.flush();

                // 向Activity发送消息
                handler.obtainMessage(GlobalConstants.STATE_WIFI_WRITE, -1, lightNO, msg).sendToTarget();

                DebugLog.debug(null, "给" + lightNO + "号柱发送信息：" + msg);
            } else {
                // 重试信息通信次数
                int reSendCount = 0;
                // 设置为等待客户端回信
                connectedThread.setReFlag(true);
                while (connectedThread.isReFlag() && reSendCount < Parameter.RESEND_MAX_COUNT && connectedThread.isAlive() && connectedThread.isConnected()) {
                    byte[] send = CharHelper.hexStringToBytes(msg);
                    ous.write(send);
                    ous.flush();

                    // 向Activity发送消息
                    handler.obtainMessage(GlobalConstants.STATE_WIFI_WRITE, -1, lightNO, msg).sendToTarget();

                    DebugLog.debug(null, "给" + lightNO + "号柱发送信息：" + msg);

                    // 发送计数器++
                    reSendCount++;

                    // 延迟3秒重新发送
                    Thread.sleep(Parameter.RESEND_WAIT_TIME);
                }
                // 多次发送没有回信，通知通信失败
                if (connectedThread.isReFlag()) {
                    // 设置当前客户端通讯线程为未连接
                    connectedThread.setConnected(false);

                    // 断开当前连接
                    connectedThread.cancel();

                    // 写入客户端连接丢失日志
                    handler.obtainMessage(GlobalConstants.STATE_WIFI_LOST, lightNO, -1).sendToTarget();
                }
            }
        } catch (Exception e) {
            DebugLog.debug(null, "向灯柱[" + lightNO + "]发送消息失败：" + e);
            // 设置当前客户端通讯线程为未连接
            connectedThread.setConnected(false);

            // 断开当前连接
            connectedThread.cancel();

            // 写入客户端连接丢失日志
            handler.obtainMessage(GlobalConstants.STATE_WIFI_LOST, lightNO, -1).sendToTarget();
        }
    }

}
