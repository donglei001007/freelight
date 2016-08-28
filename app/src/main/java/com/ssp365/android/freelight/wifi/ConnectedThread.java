package com.ssp365.android.freelight.wifi;

import android.os.Handler;
import android.util.Log;

import com.ssp365.android.freelight.common.CharHelper;
import com.ssp365.android.freelight.common.DebugLog;
import com.ssp365.android.freelight.common.GlobalConstants;
import com.ssp365.android.freelight.model.Parameter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

/**
 * ConnectedThread
 * 客户端连接线程
 *
 * @author donglei 2016/08/21
 * @version 1.0.0.1
 */
public class ConnectedThread extends Thread {
    // Socket
    private final Socket mSocket;
    // handler
    private final Handler mHandler;
    // 连接状态
    public boolean connected = true;
    // 当前发送信号的序号
    public int sendMsgIndex = -1;
    Vector<String> vecMsg = new Vector<>();
    String endStr = "";
    // 灯柱编号
    private int lightNo;
    // 等待回信标志位
    private boolean reFlag = false;
    // 通讯输入流
    private InputStream mmInStream;
    // 通讯输出流
    private OutputStream mmOutStream;
    // 未读取完成的客户端消息
    private String remainderMsg;
    // 重复送信次数
    private int reSendCount = 0;
    // 发送信号序列
    private String[] sendMsg = null;
    // 响应序列
    private String[] waitStatus = null;

    /**
     * 灯柱连接线程
     *
     * @param socket 连接的Socket
     * @param no     灯柱编号
     */
    public ConnectedThread(Socket socket, int no, Handler handler) {
        // Socket
        this.mSocket = socket;
        // 灯柱编号
        this.lightNo = no;
        // 处理句柄
        this.mHandler = handler;

        // Get the BluetoothSocket input and output streams
        try {
            // Socket输入流
            mmInStream = mSocket.getInputStream();
            // Socket输出流
            mmOutStream = mSocket.getOutputStream();
        } catch (IOException e) {
            DebugLog.debug(null, "与客户端建立连接实例时，Socket无法取得输入输出流：" + e);
        }

        // 与灯柱连接成功后给信号柱发送等待中信号
        writeThread(Parameter.CONNECT_INF_WAIT);
    }

    /**
     * 线程入口
     */
    public synchronized void run() {
        DebugLog.debug(null, "与灯柱" + lightNo + "的通讯连接实例开始运行");

        try {
            // 发送灯柱信号
            reFlag = false;
            // 通知灯柱编号
            if (lightNo == 1) {
                writeThread(Parameter.CONNECT_INF_POINT_1);
            } else if (lightNo == 2) {
                writeThread(Parameter.CONNECT_INF_POINT_2);
            } else if (lightNo == 3) {
                writeThread(Parameter.CONNECT_INF_POINT_3);
            } else if (lightNo == 4) {
                writeThread(Parameter.CONNECT_INF_POINT_4);
            }
        } catch (Exception e) {
            DebugLog.debug(null, "连接第[" + lightNo + "]号柱报错。" + e);
        }

        byte[] buffer = new byte[1024];
        int bytes;

        // Keep listening to the InputStream while connected
        while (mSocket != null) {
            try {
                DebugLog.debug(null, "客户端消息读取进程开启。");
                // Read from the InputStream
                if ((bytes = mmInStream.read(buffer)) > 0) {
                    // 读取到客户端发送的相关消息，解析信号
                    byte[] readBuf = new byte[bytes];
                    for (int i = 0; i < bytes; i++) {
                        readBuf[i] = buffer[i];
                    }
                    // 将消息转换为字符串
                    String readMessage = CharHelper.bytesToHexString(readBuf);
                    DebugLog.debug(null, "从第[" + lightNo + "]号柱收到信息：" + readMessage);

                    // 上次残留信息的连接
                    if (remainderMsg != null && !remainderMsg.isEmpty()) {
                        readMessage = remainderMsg.concat(readMessage);
                    }

                    // 本次信息的截取
                    while (!readMessage.isEmpty()) {
                        if (readMessage.length() >= 8) {
                            // 如果接收到的消息超过8（一条指令的长度），则读取msg
                            vecMsg.add(readMessage.substring(0, 8));
                            // 从消息串中剔除读取到的信息
                            readMessage = readMessage.substring(8);
                        } else if (readMessage.length() > 0) {
                            // 暂存不够长度的消息
                            remainderMsg = readMessage;
                            break;
                        } else {
                            break;
                        }
                    }

                    // 逐条处理收到的信息
                    for (int i = 0; i < vecMsg.size(); i++) {
                        // 取得当前这条信息
                        readMessage = vecMsg.get(i);
                        DebugLog.debug(null, "处理从第[" + lightNo + "]号柱收到信息：" + readMessage);
                        // 向Activity发送消息
                        mHandler.obtainMessage(GlobalConstants.STATE_WIFI_READ, -1, lightNo, readMessage).sendToTarget();

                        // 判断收到信息的正确性
                        if (readMessage.startsWith(Parameter.CONNECT_INF_START)                             // 有开始标志头
                                && readMessage.endsWith(Parameter.CONNECT_INF_END)                          // 有结束标志尾
                                && readMessage.length() == 8                                                // 信号长度为8
                                && readMessage.substring(2, 4).equals(readMessage.substring(4, 6))) {       // 传输的信号完全一致(数据校验)
                            if (reFlag && readMessage.substring(2, 4).equals(Parameter.CONNECT_INF_OK)) {
                                // 收到信号柱发送OK信号后，设置标志位，可以继续向信号柱发送信号
                                reFlag = false;
                            } else if (reFlag && readMessage.substring(2, 4).equals(Parameter.CONNECT_INF_NG)) {
                                // 收到信号柱发送NG信号后，不设置标志位，继续发送重复信号
                            } else {
                                // 收到灯柱的通过信号后，处理通过信号
                                mHandler.obtainMessage(GlobalConstants.STATE_WIFI_CLICK, -1, lightNo, readMessage).sendToTarget();
                            }
                        } else {
                            // 信号不符合协议时，发送【通信失败】信号，连接断开
                            writeThread(Parameter.CONNECT_INF_NG);
                            connected = false;
                            // 关闭连接
                            cancel();
                            // 输出日志
                            mHandler.obtainMessage(GlobalConstants.STATE_WIFI_LOST, lightNo, -1).sendToTarget();
                        }
                    }
                    //初始化信息
                    vecMsg = new Vector<>();
                } else if ((bytes = mmInStream.read(buffer)) == -1) {
                    connected = false;
                    // 关闭连接
                    cancel();
                    // 输出日志
                    mHandler.obtainMessage(GlobalConstants.STATE_WIFI_LOST, lightNo, -1).sendToTarget();
                    return;
                }
            } catch (Exception e) {
                Log.e("freelight", "客户端连接报错." + e);
                DebugLog.debug(null, "客户端连接报错" + e);
                connected = false;
                cancel();
                mHandler.obtainMessage(GlobalConstants.STATE_WIFI_LOST, lightNo, -1).sendToTarget();
                return;
            }
        }
    }

