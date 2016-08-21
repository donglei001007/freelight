package com.ssp365.android.freelight.wifi;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

import com.ssp365.android.freelight.activity.WifisetActivity.ClientList;
import com.ssp365.android.freelight.common.CharHelper;
import com.ssp365.android.freelight.common.DebugLog;
import com.ssp365.android.freelight.common.GlobalConstants;
import com.ssp365.android.freelight.common.SmartSportApplication;
import com.ssp365.android.freelight.common.SmartSportHandler;
import com.ssp365.android.freelight.model.Parameter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class WifiServer {

    private static final String TAG = "WifiServer";
    public final SmartSportHandler mHandler;
    //全局变量
    public SmartSportApplication mApplication = null;
    // 客户端socket列表
    public ArrayList<ClientList> clientSocketList = new ArrayList<ClientList>();
    public ArrayList<ConnectedThread> clientConnectedThreadList = new ArrayList<>();
    //定义一个WifiLock
    WifiLock mWifiLock;
    //定义WifiManager对象
    private WifiManager mWifiManager;
    //定义WifiInfo对象
    private WifiInfo mWifiInfo;
    //网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    //监听客户端用socket
    private ServerSocket serverSocket = null;
    private AcceptThread mAcceptThread = null;
    private Context context = null;
    // wifiserver状态
    private int mState;

    public WifiServer(Context context, SmartSportApplication application) {
        // 获得程序参数
        this.mApplication = application;
        // 获得上下文
        this.context = context;
        // 获得事件处理器
        mHandler = mApplication.getHandler();
        // 设定当前状态为什么也没做
        mState = GlobalConstants.STATE_WIFI_NONE;
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();

        // WIFI信息打印
        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
        StringBuilder sb = new StringBuilder();
        sb.append("网络信息：");
        sb.append("\nipAddress：" + CharHelper.intToIp(dhcpInfo.ipAddress));
        sb.append("\nnetmask：" + CharHelper.intToIp(dhcpInfo.netmask));
        sb.append("\ngateway：" + CharHelper.intToIp(dhcpInfo.gateway));
        sb.append("\nserverAddress：" + CharHelper.intToIp(dhcpInfo.serverAddress));
        sb.append("\ndns1：" + CharHelper.intToIp(dhcpInfo.dns1));
        sb.append("\ndns2：" + CharHelper.intToIp(dhcpInfo.dns2));
        sb.append("\n");
        sb.append("Wifi信息：");
        sb.append("\nIpAddress：" + CharHelper.intToIp(mWifiInfo.getIpAddress()));
        sb.append("\nMacAddress：" + mWifiInfo.getMacAddress());
        DebugLog.debug(context, sb.toString());

    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        // 如果AP已经开启，先关闭热点
        if (isApEnabled()) {
            // 关闭热点
            setWifiApEnabled(false);
        }
        // 打开热点
        setWifiApEnabled(true);

        // 重新初始化所有连接的客户端
        for (int i = 0; i < clientConnectedThreadList.size(); i++) {
            if (clientConnectedThreadList.get(i) != null) {
                clientConnectedThreadList.get(i).cancel();
            }
            clientConnectedThreadList.remove(i);
        }
        clientConnectedThreadList = new ArrayList<>();

        // 启动监听进程
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(GlobalConstants.STATE_WIFI_START);
        // 发送热点启动信息
        mHandler.obtainMessage(GlobalConstants.STATE_WIFI_START, -1, -1).sendToTarget();
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {

        //关闭监听进程
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        //关闭所有客户端
        for (int i = clientConnectedThreadList.size() - 1; i >= 0; i--) {
            if (clientConnectedThreadList.get(i) != null) {
                // 如果线程已经结束，释放线程资源
                clientConnectedThreadList.get(i).cancel();
            }
            clientConnectedThreadList.remove(i);
        }

        //关闭热点
        setWifiApEnabled(false);

        setState(GlobalConstants.STATE_WIFI_NONE);
        //发送热点关闭信息
        mHandler.obtainMessage(GlobalConstants.STATE_WIFI_NONE, -1, -1).sendToTarget();
    }

    /*
     * 设置WIFI状态
     */
    private synchronized void setState(int state) {
        mState = state;
    }

    // 发送连接断开信号
    private void connectionLost(int no) {
        mHandler.obtainMessage(GlobalConstants.STATE_WIFI_LOST, no, -1).sendToTarget();
    }

    //发送通信失败信号
    private void connectionFail(int no) {
        mHandler.obtainMessage(GlobalConstants.STATE_WIFI_CONNECT_FAILE, no, -1).sendToTarget();
    }

    //发送读取信息信号
    private void connectionRead(int no, String str) {
        mHandler.obtainMessage(GlobalConstants.STATE_WIFI_READ, -1, no, str).sendToTarget();
    }

    //发送写入信息信号
    private void connectionWrite(int no, String msg) {
        mHandler.obtainMessage(GlobalConstants.STATE_WIFI_WRITE, -1, no, msg).sendToTarget();
    }

    //处理灯柱的通过时间
    private void connectionClick(int no, String str) {
        mHandler.obtainMessage(GlobalConstants.STATE_WIFI_CLICK, -1, no, str).sendToTarget();
    }

    /**
     * 打开AP热点
     *
     * @param enabled true：打开AP；false：关闭AP
     * @return 操作是否成功
     */
    public boolean setWifiApEnabled(boolean enabled) {
        // disable WiFi in any case
        if (enabled) {
            mWifiManager.setWifiEnabled(false);
        }

        try {
            // WIFI设定
            WifiConfiguration apConfig = new WifiConfiguration();
            //2016/03/27 chenxy update start
            apConfig.SSID = Parameter.WIFI_SSID;
            //2016/03/27 chenxy update end
            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            //2016/03/27 chenxy update start
            apConfig.preSharedKey = Parameter.WIFI_SHARED_KEY;
            //2016/03/27 chenxy update end

            // 关闭热点前，先解开AP锁（AP锁可以防止黑屏时WIFI自动关闭）
            if (!enabled) {
                releaseWifiLock();
            }

            // 获得设定WIFIAP的方法
            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            boolean reFlag = (Boolean) method.invoke(mWifiManager, apConfig, enabled);

            // 打开热点后，先打开AP锁（AP锁可以防止黑屏时WIFI自动关闭）
            if (enabled) {
                creatWifiLock();
            }
            DebugLog.debug(context, "WifiApEnabled:" + enabled);

            return reFlag;
        } catch (Exception e) {
            DebugLog.debug(context, "Cannot set WiFi AP state：" + e);
            return false;
        }
    }

    /*
     * 返回AP状态
     */
    public int getWifiApState() {
        try {
            // 因为此方法是@hide标注，故只能反射取得（可能存在兼容性问题）
            Method method = mWifiManager.getClass().getMethod("getWifiApState");
            return (Integer) method.invoke(mWifiManager);
        } catch (Exception e) {
            DebugLog.debug(context, "Cannot get WiFi AP state：" + e);
            return WifiManager.WIFI_STATE_UNKNOWN;
        }
    }

    /*
     * 返回AP状态，如果AP已经启动或者启动中，
     * 则返回true
     */
    public boolean isApEnabled() {
        int state = getWifiApState();
        return WifiManager.WIFI_STATE_ENABLING == state || WifiManager.WIFI_STATE_ENABLED == state;
    }

    /*
     * 返回WIFI状态，如果WIFI已经启动或者启动中，
     * 则返回true
     */
    public boolean isWifiEnabled() {
        int state = mWifiManager.getWifiState();
        return WifiManager.WIFI_STATE_ENABLING == state || WifiManager.WIFI_STATE_ENABLED == state;
    }

    //打开WIFI
    public void OpenWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    //关闭WIFI
    public void CloseWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    //解锁WifiLock
    public void releaseWifiLock() {
        // 判断wifi是否被lock锁持用
        if (mWifiLock.isHeld()) {
            // 释放WIFI锁
            mWifiLock.release();
        }
    }

    /**
     * 创建一个WifiLock
     */
    public void creatWifiLock() {
        // 创建WIFILOCK
        mWifiLock = mWifiManager.createWifiLock(Parameter.WIFI_LOCK_NAME);
        // WIFI加锁
        mWifiLock.acquire();
    }

    /**
     * WIFI监听进程
     */
    private class AcceptThread extends Thread {
        boolean acceptFlag = true;

        /** 监听进程初始化 */
        public AcceptThread() {
            try {
                DebugLog.debug(context, "AcceptThread init start");

                serverSocket = new ServerSocket(Parameter.WIFI_SERVER_PORT);
                SocketAddress address = null;
                if (!serverSocket.isBound()) {
                    serverSocket.bind(address, Parameter.WIFI_SERVER_PORT_BACKUP);
                }
                DebugLog.debug(context, "AcceptThread_serverSocket.getLocalSocketAddress()：" + serverSocket.getLocalSocketAddress());
                DebugLog.debug(context, "AcceptThread_serverSocket.getReuseAddress()：" + serverSocket.getReuseAddress());

                DhcpInfo info = mWifiManager.getDhcpInfo();
                DebugLog.debug(context, "AcceptThread_info.serverAddress：" + info.serverAddress);

                DebugLog.debug(context, "AcceptThread init end");
            } catch (IOException e) {
                DebugLog.debug(context, "AcceptThread init e:" + e.toString());
                e.printStackTrace();
            }
        }

        /** 监听进程启动 */
        public void run() {
            Socket socket = null;
            try {
                while (acceptFlag) {
                    DebugLog.debug(context, "AcceptThread start");

                    // 将每一个连接到该服务器的客户端，加到List中
                    socket = serverSocket.accept();

                    //2016/03/27 chenxy add start
                    // 最多添加到4个客户端，如果超过4个的话，关闭
                    if (clientSocketList.size() >= 4) {
                        DebugLog.debug(context, "客户端达到4个，不再添加新的客户端");
                        socket.close();
                        continue;
                    }
                    //2016/03/27 chenxy add end

                    ClientList socketClient = new ClientList();
                    // 顺序取下一个灯柱编号
                    socketClient.value_no = String.valueOf(clientSocketList.size() + 1);
                    socketClient.socket = socket;
                    clientSocketList.add(socketClient);

                    // 每一个连接到服务器的客户端，服务器开启一个新的线程来处理
                    ConnectedThread connectedThread = new ConnectedThread(socket, clientSocketList.size());
                    connectedThread.start();
                    clientConnectedThreadList.add(connectedThread);

                    DebugLog.debug(context, "AcceptThread_serverSocket.getLocalSocketAddress()：" + serverSocket.getLocalSocketAddress());
                    DebugLog.debug(context, "AcceptThread_socket.getLocalSocketAddress()：" + socket.getLocalSocketAddress());
                    DebugLog.debug(context, "AcceptThread_socket.getRemoteSocketAddress()：" + socket.getRemoteSocketAddress());

                    // 发送客户端连接信息
                    mHandler.obtainMessage(GlobalConstants.STATE_WIFI_ACCEPT, -1, -1).sendToTarget();
                }
            } catch (IOException ee) {
                //强行关闭时不需要打印信息
                if (acceptFlag) {
                    mHandler.obtainMessage(GlobalConstants.STATE_WIFI_ACCEPT_FAILE, -1, -1, ee.toString()).sendToTarget();
                    DebugLog.debug(context, "AcceptThread run Exception:" + ee);
                }
                return;
            }
        }

        //监听进程关闭
        public void cancel() {
            try {
                //关闭所有客户端进程
                if (clientConnectedThreadList != null && clientConnectedThreadList.size() > 0) {
                    for (int i = 0; i < clientConnectedThreadList.size(); i++) {
                        if (clientConnectedThreadList.get(i) != null) {
                            clientConnectedThreadList.get(i).cancel();
                        }
                        clientConnectedThreadList.remove(i);
                    }
                }
                //关闭监听进程
                if (serverSocket != null) {
                    acceptFlag = false;
                    serverSocket.close();
                }
                this.interrupt();
            } catch (IOException e) {
                DebugLog.debug(context, "AcceptThread cancel Exception:" + e);
            }
        }
    }

    /*
     * 连接的客户端线程
     */
    public class ConnectedThread extends Thread {
        private final Socket mSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        //客户端编号
        public int no;
        // 连接状态
        public boolean connected = true;
        //当前发送信号的序号
        public int sendMsgIndex = -1;
        Vector<String> vecMsg = new Vector<String>();
        String endStr = "";
        //等待回信标志位
        private boolean reFlag = false;
        //重复送信次数
        private int reSendCount = 0;
        //发送信号序列
        private String[] sendMsg = null;
        //响应序列
        private String[] waitStatus = null;

        /**
         * 灯柱连接线程
         * @param socket 连接的Socket
         * @param no 灯柱编号
         */
        public ConnectedThread(Socket socket, int no) {
            // Socket
            mSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // 灯柱编号
            this.no = no;

            // Get the BluetoothSocket input and output streams
            try {
                // Socket输入流
                tmpIn = mSocket.getInputStream();
                // Socket输出流
                tmpOut = mSocket.getOutputStream();
            } catch (IOException e) {
                DebugLog.debug(context, "temp sockets not created：" + e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;

            // 与灯柱连接成功后给信号柱发送等待中信号
            writeThread(Parameter.CONNECT_INF_WAIT);
        }

        /**
         * @取得发送信号序列
         */
        public String[] getSendMsg() {
            return sendMsg;
        }

        /**
         * @设定发送信号序列
         */
        public void setSendMsg(String[] sendMsg) {
            this.sendMsg = sendMsg;
        }

        /**
         * @取得指定发送信号序列
         */
        public String getOneSendMsg() {
            return sendMsg[sendMsgIndex];
        }

        /**
         * @取得响应序列
         */
        public String[] getWaitStatus() {
            return waitStatus;
        }

        /**
         * @设定响应序列
         */
        public void setWaitStatus(String[] waitStatus) {
            this.waitStatus = waitStatus;
        }

        /**
         * @取得指定响应序列
         */
        public String getOneWaitStatus() {
            return waitStatus[sendMsgIndex];
        }

        public synchronized void run() {
            DebugLog.debug(context, "BEGIN mConnectedThread：" + no);

            //等待1秒
            try {
                //发送灯柱信号
                reFlag = false;
                if (this.no == 1) {
                    writeThread(Parameter.CONNECT_INF_POINT_1);
                } else if (this.no == 2) {
                    writeThread(Parameter.CONNECT_INF_POINT_2);
                } else if (this.no == 3) {
                    writeThread(Parameter.CONNECT_INF_POINT_3);
                } else if (this.no == 4) {
                    writeThread(Parameter.CONNECT_INF_POINT_4);
                }
            } catch (Exception e) {
                DebugLog.debug(context, "连接信号柱报错。");
                e.printStackTrace();
            }

            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (mSocket != null) {
                try {
                    DebugLog.debug(context, "mConnectedThread wait read.");
                    // Read from the InputStream
                    if ((bytes = mmInStream.read(buffer)) > 0) {
                        // 信号解析
                        byte[] readBuf = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            readBuf[i] = buffer[i];
                        }
                        String readMessage = CharHelper.bytesToHexString(readBuf);

                        DebugLog.debug(context, "从" + no + "号柱收到信息：" + readMessage);

                        // 上次残留信息的连接
                        if (endStr != null && endStr.length() > 0) {
                            readMessage = endStr + readMessage;
                        }

                        // 本次信息的截取
                        while (readMessage.length() > 0) {
                            if (readMessage.length() >= 8) {
                                vecMsg.add(readMessage.substring(0, 8));
                                readMessage = readMessage.substring(8);
                            } else if (readMessage.length() > 0) {
                                endStr = readMessage;
                                break;
                            } else {
                                endStr = "";
                                break;
                            }
                        }

                        // 逐条处理收到的信息
                        for (int i = 0; i < vecMsg.size(); i++) {
                            readMessage = vecMsg.get(i);

                            DebugLog.debug(context, "处理从" + no + "号柱收到信息：" + readMessage);
                            // 输出日志
                            connectionRead(no, readMessage);

                            // 判断收到信息的正确性
                            if (readMessage.startsWith(Parameter.CONNECT_INF_START)                             //有开始标志头
                                    && readMessage.endsWith(Parameter.CONNECT_INF_END)                          //有结束标志尾
                                    && readMessage.length() == 8                                                //信号长度为8
                                    && readMessage.substring(2, 4).equals(readMessage.substring(4, 6))) {       //传输的信号完全一致
                                // 收到信号柱发送OK信号后，设置标志位，可以继续向信号柱发送信号
                                if (reFlag && readMessage.substring(2, 4).equals(Parameter.CONNECT_INF_OK)) {
                                    reFlag = false;
                                    // 收到信号柱发送NG信号后，不设置标志位，继续发送重复信号
                                } else if (reFlag && readMessage.substring(2, 4).equals(Parameter.CONNECT_INF_NG)) {
                                    // 收到灯柱的通过信号后，处理通过信号
                                } else {
                                    connectionClick(no, readMessage);
                                }
                            } else {
                                // 信号不符合协议时，发送【通信失败】信号，连接断开
                                writeThread(Parameter.CONNECT_INF_NG);
                                connected = false;
                                // 关闭连接
                                cancel();
                                // 输出日志
                                connectionLost(no);
                            }
                        }
                        //初始化信息
                        vecMsg = new Vector<String>();
                    } else if ((bytes = mmInStream.read(buffer)) == -1) {
                        connected = false;
                        // 关闭连接
                        cancel();
                        // 输出日志
                        connectionLost(no);
                        return;
                    }
                } catch (Exception e) {
                    Log.e("freelight", "客户端连接报错." + e);
                    DebugLog.debug(context, "ConnectedThread disconnected：" + e);
                    connected = false;
                    cancel();
                    connectionLost(no);
                    return;
                }
            }
        }

        /**
         * 关闭连接
         */
        public void cancel() {
            try {
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
                DebugLog.debug(context, "clientSocketList cancel failed" + e);
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
                ConnectedThreadWriter connectedThreadWriter = new ConnectedThreadWriter(msg);
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

                ConnectedThreadWriter connectedThreadWriter = new ConnectedThreadWriter(msg);
                connectedThreadWriter.start();
            }
        }

        /**
         * 发送信号用线程，由于发送不成功需要重复发送，所以用多线程实现
         */
        public class ConnectedThreadWriter extends Thread {
            private String msg;

            public ConnectedThreadWriter(String msg) {
                this.msg = msg;
            }

            public synchronized void run() {
                try {
                    // 发送OK和NG消息时，只发送不再接收
                    if (msg.substring(2, 4).equals(Parameter.CONNECT_INF_OK) || msg.substring(2, 4).equals(Parameter.CONNECT_INF_NG)) {
                        byte[] send = CharHelper.hexStringToBytes(msg);
                        mmOutStream.write(send);
                        mmOutStream.flush();
                        // 给客户端发送信息（写日志）
                        mHandler.obtainMessage(GlobalConstants.STATE_WIFI_WRITE, -1, no, msg).sendToTarget();

                        DebugLog.debug(context, "给" + no + "号柱发送信息：" + msg);
                    } else {
                        reSendCount = 0;
                        reFlag = true;
                        while (reFlag && reSendCount < Parameter.RESEND_MAX_COUNT && connected) {
                            byte[] send = CharHelper.hexStringToBytes(msg);
                            mmOutStream.write(send);
                            mmOutStream.flush();
                            // 给客户端发送信息（日志）
                            connectionWrite(no, msg);
                            DebugLog.debug(context, "给" + no + "号柱发送信息：" + msg);
                            // 发送计数器++
                            reSendCount++;
                            // 延迟3秒重新发送
                            Thread.sleep(Parameter.RESEND_WAIT_TIME);
                        }
                        // 多次发送没有回信，通知通信失败
                        if (reFlag) {
                            connected = false;
                            // 断开连接
                            cancel();
                            // 写入客户端连接丢失日志
                            connectionLost(no);
                        }
                    }
                    // 中断线程
                    this.interrupt();
                } catch (Exception e) {

                    DebugLog.debug(context, "Exception during write：" + e);
                    connected = false;
                    // 断开连接
                    cancel();
                    // 写入客户端连接丢失日志
                    connectionLost(no);
                }
            }
        }
    }
}
