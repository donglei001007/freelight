package com.ssp365.android.freelight.wifi;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import com.ssp365.android.freelight.activity.WifisetActivity.ClientList;
import com.ssp365.android.freelight.common.CharHelper;
import com.ssp365.android.freelight.common.DebugLog;
import com.ssp365.android.freelight.common.GlobalConstants;
import com.ssp365.android.freelight.common.SmartSportApplication;
import com.ssp365.android.freelight.common.SmartSportHandler;
import com.ssp365.android.freelight.model.Parameter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

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

        /**
         * 监听进程初始化
         */
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

        /**
         * 监听进程启动
         */
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
                    ConnectedThread connectedThread = new ConnectedThread(socket, clientSocketList.size(), mHandler);
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
}