    /**
     * 取得发送信号序列
     */
    public String[] getSendMsg() {
        return sendMsg;
    }

    /**
     * 设定发送信号序列
     */
    public void setSendMsg(String[] sendMsg) {
        this.sendMsg = sendMsg;
    }

    /**
     * 取得指定发送信号序列
     */
    public String getOneSendMsg() {
        return sendMsg[sendMsgIndex];
    }

    /**
     * 取得响应序列
     */
    public String[] getWaitStatus() {
        return waitStatus;
    }

    /**
     * 设定响应序列
     */
    public void setWaitStatus(String[] waitStatus) {
        this.waitStatus = waitStatus;
    }

    /**
     * 取得指定响应序列
     */
    public String getOneWaitStatus() {
        return waitStatus[sendMsgIndex];
    }


    /**
     * 关闭连接
     */
    public void cancel() {
        try {
            DebugLog.debug(null, "第[" + lightNo + "]号灯柱的客户端关闭开始。");
            if (mmInStream != null) {
                mmInStream.close();
            }
            if (mmOutStream != null) {
                mmOutStream.close();
            }
            if (mSocket != null) {
                mSocket.close();
            }

            // 终端线程
            this.interrupt();
        } catch (IOException e) {
            DebugLog.debug(null, "第[" + lightNo + "]号灯柱的客户端关闭过程中发生问题。" + e);
        }
    }

    /**
     * 发送指定信号
     */
    public void writeThread(String msg) {
        // 不用等待时才发送新的信号，发送反馈信号时不用等
        if (!reFlag || msg.equals(Parameter.CONNECT_INF_OK) || msg.equals(Parameter.CONNECT_INF_NG)) {
            // 拼装向客户端发送的消息
            msg = Parameter.CONNECT_INF_START + msg + msg + Parameter.CONNECT_INF_END;
            ConnectedThreadWriter connectedThreadWriter = new ConnectedThreadWriter(mHandler, this, lightNo, mmInStream, mmOutStream, msg);
            connectedThreadWriter.start();
        }
    }

    /*
     * 按照信号序列发送信号
     * true：重复发送信号序列（不发送最后一个通过后信号，直接跳到预备信号）
     * false:不重复发送信号序列（发送最后一个通过后信号）
     */
    public void arrayWriteThread(boolean flag) {
        //不用等待时才发送新的信号
        if (!reFlag) {
            //信号序列跳转
            sendMsgIndex = sendMsgIndex + 1;
            //发送信号
            String msg = Parameter.CONNECT_INF_START + sendMsg[sendMsgIndex] + sendMsg[sendMsgIndex] + Parameter.CONNECT_INF_END;

            ConnectedThreadWriter connectedThreadWriter = new ConnectedThreadWriter(mHandler, this, lightNo, mmInStream, mmOutStream, msg);
            connectedThreadWriter.start();
        }
    }

    /**
     * 客户端是否回复消息
     *
     * @return true：需要，客户端没有返回消息；false：不需要，客户端已经返回消息
     */
    public boolean isReFlag() {
        return reFlag;
    }

    /**
     * 设定是否需要向客户端重发消息
     *
     * @param reFlag
     */
    public void setReFlag(boolean reFlag) {
        this.reFlag = reFlag;
    }

    public int getLightNo() {
        return lightNo;
    }

    public void setLightNo(int lightNo) {
        this.lightNo = lightNo;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
